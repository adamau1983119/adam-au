$ErrorActionPreference='Stop'
$p="app/src/main/assets/fortunes_source.csv"
if(!(Test-Path $p)){ Write-Output ("ERROR="+$p); exit 1 }
$bytes=[IO.File]::ReadAllBytes($p)
$action=""
$text=$null
if($bytes.Length -ge 3 -and $bytes[0]-eq 0xEF -and $bytes[1]-eq 0xBB -and $bytes[2]-eq 0xBF){
  $text=[Text.Encoding]::UTF8.GetString($bytes,3,$bytes.Length-3)
  $action="stripped_bom"
} elseif($bytes.Length -ge 2 -and $bytes[0]-eq 0xFF -and $bytes[1]-eq 0xFE){
  $enc=[Text.Encoding]::Unicode
  $text=$enc.GetString($bytes)
  $action="converted_utf16le"
} elseif($bytes.Length -ge 2 -and $bytes[0]-eq 0xFE -and $bytes[1]-eq 0xFF){
  $enc=[Text.Encoding]::BigEndianUnicode
  $text=$enc.GetString($bytes)
  $action="converted_utf16be"
} else {
  $enc=[Text.Encoding]::GetEncoding(950)
  $text=$enc.GetString($bytes)
  $action="converted_ms950"
}
[IO.File]::WriteAllText($p,$text,(New-Object Text.UTF8Encoding($false)))
Write-Output ("FILE="+(Resolve-Path $p))
Write-Output ("ACTION="+$action)



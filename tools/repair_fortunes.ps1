$ErrorActionPreference='Stop'

$src='app/src/main/assets/fortunes_source.csv'
$bak='app/src/main/assets/fortunes_source.backup.csv'
$out='app/src/main/assets/fortunes_source.csv'

if(!(Test-Path $src)){ Write-Output ('ERROR=MISSING '+$src); exit 1 }

# 先備份原檔
Copy-Item -Path $src -Destination $bak -Force

$rows = Import-Csv -Path $src -Encoding UTF8
$fixed = @()

foreach($r in $rows){
  $id = [int]$r.id
  if($id -lt 1 -or $id -gt 100){ continue }

  $title = [string]$r.title
  $summary = [string]$r.summary
  $content = [string]$r.content

  if([string]::IsNullOrWhiteSpace($content)){
    $lines = @()
    if($summary){
      $lines = ($summary -split "`r?`n") | Where-Object { $_ -ne '' }
    }

    if($lines.Count -ge 1){
      # 第一行作為更完整的標題（若有）
      $first = $lines[0].Trim()
      if($first){ $title = $first }
    }
    if($lines.Count -ge 2){
      # 第二行作為摘要
      $summary = $lines[1].Trim()
    }
    if($lines.Count -ge 3){
      # 其餘行全部併成 content（保留換行）
      $content = ($lines[2..($lines.Count-1)] -join "`n").Trim()
    }
  }

  # 仍然空白就用保底值以通過解析
  if([string]::IsNullOrWhiteSpace($summary)){ $summary = '（待補）' }
  if([string]::IsNullOrWhiteSpace($content)){ $content = '（待補全文）' }

  $fixed += [PSCustomObject]@{
    id      = $id
    title   = $title
    summary = $summary
    content = $content
  }
}

# 依 id 排序輸出為 UTF-8（無 BOM）
$fixed = $fixed | Sort-Object { [int]$_.id }
$csv = $fixed | ConvertTo-Csv -NoTypeInformation
[IO.File]::WriteAllText($out, ($csv -join "`r`n"), (New-Object Text.UTF8Encoding($false)))

Write-Output ('BACKUP='+ (Resolve-Path $bak))
Write-Output ('WROTE='+ (Resolve-Path $out))
Write-Output ('COUNT='+ $fixed.Count)


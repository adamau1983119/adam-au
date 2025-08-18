Add-Type -AssemblyName System.Drawing
$src = "D:\\Users\\Adam\\Desktop\\Myproject\\Androidproject\\WTSaskingforsignature\\app\\src\\main\\res\\drawable\\WTS01.png"
$dst = "D:\\Users\\Adam\\Desktop\\Myproject\\Androidproject\\WTSaskingforsignature\\app\\src\\main\\res\\drawable\\WTS01.gif"
if (-not (Test-Path $src)) { throw "找不到來源圖：$src" }
$img = [System.Drawing.Image]::FromFile($src)
$img.Save($dst, [System.Drawing.Imaging.ImageFormat]::Gif)
$img.Dispose()
Write-Output "OK: $dst"



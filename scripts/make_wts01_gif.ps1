param(
    [string]$SrcPath = "D:\\Users\\Adam\\Desktop\\Myproject\\Androidproject\\WTSaskingforsignature\\app\\src\\main\\res\\drawable\\wts01.png",
    [string]$RefGifPath = "D:\\Users\\Adam\\Desktop\\Myproject\\Androidproject\\WTSaskingforsignature\\app\\src\\main\\res\\drawable\\celestial.gif",
    [string]$OutPath = "D:\\Users\\Adam\\Desktop\\Myproject\\Androidproject\\WTSaskingforsignature\\app\\src\\main\\res\\raw\\wts01_celestial.gif",
    [int]$Frames = 16,
    [double]$PulseAmplitude = 0.12
)

Add-Type -AssemblyName System.Drawing

function New-TintedFrame {
    param(
        [System.Drawing.Bitmap]$BaseBitmap,
        [double]$phase,
        [double]$pulseAmp,
        [double]$tintBase
    )

    $width = $BaseBitmap.Width
    $height = $BaseBitmap.Height
    $bmp = New-Object System.Drawing.Bitmap $width, $height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb
    $gfx = [System.Drawing.Graphics]::FromImage($bmp)
    $gfx.CompositingMode = [System.Drawing.Drawing2D.CompositingMode]::SourceOver

    $brightness = 1.0 + $pulseAmp * [Math]::Sin(2 * [Math]::PI * $phase)
    $ia = New-Object System.Drawing.Imaging.ImageAttributes
    $cm = New-Object System.Drawing.Imaging.ColorMatrix
    $cm.Matrix00 = $brightness
    $cm.Matrix11 = $brightness
    $cm.Matrix22 = $brightness
    $cm.Matrix33 = 1
    $cm.Matrix44 = 1
    $ia.SetColorMatrix($cm)

    $rect = New-Object System.Drawing.Rectangle 0,0,$width,$height
    $gfx.DrawImage($BaseBitmap, $rect, 0,0,$width,$height, [System.Drawing.GraphicsUnit]::Pixel, $ia)

    $gfx.Dispose()
    return $bmp
}

if (-not (Test-Path $SrcPath)) { throw "找不到來源圖：$SrcPath" }

$baseImg = [System.Drawing.Bitmap]::FromFile($SrcPath)

$refGif = $null
if (Test-Path $RefGifPath) { try { $refGif = [System.Drawing.Image]::FromFile($RefGifPath) } catch {} }

$images = New-Object System.Collections.ArrayList
for ($i=0; $i -lt $Frames; $i++) {
    $phase = $i / [double]$Frames
    $frame = New-TintedFrame -BaseBitmap $baseImg -phase $phase -pulseAmp $PulseAmplitude -tintBase $TintAlphaBase
    [void]$images.Add($frame)
}

$gifEncoder = [System.Drawing.Imaging.ImageCodecInfo]::GetImageEncoders() | Where-Object { $_.MimeType -eq 'image/gif' }
$saveFlag = [System.Drawing.Imaging.Encoder]::SaveFlag

function New-EncoderParams([System.Drawing.Imaging.EncoderValue]$val) {
    $p = New-Object System.Drawing.Imaging.EncoderParameters 1
    $p.Param[0] = New-Object System.Drawing.Imaging.EncoderParameter ($saveFlag, [long]$val)
    return $p
}

$first = [System.Drawing.Image]$images[0]

try {
    if ($refGif -ne $null) {
        $delayProp = $refGif.PropertyItems | Where-Object { $_.Id -eq 0x5100 } | Select-Object -First 1
        $loopProp  = $refGif.PropertyItems | Where-Object { $_.Id -eq 0x5101 } | Select-Object -First 1
        if ($delayProp -ne $null) {
            $perFrameDelayCs = 8
            $delayBytes = New-Object byte[] ($Frames*4)
            for ($i=0; $i -lt $Frames; $i++) {
                [byte[]]$d = [System.BitConverter]::GetBytes([UInt32]$perFrameDelayCs)
                $d.CopyTo($delayBytes, $i*4)
            }
            $delayProp.Value = $delayBytes
            $delayProp.Len = $delayBytes.Length
            $first.SetPropertyItem($delayProp)
        }
        if ($loopProp -ne $null) { $first.SetPropertyItem($loopProp) }
    }
} catch {}

if (Test-Path $OutPath) { Remove-Item -Force $OutPath }

$p = New-EncoderParams ([System.Drawing.Imaging.EncoderValue]::MultiFrame)
$first.Save($OutPath, $gifEncoder, $p)

$p = New-EncoderParams ([System.Drawing.Imaging.EncoderValue]::FrameDimensionTime)
for ($i=1; $i -lt $images.Count; $i++) { $first.SaveAdd($images[$i], $p) }

$p = New-EncoderParams ([System.Drawing.Imaging.EncoderValue]::Flush)
$first.SaveAdd($p)

foreach ($f in $images) { $f.Dispose() }
$first.Dispose()
if ($refGif -ne $null) { $refGif.Dispose() }
$baseImg.Dispose()

Write-Output "OK: $OutPath"



# 图片资源检查脚本
# 检查drawable和mipmap目录中的图片文件

Write-Host "🖼️ WTS灵签APP图片资源检查" -ForegroundColor Cyan
Write-Host "=" * 60 -ForegroundColor Cyan

# 检查drawable目录
Write-Host "`n📁 检查drawable目录..." -ForegroundColor Yellow
$drawablePath = "app/src/main/res/drawable"

if (Test-Path $drawablePath) {
    $drawableFiles = Get-ChildItem $drawablePath -File
    
    Write-Host "`n🔍 drawable文件列表:" -ForegroundColor Green
    foreach ($file in $drawableFiles) {
        $sizeKB = [math]::Round($file.Length / 1KB, 2)
        $extension = $file.Extension.ToLower()
        
        # 根据文件扩展名判断类型
        $fileType = switch ($extension) {
            ".png" { "PNG图片" }
            ".jpg" { "JPEG图片" }
            ".jpeg" { "JPEG图片" }
            ".gif" { "GIF动画" }
            ".webp" { "WebP图片" }
            default { "其他格式" }
        }
        
        Write-Host "  📸 $($file.Name)" -ForegroundColor White
        Write-Host "     类型: $fileType" -ForegroundColor Gray
        Write-Host "     大小: $sizeKB KB" -ForegroundColor Gray
        Write-Host "     路径: $($file.FullName)" -ForegroundColor Gray
        Write-Host ""
    }
} else {
    Write-Host "❌ drawable目录不存在" -ForegroundColor Red
}

# 检查mipmap目录
Write-Host "`n📁 检查mipmap目录..." -ForegroundColor Yellow

$mipmapDirs = @(
    "mipmap-mdpi",
    "mipmap-hdpi", 
    "mipmap-xhdpi",
    "mipmap-xxhdpi",
    "mipmap-xxxhdpi"
)

foreach ($dir in $mipmapDirs) {
    $fullPath = "app/src/main/res/$dir"
    if (Test-Path $fullPath) {
        $files = Get-ChildItem $fullPath -File
        Write-Host "`n  📂 $dir:" -ForegroundColor Cyan
        
        foreach ($file in $files) {
            $sizeKB = [math]::Round($file.Length / 1KB, 2)
            Write-Host "    📸 $($file.Name) - $sizeKB KB" -ForegroundColor White
        }
    } else {
        Write-Host "  ❌ $dir 目录不存在" -ForegroundColor Red
    }
}

# 检查图片资源规范
Write-Host "`n📋 图片资源规范检查:" -ForegroundColor Yellow

Write-Host "`n✅ 推荐规范:" -ForegroundColor Green
Write-Host "  • PNG格式: 适合图标和透明背景图片" -ForegroundColor White
Write-Host "  • JPEG格式: 适合照片和复杂图片" -ForegroundColor White
Write-Host "  • GIF格式: 适合简单动画" -ForegroundColor White
Write-Host "  • WebP格式: 现代压缩格式，推荐使用" -ForegroundColor White

Write-Host "`n📏 尺寸建议:" -ForegroundColor Green
Write-Host "  • 应用图标: 512x512px (主图标)" -ForegroundColor White
Write-Host "  • 启动图标: 1024x1024px (Play Store)" -ForegroundColor White
Write-Host "  • 背景图片: 1080x1920px (全屏背景)" -ForegroundColor White
Write-Host "  • 内容图片: 根据实际需要，建议不超过2048px" -ForegroundColor White

Write-Host "`n💾 文件大小建议:" -ForegroundColor Green
Write-Host "  • 应用图标: < 100KB" -ForegroundColor White
Write-Host "  • 背景图片: < 500KB" -ForegroundColor White
Write-Host "  • 内容图片: < 1MB" -ForegroundColor White
Write-Host "  • 总图片资源: < 10MB" -ForegroundColor White

# 检查当前资源状态
Write-Host "`n🔍 当前资源状态分析:" -ForegroundColor Yellow

$totalSize = 0
$pngCount = 0
$jpgCount = 0
$gifCount = 0

if (Test-Path $drawablePath) {
    $drawableFiles = Get-ChildItem $drawablePath -File
    foreach ($file in $drawableFiles) {
        $totalSize += $file.Length
        $extension = $file.Extension.ToLower()
        
        switch ($extension) {
            ".png" { $pngCount++ }
            ".jpg" { $jpgCount++ }
            ".gif" { $gifCount++ }
        }
    }
}

$totalSizeMB = [math]::Round($totalSize / 1MB, 2)

Write-Host "`n📊 资源统计:" -ForegroundColor Green
Write-Host "  • 总文件数: $($drawableFiles.Count)" -ForegroundColor White
Write-Host "  • PNG文件: $pngCount" -ForegroundColor White
Write-Host "  • JPEG文件: $jpgCount" -ForegroundColor White
Write-Host "  • GIF文件: $gifCount" -ForegroundColor White
Write-Host "  • 总大小: $totalSizeMB MB" -ForegroundColor White

# 检查结果和建议
Write-Host "`n📋 检查结果和建议:" -ForegroundColor Cyan
Write-Host "=" * 60 -ForegroundColor Cyan

if ($totalSizeMB -gt 10) {
    Write-Host "⚠️  注意: 图片资源总大小超过10MB，建议优化" -ForegroundColor Yellow
} else {
    Write-Host "✅ 图片资源总大小合理" -ForegroundColor Green
}

if ($jpgCount -gt 5) {
    Write-Host "💡 建议: 考虑将部分JPEG转换为WebP格式以减小文件大小" -ForegroundColor Blue
}

if ($gifCount -gt 0) {
    Write-Host "💡 建议: GIF文件较大，考虑使用WebP动画或视频替代" -ForegroundColor Blue
}

Write-Host "`n🚀 优化建议:" -ForegroundColor Yellow
Write-Host "1. 使用WebP格式替代JPEG和PNG" -ForegroundColor White
Write-Host "2. 压缩大尺寸图片" -ForegroundColor White
Write-Host "3. 为不同密度设备提供不同尺寸图片" -ForegroundColor White
Write-Host "4. 使用矢量图形(SVG)替代位图" -ForegroundColor White

Write-Host "`n✅ 图片资源检查完成！" -ForegroundColor Green

# 籤文数据修复脚本
# 解决编码问题和数据完整性问题

Write-Host "🚀 籤文数据修复工具" -ForegroundColor Green
Write-Host "=" * 40 -ForegroundColor Green

# 创建备份
Write-Host "💾 创建备份..." -ForegroundColor Yellow
$csvPath = "app/src/main/assets/fortunes_source.csv"
$backupPath = "app/src/main/assets/fortunes_source.backup.csv"

if (Test-Path $csvPath) {
    try {
        Copy-Item $csvPath $backupPath -Force
        Write-Host "✅ 备份创建完成" -ForegroundColor Green
    }
    catch {
        Write-Host "❌ 备份失败: $_" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "❌ CSV文件不存在: $csvPath" -ForegroundColor Red
    exit 1
}

# 修复编码问题
Write-Host "🔧 修复CSV文件编码..." -ForegroundColor Yellow
try {
    # 读取UTF8内容
    $content = Get-Content $csvPath -Encoding UTF8 -Raw
    
    # 重新写入，确保UTF8编码
    $content | Out-File -FilePath $csvPath -Encoding UTF8 -NoNewline
    
    Write-Host "✅ CSV编码修复完成" -ForegroundColor Green
}
catch {
    Write-Host "❌ 编码修复失败: $_" -ForegroundColor Red
    exit 1
}

# 验证籤文数据
Write-Host "🔍 验证籤文数据..." -ForegroundColor Yellow
try {
    $lines = Get-Content $csvPath -Encoding UTF8
    $totalLines = $lines.Count
    Write-Host "📊 总行数: $totalLines" -ForegroundColor Cyan
    
    # 计算籤文数量（减去表头）
    $fortuneCount = $totalLines - 1
    Write-Host "🔢 籤文数量: $fortuneCount" -ForegroundColor Cyan
    
    # 检查前几行和后几行
    Write-Host "📖 前5行内容:" -ForegroundColor Cyan
    $lines[0..4] | ForEach-Object { Write-Host "   $_" -ForegroundColor White }
    
    Write-Host "📖 后5行内容:" -ForegroundColor Cyan
    $lines[($totalLines-5)..($totalLines-1)] | ForEach-Object { Write-Host "   $_" -ForegroundColor White }
    
    # 检查是否有100个籤文
    if ($fortuneCount -ge 100) {
        Write-Host "✅ 籤文数量充足，满足100籤要求" -ForegroundColor Green
    } else {
        Write-Host "⚠️  籤文数量不足，只有 $fortuneCount 籤" -ForegroundColor Yellow
    }
    
}
catch {
    Write-Host "❌ 验证失败: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "🎉 修复完成！" -ForegroundColor Green
Write-Host "请重新测试应用程式。" -ForegroundColor Cyan
Write-Host ""
Write-Host "📋 下一步操作:" -ForegroundColor Yellow
Write-Host "1. 重新构建应用程式: ./gradlew clean assembleDebug" -ForegroundColor White
Write-Host "2. 安装到设备: ./gradlew installDebug" -ForegroundColor White
Write-Host "3. 测试籤文功能" -ForegroundColor White

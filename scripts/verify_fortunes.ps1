# 籤文数据验证脚本
Write-Host "🔍 籤文数据完整性验证" -ForegroundColor Green
Write-Host "=" * 40 -ForegroundColor Green

$csvPath = "app/src/main/assets/fortunes_source.csv"

if (-not (Test-Path $csvPath)) {
    Write-Host "❌ CSV文件不存在: $csvPath" -ForegroundColor Red
    exit 1
}

try {
    # 读取CSV文件
    $content = Get-Content $csvPath -Encoding UTF8
    $totalLines = $content.Count
    
    Write-Host "📊 文件信息:" -ForegroundColor Cyan
    Write-Host "   总行数: $totalLines" -ForegroundColor White
    Write-Host "   籤文数量: $($totalLines - 1)" -ForegroundColor White
    
    # 检查表头
    $header = $content[0]
    Write-Host "📋 表头: $header" -ForegroundColor Cyan
    
    # 提取籤文ID
    $fortuneIds = @()
    for ($i = 1; $i -lt $content.Count; $i++) {
        $line = $content[$i]
        if ($line -match '^"(\d+)"') {
            $id = [int]$matches[1]
            $fortuneIds += $id
        }
    }
    
    Write-Host "🔢 籤文ID统计:" -ForegroundColor Cyan
    Write-Host "   找到籤文: $($fortuneIds.Count)" -ForegroundColor White
    $minMax = $fortuneIds | Measure-Object -Minimum -Maximum
    Write-Host "   ID范围: $($minMax.Minimum) - $($minMax.Maximum)" -ForegroundColor White
    
    # 检查是否完整
    $expectedIds = 1..100
    $missingIds = $expectedIds | Where-Object { $_ -notin $fortuneIds }
    $extraIds = $fortuneIds | Where-Object { $_ -notin $expectedIds }
    
    if ($missingIds.Count -eq 0 -and $extraIds.Count -eq 0) {
        Write-Host "✅ 籤文数据完整！包含所有100籤" -ForegroundColor Green
    } else {
        if ($missingIds.Count -gt 0) {
            Write-Host "❌ 缺少籤文: $($missingIds -join ', ')" -ForegroundColor Red
        }
        if ($extraIds.Count -gt 0) {
            Write-Host "⚠️  多余籤文: $($extraIds -join ', ')" -ForegroundColor Yellow
        }
    }
    
    # 检查样本籤文内容
    Write-Host "📖 样本籤文检查:" -ForegroundColor Cyan
    
    $sampleIds = @(1, 50, 100)
    foreach ($id in $sampleIds) {
        $line = $content | Where-Object { $_ -match "^`"$id`"," } | Select-Object -First 1
        if ($line) {
            $title = ($line -split '","')[1] -replace '"', ''
            Write-Host "   第$id籤: $($title.Substring(0, [Math]::Min(30, $title.Length)))..." -ForegroundColor White
        } else {
            Write-Host "   ❌ 第$id籤不存在" -ForegroundColor Red
        }
    }
    
    Write-Host ""
    Write-Host "🎯 验证结果:" -ForegroundColor Green
    if ($missingIds.Count -eq 0) {
        Write-Host "   ✅ 籤文数据完整，可以上架" -ForegroundColor Green
    } else {
        Write-Host "   ❌ 籤文数据不完整，需要修复" -ForegroundColor Red
    }
    
} catch {
    Write-Host "❌ 验证失败: $_" -ForegroundColor Red
    exit 1
}

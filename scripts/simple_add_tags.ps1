# 簡化的籤文標籤添加腳本
# 使用更簡單的方法處理籤文38-100

param(
    [string]$InputFile = "app\src\main\assets\fortunes_source_v21.csv"
)

Write-Host "開始為籤文38-100添加標籤..."

# 讀取文件內容
$content = Get-Content $InputFile -Encoding UTF8
$newContent = @()

foreach ($line in $content) {
    $newLine = $line
    
    # 為籤文38-100添加標題標籤
    if ($line -match '^"([3-9][8-9]|[4-9][0-9]|100)",') {
        $newLine = $newLine -replace '"求籤吉凶：', '"[A1]求籤吉凶：'
        $newLine = $newLine -replace '"算命籤詩：', '"[A2-1]算命籤詩：'
        Write-Host "處理籤文標題: $line"
    }
    
    # 為內容添加標籤（只在籤文38-100範圍內）
    if ($line -match '^"([3-9][8-9]|[4-9][0-9]|100)",') {
        $inFortune38Plus = $true
    } elseif ($line -match '^"[1-3][0-7]",') {
        $inFortune38Plus = $false
    }
    
    if ($inFortune38Plus) {
        # 添加各種標籤
        if ($newLine -match '^黃大仙算命解籤詩：') {
            $newLine = $newLine -replace '^黃大仙算命解籤詩：', '[A2-2]黃大仙算命解籤詩：'
        }
        if ($newLine -match '^黃大仙算命解運勢：') {
            $newLine = $newLine -replace '^黃大仙算命解運勢：', '[A2-2]黃大仙算命解運勢：'
        }
        if ($newLine -match '^流年：') {
            $newLine = $newLine -replace '^流年：', '[A3]流年：'
        }
        if ($newLine -match '^事業：') {
            $newLine = $newLine -replace '^事業：', '[B]事業：'
        }
        if ($newLine -match '^財富：') {
            $newLine = $newLine -replace '^財富：', '[C]財富：'
        }
        if ($newLine -match '^自身：') {
            $newLine = $newLine -replace '^自身：', '[D]自身：'
        }
        if ($newLine -match '^家庭：') {
            $newLine = $newLine -replace '^家庭：', '[E]家庭：'
        }
        if ($newLine -match '^姻緣：') {
            $newLine = $newLine -replace '^姻緣：', '[F]姻緣：'
        }
        if ($newLine -match '^移居：') {
            $newLine = $newLine -replace '^移居：', '[G]移居：'
        }
        if ($newLine -match '^名譽：') {
            $newLine = $newLine -replace '^名譽：', '[H]名譽：'
        }
        if ($newLine -match '^健康：') {
            $newLine = $newLine -replace '^健康：', '[I]健康：'
        }
        if ($newLine -match '^友誼：') {
            $newLine = $newLine -replace '^友誼：', '[J]友誼：'
        }
        if ($newLine -match '^風水：') {
            $newLine = $newLine -replace '^風水：', '[K]風水：'
        }
        if ($newLine -match '^遺失：') {
            $newLine = $newLine -replace '^遺失：', '[L]遺失：'
        }
        if ($newLine -match '^天時：') {
            $newLine = $newLine -replace '^天時：', '[N]天時：'
        }
        if ($newLine -match '^交易：') {
            $newLine = $newLine -replace '^交易：', '[O]交易：'
        }
        if ($newLine -match '^出行：') {
            $newLine = $newLine -replace '^出行：', '[P]出行：'
        }
    }
    
    $newContent += $newLine
}

# 寫入文件
$newContent | Out-File -FilePath $InputFile -Encoding UTF8

Write-Host "標籤添加完成！"
Write-Host "處理了 $($newContent.Count) 行"

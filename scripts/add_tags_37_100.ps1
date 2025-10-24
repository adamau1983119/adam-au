# 為籤文37-100添加標籤的PowerShell腳本

param(
    [string]$InputFile = "app\src\main\assets\fortunes_source_v21.csv",
    [string]$OutputFile = "app\src\main\assets\fortunes_source_v21_tagged.csv"
)

Write-Host "開始為籤文37-100添加標籤..."

# 讀取文件內容
$content = Get-Content $InputFile -Encoding UTF8
$newContent = @()

$inFortune37Plus = $false
$fortuneNumber = 0

foreach ($line in $content) {
    # 檢查是否是新籤文的開始
    if ($line -match '^"(\d+)",') {
        $fortuneNumber = [int]$matches[1]
        $inFortune37Plus = ($fortuneNumber -ge 37)
        
        if ($inFortune37Plus) {
            Write-Host "處理籤文 $fortuneNumber"
            # 為籤文標題行添加 [A1] 和 [A2-1] 標籤
            $taggedLine = $line
            $taggedLine = $taggedLine -replace '"求籤吉凶：', '"[A1]求籤吉凶：'
            $taggedLine = $taggedLine -replace '"算命籤詩：', '"[A2-1]算命籤詩：'
            $newContent += $taggedLine
        } else {
            $newContent += $line
        }
    }
    elseif ($inFortune37Plus) {
        # 為籤文37-100的內容添加標籤
        $taggedLine = $line
        
        # 添加各種標籤
        if ($taggedLine -match '^黃大仙算命解籤詩：') {
            $taggedLine = $taggedLine -replace '^黃大仙算命解籤詩：', '[A2-2]黃大仙算命解籤詩：'
        }
        elseif ($taggedLine -match '^黃大仙算命解運勢：') {
            $taggedLine = $taggedLine -replace '^黃大仙算命解運勢：', '[A2-2]黃大仙算命解運勢：'
        }
        elseif ($taggedLine -match '^流年：') {
            $taggedLine = $taggedLine -replace '^流年：', '[A3]流年：'
        }
        elseif ($taggedLine -match '^事業：') {
            $taggedLine = $taggedLine -replace '^事業：', '[B]事業：'
        }
        elseif ($taggedLine -match '^財富：') {
            $taggedLine = $taggedLine -replace '^財富：', '[C]財富：'
        }
        elseif ($taggedLine -match '^自身：') {
            $taggedLine = $taggedLine -replace '^自身：', '[D]自身：'
        }
        elseif ($taggedLine -match '^家庭：') {
            $taggedLine = $taggedLine -replace '^家庭：', '[E]家庭：'
        }
        elseif ($taggedLine -match '^姻緣：') {
            $taggedLine = $taggedLine -replace '^姻緣：', '[F]姻緣：'
        }
        elseif ($taggedLine -match '^移居：') {
            $taggedLine = $taggedLine -replace '^移居：', '[G]移居：'
        }
        elseif ($taggedLine -match '^名譽：') {
            $taggedLine = $taggedLine -replace '^名譽：', '[H]名譽：'
        }
        elseif ($taggedLine -match '^健康：') {
            $taggedLine = $taggedLine -replace '^健康：', '[I]健康：'
        }
        elseif ($taggedLine -match '^友誼：') {
            $taggedLine = $taggedLine -replace '^友誼：', '[J]友誼：'
        }
        elseif ($taggedLine -match '^風水：') {
            $taggedLine = $taggedLine -replace '^風水：', '[K]風水：'
        }
        elseif ($taggedLine -match '^遺失：') {
            $taggedLine = $taggedLine -replace '^遺失：', '[L]遺失：'
        }
        elseif ($taggedLine -match '^天時：') {
            $taggedLine = $taggedLine -replace '^天時：', '[N]天時：'
        }
        elseif ($taggedLine -match '^交易：') {
            $taggedLine = $taggedLine -replace '^交易：', '[O]交易：'
        }
        elseif ($taggedLine -match '^出行：') {
            $taggedLine = $taggedLine -replace '^出行：', '[P]出行：'
        }
        
        $newContent += $taggedLine
    }
    else {
        $newContent += $line
    }
}

# 寫入新文件
$newContent | Out-File -FilePath $OutputFile -Encoding UTF8

Write-Host "標籤添加完成！輸出文件：$OutputFile"
Write-Host "處理了 $($newContent.Count) 行"

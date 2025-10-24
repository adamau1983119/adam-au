# 修復籤文35-100的標籤化腳本
# 為籤文35-100添加標籤

param(
    [string]$InputFile = "app\src\main\assets\fortunes_source_v21.csv",
    [string]$OutputFile = "app\src\main\assets\fortunes_source_v21_fixed.csv"
)

Write-Host "開始修復籤文35-100的標籤化..."

# 讀取文件內容
$content = Get-Content $InputFile -Encoding UTF8
$newContent = @()

$inFortune = $false
$fortuneNumber = 0

foreach ($line in $content) {
    # 檢查是否是新籤文的開始
    if ($line -match '^"(\d+)",') {
        $fortuneNumber = [int]$matches[1]
        $inFortune = $true
        $newContent += $line
        Write-Host "處理籤文 $fortuneNumber"
    }
    elseif ($inFortune -and $fortuneNumber -ge 35) {
        # 為籤文35-100添加標籤
        $taggedLine = $line
        
        # 添加各種標籤
        $taggedLine = $taggedLine -replace "求籤吉凶：", "[A1]求籤吉凶："
        $taggedLine = $taggedLine -replace "算命籤詩：", "[A2-1]算命籤詩："
        $taggedLine = $taggedLine -replace "黃大仙算命解籤詩：", "[A2-2]黃大仙算命解籤詩："
        $taggedLine = $taggedLine -replace "黃大仙算命解運勢：", "[A2-2]黃大仙算命解運勢："
        $taggedLine = $taggedLine -replace "流年：", "[A3]流年："
        $taggedLine = $taggedLine -replace "事業：", "[B]事業："
        $taggedLine = $taggedLine -replace "財富：", "[C]財富："
        $taggedLine = $taggedLine -replace "自身：", "[D]自身："
        $taggedLine = $taggedLine -replace "家庭：", "[E]家庭："
        $taggedLine = $taggedLine -replace "姻緣：", "[F]姻緣："
        $taggedLine = $taggedLine -replace "移居：", "[G]移居："
        $taggedLine = $taggedLine -replace "名譽：", "[H]名譽："
        $taggedLine = $taggedLine -replace "健康：", "[I]健康："
        $taggedLine = $taggedLine -replace "友誼：", "[J]友誼："
        $taggedLine = $taggedLine -replace "風水：", "[K]風水："
        $taggedLine = $taggedLine -replace "遺失：", "[L]遺失："
        $taggedLine = $taggedLine -replace "天時：", "[N]天時："
        $taggedLine = $taggedLine -replace "交易：", "[O]交易："
        $taggedLine = $taggedLine -replace "出行：", "[P]出行："
        
        $newContent += $taggedLine
        
        # 檢查是否籤文結束
        if ($line -match '^"$') {
            $inFortune = $false
        }
    }
    else {
        $newContent += $line
    }
}

# 寫入新文件
$newContent | Out-File -FilePath $OutputFile -Encoding UTF8

Write-Host "修復完成！輸出文件：$OutputFile"
Write-Host "處理了 $($newContent.Count) 行"

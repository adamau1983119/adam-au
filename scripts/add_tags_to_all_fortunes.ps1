# PowerShell 腳本：為所有籤文添加標籤
# 讀取原始 CSV 文件並添加標籤

$inputFile = "D:\Adam 2025\Myproject\Androidproject\WTSaskingforsignature\app\src\main\assets\fortunes_source.csv"
$outputFile = "D:\Adam 2025\Myproject\Androidproject\WTSaskingforsignature\app\src\main\assets\fortunes_source_v21_tagged_2_fixed.csv"

# 讀取原始文件
$content = Get-Content $inputFile -Encoding UTF8

# 創建輸出內容
$output = @()
$output += '"id","title","summary","content"'

# 處理每一行（跳過標題行）
for ($i = 1; $i -lt $content.Length; $i++) {
    $line = $content[$i]
    
    # 解析 CSV 行
    if ($line -match '^"(\d+)","([^"]+)","([^"]+)","(.+)"$') {
        $id = $matches[1]
        $title = $matches[2]
        $summary = $matches[3]
        $content_text = $matches[4]
        
        # 為內容添加標籤
        $tagged_content = Add-TagsToContent $content_text
        
        # 添加到輸出
        $output += "`"$id`",`"$title`",`"$summary`",`"$tagged_content`""
    }
}

# 寫入輸出文件
$output | Out-File $outputFile -Encoding UTF8

Write-Host "標籤化完成！輸出文件：$outputFile"

# 函數：為內容添加標籤
function Add-TagsToContent($content) {
    $result = $content
    
    # 添加 A1 標籤（求籤吉凶）
    $result = $result -replace "求籤吉凶：([^`n]+)", "[A1]求籤吉凶：`$1"
    
    # 添加 A2-1 標籤（算命籤詩）
    $result = $result -replace "算命籤詩：", "[A2-1]算命籤詩："
    
    # 添加 A2-2 標籤（黃大仙算命解籤詩）
    $result = $result -replace "黃大仙算命解籤詩：", "[A2-2]黃大仙算命解籤詩："
    
    # 添加 A3 標籤（流年）
    $result = $result -replace "流年：", "[A3]流年："
    
    # 添加 B 標籤（事業）
    $result = $result -replace "事業：", "[B]事業："
    
    # 添加 C 標籤（財富）
    $result = $result -replace "財富：", "[C]財富："
    
    # 添加 D 標籤（自身）
    $result = $result -replace "自身：", "[D]自身："
    
    # 添加 E 標籤（家庭）
    $result = $result -replace "家庭：", "[E]家庭："
    
    # 添加 F 標籤（姻緣）
    $result = $result -replace "姻緣：", "[F]姻緣："
    
    # 添加 G 標籤（移居）
    $result = $result -replace "移居：", "[G]移居："
    
    # 添加 H 標籤（名譽）
    $result = $result -replace "名譽：", "[H]名譽："
    
    # 添加 I 標籤（健康）
    $result = $result -replace "健康：", "[I]健康："
    
    # 添加 J 標籤（友誼）
    $result = $result -replace "友誼：", "[J]友誼："
    
    # 添加 K 標籤（風水）
    $result = $result -replace "風水：", "[K]風水："
    
    # 添加 L 標籤（遺失）
    $result = $result -replace "遺失：", "[L]遺失："
    
    # 添加 M 標籤（自身）
    $result = $result -replace "自身：", "[M]自身："
    
    # 添加 N 標籤（天時）
    $result = $result -replace "天時：", "[N]天時："
    
    # 添加 O 標籤（交易）
    $result = $result -replace "交易：", "[O]交易："
    
    # 添加 P 標籤（出行）
    $result = $result -replace "出行：", "[P]出行："
    
    return $result
}

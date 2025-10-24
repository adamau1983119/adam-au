# Simple Fortune Tag Processor
# Process first 5 fortunes with basic tagging

$InputFile = "app\src\main\assets\fortunes_source_v21.csv"
$OutputFile = "app\src\main\assets\fortunes_source_v21_tagged.csv"

Write-Host "Reading file: $InputFile"

if (-not (Test-Path $InputFile)) {
    Write-Error "File not found: $InputFile"
    exit 1
}

$content = Get-Content $InputFile -Encoding UTF8
$header = $content[0]
$data = $content[1..($content.Length-1)]

Write-Host "Found $($data.Length) records"

# Process only first 5 records
$processedData = @()
$processedCount = 0

foreach ($row in $data) {
    if ($processedCount -ge 5) {
        $processedData += $row
        continue
    }
    
    # Simple CSV parsing
    $fields = $row -split '","'
    if ($fields.Length -ge 4) {
        $id = $fields[0].Trim('"')
        $title = $fields[1].Trim('"')
        $summary = $fields[2].Trim('"')
        $content = $fields[3].Trim('"')
        
        Write-Host "Processing fortune $id..."
        
        # Add basic tags to content
        $taggedContent = $content -replace "求籤吉凶", "[A1]求籤吉凶"
        $taggedContent = $taggedContent -replace "黃大仙算命解籤詩", "[A2]黃大仙算命解籤詩"
        $taggedContent = $taggedContent -replace "流年", "[A3]流年"
        $taggedContent = $taggedContent -replace "事業", "[B]事業"
        $taggedContent = $taggedContent -replace "財富", "[C]財富"
        $taggedContent = $taggedContent -replace "自身", "[D]自身"
        $taggedContent = $taggedContent -replace "家庭", "[E]家庭"
        $taggedContent = $taggedContent -replace "姻緣", "[F]姻緣"
        $taggedContent = $taggedContent -replace "移居", "[G]移居"
        $taggedContent = $taggedContent -replace "名譽", "[H]名譽"
        $taggedContent = $taggedContent -replace "健康", "[I]健康"
        $taggedContent = $taggedContent -replace "友誼", "[J]友誼"
        $taggedContent = $taggedContent -replace "風水", "[K]風水"
        $taggedContent = $taggedContent -replace "遺失", "[L]遺失"
        $taggedContent = $taggedContent -replace "天時", "[N]天時"
        $taggedContent = $taggedContent -replace "交易", "[O]交易"
        $taggedContent = $taggedContent -replace "出行", "[P]出行"
        
        # Add basic tags to summary
        $taggedSummary = $summary -replace "求籤吉凶", "[A1]求籤吉凶"
        $taggedSummary = $taggedSummary -replace "黃大仙算命解籤詩", "[A2]黃大仙算命解籤詩"
        $taggedSummary = $taggedSummary -replace "流年", "[A3]流年"
        $taggedSummary = $taggedSummary -replace "事業", "[B]事業"
        $taggedSummary = $taggedSummary -replace "財富", "[C]財富"
        $taggedSummary = $taggedSummary -replace "自身", "[D]自身"
        $taggedSummary = $taggedSummary -replace "家庭", "[E]家庭"
        $taggedSummary = $taggedSummary -replace "姻緣", "[F]姻緣"
        $taggedSummary = $taggedSummary -replace "移居", "[G]移居"
        $taggedSummary = $taggedSummary -replace "名譽", "[H]名譽"
        $taggedSummary = $taggedSummary -replace "健康", "[I]健康"
        $taggedSummary = $taggedSummary -replace "友誼", "[J]友誼"
        $taggedSummary = $taggedSummary -replace "風水", "[K]風水"
        $taggedSummary = $taggedSummary -replace "遺失", "[L]遺失"
        $taggedSummary = $taggedSummary -replace "天時", "[N]天時"
        $taggedSummary = $taggedSummary -replace "交易", "[O]交易"
        $taggedSummary = $taggedSummary -replace "出行", "[P]出行"
        
        # Reassemble CSV row
        $newRow = """$id"",""$title"",""$taggedSummary"",""$taggedContent"""
        $processedData += $newRow
        $processedCount++
    } else {
        $processedData += $row
    }
}

# Write output file
$outputContent = @($header) + $processedData
$outputContent | Out-File -FilePath $OutputFile -Encoding UTF8

Write-Host "Processing completed!"
Write-Host "Processed $processedCount fortune records"
Write-Host "Output file: $OutputFile"

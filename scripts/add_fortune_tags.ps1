# Fortune Tags Adding PowerShell Script
# Add NLP analysis tags for Version 21 fortune content

param(
    [string]$InputFile = "app\src\main\assets\fortunes_source_v21.csv",
    [string]$OutputFile = "app\src\main\assets\fortunes_source_v21_tagged.csv",
    [int]$MaxFortunes = 5
)

Write-Host "Starting to process fortune file: $InputFile"

# Check if input file exists
if (-not (Test-Path $InputFile)) {
    Write-Error "Input file not found: $InputFile"
    exit 1
}

# Read CSV file
$content = Get-Content $InputFile -Encoding UTF8
$header = $content[0]
$data = $content[1..($content.Length-1)]

Write-Host "Read $($data.Length) fortune records"

# Tag mapping table
$tagMapping = @{
    'A1' = 'Fortune Level'
    'A2' = 'Wong Tai Sin Fortune Poem'
    'A3' = 'Yearly Fortune'
    'B' = 'Career'
    'C' = 'Wealth'
    'D' = 'Personal'
    'E' = 'Family'
    'F' = 'Love'
    'G' = 'Migration'
    'H' = 'Reputation'
    'I' = 'Health'
    'J' = 'Friendship'
    'K' = 'Feng Shui'
    'L' = 'Lost Items'
    'M' = 'Personal'
    'N' = 'Weather'
    'O' = 'Business'
    'P' = 'Travel'
}

# Keyword matching rules
$keywordRules = @{
    'A1' = @('求籤吉凶', '上上籤', '上籤', '中籤', '下籤', '下下籤', '吉凶')
    'A2' = @('黃大仙算命解籤詩', '算命籤詩', '可喜可賀', '左右逢源')
    'A3' = @('流年', '今年運勢', '去年失去', '今年得到')
    'B' = @('事業', '功名', '貴人', '升遷', '謀事', '工作')
    'C' = @('財富', '求財', '正財', '橫財', '財運')
    'D' = @('自身', '四季平安', '順遂')
    'E' = @('家庭', '家宅', '添丁', '和氣')
    'F' = @('姻緣', '愛情', '婚姻', '適婚')
    'G' = @('移居', '搬遷', '移民', '置業')
    'H' = @('名譽', '嘉獎', '學業', '社會服務')
    'I' = @('健康', '病即愈', '小病')
    'J' = @('友誼', '貴人', '廣結善緣')
    'K' = @('風水', '丁財兩旺', '風水發貴')
    'L' = @('遺失', '失物', '尋回')
    'M' = @('自身')
    'N' = @('天時', '豐稔')
    'O' = @('交易', '買賣')
    'P' = @('出行', '行人', '往來')
}

function Add-TagsToContent {
    param([string]$content)
    
    $lines = $content -split "`n"
    $taggedLines = @()
    
    foreach ($line in $lines) {
        $line = $line.Trim()
        if ([string]::IsNullOrEmpty($line)) {
            $taggedLines += $line
            continue
        }
        
        # Check if already has tags
        if ($line -match '^\[[A-P]\d*\]') {
            $taggedLines += $line
            continue
        }
        
        # Add tags based on keyword matching
        $tagAdded = $false
        foreach ($tag in $keywordRules.Keys) {
            foreach ($keyword in $keywordRules[$tag]) {
                if ($line -match $keyword) {
                    $taggedLine = "[$tag]$line"
                    $taggedLines += $taggedLine
                    $tagAdded = $true
                    break
                }
            }
            if ($tagAdded) { break }
        }
        
        if (-not $tagAdded) {
            $taggedLines += $line
        }
    }
    
    return $taggedLines -join "`n"
}

# Process fortune data
$processedData = @()
$processedCount = 0

foreach ($row in $data) {
    if ($processedCount -ge $MaxFortunes) {
        # For rows beyond limit, copy without processing
        $processedData += $row
        continue
    }
    
    # Parse CSV row
    $fields = $row -split '","'
    if ($fields.Length -ge 4) {
        $id = $fields[0].Trim('"')
        $title = $fields[1].Trim('"')
        $summary = $fields[2].Trim('"')
        $content = $fields[3].Trim('"')
        
        Write-Host "Processing fortune $id..."
        
        # Add tags to content
        $taggedContent = Add-TagsToContent -content $content
        
        # Add tags to summary
        $taggedSummary = Add-TagsToContent -content $summary
        
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

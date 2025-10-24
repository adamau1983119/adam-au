# Process Fortunes #1-#100 with Enhanced Tagging
# 專門處理#1-#100籤文的標籤化腳本

$InputFile = "app\src\main\assets\fortunes_source_v21.csv"
$OutputFile = "app\src\main\assets\fortunes_source_v21_tagged_1_to_100.csv"

Write-Host "Processing Fortunes #1-#100 with Enhanced Tagging"
Write-Host "Input: $InputFile"
Write-Host "Output: $OutputFile"

if (-not (Test-Path $InputFile)) {
    Write-Error "Input file not found: $InputFile"
    exit 1
}

# Read the CSV file
$content = Get-Content $InputFile -Encoding UTF8
$header = $content[0]
$data = $content[1..($content.Length-1)]

Write-Host "Total records: $($data.Length)"

# Process only fortunes #1-#100
$processedData = @()
$processedCount = 0

foreach ($row in $data) {
    # Parse CSV row
    $fields = $row -split '","'
    if ($fields.Length -ge 4) {
        $id = $fields[0].Trim('"')
        $idNum = [int]$id
        
        # Only process fortunes #1-#100
        if ($idNum -ge 1 -and $idNum -le 100) {
            $title = $fields[1].Trim('"')
            $summary = $fields[2].Trim('"')
            $content = $fields[3].Trim('"')
            
            Write-Host "Processing fortune #$id..."
            
            # Add enhanced tags to content
            $taggedContent = Add-EnhancedTags -content $content
            
            # Add enhanced tags to summary
            $taggedSummary = Add-EnhancedTags -content $summary
            
            # Create intent mapping for this fortune
            $intentMapping = CreateIntentMapping -id $id
            
            # Create time range (default T2 for most fortunes)
            $timeRange = "T2"
            
            # Create semantic anchors
            $semanticAnchors = CreateSemanticAnchors
            
            # Reassemble CSV row with new columns
            $newRow = """$id"",""$title"",""$taggedSummary"",""$taggedContent"",""$intentMapping"",""$timeRange"",""$semanticAnchors"""
            $processedData += $newRow
            $processedCount++
        } else {
            # For fortunes > 100, keep original format
            $processedData += $row
        }
    } else {
        $processedData += $row
    }
}

# Write output file
$outputContent = @($header) + $processedData
$outputContent | Out-File -FilePath $OutputFile -Encoding UTF8

Write-Host "Processing completed!"
Write-Host "Processed $processedCount fortune records (#1-#100)"
Write-Host "Output file: $OutputFile"

# Function to add enhanced tags
function Add-EnhancedTags {
    param([string]$content)
    
    # Tag mapping for enhanced processing
    $tagMappings = @{
        '求籤吉凶' = '[A1]求籤吉凶'
        '黃大仙算命解籤詩' = '[A2]黃大仙算命解籤詩'
        '流年' = '[A3]流年'
        '事業' = '[B]事業'
        '財富' = '[C]財富'
        '自身' = '[D]自身'
        '家庭' = '[E]家庭'
        '姻緣' = '[F]姻緣'
        '移居' = '[G]移居'
        '名譽' = '[H]名譽'
        '健康' = '[I]健康'
        '友誼' = '[J]友誼'
        '風水' = '[K]風水'
        '遺失' = '[L]遺失'
        '天時' = '[N]天時'
        '交易' = '[O]交易'
        '出行' = '[P]出行'
    }
    
    $taggedContent = $content
    
    # Apply tag mappings
    foreach ($key in $tagMappings.Keys) {
        $taggedContent = $taggedContent -replace $key, $tagMappings[$key]
    }
    
    return $taggedContent
}

# Function to create intent mapping
function CreateIntentMapping {
    param([string]$id)
    
    # Standard intent mapping for all fortunes
    $intentMapping = @{
        "general_fortune" = @("A1", "A2", "A3", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P")
        "career" = @("A1", "B")
        "wealth" = @("A1", "C")
        "love" = @("A1", "F")
        "marriage" = @("A1", "F")
        "health" = @("A1", "I")
        "family" = @("A1", "E")
        "self" = @("A1", "D", "M")
        "reputation" = @("A1", "H")
        "travel" = @("A1", "P")
    }
    
    return ($intentMapping | ConvertTo-Json -Compress)
}

# Function to create semantic anchors
function CreateSemanticAnchors {
    $semanticAnchors = @{
        "A1" = "fortune_level"
        "A2" = "poem_explanation"
        "A3" = "yearly_fortune"
        "B" = "career"
        "C" = "wealth"
        "D" = "personal"
        "E" = "family"
        "F" = "love"
        "G" = "migration"
        "H" = "reputation"
        "I" = "health"
        "J" = "friendship"
        "K" = "feng_shui"
        "L" = "lost_items"
        "M" = "personal"
        "N" = "weather"
        "O" = "business"
        "P" = "travel"
    }
    
    return ($semanticAnchors | ConvertTo-Json -Compress)
}

# CSV Fortune Files Validator
Write-Host "=== CSV Fortune Files Validator ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$assetsPath = "app/src/main/assets"
$errors = @()
$warnings = @()

Write-Host "`nChecking CSV fortune files..." -ForegroundColor Green

# 1. Check file existence
Write-Host "`n1. File Existence Check" -ForegroundColor Yellow
$csvFiles = @(
    "fortunes_source.csv",
    "fortunes_cn.csv", 
    "fortunes_en2.csv",
    "fortunes.json"
)

foreach ($file in $csvFiles) {
    $filePath = Join-Path $assetsPath $file
    if (Test-Path $filePath) {
        $fileSize = (Get-Item $filePath).Length / 1KB
        Write-Host "   PASS: $file exists ($([math]::Round($fileSize, 1)) KB)" -ForegroundColor Green
    } else {
        $errors += "Missing file: $file"
        Write-Host "   FAIL: $file missing" -ForegroundColor Red
    }
}

# 2. Check main CSV structure
Write-Host "`n2. CSV Structure Check" -ForegroundColor Yellow
$mainCsv = Join-Path $assetsPath "fortunes_source.csv"

if (Test-Path $mainCsv) {
    try {
        $content = Get-Content $mainCsv -Encoding UTF8
        $headerLine = $content[0]
        
        # Check header
        if ($headerLine -match '"id","title","summary","content"') {
            Write-Host "   PASS: CSV header correct" -ForegroundColor Green
        } else {
            $errors += "CSV header incorrect: $headerLine"
            Write-Host "   FAIL: CSV header wrong" -ForegroundColor Red
        }
        
        # Count records
        $recordCount = $content.Count - 1  # Subtract header
        Write-Host "   INFO: $recordCount fortune records found" -ForegroundColor Gray
        
        # Check for empty lines
        $emptyLines = 0
        for ($i = 1; $i -lt $content.Count; $i++) {
            if ([string]::IsNullOrWhiteSpace($content[$i])) {
                $emptyLines++
            }
        }
        
        if ($emptyLines -eq 0) {
            Write-Host "   PASS: No empty lines" -ForegroundColor Green
        } else {
            $warnings += "Found $emptyLines empty lines"
            Write-Host "   WARN: $emptyLines empty lines found" -ForegroundColor Yellow
        }
        
    } catch {
        $errors += "Failed to read CSV: $($_.Exception.Message)"
        Write-Host "   FAIL: Cannot read CSV" -ForegroundColor Red
    }
} else {
    $errors += "Main CSV file not found"
    Write-Host "   FAIL: Main CSV not found" -ForegroundColor Red
}

# 3. Check encoding
Write-Host "`n3. Encoding Check" -ForegroundColor Yellow
if (Test-Path $mainCsv) {
    try {
        # Read with different encodings to test
        $utf8Content = Get-Content $mainCsv -Encoding UTF8 -TotalCount 5
        $defaultContent = Get-Content $mainCsv -TotalCount 5
        
        # Check for Chinese characters
        $hasChinese = $utf8Content -match '[\u4e00-\u9fff]'
        
        if ($hasChinese) {
            Write-Host "   PASS: Chinese characters detected, UTF-8 encoding OK" -ForegroundColor Green
        } else {
            $warnings += "No Chinese characters detected"
            Write-Host "   WARN: No Chinese characters found" -ForegroundColor Yellow
        }
        
    } catch {
        $errors += "Encoding check failed"
        Write-Host "   FAIL: Encoding check failed" -ForegroundColor Red
    }
}

# 4. Sample data validation
Write-Host "`n4. Sample Data Validation" -ForegroundColor Yellow
if (Test-Path $mainCsv) {
    try {
        $sampleLines = Get-Content $mainCsv -Encoding UTF8 -TotalCount 10
        
        # Check first fortune record
        if ($sampleLines.Count -gt 1) {
            $firstRecord = $sampleLines[1]
            
            # Should start with "1"
            if ($firstRecord -match '^"1"') {
                Write-Host "   PASS: First record ID is 1" -ForegroundColor Green
            } else {
                $warnings += "First record ID might not be 1"
                Write-Host "   WARN: First record ID unusual" -ForegroundColor Yellow
            }
            
            # Should have 4 fields
            $fieldCount = ($firstRecord -split '","').Count
            if ($fieldCount -eq 4) {
                Write-Host "   PASS: Record has 4 fields" -ForegroundColor Green
            } else {
                $errors += "Record field count wrong: $fieldCount"
                Write-Host "   FAIL: Wrong field count: $fieldCount" -ForegroundColor Red
            }
        }
        
    } catch {
        $errors += "Sample data validation failed"
        Write-Host "   FAIL: Sample validation failed" -ForegroundColor Red
    }
}

# 5. Check for duplicate IDs
Write-Host "`n5. Duplicate ID Check" -ForegroundColor Yellow
if (Test-Path $mainCsv) {
    try {
        $content = Get-Content $mainCsv -Encoding UTF8
        $ids = @()
        
        for ($i = 1; $i -lt $content.Count; $i++) {
            if ($content[$i] -match '^"(\d+)"') {
                $ids += $matches[1]
            }
        }
        
        $uniqueIds = $ids | Sort-Object -Unique
        $duplicateCount = $ids.Count - $uniqueIds.Count
        
        if ($duplicateCount -eq 0) {
            Write-Host "   PASS: No duplicate IDs ($($ids.Count) unique)" -ForegroundColor Green
        } else {
            $errors += "Found $duplicateCount duplicate IDs"
            Write-Host "   FAIL: $duplicateCount duplicate IDs" -ForegroundColor Red
        }
        
    } catch {
        $warnings += "Duplicate check failed"
        Write-Host "   WARN: Duplicate check failed" -ForegroundColor Yellow
    }
}

# 6. Check backup file
Write-Host "`n6. Backup File Check" -ForegroundColor Yellow
$backupCsv = Join-Path $assetsPath "fortunes_source.backup.csv"
if (Test-Path $backupCsv) {
    $mainSize = (Get-Item $mainCsv).Length
    $backupSize = (Get-Item $backupCsv).Length
    $sizeDiff = [math]::Abs($mainSize - $backupSize)
    
    if ($sizeDiff -lt 1000) {  # Less than 1KB difference
        Write-Host "   PASS: Backup file size similar" -ForegroundColor Green
    } else {
        $warnings += "Backup file size very different"
        Write-Host "   WARN: Backup size differs significantly" -ForegroundColor Yellow
    }
} else {
    $warnings += "No backup file found"
    Write-Host "   WARN: No backup file" -ForegroundColor Yellow
}

# Results
Write-Host "`n=== VALIDATION RESULTS ===" -ForegroundColor Cyan

if ($errors.Count -eq 0) {
    Write-Host "SUCCESS: CSV files validation passed!" -ForegroundColor Green
    Write-Host "Your fortune content is ready for production!" -ForegroundColor Green
} else {
    Write-Host "ERRORS FOUND: $($errors.Count)" -ForegroundColor Red
    foreach ($error in $errors) {
        Write-Host "  - $error" -ForegroundColor Red
    }
}

if ($warnings.Count -gt 0) {
    Write-Host "`nWARNINGS: $($warnings.Count)" -ForegroundColor Yellow
    foreach ($warning in $warnings) {
        Write-Host "  - $warning" -ForegroundColor Yellow
    }
}

Write-Host "`nRecommendations:" -ForegroundColor Cyan
Write-Host "1. Keep backup files updated" -ForegroundColor White
Write-Host "2. Test CSV loading in both Debug and Release" -ForegroundColor White
Write-Host "3. Verify all fortunes display correctly" -ForegroundColor White
Write-Host "4. Check internationalization versions" -ForegroundColor White

# Generate report
$reportPath = "csv_validation_$(Get-Date -Format 'MMdd_HHmm').txt"
$report = @"
CSV Fortune Files Validation Report
Generated: $(Get-Date)

Files Checked:
- fortunes_source.csv
- fortunes_cn.csv  
- fortunes_en2.csv
- fortunes.json

Errors: $($errors.Count)
$(if ($errors.Count -gt 0) { $errors | ForEach-Object { "- $_" } | Out-String })

Warnings: $($warnings.Count)
$(if ($warnings.Count -gt 0) { $warnings | ForEach-Object { "- $_" } | Out-String })

Status: $(if ($errors.Count -eq 0) { "READY FOR PRODUCTION" } else { "NEEDS FIXES" })
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "`nReport saved: $reportPath" -ForegroundColor Gray

exit $(if ($errors.Count -eq 0) { 0 } else { 1 })

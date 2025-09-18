# PowerShell Encoding Issue Analysis
# Why encoding problems keep recurring in scripts

Write-Host "=== PowerShell Encoding Issue Analysis ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$scriptFiles = Get-ChildItem "scripts" -Name "*.ps1"
$encodingIssues = @()
$chineseCharFiles = @()
$problematicPatterns = @()

Write-Host "`nAnalyzing $($scriptFiles.Count) PowerShell scripts..." -ForegroundColor Green

# 1. Identify files with Chinese characters
Write-Host "`n1. Chinese Character Detection" -ForegroundColor Yellow

foreach ($script in $scriptFiles) {
    $scriptPath = "scripts/$script"
    if (Test-Path $scriptPath) {
        try {
            $content = Get-Content $scriptPath -Raw -Encoding UTF8
            
            # Check for Chinese characters
            if ($content -match '[\u4e00-\u9fff]') {
                $chineseCharFiles += $script
                Write-Host "   FOUND: $script contains Chinese characters" -ForegroundColor Yellow
                
                # Count Chinese character occurrences
                $chineseMatches = [regex]::Matches($content, '[\u4e00-\u9fff]')
                Write-Host "     Count: $($chineseMatches.Count) Chinese characters" -ForegroundColor Gray
            }
            
        } catch {
            $encodingIssues += @{
                File = $script
                Error = "Failed to read with UTF-8: $($_.Exception.Message)"
            }
            Write-Host "   ERROR: Cannot read $script" -ForegroundColor Red
        }
    }
}

Write-Host "   Summary: $($chineseCharFiles.Count) files contain Chinese characters" -ForegroundColor Cyan

# 2. Identify problematic PowerShell patterns
Write-Host "`n2. Problematic Pattern Detection" -ForegroundColor Yellow

$problemPatterns = @(
    @{ Pattern = 'Write-Host.*[\u4e00-\u9fff]'; Description = "Chinese in Write-Host" },
    @{ Pattern = '\$.*[\u4e00-\u9fff]'; Description = "Chinese in variable names" },
    @{ Pattern = '".*[\u4e00-\u9fff].*".*-match'; Description = "Chinese in regex matching" },
    @{ Pattern = 'foreach.*[\u4e00-\u9fff]'; Description = "Chinese in foreach loops" },
    @{ Pattern = 'if.*[\u4e00-\u9fff]'; Description = "Chinese in if conditions" }
)

foreach ($script in $chineseCharFiles) {
    $scriptPath = "scripts/$script"
    $content = Get-Content $scriptPath -Raw -Encoding UTF8
    
    foreach ($pattern in $problemPatterns) {
        if ($content -match $pattern.Pattern) {
            $problematicPatterns += @{
                File = $script
                Pattern = $pattern.Description
                Issue = "PowerShell parser may fail on Chinese characters"
            }
        }
    }
}

if ($problematicPatterns.Count -gt 0) {
    Write-Host "   CRITICAL: Found $($problematicPatterns.Count) problematic patterns" -ForegroundColor Red
    foreach ($problem in $problematicPatterns) {
        Write-Host "     $($problem.File): $($problem.Pattern)" -ForegroundColor Red
    }
} else {
    Write-Host "   GOOD: No critical patterns found" -ForegroundColor Green
}

# 3. PowerShell Console Encoding Check
Write-Host "`n3. PowerShell Console Encoding" -ForegroundColor Yellow

$consoleEncoding = [Console]::OutputEncoding.EncodingName
$psEncoding = $OutputEncoding.EncodingName

Write-Host "   Console Encoding: $consoleEncoding" -ForegroundColor Gray
Write-Host "   PowerShell Encoding: $psEncoding" -ForegroundColor Gray

if ($consoleEncoding -notmatch "UTF" -and $chineseCharFiles.Count -gt 0) {
    Write-Host "   WARNING: Console encoding may not support Chinese characters" -ForegroundColor Yellow
    $encodingIssues += @{
        File = "Console"
        Error = "Console encoding ($consoleEncoding) may not display Chinese correctly"
    }
}

# 4. File Encoding Detection
Write-Host "`n4. File Encoding Analysis" -ForegroundColor Yellow

foreach ($script in $chineseCharFiles) {
    $scriptPath = "scripts/$script"
    
    try {
        # Try different encodings
        $utf8Content = Get-Content $scriptPath -Encoding UTF8 -TotalCount 1
        $defaultContent = Get-Content $scriptPath -TotalCount 1
        $asciiContent = Get-Content $scriptPath -Encoding ASCII -TotalCount 1 2>$null
        
        if ($utf8Content -ne $defaultContent) {
            Write-Host "   ISSUE: $script - UTF8 vs Default encoding mismatch" -ForegroundColor Red
            $encodingIssues += @{
                File = $script
                Error = "Encoding mismatch between UTF8 and default"
            }
        } else {
            Write-Host "   OK: $script encoding consistent" -ForegroundColor Green
        }
        
    } catch {
        Write-Host "   ERROR: $script encoding test failed" -ForegroundColor Red
        $encodingIssues += @{
            File = $script
            Error = "Encoding test failed: $($_.Exception.Message)"
        }
    }
}

# 5. Windows PowerShell vs PowerShell Core
Write-Host "`n5. PowerShell Version Analysis" -ForegroundColor Yellow

$psVersion = $PSVersionTable.PSVersion
$psEdition = $PSVersionTable.PSEdition

Write-Host "   Version: $psVersion" -ForegroundColor Gray
Write-Host "   Edition: $psEdition" -ForegroundColor Gray

if ($psEdition -eq "Desktop" -and $chineseCharFiles.Count -gt 0) {
    Write-Host "   WARNING: Windows PowerShell (Desktop) has known Unicode issues" -ForegroundColor Yellow
    $encodingIssues += @{
        File = "PowerShell"
        Error = "Windows PowerShell Desktop edition has Unicode limitations"
    }
} elseif ($psEdition -eq "Core") {
    Write-Host "   GOOD: PowerShell Core has better Unicode support" -ForegroundColor Green
}

# 6. Root Cause Analysis
Write-Host "`n=== ROOT CAUSE ANALYSIS ===" -ForegroundColor Cyan

Write-Host "`nWHY ENCODING ISSUES KEEP RECURRING:" -ForegroundColor Red

Write-Host "`n1. FUNDAMENTAL PROBLEM:" -ForegroundColor Yellow
Write-Host "   - PowerShell scripts contain Chinese characters" -ForegroundColor White
Write-Host "   - Windows PowerShell has poor Unicode handling" -ForegroundColor White
Write-Host "   - Console encoding often defaults to legacy codepages" -ForegroundColor White

Write-Host "`n2. SPECIFIC TRIGGERS:" -ForegroundColor Yellow
Write-Host "   - Write-Host with Chinese text" -ForegroundColor White
Write-Host "   - String matching with Chinese patterns" -ForegroundColor White
Write-Host "   - Variable assignments with Chinese content" -ForegroundColor White

Write-Host "`n3. ENVIRONMENT FACTORS:" -ForegroundColor Yellow
Write-Host "   - Windows regional settings" -ForegroundColor White
Write-Host "   - Console font limitations" -ForegroundColor White
Write-Host "   - File system encoding differences" -ForegroundColor White

# 7. Solutions
Write-Host "`n=== PERMANENT SOLUTIONS ===" -ForegroundColor Green

Write-Host "`n1. IMMEDIATE FIX - English-Only Scripts:" -ForegroundColor Cyan
Write-Host "   Replace all Chinese text with English equivalents" -ForegroundColor White
Write-Host "   Use Unicode escape sequences for essential Chinese text" -ForegroundColor White

Write-Host "`n2. ENCODING STANDARDIZATION:" -ForegroundColor Cyan
Write-Host "   Set consistent UTF-8 encoding for all scripts" -ForegroundColor White
Write-Host "   Add BOM (Byte Order Mark) to PowerShell files" -ForegroundColor White

Write-Host "`n3. RUNTIME CONFIGURATION:" -ForegroundColor Cyan
Write-Host "   Set console encoding at script start" -ForegroundColor White
Write-Host "   Use -Encoding UTF8 for all file operations" -ForegroundColor White

# 8. Generate English-only template
Write-Host "`n8. Creating English-Only Script Template" -ForegroundColor Yellow

$templateContent = @'
# English-Only PowerShell Script Template
# Avoids all encoding issues by using only ASCII characters

Write-Host "=== Build Verification Script ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$errors = @()
$warnings = @()

Write-Host "`nChecking build configuration..." -ForegroundColor Green

# Example checks (English only)
Write-Host "`n1. File Existence Check" -ForegroundColor Yellow

$requiredFiles = @(
    "app/build.gradle.kts",
    "app/proguard-rules.pro",
    "app/src/main/AndroidManifest.xml"
)

foreach ($file in $requiredFiles) {
    if (Test-Path $file) {
        Write-Host "   PASS: $file exists" -ForegroundColor Green
    } else {
        Write-Host "   FAIL: $file missing" -ForegroundColor Red
        $errors += "Missing file: $file"
    }
}

# Results (English only)
Write-Host "`n=== RESULTS ===" -ForegroundColor Cyan

if ($errors.Count -eq 0) {
    Write-Host "SUCCESS: All checks passed" -ForegroundColor Green
    exit 0
} else {
    Write-Host "ERRORS FOUND: $($errors.Count)" -ForegroundColor Red
    foreach ($error in $errors) {
        Write-Host "  - $error" -ForegroundColor Red
    }
    exit 1
}
'@

$templatePath = "scripts/english_only_template.ps1"
$templateContent | Out-File -FilePath $templatePath -Encoding UTF8
Write-Host "   Template created: $templatePath" -ForegroundColor Green

# Generate summary report
Write-Host "`n=== SUMMARY ===" -ForegroundColor Cyan

Write-Host "`nFILES WITH ENCODING ISSUES: $($encodingIssues.Count)" -ForegroundColor $(if ($encodingIssues.Count -gt 0) { "Red" } else { "Green" })
Write-Host "FILES WITH CHINESE CHARS: $($chineseCharFiles.Count)" -ForegroundColor $(if ($chineseCharFiles.Count -gt 0) { "Yellow" } else { "Green" })
Write-Host "PROBLEMATIC PATTERNS: $($problematicPatterns.Count)" -ForegroundColor $(if ($problematicPatterns.Count -gt 0) { "Red" } else { "Green" })

Write-Host "`nRECOMMENDATIONS:" -ForegroundColor Green
Write-Host "1. PRIORITY: Replace Chinese scripts with English versions" -ForegroundColor White
Write-Host "2. Use the provided english_only_template.ps1 as base" -ForegroundColor White
Write-Host "3. Set UTF-8 encoding for all new scripts" -ForegroundColor White
Write-Host "4. Test scripts in both PowerShell editions" -ForegroundColor White
Write-Host "5. Consider using PowerShell Core instead of Windows PowerShell" -ForegroundColor White

# Save detailed report
$reportPath = "encoding_analysis_$(Get-Date -Format 'MMdd_HHmm').txt"
$report = @"
PowerShell Encoding Issue Analysis Report
Generated: $(Get-Date)

ENVIRONMENT:
- PowerShell Version: $psVersion
- Edition: $psEdition  
- Console Encoding: $consoleEncoding
- PowerShell Encoding: $psEncoding

FILES WITH CHINESE CHARACTERS ($($chineseCharFiles.Count)):
$(if ($chineseCharFiles.Count -gt 0) { $chineseCharFiles | ForEach-Object { "- $_" } | Out-String } else { "None" })

ENCODING ISSUES ($($encodingIssues.Count)):
$(if ($encodingIssues.Count -gt 0) { $encodingIssues | ForEach-Object { "- $($_.File): $($_.Error)" } | Out-String } else { "None" })

PROBLEMATIC PATTERNS ($($problematicPatterns.Count)):
$(if ($problematicPatterns.Count -gt 0) { $problematicPatterns | ForEach-Object { "- $($_.File): $($_.Pattern)" } | Out-String } else { "None" })

ROOT CAUSE:
PowerShell scripts with Chinese characters cause parsing errors in Windows PowerShell due to:
1. Console encoding limitations
2. File encoding mismatches  
3. Unicode handling differences between PowerShell editions

SOLUTION:
Replace all Chinese text in PowerShell scripts with English equivalents.
Use the generated english_only_template.ps1 as a starting point.

STATUS: $(if ($encodingIssues.Count -eq 0 -and $chineseCharFiles.Count -eq 0) { "RESOLVED" } else { "NEEDS ACTION" })
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "`nDetailed report saved: $reportPath" -ForegroundColor Gray

# Exit with appropriate code
exit $(if ($encodingIssues.Count -gt 0) { 1 } else { 0 })

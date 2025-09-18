# Cup Throwing and User Data Features Check
Write-Host "=== Cup Throwing & User Data Features Check ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$errors = @()
$warnings = @()
$missing = @()

Write-Host "`nChecking cup throwing and user data features..." -ForegroundColor Green

# 1. Check CupScreen implementation
Write-Host "`n1. Cup Throwing Feature Check" -ForegroundColor Yellow
$screensFile = "app/src/main/java/com/wts/dsfortune/ui/screens/Screens.kt"

if (Test-Path $screensFile) {
    $content = Get-Content $screensFile -Raw
    
    # Check for CupScreen function
    if ($content -match 'fun CupScreen') {
        Write-Host "   PASS: CupScreen function found" -ForegroundColor Green
        
        # Check for cup throwing logic
        if ($content -match '擲筊|擲杯|cup') {
            Write-Host "   PASS: Cup throwing UI elements found" -ForegroundColor Green
        } else {
            $missing += "Cup throwing UI elements incomplete"
            Write-Host "   MISSING: Cup throwing UI elements" -ForegroundColor Red
        }
        
        # Check for random logic
        if ($content -match 'random|Random') {
            Write-Host "   PASS: Random logic found" -ForegroundColor Green
        } else {
            $missing += "Random cup throwing logic missing"
            Write-Host "   MISSING: Random logic" -ForegroundColor Red
        }
        
    } else {
        $missing += "CupScreen function not found"
        Write-Host "   MISSING: CupScreen function" -ForegroundColor Red
    }
    
    # Check for CupResultScreen
    if ($content -match 'fun CupResultScreen') {
        Write-Host "   PASS: CupResultScreen function found" -ForegroundColor Green
    } else {
        $warnings += "CupResultScreen function not found"
        Write-Host "   WARN: CupResultScreen function not found" -ForegroundColor Yellow
    }
    
} else {
    $errors += "Screens.kt not found"
    Write-Host "   ERROR: Screens.kt not found" -ForegroundColor Red
}

# 2. Check cup images/resources
Write-Host "`n2. Cup Resources Check" -ForegroundColor Yellow
$drawableDir = "app/src/main/res/drawable"
if (Test-Path $drawableDir) {
    $cupImages = Get-ChildItem $drawableDir -Name | Where-Object { $_ -match 'wts0[3-5]|cup' }
    
    if ($cupImages.Count -ge 3) {
        Write-Host "   PASS: Found $($cupImages.Count) cup images" -ForegroundColor Green
        foreach ($img in $cupImages) {
            Write-Host "     - $img" -ForegroundColor Gray
        }
    } else {
        $missing += "Insufficient cup images (need 3+)"
        Write-Host "   MISSING: Need at least 3 cup images" -ForegroundColor Red
    }
} else {
    $errors += "Drawable directory not found"
    Write-Host "   ERROR: Drawable directory not found" -ForegroundColor Red
}

# 3. Check API integration for cup throwing
Write-Host "`n3. Cup API Integration Check" -ForegroundColor Yellow
$apiFile = "app/src/main/java/com/wts/dsfortune/data/api/DeepSeekApi.kt"
if (Test-Path $apiFile) {
    $apiContent = Get-Content $apiFile -Raw
    
    if ($apiContent -match 'getCupResult|cup') {
        Write-Host "   PASS: Cup API endpoint found" -ForegroundColor Green
    } else {
        $warnings += "Cup API endpoint not found"
        Write-Host "   WARN: Cup API endpoint not found" -ForegroundColor Yellow
    }
    
    if ($apiContent -match 'CupResponse') {
        Write-Host "   PASS: CupResponse data class found" -ForegroundColor Green
    } else {
        $warnings += "CupResponse data class not found"
        Write-Host "   WARN: CupResponse data class not found" -ForegroundColor Yellow
    }
} else {
    $warnings += "DeepSeekApi.kt not found"
    Write-Host "   WARN: DeepSeekApi.kt not found" -ForegroundColor Yellow
}

# 4. Check user data handling
Write-Host "`n4. User Data Handling Check" -ForegroundColor Yellow

# Check for user input components
if (Test-Path $screensFile) {
    $content = Get-Content $screensFile -Raw
    
    # Check for input fields
    $hasTextField = $content -match 'TextField|OutlinedTextField|TextInput'
    $hasUserInput = $content -match '個人資料|personal|user.*input|birth.*date|姓名|出生'
    
    if ($hasTextField) {
        Write-Host "   PASS: Text input components found" -ForegroundColor Green
    } else {
        $missing += "Text input components missing"
        Write-Host "   MISSING: Text input components" -ForegroundColor Red
    }
    
    if ($hasUserInput) {
        Write-Host "   PASS: User data input fields found" -ForegroundColor Green
    } else {
        $missing += "User personal data input missing"
        Write-Host "   MISSING: User personal data input" -ForegroundColor Red
    }
}

# 5. Check data storage/preferences
Write-Host "`n5. Data Storage Check" -ForegroundColor Yellow
$hasDataStorage = $false

# Check for SharedPreferences or DataStore usage
$javaFiles = Get-ChildItem "app/src/main/java" -Recurse -Name "*.kt"
foreach ($file in $javaFiles) {
    $filePath = "app/src/main/java/$file"
    if (Test-Path $filePath) {
        $fileContent = Get-Content $filePath -Raw
        if ($fileContent -match 'SharedPreferences|DataStore|getSharedPreferences') {
            $hasDataStorage = $true
            break
        }
    }
}

if ($hasDataStorage) {
    Write-Host "   PASS: Data storage mechanism found" -ForegroundColor Green
} else {
    $warnings += "No data storage mechanism found"
    Write-Host "   WARN: No data storage mechanism found" -ForegroundColor Yellow
}

# 6. Check privacy policy compliance
Write-Host "`n6. Privacy Policy Check" -ForegroundColor Yellow
$privacyFile = "docs/PRIVACY_POLICY.md"
if (Test-Path $privacyFile) {
    $privacyContent = Get-Content $privacyFile -Raw
    
    if ($privacyContent -match '个人数据|個人資料|personal.*data') {
        Write-Host "   PASS: Privacy policy addresses personal data" -ForegroundColor Green
    } else {
        $warnings += "Privacy policy doesn't address personal data"
        Write-Host "   WARN: Privacy policy incomplete" -ForegroundColor Yellow
    }
    
    if ($privacyContent -match '本地处理|本地處理|local.*processing') {
        Write-Host "   PASS: Local processing mentioned in privacy policy" -ForegroundColor Green
    } else {
        $warnings += "Local processing not mentioned in privacy policy"
        Write-Host "   WARN: Local processing not documented" -ForegroundColor Yellow
    }
} else {
    $warnings += "Privacy policy not found"
    Write-Host "   WARN: Privacy policy not found" -ForegroundColor Yellow
}

# 7. Check navigation routes
Write-Host "`n7. Navigation Routes Check" -ForegroundColor Yellow
$routesFiles = Get-ChildItem "app/src/main/java" -Recurse -Name "*Routes*"
$hasRoutes = $false

foreach ($file in $routesFiles) {
    $filePath = "app/src/main/java/$file"
    if (Test-Path $filePath) {
        $routeContent = Get-Content $filePath -Raw
        if ($routeContent -match 'cup|CUP') {
            Write-Host "   PASS: Cup routes found in $file" -ForegroundColor Green
            $hasRoutes = $true
        }
    }
}

if (-not $hasRoutes) {
    # Check in main files
    if (Test-Path $screensFile) {
        $content = Get-Content $screensFile -Raw
        if ($content -match 'Routes.*cup|navigate.*cup') {
            Write-Host "   PASS: Cup navigation found" -ForegroundColor Green
        } else {
            $warnings += "Cup navigation routes not found"
            Write-Host "   WARN: Cup navigation routes not found" -ForegroundColor Yellow
        }
    }
}

# Results Summary
Write-Host "`n=== FEATURE CHECK RESULTS ===" -ForegroundColor Cyan

$totalIssues = $errors.Count + $missing.Count
$hasWarnings = $warnings.Count -gt 0

if ($totalIssues -eq 0) {
    Write-Host "STATUS: Cup throwing and user data features READY" -ForegroundColor Green
    Write-Host "All critical features are present" -ForegroundColor Green
} else {
    Write-Host "STATUS: Features INCOMPLETE" -ForegroundColor Red
    Write-Host "Found $totalIssues critical issues" -ForegroundColor Red
}

if ($errors.Count -gt 0) {
    Write-Host "`nERRORS ($($errors.Count)):" -ForegroundColor Red
    foreach ($error in $errors) {
        Write-Host "  - $error" -ForegroundColor Red
    }
}

if ($missing.Count -gt 0) {
    Write-Host "`nMISSING FEATURES ($($missing.Count)):" -ForegroundColor Red
    foreach ($miss in $missing) {
        Write-Host "  - $miss" -ForegroundColor Red
    }
}

if ($hasWarnings) {
    Write-Host "`nWARNINGS ($($warnings.Count)):" -ForegroundColor Yellow
    foreach ($warning in $warnings) {
        Write-Host "  - $warning" -ForegroundColor Yellow
    }
}

Write-Host "`nFEATURE STATUS SUMMARY:" -ForegroundColor Cyan
Write-Host "Cup Throwing: $(if ($missing -notcontains 'CupScreen function not found') { 'IMPLEMENTED' } else { 'MISSING' })" -ForegroundColor $(if ($missing -notcontains 'CupScreen function not found') { 'Green' } else { 'Red' })
Write-Host "Random Logic: $(if ($missing -notcontains 'Random cup throwing logic missing') { 'IMPLEMENTED' } else { 'MISSING' })" -ForegroundColor $(if ($missing -notcontains 'Random cup throwing logic missing') { 'Green' } else { 'Red' })
Write-Host "User Input: $(if ($missing -notcontains 'Text input components missing') { 'IMPLEMENTED' } else { 'MISSING' })" -ForegroundColor $(if ($missing -notcontains 'Text input components missing') { 'Green' } else { 'Red' })
Write-Host "Personal Data: $(if ($missing -notcontains 'User personal data input missing') { 'IMPLEMENTED' } else { 'MISSING' })" -ForegroundColor $(if ($missing -notcontains 'User personal data input missing') { 'Green' } else { 'Red' })

Write-Host "`nRECOMMENDATIONS:" -ForegroundColor Cyan
Write-Host "1. Enhance cup throwing UI with animations" -ForegroundColor White
Write-Host "2. Add user profile/settings screen" -ForegroundColor White
Write-Host "3. Implement data validation for user inputs" -ForegroundColor White
Write-Host "4. Add privacy controls for data management" -ForegroundColor White
Write-Host "5. Test both features in Debug and Release builds" -ForegroundColor White

# Generate report
$reportPath = "cup_userdata_check_$(Get-Date -Format 'MMdd_HHmm').txt"
$report = @"
Cup Throwing & User Data Features Check Report
Generated: $(Get-Date)

SUMMARY:
- Errors: $($errors.Count)
- Missing Features: $($missing.Count)
- Warnings: $($warnings.Count)

STATUS: $(if ($totalIssues -eq 0) { "READY" } else { "INCOMPLETE" })

ERRORS:
$(if ($errors.Count -gt 0) { $errors | ForEach-Object { "- $_" } | Out-String } else { "None" })

MISSING:
$(if ($missing.Count -gt 0) { $missing | ForEach-Object { "- $_" } | Out-String } else { "None" })

WARNINGS:
$(if ($warnings.Count -gt 0) { $warnings | ForEach-Object { "- $_" } | Out-String } else { "None" })

FEATURE STATUS:
- Cup Throwing: $(if ($missing -notcontains 'CupScreen function not found') { 'IMPLEMENTED' } else { 'MISSING' })
- Random Logic: $(if ($missing -notcontains 'Random cup throwing logic missing') { 'IMPLEMENTED' } else { 'MISSING' })
- User Input: $(if ($missing -notcontains 'Text input components missing') { 'IMPLEMENTED' } else { 'MISSING' })
- Personal Data: $(if ($missing -notcontains 'User personal data input missing') { 'IMPLEMENTED' } else { 'MISSING' })

NEXT STEPS:
1. Fix all missing critical features
2. Address warnings for optimal functionality
3. Test features in both Debug and Release builds
4. Verify privacy compliance
5. Enhance user experience with animations and validations
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "`nDetailed report saved: $reportPath" -ForegroundColor Gray

exit $(if ($totalIssues -eq 0) { 0 } else { 1 })

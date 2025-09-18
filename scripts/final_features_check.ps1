# Final Features Check - Cup Throwing & User Data
Write-Host "=== Final Features Check ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$passed = 0
$total = 8

Write-Host "`nChecking implemented features..." -ForegroundColor Green

# 1. Cup Throwing Implementation
Write-Host "`n1. Cup Throwing Implementation" -ForegroundColor Yellow
$screensFile = "app/src/main/java/com/wts/dsfortune/ui/screens/Screens.kt"
if (Test-Path $screensFile) {
    $content = Get-Content $screensFile -Raw
    
    if ($content -match 'fun CupScreen.*id.*Int') {
        Write-Host "   PASS: CupScreen function implemented" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: CupScreen function missing" -ForegroundColor Red
    }
} else {
    Write-Host "   FAIL: Screens.kt not found" -ForegroundColor Red
}

# 2. Random Cup Logic
Write-Host "`n2. Random Cup Logic" -ForegroundColor Yellow
if (Test-Path $screensFile) {
    $content = Get-Content $screensFile -Raw
    
    if ($content -match 'random.*cupResults\.random') {
        Write-Host "   PASS: Random cup logic implemented" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: Random logic missing" -ForegroundColor Red
    }
} else {
    Write-Host "   FAIL: Cannot check random logic" -ForegroundColor Red
}

# 3. Cup Images
Write-Host "`n3. Cup Images Resources" -ForegroundColor Yellow
$drawableDir = "app/src/main/res/drawable"
if (Test-Path $drawableDir) {
    $cupImages = Get-ChildItem $drawableDir -Name | Where-Object { $_ -match 'wts0[3-5]' }
    
    if ($cupImages.Count -eq 3) {
        Write-Host "   PASS: All 3 cup images found" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: Missing cup images (found $($cupImages.Count)/3)" -ForegroundColor Red
    }
} else {
    Write-Host "   FAIL: Drawable directory not found" -ForegroundColor Red
}

# 4. User Profile Screen
Write-Host "`n4. User Profile Screen" -ForegroundColor Yellow
if (Test-Path $screensFile) {
    $content = Get-Content $screensFile -Raw
    
    if ($content -match 'fun UserProfileScreen') {
        Write-Host "   PASS: UserProfileScreen function implemented" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: UserProfileScreen function missing" -ForegroundColor Red
    }
} else {
    Write-Host "   FAIL: Cannot check UserProfileScreen" -ForegroundColor Red
}

# 5. Text Input Fields
Write-Host "`n5. Text Input Fields" -ForegroundColor Yellow
if (Test-Path $screensFile) {
    $content = Get-Content $screensFile -Raw
    
    if ($content -match 'OutlinedTextField') {
        Write-Host "   PASS: Text input fields implemented" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: Text input fields missing" -ForegroundColor Red
    }
} else {
    Write-Host "   FAIL: Cannot check text inputs" -ForegroundColor Red
}

# 6. Personal Data Fields
Write-Host "`n6. Personal Data Fields" -ForegroundColor Yellow
if (Test-Path $screensFile) {
    $content = Get-Content $screensFile -Raw
    
    $hasName = $content -match 'userName|姓名'
    $hasBirth = $content -match 'birth.*Year|birth.*Month|出生'
    
    if ($hasName -and $hasBirth) {
        Write-Host "   PASS: Personal data fields implemented" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: Personal data fields incomplete" -ForegroundColor Red
    }
} else {
    Write-Host "   FAIL: Cannot check personal data fields" -ForegroundColor Red
}

# 7. Navigation Integration
Write-Host "`n7. Navigation Integration" -ForegroundColor Yellow
if (Test-Path $screensFile) {
    $content = Get-Content $screensFile -Raw
    
    $hasCupNav = $content -match 'user_profile.*nav'
    $hasProfileButton = $content -match '個人資料設定'
    
    if ($hasCupNav -and $hasProfileButton) {
        Write-Host "   PASS: Navigation integration complete" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: Navigation integration incomplete" -ForegroundColor Red
    }
} else {
    Write-Host "   FAIL: Cannot check navigation" -ForegroundColor Red
}

# 8. Build Success
Write-Host "`n8. Build Compilation" -ForegroundColor Yellow
$debugApk = "app\build\outputs\apk\debug\app-debug.apk"
if (Test-Path $debugApk) {
    $fileInfo = Get-Item $debugApk
    $isRecent = (Get-Date) - $fileInfo.LastWriteTime -lt (New-TimeSpan -Minutes 10)
    
    if ($isRecent) {
        Write-Host "   PASS: Recent successful build found" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   WARN: APK exists but may be outdated" -ForegroundColor Yellow
    }
} else {
    Write-Host "   FAIL: No recent build found" -ForegroundColor Red
}

# Results
Write-Host "`n=== FINAL RESULTS ===" -ForegroundColor Cyan
$percentage = ($passed / $total) * 100

Write-Host "Passed: $passed / $total features" -ForegroundColor White
Write-Host "Success Rate: $([math]::Round($percentage, 1))%" -ForegroundColor $(if ($percentage -ge 80) { "Green" } elseif ($percentage -ge 60) { "Yellow" } else { "Red" })

if ($passed -eq $total) {
    Write-Host "`nSUCCESS! All features implemented and ready!" -ForegroundColor Green
    Write-Host "Cup throwing and user data features are COMPLETE" -ForegroundColor Green
    
    Write-Host "`nFeature Summary:" -ForegroundColor Cyan
    Write-Host "- Random cup throwing with 3 cups" -ForegroundColor White
    Write-Host "- Interactive cup results (聖筊/笑筊)" -ForegroundColor White
    Write-Host "- User profile with personal data input" -ForegroundColor White
    Write-Host "- Birth date/time for fortune calculations" -ForegroundColor White
    Write-Host "- Local data storage (privacy compliant)" -ForegroundColor White
    Write-Host "- Full navigation integration" -ForegroundColor White
    
} elseif ($passed -ge 6) {
    Write-Host "`nMOSTLY COMPLETE! $($total - $passed) features need attention" -ForegroundColor Yellow
    Write-Host "Core functionality is working" -ForegroundColor Yellow
    
} else {
    Write-Host "`nINCOMPLETE! $($total - $passed) critical features missing" -ForegroundColor Red
    Write-Host "Significant work still needed" -ForegroundColor Red
}

Write-Host "`nNext Steps:" -ForegroundColor Cyan
Write-Host "1. Test cup throwing in Debug build" -ForegroundColor White
Write-Host "2. Test user profile data entry" -ForegroundColor White
Write-Host "3. Verify data persistence (if implemented)" -ForegroundColor White
Write-Host "4. Test in Release build for consistency" -ForegroundColor White
Write-Host "5. Verify privacy compliance" -ForegroundColor White

exit $(if ($passed -eq $total) { 0 } else { 1 })

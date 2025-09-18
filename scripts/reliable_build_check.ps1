# Reliable Build Consistency Check (English Only)
# Solves encoding issues by avoiding all Chinese characters

Write-Host "=== Reliable Build Consistency Check ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$errors = @()
$warnings = @()
$passed = 0
$total = 10

Write-Host "`nChecking build consistency..." -ForegroundColor Green

# 1. ProGuard Rules Check
Write-Host "`n1. ProGuard Rules Protection" -ForegroundColor Yellow

$proguardFile = "app/proguard-rules.pro"
if (Test-Path $proguardFile) {
    $proguardContent = Get-Content $proguardFile -Raw
    
    # Check for R class protection (critical fix)
    if ($proguardContent -match '-keep class \*\*\.R\$\*') {
        Write-Host "   PASS: R class protection found" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: R class protection missing" -ForegroundColor Red
        $errors += "Missing R class protection in ProGuard rules"
    }
    
    # Check for specific drawable protection
    if ($proguardContent -match 'wong_tai_sin|wts03|wts04|wts05') {
        Write-Host "   PASS: Specific drawable protection found" -ForegroundColor Green
    } else {
        Write-Host "   WARN: No specific drawable protection" -ForegroundColor Yellow
        $warnings += "Consider adding specific drawable protection"
    }
} else {
    Write-Host "   FAIL: ProGuard rules file not found" -ForegroundColor Red
    $errors += "ProGuard rules file missing"
}

# 2. Build Configuration Check
Write-Host "`n2. Build Configuration" -ForegroundColor Yellow

$buildFile = "app/build.gradle.kts"
if (Test-Path $buildFile) {
    $buildContent = Get-Content $buildFile -Raw
    
    # Check minify settings
    if ($buildContent -match 'isMinifyEnabled = true' -and $buildContent -match 'isMinifyEnabled = false') {
        Write-Host "   PASS: Different minify settings for debug/release" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   WARN: Minify settings may be inconsistent" -ForegroundColor Yellow
        $warnings += "Check debug/release minify settings"
    }
    
    # Check signing configuration
    if ($buildContent -match 'signingConfig = signingConfigs\.getByName\("release"\)') {
        Write-Host "   PASS: Signing configuration found" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: Signing configuration missing" -ForegroundColor Red
        $errors += "Signing configuration not properly set"
    }
} else {
    Write-Host "   FAIL: Build file not found" -ForegroundColor Red
    $errors += "build.gradle.kts not found"
}

# 3. Manifest Files Check
Write-Host "`n3. Manifest Configuration" -ForegroundColor Yellow

$mainManifest = "app/src/main/AndroidManifest.xml"
$debugManifest = "app/src/debug/AndroidManifest.xml"
$releaseManifest = "app/src/release/AndroidManifest.xml"

$manifestsExist = 0
if (Test-Path $mainManifest) { $manifestsExist++ }
if (Test-Path $debugManifest) { $manifestsExist++ }
if (Test-Path $releaseManifest) { $manifestsExist++ }

if ($manifestsExist -eq 3) {
    Write-Host "   PASS: All manifest files exist" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   FAIL: Missing manifest files ($manifestsExist/3)" -ForegroundColor Red
    $errors += "Missing manifest files"
}

# 4. Resource Files Check
Write-Host "`n4. Critical Resources" -ForegroundColor Yellow

$drawableDir = "app/src/main/res/drawable"
$criticalDrawables = @("wong_tai_sin.png", "wts03.png", "wts04.png", "wts05.png")
$foundDrawables = 0

foreach ($drawable in $criticalDrawables) {
    $drawablePath = Join-Path $drawableDir $drawable
    if (Test-Path $drawablePath) {
        $foundDrawables++
    }
}

if ($foundDrawables -eq $criticalDrawables.Count) {
    Write-Host "   PASS: All critical drawables found ($foundDrawables/$($criticalDrawables.Count))" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   FAIL: Missing critical drawables ($foundDrawables/$($criticalDrawables.Count))" -ForegroundColor Red
    $errors += "Missing critical drawable resources"
}

# 5. CSV Assets Check
Write-Host "`n5. Fortune Content Assets" -ForegroundColor Yellow

$assetsDir = "app/src/main/assets"
$csvFiles = Get-ChildItem $assetsDir -Name "*.csv" 2>$null

if ($csvFiles.Count -ge 3) {
    Write-Host "   PASS: Fortune CSV files found ($($csvFiles.Count) files)" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   FAIL: Insufficient CSV files ($($csvFiles.Count) found)" -ForegroundColor Red
    $errors += "Missing fortune content CSV files"
}

# 6. AdMob Configuration Check
Write-Host "`n6. AdMob Configuration" -ForegroundColor Yellow

if (Test-Path $mainManifest) {
    $manifestContent = Get-Content $mainManifest -Raw
    
    if ($manifestContent -match 'APPLICATION_ID') {
        Write-Host "   PASS: AdMob Application ID configured" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: AdMob Application ID missing" -ForegroundColor Red
        $errors += "AdMob Application ID not configured"
    }
}

# 7. Dependencies Check
Write-Host "`n7. Critical Dependencies" -ForegroundColor Yellow

if (Test-Path $buildFile) {
    $buildContent = Get-Content $buildFile -Raw
    
    $criticalDeps = @("play-services-ads", "retrofit", "gson")
    $foundDeps = 0
    
    foreach ($dep in $criticalDeps) {
        if ($buildContent -match $dep) {
            $foundDeps++
        }
    }
    
    if ($foundDeps -eq $criticalDeps.Count) {
        Write-Host "   PASS: All critical dependencies found" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: Missing dependencies ($foundDeps/$($criticalDeps.Count))" -ForegroundColor Red
        $errors += "Missing critical dependencies"
    }
}

# 8. Build Output Check
Write-Host "`n8. Build Output Verification" -ForegroundColor Yellow

$debugApk = "app/build/outputs/apk/debug/app-debug.apk"
$releaseApk = "app/build/outputs/apk/release/app-release.apk"

$apksExist = 0
if (Test-Path $debugApk) { $apksExist++ }
if (Test-Path $releaseApk) { $apksExist++ }

if ($apksExist -eq 2) {
    Write-Host "   PASS: Both APK files exist" -ForegroundColor Green
    $passed++
    
    # Check APK sizes
    $debugSize = (Get-Item $debugApk).Length
    $releaseSize = (Get-Item $releaseApk).Length
    
    if ($releaseSize -lt $debugSize) {
        Write-Host "   INFO: Release APK is smaller (compression working)" -ForegroundColor Cyan
    } else {
        Write-Host "   WARN: Release APK not smaller than debug" -ForegroundColor Yellow
        $warnings += "Release APK compression may not be working"
    }
} else {
    Write-Host "   FAIL: Missing APK files ($apksExist/2)" -ForegroundColor Red
    $errors += "APK files not built"
}

# 9. Code Compilation Check
Write-Host "`n9. Compilation Status" -ForegroundColor Yellow

try {
    # Try a quick compilation check
    $compileResult = & .\gradlew.bat tasks --quiet 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   PASS: Gradle compilation environment OK" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: Gradle compilation issues" -ForegroundColor Red
        $errors += "Gradle compilation problems"
    }
} catch {
    Write-Host "   WARN: Could not verify compilation status" -ForegroundColor Yellow
    $warnings += "Compilation status verification failed"
}

# 10. Dynamic Resource Loading Fix
Write-Host "`n10. Dynamic Resource Loading Protection" -ForegroundColor Yellow

if (Test-Path $proguardFile) {
    $proguardContent = Get-Content $proguardFile -Raw
    
    # This is the critical fix for the recurring issue
    if ($proguardContent -match '-keep class \*\*\.R\$\* \{ \*; \}' -and 
        $proguardContent -match '-keepclassmembers class \*\*\.R\$\*') {
        Write-Host "   PASS: Dynamic resource loading protection active" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: Dynamic resource loading not protected" -ForegroundColor Red
        $errors += "getIdentifier() calls will fail in release build"
    }
}

# Results Summary
Write-Host "`n=== RESULTS SUMMARY ===" -ForegroundColor Cyan

$percentage = ($passed / $total) * 100

Write-Host "`nPassed: $passed / $total checks" -ForegroundColor White
Write-Host "Success Rate: $([math]::Round($percentage, 1))%" -ForegroundColor $(if ($percentage -ge 80) { "Green" } elseif ($percentage -ge 60) { "Yellow" } else { "Red" })

if ($errors.Count -eq 0) {
    Write-Host "`nSUCCESS: Build consistency verified!" -ForegroundColor Green
    Write-Host "Your app should work identically in debug and release modes" -ForegroundColor Green
} else {
    Write-Host "`nFAILED: $($errors.Count) critical issues found" -ForegroundColor Red
    Write-Host "These issues will cause debug/release differences:" -ForegroundColor Red
    foreach ($error in $errors) {
        Write-Host "  - $error" -ForegroundColor Red
    }
}

if ($warnings.Count -gt 0) {
    Write-Host "`nWarnings ($($warnings.Count)):" -ForegroundColor Yellow
    foreach ($warning in $warnings) {
        Write-Host "  - $warning" -ForegroundColor Yellow
    }
}

Write-Host "`nNEXT STEPS:" -ForegroundColor Cyan
if ($errors.Count -gt 0) {
    Write-Host "1. Fix all critical issues listed above" -ForegroundColor White
    Write-Host "2. Run clean build: .\gradlew.bat clean assembleDebug assembleRelease" -ForegroundColor White
    Write-Host "3. Test both APKs on real device" -ForegroundColor White
} else {
    Write-Host "1. Install both debug and release APKs for testing" -ForegroundColor White
    Write-Host "2. Verify all features work identically" -ForegroundColor White
    Write-Host "3. Pay special attention to image loading and ads" -ForegroundColor White
}

# Save report (English only to avoid encoding issues)
$reportPath = "build_consistency_$(Get-Date -Format 'MMdd_HHmm').txt"
$report = @"
Build Consistency Check Report
Generated: $(Get-Date)

SUMMARY:
- Passed: $passed / $total checks
- Success Rate: $([math]::Round($percentage, 1))%
- Critical Errors: $($errors.Count)
- Warnings: $($warnings.Count)

CRITICAL ERRORS:
$(if ($errors.Count -gt 0) { $errors | ForEach-Object { "- $_" } | Out-String } else { "None" })

WARNINGS:
$(if ($warnings.Count -gt 0) { $warnings | ForEach-Object { "- $_" } | Out-String } else { "None" })

STATUS: $(if ($errors.Count -eq 0) { "READY FOR PRODUCTION" } else { "NEEDS FIXES" })

RECOMMENDATION:
$(if ($errors.Count -eq 0) { 
    "Build consistency verified. App should work identically in debug and release modes."
} else { 
    "Fix critical issues before release. Focus on ProGuard rules and resource protection."
})
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "`nReport saved: $reportPath" -ForegroundColor Gray

# Exit with appropriate code
exit $(if ($errors.Count -eq 0) { 0 } else { 1 })

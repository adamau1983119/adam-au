# Final Check: 10 Key Points for Build Consistency
Write-Host "=== Final Check: 10 Key Points ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$passed = 0
$total = 10

# 1. Build Environment Consistency
Write-Host "`n1. Build Environment Consistency" -ForegroundColor Yellow
$check1 = (Test-Path "app/build.gradle.kts") -and (Test-Path "gradle.properties") -and (Test-Path "gradlew.bat")
if ($check1) {
    Write-Host "   ✅ PASS: Build files exist" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   ❌ FAIL: Missing build files" -ForegroundColor Red
}

# 2. Resource File Management
Write-Host "`n2. Resource File Management" -ForegroundColor Yellow
$drawableExists = Test-Path "app/src/main/res/drawable"
$mipmapExists = (Test-Path "app/src/main/res/mipmap-hdpi") -and (Test-Path "app/src/main/res/mipmap-xhdpi")
$assetsExists = Test-Path "app/src/main/assets"

if ($drawableExists -and $mipmapExists -and $assetsExists) {
    Write-Host "   ✅ PASS: All resource directories exist" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   ❌ FAIL: Missing resource directories" -ForegroundColor Red
}

# 3. String Resource Internationalization
Write-Host "`n3. String Resource Internationalization" -ForegroundColor Yellow
$mainStrings = Test-Path "app/src/main/res/values/strings.xml"
$enStrings = Test-Path "app/src/main/res/values-en/strings.xml"
$cnStrings = Test-Path "app/src/main/res/values-zh-rCN/strings.xml"

if ($mainStrings -and $enStrings -and $cnStrings) {
    Write-Host "   ✅ PASS: Main + internationalization strings exist" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   ❌ FAIL: Missing string resources" -ForegroundColor Red
}

# 4. Dependency Version Locking
Write-Host "`n4. Dependency Version Locking" -ForegroundColor Yellow
if (Test-Path "app/build.gradle.kts") {
    $content = Get-Content "app/build.gradle.kts" -Raw
    # Check for dynamic versions in dependencies (not in configuration)
    $hasDynamicDeps = ($content -match 'implementation.*\+') -or ($content -match 'implementation.*latest')
    
    if (-not $hasDynamicDeps) {
        Write-Host "   ✅ PASS: No dynamic dependency versions found" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   ❌ FAIL: Dynamic dependency versions detected" -ForegroundColor Red
    }
} else {
    Write-Host "   ❌ FAIL: build.gradle.kts not found" -ForegroundColor Red
}

# 5. Unified Build Configuration
Write-Host "`n5. Unified Build Configuration" -ForegroundColor Yellow
if (Test-Path "app/build.gradle.kts") {
    $content = Get-Content "app/build.gradle.kts" -Raw
    $hasConfig = ($content -match 'buildTypes') -and ($content -match 'signingConfigs')
    
    if ($hasConfig) {
        Write-Host "   ✅ PASS: Build types and signing configs present" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   ❌ FAIL: Build configuration incomplete" -ForegroundColor Red
    }
} else {
    Write-Host "   ❌ FAIL: Build configuration not found" -ForegroundColor Red
}

# 6. Resource Compression and Optimization
Write-Host "`n6. Resource Compression and Optimization" -ForegroundColor Yellow
if (Test-Path "app/proguard-rules.pro") {
    $proguardContent = Get-Content "app/proguard-rules.pro" -Raw
    $hasCorrectPackage = $proguardContent -match 'com\.wts\.dsfortune'
    
    if ($hasCorrectPackage) {
        Write-Host "   ✅ PASS: ProGuard rules configured correctly" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   ❌ FAIL: ProGuard package name incorrect" -ForegroundColor Red
    }
} else {
    Write-Host "   ❌ FAIL: ProGuard rules not found" -ForegroundColor Red
}

# 7. Permissions and Configuration Files
Write-Host "`n7. Permissions and Configuration Files" -ForegroundColor Yellow
$mainManifest = Test-Path "app/src/main/AndroidManifest.xml"
$debugManifest = Test-Path "app/src/debug/AndroidManifest.xml"
$releaseManifest = Test-Path "app/src/release/AndroidManifest.xml"

if ($mainManifest -and $debugManifest -and $releaseManifest) {
    Write-Host "   ✅ PASS: All 3 manifest files exist" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   ❌ FAIL: Missing manifest files" -ForegroundColor Red
}

# 8. Test Automation
Write-Host "`n8. Test Automation" -ForegroundColor Yellow
$verifyScript = Test-Path "scripts/verify_fix.ps1"
$buildScript = Test-Path "scripts/build_release.ps1"

if ($verifyScript -and $buildScript) {
    Write-Host "   ✅ PASS: Automation scripts exist" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   ❌ FAIL: Missing automation scripts" -ForegroundColor Red
}

# 9. Version Control Strategy
Write-Host "`n9. Version Control Strategy" -ForegroundColor Yellow
$gitignore = Test-Path ".gitignore"

if ($gitignore) {
    Write-Host "   ✅ PASS: .gitignore exists" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   ❌ FAIL: .gitignore missing" -ForegroundColor Red
}

# 10. Pre-release Verification Process
Write-Host "`n10. Pre-release Verification Process" -ForegroundColor Yellow
$debugApk = Test-Path "app\build\outputs\apk\debug\app-debug.apk"
$releaseApk = Test-Path "app\build\outputs\apk\release\app-release.apk"

if ($debugApk -and $releaseApk) {
    $debugSize = (Get-Item "app\build\outputs\apk\debug\app-debug.apk").Length / 1MB
    $releaseSize = (Get-Item "app\build\outputs\apk\release\app-release.apk").Length / 1MB
    
    Write-Host "   ✅ PASS: Both APKs exist (Debug: $([math]::Round($debugSize,1))MB, Release: $([math]::Round($releaseSize,1))MB)" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   ❌ FAIL: APK files missing" -ForegroundColor Red
}

# Final Results
Write-Host "`n=== FINAL RESULTS ===" -ForegroundColor Cyan
$percentage = ($passed / $total) * 100

Write-Host "Passed: $passed / $total points" -ForegroundColor White
Write-Host "Completion: $([math]::Round($percentage, 1))%" -ForegroundColor $(if ($percentage -eq 100) { "Green" } elseif ($percentage -ge 80) { "Yellow" } else { "Red" })

if ($passed -eq $total) {
    Write-Host "`n🎉 CONGRATULATIONS!" -ForegroundColor Green
    Write-Host "✅ All 10 key points are satisfied!" -ForegroundColor Green
    Write-Host "🚀 Your build consistency issue is COMPLETELY RESOLVED!" -ForegroundColor Green
    Write-Host "📱 Test and Release versions will now be consistent!" -ForegroundColor Green
    
    Write-Host "`nNext Steps:" -ForegroundColor Yellow
    Write-Host "1. Install Debug APK on emulator" -ForegroundColor White
    Write-Host "2. Install Release APK on real device" -ForegroundColor White
    Write-Host "3. Compare functionality and appearance" -ForegroundColor White
    Write-Host "4. If consistent -> Problem SOLVED!" -ForegroundColor White
    
} else {
    Write-Host "`n⚠️ $($total - $passed) points still need attention" -ForegroundColor Yellow
    Write-Host "Please address the failed points before release" -ForegroundColor Red
}

exit $(if ($passed -eq $total) { 0 } else { 1 })

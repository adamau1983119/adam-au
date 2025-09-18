# Simple Build Consistency Check - Pure ASCII
Write-Host "=== Build Consistency Check ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$passed = 0
$total = 10

Write-Host ""
Write-Host "Checking 10 key points for build consistency..." -ForegroundColor Green
Write-Host ""

# Point 1
Write-Host "1. Build Environment" -ForegroundColor Yellow
$check1 = (Test-Path "app/build.gradle.kts") -and (Test-Path "gradle.properties")
if ($check1) {
    Write-Host "   PASS: Build files exist" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   FAIL: Missing build files" -ForegroundColor Red
}

# Point 2  
Write-Host "2. Resource Files" -ForegroundColor Yellow
$check2 = (Test-Path "app/src/main/res/drawable") -and (Test-Path "app/src/main/assets")
if ($check2) {
    Write-Host "   PASS: Resource directories exist" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   FAIL: Missing resources" -ForegroundColor Red
}

# Point 3
Write-Host "3. String Resources" -ForegroundColor Yellow
$check3 = (Test-Path "app/src/main/res/values/strings.xml") -and (Test-Path "app/src/main/res/values-en/strings.xml")
if ($check3) {
    Write-Host "   PASS: String files exist" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   FAIL: Missing strings" -ForegroundColor Red
}

# Point 4
Write-Host "4. Dependencies" -ForegroundColor Yellow
if (Test-Path "app/build.gradle.kts") {
    $content = Get-Content "app/build.gradle.kts" -Raw
    $check4 = -not ($content -match 'implementation.*\+')
    
    if ($check4) {
        Write-Host "   PASS: Fixed versions" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: Dynamic versions" -ForegroundColor Red
    }
} else {
    Write-Host "   FAIL: No build file" -ForegroundColor Red
}

# Point 5
Write-Host "5. Build Config" -ForegroundColor Yellow
if (Test-Path "app/build.gradle.kts") {
    $content = Get-Content "app/build.gradle.kts" -Raw
    $check5 = ($content -match 'buildTypes') -and ($content -match 'signingConfigs')
    
    if ($check5) {
        Write-Host "   PASS: Build config OK" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: Build config incomplete" -ForegroundColor Red
    }
} else {
    Write-Host "   FAIL: No build config" -ForegroundColor Red
}

# Point 6
Write-Host "6. ProGuard Rules" -ForegroundColor Yellow
if (Test-Path "app/proguard-rules.pro") {
    $content = Get-Content "app/proguard-rules.pro" -Raw
    $check6 = $content -match 'com\.wts\.dsfortune'
    
    if ($check6) {
        Write-Host "   PASS: ProGuard OK" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: ProGuard wrong package" -ForegroundColor Red
    }
} else {
    Write-Host "   FAIL: No ProGuard rules" -ForegroundColor Red
}

# Point 7
Write-Host "7. Manifest Files" -ForegroundColor Yellow
$main = Test-Path "app/src/main/AndroidManifest.xml"
$debug = Test-Path "app/src/debug/AndroidManifest.xml"
$release = Test-Path "app/src/release/AndroidManifest.xml"

if ($main -and $debug -and $release) {
    Write-Host "   PASS: All manifests exist" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   FAIL: Missing manifests" -ForegroundColor Red
}

# Point 8
Write-Host "8. Scripts" -ForegroundColor Yellow
$verify = Test-Path "scripts/verify_fix.ps1"
$build = Test-Path "scripts/build_release.ps1"

if ($verify -and $build) {
    Write-Host "   PASS: Scripts exist" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   FAIL: Missing scripts" -ForegroundColor Red
}

# Point 9
Write-Host "9. Version Control" -ForegroundColor Yellow
$gitignore = Test-Path ".gitignore"

if ($gitignore) {
    Write-Host "   PASS: .gitignore exists" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   FAIL: No .gitignore" -ForegroundColor Red
}

# Point 10
Write-Host "10. APK Files" -ForegroundColor Yellow
$debugApk = Test-Path "app\build\outputs\apk\debug\app-debug.apk"
$releaseApk = Test-Path "app\build\outputs\apk\release\app-release.apk"

if ($debugApk -and $releaseApk) {
    Write-Host "   PASS: Both APKs exist" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   FAIL: APKs missing" -ForegroundColor Red
}

# Results
Write-Host ""
Write-Host "=== RESULTS ===" -ForegroundColor Cyan
Write-Host "Passed: $passed / $total" -ForegroundColor White

$percent = ($passed / $total) * 100
Write-Host "Score: $percent%" -ForegroundColor $(if ($percent -eq 100) { "Green" } else { "Yellow" })

if ($passed -eq $total) {
    Write-Host ""
    Write-Host "SUCCESS! All 10 points passed!" -ForegroundColor Green
    Write-Host "Your build consistency issue is FIXED!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Yellow
    Write-Host "1. Install Debug APK on emulator" -ForegroundColor White
    Write-Host "2. Install Release APK on device" -ForegroundColor White
    Write-Host "3. Compare both versions" -ForegroundColor White
    Write-Host "4. If same -> Problem solved!" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "Need to fix $($total - $passed) more points" -ForegroundColor Yellow
}

Write-Host ""

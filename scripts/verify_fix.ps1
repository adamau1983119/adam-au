# Build Consistency Verification Script
Write-Host "=== Build Consistency Verification ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$errors = @()

# 1. Check key files
Write-Host "`n1. Checking key files..." -ForegroundColor Green

$keyFiles = @(
    "app/build.gradle.kts",
    "app/proguard-rules.pro",
    "app/src/main/AndroidManifest.xml",
    "app/src/debug/AndroidManifest.xml", 
    "app/src/release/AndroidManifest.xml"
)

foreach ($file in $keyFiles) {
    if (Test-Path $file) {
        $fileInfo = Get-Item $file
        Write-Host "  OK: $file (Modified: $($fileInfo.LastWriteTime))" -ForegroundColor Gray
    } else {
        $errors += "Missing file: $file"
        Write-Host "  ERROR: Missing $file" -ForegroundColor Red
    }
}

# 2. Check ProGuard config
Write-Host "`n2. Checking ProGuard config..." -ForegroundColor Green
$proguardFile = "app/proguard-rules.pro"
if (Test-Path $proguardFile) {
    $content = Get-Content $proguardFile -Raw
    if ($content -match "com\.wts\.dsfortune") {
        Write-Host "  OK: ProGuard package name correct" -ForegroundColor Gray
    } else {
        $errors += "ProGuard package name incorrect"
        Write-Host "  ERROR: ProGuard package name incorrect" -ForegroundColor Red
    }
}

# 3. Clean build
Write-Host "`n3. Cleaning build..." -ForegroundColor Green
& .\gradlew.bat clean
if ($LASTEXITCODE -eq 0) {
    Write-Host "  OK: Clean successful" -ForegroundColor Gray
} else {
    $errors += "Clean failed"
    Write-Host "  ERROR: Clean failed" -ForegroundColor Red
}

Write-Host "`n4. Building Debug..." -ForegroundColor Green
& .\gradlew.bat assembleDebug
if ($LASTEXITCODE -eq 0) {
    Write-Host "  OK: Debug build successful" -ForegroundColor Gray
} else {
    $errors += "Debug build failed"
    Write-Host "  ERROR: Debug build failed" -ForegroundColor Red
}

Write-Host "`n5. Building Release..." -ForegroundColor Green
& .\gradlew.bat assembleRelease
if ($LASTEXITCODE -eq 0) {
    Write-Host "  OK: Release build successful" -ForegroundColor Gray
} else {
    $errors += "Release build failed"
    Write-Host "  ERROR: Release build failed" -ForegroundColor Red
}

# 6. Check APK files
Write-Host "`n6. Checking APK files..." -ForegroundColor Green
$debugApk = "app\build\outputs\apk\debug\app-debug.apk"
$releaseApk = "app\build\outputs\apk\release\app-release.apk"

$debugExists = Test-Path $debugApk
$releaseExists = Test-Path $releaseApk

if ($debugExists) {
    $debugSize = (Get-Item $debugApk).Length / 1MB
    $debugHash = (Get-FileHash $debugApk).Hash.Substring(0,8)
    Write-Host "  OK: Debug APK exists ($([math]::Round($debugSize, 2)) MB, Hash: $debugHash)" -ForegroundColor Gray
} else {
    $errors += "Debug APK missing"
    Write-Host "  ERROR: Debug APK missing" -ForegroundColor Red
}

if ($releaseExists) {
    $releaseSize = (Get-Item $releaseApk).Length / 1MB
    $releaseHash = (Get-FileHash $releaseApk).Hash.Substring(0,8)
    Write-Host "  OK: Release APK exists ($([math]::Round($releaseSize, 2)) MB, Hash: $releaseHash)" -ForegroundColor Gray
} else {
    $errors += "Release APK missing"
    Write-Host "  ERROR: Release APK missing" -ForegroundColor Red
}

# Results
Write-Host "`n=== VERIFICATION RESULTS ===" -ForegroundColor Cyan

if ($errors.Count -eq 0) {
    Write-Host "SUCCESS: All checks passed!" -ForegroundColor Green
    Write-Host "`nNext steps:" -ForegroundColor Yellow
    Write-Host "1. Install Debug APK on emulator: $debugApk" -ForegroundColor White
    Write-Host "2. Install Release APK on real device: $releaseApk" -ForegroundColor White
    Write-Host "3. Compare appearance and functionality" -ForegroundColor White
    Write-Host "4. If consistent, the issue is FIXED!" -ForegroundColor White
    
    if ($debugExists -and $releaseExists) {
        $sizeDiff = [math]::Round((($debugSize - $releaseSize) / $debugSize) * 100, 1)
        Write-Host "`nAPK Analysis:" -ForegroundColor Cyan
        Write-Host "- Size reduction: $sizeDiff% (normal for release builds)" -ForegroundColor Gray
        Write-Host "- Different hashes: GOOD (indicates proper build variants)" -ForegroundColor Gray
    }
    
    # Generate report
    $report = @"
Build Verification Report - $(Get-Date)

RESULT: SUCCESS - All checks passed

APK Information:
- Debug: $([math]::Round($debugSize, 2)) MB (Hash: $debugHash)
- Release: $([math]::Round($releaseSize, 2)) MB (Hash: $releaseHash)
- Size reduction: $sizeDiff%

Test Steps:
1. adb install -r "$debugApk"
2. adb install -r "$releaseApk" 
3. Compare functionality and appearance
4. Verify consistency

If still inconsistent, check:
- ProGuard logs in app/build/outputs/mapping/release/
- Resource files completeness
- Manifest configurations
"@
    
    $report | Out-File "build_verification_$(Get-Date -Format 'MMdd_HHmm').txt" -Encoding UTF8
    Write-Host "`nReport saved: build_verification_$(Get-Date -Format 'MMdd_HHmm').txt" -ForegroundColor Cyan
    exit 0
    
} else {
    Write-Host "FAILED: Found $($errors.Count) errors:" -ForegroundColor Red
    foreach ($error in $errors) {
        Write-Host "  - $error" -ForegroundColor Red
    }
    Write-Host "`nPlease fix these issues before proceeding." -ForegroundColor Yellow
    exit 1
}

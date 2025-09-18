# Hidden Test Version 4 Package
# Internal testing build for upload validation

Write-Host "=== Hidden Test Version 4 Package ===" -ForegroundColor Cyan
Write-Host "DS Fortune App - Internal Testing Build v1.0.4-beta" -ForegroundColor Green

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$buildStartTime = Get-Date

Write-Host "`nPreparing hidden test version 4..." -ForegroundColor Green

# Step 1: Version verification
Write-Host "`n[1] Version Configuration Check" -ForegroundColor Yellow

$buildFile = "app/build.gradle.kts"
if (Test-Path $buildFile) {
    $buildContent = Get-Content $buildFile -Raw
    
    if ($buildContent -match 'versionCode = 4' -and $buildContent -match 'versionName = "1\.0\.4-beta"') {
        Write-Host "   ✓ Version updated to 1.0.4-beta (Build 4)" -ForegroundColor Green
    } else {
        Write-Host "   ✗ Version not properly updated" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "   ✗ Build file not found" -ForegroundColor Red
    exit 1
}

# Step 2: Clean build for test version
Write-Host "`n[2] Clean Build Environment" -ForegroundColor Yellow
Write-Host "   Cleaning previous builds..." -ForegroundColor Gray

try {
    & .\gradlew.bat clean | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✓ Build environment cleaned" -ForegroundColor Green
    } else {
        Write-Host "   ✗ Clean failed" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "   ✗ Clean error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 3: Build debug version for hidden test
Write-Host "`n[3] Build Hidden Test Debug Version" -ForegroundColor Yellow
Write-Host "   Building debug APK for internal testing..." -ForegroundColor Gray

try {
    & .\gradlew.bat assembleDebug
    if ($LASTEXITCODE -eq 0) {
        $debugApk = "app\build\outputs\apk\debug\app-debug.apk"
        if (Test-Path $debugApk) {
            $debugSize = [math]::Round((Get-Item $debugApk).Length / 1024 / 1024, 1)
            Write-Host "   ✓ Debug APK built successfully ($debugSize MB)" -ForegroundColor Green
        } else {
            Write-Host "   ✗ Debug APK not found" -ForegroundColor Red
            exit 1
        }
    } else {
        Write-Host "   ✗ Debug build failed" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "   ✗ Debug build error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 4: Build release version for hidden test
Write-Host "`n[4] Build Hidden Test Release Version" -ForegroundColor Yellow
Write-Host "   Building release APK with ProGuard..." -ForegroundColor Gray

try {
    & .\gradlew.bat assembleRelease
    if ($LASTEXITCODE -eq 0) {
        $releaseApk = "app\build\outputs\apk\release\app-release.apk"
        if (Test-Path $releaseApk) {
            $releaseSize = [math]::Round((Get-Item $releaseApk).Length / 1024 / 1024, 1)
            Write-Host "   ✓ Release APK built successfully ($releaseSize MB)" -ForegroundColor Green
            
            # Calculate compression
            $debugSize = (Get-Item "app\build\outputs\apk\debug\app-debug.apk").Length
            $compression = [math]::Round((1 - (Get-Item $releaseApk).Length / $debugSize) * 100, 1)
            Write-Host "   ✓ Compression: $compression% reduction" -ForegroundColor Green
        } else {
            Write-Host "   ✗ Release APK not found" -ForegroundColor Red
            exit 1
        }
    } else {
        Write-Host "   ✗ Release build failed" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "   ✗ Release build error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Step 5: Quick integrity check
Write-Host "`n[5] Quick Integrity Check" -ForegroundColor Yellow

$debugApk = "app\build\outputs\apk\debug\app-debug.apk"
$releaseApk = "app\build\outputs\apk\release\app-release.apk"

# Check critical resources
$criticalResources = @(
    "app/src/main/res/drawable/wong_tai_sin.png",
    "app/src/main/res/drawable/wts03.png",
    "app/src/main/res/drawable/wts04.png",
    "app/src/main/res/drawable/wts05.png",
    "app/src/main/assets/fortunes_source.csv"
)

$resourcesOK = $true
foreach ($resource in $criticalResources) {
    if (Test-Path $resource) {
        Write-Host "   ✓ $([System.IO.Path]::GetFileName($resource))" -ForegroundColor Green
    } else {
        Write-Host "   ✗ $([System.IO.Path]::GetFileName($resource)) missing" -ForegroundColor Red
        $resourcesOK = $false
    }
}

if (-not $resourcesOK) {
    Write-Host "   ✗ Critical resources missing" -ForegroundColor Red
    exit 1
}

# Step 6: Create hidden test package
Write-Host "`n[6] Create Hidden Test Package" -ForegroundColor Yellow

$testPackageDir = "hidden_test_v4_$(Get-Date -Format 'MMdd_HHmm')"
$testPackagePath = Join-Path $PWD $testPackageDir

if (Test-Path $testPackagePath) {
    Remove-Item $testPackagePath -Recurse -Force
}
New-Item -ItemType Directory -Path $testPackagePath | Out-Null

# Copy APKs with test naming
Copy-Item $debugApk -Destination (Join-Path $testPackagePath "DS_Fortune_v4_Debug_Test.apk")
Copy-Item $releaseApk -Destination (Join-Path $testPackagePath "DS_Fortune_v4_Release_Test.apk")

Write-Host "   ✓ Test APKs copied to package" -ForegroundColor Green

# Step 7: Generate test documentation
Write-Host "`n[7] Generate Test Documentation" -ForegroundColor Yellow

$buildEndTime = Get-Date
$buildDuration = $buildEndTime - $buildStartTime

$testDoc = @"
DS Fortune App - Hidden Test Version 4
Internal Testing Build Documentation
Generated: $(Get-Date)

=== BUILD INFORMATION ===
Version: 1.0.4-beta (Build 4)
Build Duration: $([math]::Round($buildDuration.TotalMinutes, 1)) minutes
Package ID: com.wts.dsfortune

=== APK FILES ===
1. DS_Fortune_v4_Debug_Test.apk
   - Size: $([math]::Round((Get-Item $debugApk).Length / 1024 / 1024, 1)) MB
   - Type: Debug build with full logging
   - Purpose: Internal testing and debugging
   - ProGuard: Disabled
   - Signed: Yes

2. DS_Fortune_v4_Release_Test.apk  
   - Size: $([math]::Round((Get-Item $releaseApk).Length / 1024 / 1024, 1)) MB
   - Type: Release build optimized
   - Purpose: Upload testing validation
   - ProGuard: Enabled with resource protection
   - Signed: Yes
   - Compression: $([math]::Round((1 - (Get-Item $releaseApk).Length / (Get-Item $debugApk).Length) * 100, 1))% smaller than debug

=== HIDDEN TEST FOCUS AREAS ===
✓ Dynamic resource loading (wong_tai_sin, wts03-05)
✓ ProGuard obfuscation compatibility
✓ AdMob advertisement system
✓ DeepSeek API integration
✓ Cup throwing animations
✓ User profile data handling
✓ Multi-language support
✓ Fortune content integrity (5000+ entries)

=== TEST SCENARIOS ===
1. Image Loading Test
   - Verify wong_tai_sin displays correctly
   - Check all cup throwing images (wts03, wts04, wts05)
   - Confirm no resource loading failures

2. Feature Functionality Test
   - Random fortune drawing
   - Cup throwing with animations
   - User profile input and storage
   - Language switching
   - AdMob ad loading and display

3. Build Consistency Test
   - Compare debug vs release behavior
   - Verify identical functionality
   - Check performance differences
   - Validate ProGuard protection effectiveness

=== KNOWN ISSUES RESOLVED ===
✓ Dynamic resource loading failures (Fixed with ProGuard rules)
✓ Build consistency between debug/release (Fixed)
✓ PowerShell encoding issues (Fixed with English scripts)
✓ AdMob integration incomplete (Fixed)
✓ DeepSeek API setup (Completed)

=== UPLOAD VALIDATION CHECKLIST ===
□ Install both APKs on test devices
□ Verify all images load correctly in release build
□ Test fortune drawing functionality
□ Confirm cup throwing works properly
□ Check AdMob ads display (test IDs)
□ Validate user profile features
□ Test language switching
□ Compare debug vs release behavior
□ Monitor for any crashes or errors

=== INTERNAL NOTES ===
- This is hidden test version 4 for upload validation
- All previous build consistency issues have been resolved
- ProGuard rules now protect dynamic resource loading
- Both debug and release builds should behave identically
- Ready for Google Play Console upload testing

=== NEXT STEPS ===
1. Install and test both APKs on multiple devices
2. Validate all functionality works in release build
3. If tests pass, proceed with Google Play Console upload
4. Monitor for any issues during upload process
5. Prepare for gradual rollout if upload successful

Build completed successfully at $(Get-Date)
Package location: $testPackagePath
Status: Ready for hidden testing
"@

$testDocPath = Join-Path $testPackagePath "Hidden_Test_v4_Documentation.txt"
$testDoc | Out-File -FilePath $testDocPath -Encoding UTF8

Write-Host "   ✓ Test documentation created" -ForegroundColor Green

# Step 8: Final verification
Write-Host "`n[8] Final Hidden Test Verification" -ForegroundColor Yellow

$testFiles = Get-ChildItem $testPackagePath
Write-Host "   Test package contents:" -ForegroundColor Gray
foreach ($file in $testFiles) {
    $size = if ($file.Name.EndsWith('.apk')) { 
        " ($([math]::Round($file.Length / 1024 / 1024, 1)) MB)" 
    } else { 
        " ($([math]::Round($file.Length / 1024, 1)) KB)" 
    }
    Write-Host "     - $($file.Name)$size" -ForegroundColor Gray
}

Write-Host "`n=== HIDDEN TEST V4 COMPLETE ===" -ForegroundColor Green
Write-Host "✓ Version 1.0.4-beta (Build 4) ready for testing" -ForegroundColor Green
Write-Host "✓ Both debug and release APKs built successfully" -ForegroundColor Green
Write-Host "✓ All critical resources verified" -ForegroundColor Green
Write-Host "✓ Test package prepared for upload validation" -ForegroundColor Green

Write-Host "`nHIDDEN TEST PACKAGE LOCATION:" -ForegroundColor Cyan
Write-Host "$testPackagePath" -ForegroundColor Yellow

Write-Host "`nREADY FOR:" -ForegroundColor Cyan
Write-Host "• Internal device testing" -ForegroundColor White
Write-Host "• Upload validation testing" -ForegroundColor White
Write-Host "• Google Play Console upload" -ForegroundColor White
Write-Host "• Build consistency verification" -ForegroundColor White

# Save build report
$reportPath = "hidden_test_v4_report_$(Get-Date -Format 'MMdd_HHmm').txt"
$report = @"
Hidden Test Version 4 Build Report
Generated: $(Get-Date)

BUILD SUMMARY:
- Version: 1.0.4-beta (Build 4)
- Build Duration: $([math]::Round($buildDuration.TotalMinutes, 1)) minutes
- Debug APK: $([math]::Round((Get-Item $debugApk).Length / 1024 / 1024, 1)) MB
- Release APK: $([math]::Round((Get-Item $releaseApk).Length / 1024 / 1024, 1)) MB
- Compression: $([math]::Round((1 - (Get-Item $releaseApk).Length / (Get-Item $debugApk).Length) * 100, 1))%

PACKAGE LOCATION:
$testPackagePath

STATUS: READY FOR HIDDEN TESTING
All builds completed successfully. Package ready for upload validation.
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "`nBuild report saved: $reportPath" -ForegroundColor Gray

exit 0

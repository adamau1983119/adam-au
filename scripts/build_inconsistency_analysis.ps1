# Build Inconsistency Analysis
# Why packaged version differs from simulator version

Write-Host "=== Build Inconsistency Analysis ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$criticalIssues = @()
$warnings = @()

Write-Host "`nAnalyzing potential causes..." -ForegroundColor Green

# 1. ProGuard/R8 Analysis
Write-Host "`n1. ProGuard/R8 Code Shrinking" -ForegroundColor Yellow

$proguardFile = "app/proguard-rules.pro"
if (Test-Path $proguardFile) {
    $proguardContent = Get-Content $proguardFile -Raw
    
    # Check for dynamic resource loading protection
    if ($proguardContent -notmatch 'keep.*R\$') {
        Write-Host "   CRITICAL: No R class protection found" -ForegroundColor Red
        $criticalIssues += "Dynamic resources (getIdentifier) may fail after ProGuard"
        Write-Host "     Impact: wong_tai_sin, wts03, wts04, wts05 images may not load" -ForegroundColor Red
    }
    
    # Check for AdMob protection
    if ($proguardContent -notmatch 'play-services-ads') {
        Write-Host "   WARNING: No specific AdMob ProGuard rules" -ForegroundColor Yellow
        $warnings += "AdMob functionality may be affected by obfuscation"
    }
    
    # Check for Gson/Retrofit protection
    if ($proguardContent -notmatch 'gson|retrofit') {
        Write-Host "   WARNING: No Gson/Retrofit protection rules" -ForegroundColor Yellow
        $warnings += "DeepSeek API calls may fail due to JSON serialization issues"
    }
    
} else {
    Write-Host "   ERROR: proguard-rules.pro not found" -ForegroundColor Red
    $criticalIssues += "ProGuard rules file missing"
}

# 2. Resource Shrinking Analysis
Write-Host "`n2. Resource Shrinking" -ForegroundColor Yellow

$buildFile = "app/build.gradle.kts"
$buildContent = Get-Content $buildFile -Raw

if ($buildContent -match 'isShrinkResources = true') {
    Write-Host "   ACTIVE: Resource shrinking enabled" -ForegroundColor Yellow
    
    # Check for dynamic resource usage
    $javaFiles = Get-ChildItem "app/src/main/java" -Recurse -Name "*.kt" 2>$null
    $dynamicResources = @()
    
    foreach ($file in $javaFiles) {
        $filePath = "app/src/main/java/$file"
        if (Test-Path $filePath) {
            $fileContent = Get-Content $filePath -Raw
            if ($fileContent -match 'getIdentifier') {
                $matches = [regex]::Matches($fileContent, 'getIdentifier\s*\(\s*"([^"]+)"')
                foreach ($match in $matches) {
                    $dynamicResources += $match.Groups[1].Value
                }
            }
        }
    }
    
    if ($dynamicResources.Count -gt 0) {
        Write-Host "   CRITICAL: Found $($dynamicResources.Count) dynamic resources" -ForegroundColor Red
        foreach ($resource in $dynamicResources) {
            Write-Host "     - $resource (may be removed by resource shrinking)" -ForegroundColor Red
        }
        $criticalIssues += "Dynamic resources may be removed by resource shrinking"
    }
}

# 3. Build Type Differences
Write-Host "`n3. Build Type Configuration" -ForegroundColor Yellow

# Check minify settings
if ($buildContent -match 'debug \{[^}]*isMinifyEnabled = false' -and 
    $buildContent -match 'release \{[^}]*isMinifyEnabled = true') {
    Write-Host "   DIFFERENCE: Debug (no minify) vs Release (minify enabled)" -ForegroundColor Red
    $criticalIssues += "Debug uses original code, Release uses minified/obfuscated code"
}

# Check BuildConfig fields
$buildConfigFields = [regex]::Matches($buildContent, 'buildConfigField\("([^"]+)",\s*"([^"]+)",\s*"([^"]+)"\)')
if ($buildConfigFields.Count -gt 0) {
    Write-Host "   INFO: Found $($buildConfigFields.Count) BuildConfig fields" -ForegroundColor Cyan
    foreach ($field in $buildConfigFields) {
        $type = $field.Groups[1].Value
        $name = $field.Groups[2].Value
        $value = $field.Groups[3].Value
        Write-Host "     $name ($type): $value" -ForegroundColor Gray
    }
    
    # Check if BUILD_TYPE is used conditionally
    foreach ($file in $javaFiles) {
        $filePath = "app/src/main/java/$file"
        if (Test-Path $filePath) {
            $fileContent = Get-Content $filePath -Raw
            if ($fileContent -match 'BuildConfig\.BUILD_TYPE') {
                Write-Host "   WARNING: BUILD_TYPE used for conditional logic" -ForegroundColor Yellow
                $warnings += "Different behavior between debug and release builds"
                break
            }
        }
    }
}

# 4. Manifest Differences
Write-Host "`n4. Manifest Configuration" -ForegroundColor Yellow

$mainManifest = "app/src/main/AndroidManifest.xml"
$debugManifest = "app/src/debug/AndroidManifest.xml"
$releaseManifest = "app/src/release/AndroidManifest.xml"

if (Test-Path $debugManifest -and Test-Path $releaseManifest) {
    $debugContent = Get-Content $debugManifest -Raw
    $releaseContent = Get-Content $releaseManifest -Raw
    
    # Check debuggable flag
    $debugDebuggable = $debugContent -match 'android:debuggable="true"'
    $releaseDebuggable = $releaseContent -match 'android:debuggable="false"'
    
    if ($debugDebuggable -and $releaseDebuggable) {
        Write-Host "   CORRECT: Debuggable flags properly set" -ForegroundColor Green
    } else {
        Write-Host "   WARNING: Debuggable flags may not be set correctly" -ForegroundColor Yellow
        $warnings += "Debuggable flag differences may affect runtime behavior"
    }
    
    # Check AdMob IDs
    $debugAdMob = [regex]::Match($debugContent, 'ca-app-pub-[0-9~]+').Value
    $releaseAdMob = [regex]::Match($releaseContent, 'ca-app-pub-[0-9~]+').Value
    
    if ($debugAdMob -ne $releaseAdMob) {
        Write-Host "   INFO: Different AdMob IDs for debug/release" -ForegroundColor Cyan
        Write-Host "     Debug: $debugAdMob" -ForegroundColor Gray
        Write-Host "     Release: $releaseAdMob" -ForegroundColor Gray
    }
}

# 5. Signing Configuration
Write-Host "`n5. Signing Configuration" -ForegroundColor Yellow

$debugSigning = $buildContent -match 'debug \{[^}]*signingConfig = signingConfigs\.getByName\("release"\)'
$releaseSigning = $buildContent -match 'release \{[^}]*signingConfig = signingConfigs\.getByName\("release"\)'

if ($debugSigning -and $releaseSigning) {
    Write-Host "   GOOD: Both debug and release use same signing config" -ForegroundColor Green
} else {
    Write-Host "   WARNING: Different signing configurations" -ForegroundColor Yellow
    $warnings += "Different signing may cause permission or behavior differences"
}

# 6. Dependency Analysis
Write-Host "`n6. Dependencies" -ForegroundColor Yellow

$dependencies = @("play-services-ads", "retrofit", "gson", "coil")
foreach ($dep in $dependencies) {
    if ($buildContent -match "${dep}:([0-9.]+)") {
        $version = [regex]::Match($buildContent, "${dep}:([0-9.]+)").Groups[1].Value
        Write-Host "   GOOD: $dep locked to version $version" -ForegroundColor Green
    } elseif ($buildContent -match "${dep}:\+") {
        Write-Host "   WARNING: $dep uses dynamic version (+)" -ForegroundColor Yellow
        $warnings += "$dep version not locked, may cause inconsistencies"
    }
}

# 7. Asset Analysis
Write-Host "`n7. Assets and Resources" -ForegroundColor Yellow

# Check CSV files
$csvFiles = Get-ChildItem "app/src/main/assets" -Name "*.csv" 2>$null
if ($csvFiles.Count -gt 0) {
    Write-Host "   INFO: Found $($csvFiles.Count) CSV files in assets" -ForegroundColor Cyan
    foreach ($csv in $csvFiles) {
        $csvPath = "app/src/main/assets/$csv"
        $csvSize = (Get-Item $csvPath).Length
        Write-Host "     $csv ($csvSize bytes)" -ForegroundColor Gray
    }
} else {
    Write-Host "   WARNING: No CSV files found in assets" -ForegroundColor Yellow
    $warnings += "Fortune content CSV files may be missing"
}

# Check drawable resources
$drawableDir = "app/src/main/res/drawable"
if (Test-Path $drawableDir) {
    $drawableFiles = Get-ChildItem $drawableDir -Name "*.png", "*.jpg", "*.webp" 2>$null
    Write-Host "   INFO: Found $($drawableFiles.Count) drawable files" -ForegroundColor Cyan
    
    $criticalDrawables = @("wong_tai_sin", "wts03", "wts04", "wts05")
    foreach ($critical in $criticalDrawables) {
        $found = $drawableFiles | Where-Object { $_ -match $critical }
        if ($found) {
            Write-Host "     FOUND: $critical" -ForegroundColor Green
        } else {
            Write-Host "     MISSING: $critical" -ForegroundColor Red
            $criticalIssues += "Critical drawable $critical not found"
        }
    }
}

# Generate Summary
Write-Host "`n=== ANALYSIS SUMMARY ===" -ForegroundColor Cyan

$totalIssues = $criticalIssues.Count
$totalWarnings = $warnings.Count

Write-Host "`nCRITICAL ISSUES ($totalIssues):" -ForegroundColor Red
if ($totalIssues -eq 0) {
    Write-Host "   No critical issues found" -ForegroundColor Green
} else {
    foreach ($issue in $criticalIssues) {
        Write-Host "   - $issue" -ForegroundColor Red
    }
}

Write-Host "`nWARNINGS ($totalWarnings):" -ForegroundColor Yellow
if ($totalWarnings -eq 0) {
    Write-Host "   No warnings" -ForegroundColor Green
} else {
    foreach ($warning in $warnings) {
        Write-Host "   - $warning" -ForegroundColor Yellow
    }
}

Write-Host "`nMOST LIKELY CAUSES:" -ForegroundColor Cyan

if ($criticalIssues -match "Dynamic resources.*may fail") {
    Write-Host "   1. DYNAMIC RESOURCE LOADING FAILURE" -ForegroundColor Red
    Write-Host "      - getIdentifier() calls fail after ProGuard obfuscation" -ForegroundColor White
    Write-Host "      - Images (wong_tai_sin, wts03-05) not loading in release" -ForegroundColor White
    Write-Host "      - Solution: Add R class protection rules" -ForegroundColor Green
}

if ($criticalIssues -match "Debug.*minify.*Release") {
    Write-Host "   2. CODE MINIFICATION DIFFERENCES" -ForegroundColor Red
    Write-Host "      - Debug runs original code, Release runs minified code" -ForegroundColor White
    Write-Host "      - Different execution paths and behaviors" -ForegroundColor White
    Write-Host "      - Solution: Test with minified debug build" -ForegroundColor Green
}

if ($warnings -match "AdMob") {
    Write-Host "   3. ADMOB INTEGRATION ISSUES" -ForegroundColor Yellow
    Write-Host "      - Ad loading may fail in obfuscated release build" -ForegroundColor White
    Write-Host "      - Different AdMob IDs between debug/release" -ForegroundColor White
    Write-Host "      - Solution: Add AdMob ProGuard rules" -ForegroundColor Green
}

Write-Host "`nIMMEDIATE ACTION REQUIRED:" -ForegroundColor Red

Write-Host "   1. UPDATE PROGUARD RULES:" -ForegroundColor White
Write-Host "      Add to app/proguard-rules.pro:" -ForegroundColor Gray
Write-Host "      -keep class **.R`$* { *; }" -ForegroundColor Gray
Write-Host "      -keepclassmembers class **.R`$* { public static <fields>; }" -ForegroundColor Gray
Write-Host "      -keep class com.wts.dsfortune.util.AdManager { *; }" -ForegroundColor Gray

Write-Host "   2. TEST MINIFIED DEBUG BUILD:" -ForegroundColor White
Write-Host "      Enable minification in debug build temporarily" -ForegroundColor Gray

Write-Host "   3. CLEAN REBUILD:" -ForegroundColor White
Write-Host "      .\gradlew.bat clean" -ForegroundColor Gray
Write-Host "      .\gradlew.bat assembleRelease" -ForegroundColor Gray

Write-Host "   4. COMPARE APK CONTENTS:" -ForegroundColor White
Write-Host "      Use Android Studio APK Analyzer" -ForegroundColor Gray

# Save report
$reportPath = "inconsistency_analysis_$(Get-Date -Format 'MMdd_HHmm').txt"
$report = @"
Build Inconsistency Analysis Report
Generated: $(Get-Date)

CRITICAL ISSUES ($totalIssues):
$(if ($totalIssues -gt 0) { $criticalIssues | ForEach-Object { "- $_" } | Out-String } else { "None" })

WARNINGS ($totalWarnings):
$(if ($totalWarnings -gt 0) { $warnings | ForEach-Object { "- $_" } | Out-String } else { "None" })

MOST LIKELY ROOT CAUSE:
Dynamic resource loading (getIdentifier) fails after ProGuard obfuscation.
Images like wong_tai_sin, wts03, wts04, wts05 cannot be loaded in release build.

IMMEDIATE SOLUTION:
Add R class protection rules to proguard-rules.pro:
-keep class **.R`$* { *; }
-keepclassmembers class **.R`$* { public static <fields>; }

VERIFICATION STEPS:
1. Add ProGuard rules
2. Clean rebuild
3. Test release APK
4. Compare with debug behavior
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "`nReport saved: $reportPath" -ForegroundColor Gray

# Exit with error code if critical issues found
exit $(if ($totalIssues -gt 0) { 1 } else { 0 })

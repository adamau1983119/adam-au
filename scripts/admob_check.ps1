# AdMob Advertisement Dependencies and Trigger Points Check
Write-Host "=== AdMob Advertisement Check ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$errors = @()
$warnings = @()
$missing = @()

Write-Host "`nChecking AdMob advertisement implementation..." -ForegroundColor Green

# 1. Check AdMob dependency
Write-Host "`n1. AdMob Dependency Check" -ForegroundColor Yellow
$buildFile = "app/build.gradle.kts"
if (Test-Path $buildFile) {
    $buildContent = Get-Content $buildFile -Raw
    
    if ($buildContent -match 'play-services-ads') {
        Write-Host "   PASS: AdMob dependency found" -ForegroundColor Green
        
        # Extract version
        $versionMatch = [regex]::Match($buildContent, 'play-services-ads:([0-9.]+)')
        if ($versionMatch.Success) {
            Write-Host "   INFO: AdMob version $($versionMatch.Groups[1].Value)" -ForegroundColor Gray
        }
    } else {
        $missing += "AdMob dependency missing from build.gradle.kts"
        Write-Host "   MISSING: AdMob dependency" -ForegroundColor Red
    }
} else {
    $errors += "build.gradle.kts not found"
    Write-Host "   ERROR: build.gradle.kts not found" -ForegroundColor Red
}

# 2. Check Manifest configuration
Write-Host "`n2. Manifest Configuration Check" -ForegroundColor Yellow
$manifestFile = "app/src/main/AndroidManifest.xml"
if (Test-Path $manifestFile) {
    $manifestContent = Get-Content $manifestFile -Raw
    
    if ($manifestContent -match 'APPLICATION_ID') {
        Write-Host "   PASS: AdMob Application ID configured" -ForegroundColor Green
    } else {
        $missing += "AdMob Application ID not configured in manifest"
        Write-Host "   MISSING: AdMob Application ID" -ForegroundColor Red
    }
    
    # Check for test vs production ID
    if ($manifestContent -match 'ca-app-pub-3940256099942544') {
        Write-Host "   WARN: Using test AdMob ID (should change for production)" -ForegroundColor Yellow
        $warnings += "Using test AdMob Application ID"
    }
} else {
    $errors += "AndroidManifest.xml not found"
    Write-Host "   ERROR: AndroidManifest.xml not found" -ForegroundColor Red
}

# 3. Check for AdMob initialization
Write-Host "`n3. AdMob Initialization Check" -ForegroundColor Yellow
$javaFiles = Get-ChildItem "app/src/main/java" -Recurse -Name "*.kt" 2>$null

$hasInitialization = $false
$initializationFiles = @()

foreach ($file in $javaFiles) {
    $filePath = "app/src/main/java/$file"
    if (Test-Path $filePath) {
        $fileContent = Get-Content $filePath -Raw
        if ($fileContent -match 'MobileAds\.initialize|AdMob.*initialize') {
            $hasInitialization = $true
            $initializationFiles += $file
        }
    }
}

if ($hasInitialization) {
    Write-Host "   PASS: AdMob initialization found in:" -ForegroundColor Green
    foreach ($file in $initializationFiles) {
        Write-Host "     - $file" -ForegroundColor Gray
    }
} else {
    $missing += "AdMob initialization code missing"
    Write-Host "   MISSING: AdMob initialization code" -ForegroundColor Red
}

# 4. Check for Ad implementations
Write-Host "`n4. Advertisement Implementation Check" -ForegroundColor Yellow

$adTypes = @{
    "InterstitialAd" = "插頁廣告"
    "BannerAd" = "橫幅廣告" 
    "RewardedAd" = "獎勵廣告"
    "NativeAd" = "原生廣告"
}

$implementedAds = @()

foreach ($file in $javaFiles) {
    $filePath = "app/src/main/java/$file"
    if (Test-Path $filePath) {
        $fileContent = Get-Content $filePath -Raw
        
        foreach ($adType in $adTypes.Keys) {
            if ($fileContent -match $adType) {
                $implementedAds += @{
                    Type = $adType
                    Name = $adTypes[$adType]
                    File = $file
                }
            }
        }
    }
}

if ($implementedAds.Count -gt 0) {
    Write-Host "   PASS: Found $($implementedAds.Count) ad implementations:" -ForegroundColor Green
    foreach ($ad in $implementedAds) {
        Write-Host "     - $($ad.Name) ($($ad.Type)) in $($ad.File)" -ForegroundColor Gray
    }
} else {
    $missing += "No advertisement implementations found"
    Write-Host "   MISSING: No advertisement implementations" -ForegroundColor Red
}

# 5. Check for Ad trigger points
Write-Host "`n5. Advertisement Trigger Points Check" -ForegroundColor Yellow

$triggerKeywords = @(
    "showAd",
    "loadAd", 
    "displayAd",
    "InterstitialAd.*show",
    "AdRequest",
    "onAdLoaded"
)

$triggerPoints = @()

foreach ($file in $javaFiles) {
    $filePath = "app/src/main/java/$file"
    if (Test-Path $filePath) {
        $fileContent = Get-Content $filePath -Raw
        
        foreach ($keyword in $triggerKeywords) {
            if ($fileContent -match $keyword) {
                $triggerPoints += @{
                    Keyword = $keyword
                    File = $file
                }
            }
        }
    }
}

if ($triggerPoints.Count -gt 0) {
    Write-Host "   PASS: Found $($triggerPoints.Count) ad trigger points:" -ForegroundColor Green
    $uniqueFiles = $triggerPoints | Group-Object File
    foreach ($group in $uniqueFiles) {
        Write-Host "     - $($group.Name): $($group.Count) triggers" -ForegroundColor Gray
    }
} else {
    $missing += "No advertisement trigger points found"
    Write-Host "   MISSING: No advertisement trigger points" -ForegroundColor Red
}

# 6. Check for Ad placement strategy
Write-Host "`n6. Ad Placement Strategy Check" -ForegroundColor Yellow

$placementScenarios = @(
    "抽籤完成後",
    "查看籤文前", 
    "擲筊結果後",
    "首頁載入時",
    "設定頁面"
)

$strategicPlacements = @()

# Check screens for potential ad placements
$screensFile = "app/src/main/java/com/wts/dsfortune/ui/screens/Screens.kt"
if (Test-Path $screensFile) {
    $screensContent = Get-Content $screensFile -Raw
    
    # Look for strategic placement points
    $hasHomeScreen = $screensContent -match 'fun HomeScreen'
    $hasContentScreen = $screensContent -match 'fun ContentScreen'
    $hasCupScreen = $screensContent -match 'fun CupScreen'
    
    if ($hasHomeScreen) { $strategicPlacements += "HomeScreen (首頁)" }
    if ($hasContentScreen) { $strategicPlacements += "ContentScreen (籤文頁)" }
    if ($hasCupScreen) { $strategicPlacements += "CupScreen (擲筊頁)" }
}

if ($strategicPlacements.Count -gt 0) {
    Write-Host "   INFO: Found $($strategicPlacements.Count) strategic placement opportunities:" -ForegroundColor Green
    foreach ($placement in $strategicPlacements) {
        Write-Host "     - $placement" -ForegroundColor Gray
    }
    
    # Check if ads are actually implemented in these screens
    $hasAdsInScreens = $false
    if (Test-Path $screensFile) {
        $screensContent = Get-Content $screensFile -Raw
        if ($screensContent -match 'Ad|廣告') {
            $hasAdsInScreens = $true
        }
    }
    
    if (-not $hasAdsInScreens) {
        $warnings += "Strategic screens found but no ads implemented"
        Write-Host "   WARN: No ads implemented in strategic screens" -ForegroundColor Yellow
    }
} else {
    $warnings += "No strategic placement opportunities identified"
    Write-Host "   WARN: No strategic placement opportunities" -ForegroundColor Yellow
}

# Results Summary
Write-Host "`n=== ADMOB CHECK RESULTS ===" -ForegroundColor Cyan

$totalIssues = $errors.Count + $missing.Count
$hasWarnings = $warnings.Count -gt 0

if ($totalIssues -eq 0) {
    Write-Host "STATUS: AdMob advertisements READY" -ForegroundColor Green
    Write-Host "All critical components are present" -ForegroundColor Green
} else {
    Write-Host "STATUS: AdMob advertisements INCOMPLETE" -ForegroundColor Red
    Write-Host "Found $totalIssues critical issues" -ForegroundColor Red
}

if ($errors.Count -gt 0) {
    Write-Host "`nERRORS ($($errors.Count)):" -ForegroundColor Red
    foreach ($error in $errors) {
        Write-Host "  - $error" -ForegroundColor Red
    }
}

if ($missing.Count -gt 0) {
    Write-Host "`nMISSING COMPONENTS ($($missing.Count)):" -ForegroundColor Red
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

Write-Host "`nADMOB IMPLEMENTATION STATUS:" -ForegroundColor Cyan
Write-Host "Dependency: $(if ($buildContent -match 'play-services-ads') { 'INSTALLED' } else { 'MISSING' })" -ForegroundColor $(if ($buildContent -match 'play-services-ads') { 'Green' } else { 'Red' })
Write-Host "Configuration: $(if ($manifestContent -match 'APPLICATION_ID') { 'CONFIGURED' } else { 'MISSING' })" -ForegroundColor $(if ($manifestContent -match 'APPLICATION_ID') { 'Green' } else { 'Red' })
Write-Host "Initialization: $(if ($hasInitialization) { 'IMPLEMENTED' } else { 'MISSING' })" -ForegroundColor $(if ($hasInitialization) { 'Green' } else { 'Red' })
Write-Host "Ad Types: $(if ($implementedAds.Count -gt 0) { "$($implementedAds.Count) TYPES" } else { 'NONE' })" -ForegroundColor $(if ($implementedAds.Count -gt 0) { 'Green' } else { 'Red' })
Write-Host "Trigger Points: $(if ($triggerPoints.Count -gt 0) { "$($triggerPoints.Count) POINTS" } else { 'NONE' })" -ForegroundColor $(if ($triggerPoints.Count -gt 0) { 'Green' } else { 'Red' })

Write-Host "`nRECOMMENDATIONS:" -ForegroundColor Cyan
Write-Host "1. Implement AdMob initialization in Application class" -ForegroundColor White
Write-Host "2. Add InterstitialAd for key user actions" -ForegroundColor White
Write-Host "3. Place BannerAd in main screens" -ForegroundColor White
Write-Host "4. Implement RewardedAd for premium features" -ForegroundColor White
Write-Host "5. Test ad loading and display in both builds" -ForegroundColor White
Write-Host "6. Replace test AdMob ID with production ID" -ForegroundColor White

# Generate report
$reportPath = "admob_check_$(Get-Date -Format 'MMdd_HHmm').txt"
$report = @"
AdMob Advertisement Check Report
Generated: $(Get-Date)

SUMMARY:
- Errors: $($errors.Count)
- Missing Components: $($missing.Count)
- Warnings: $($warnings.Count)

STATUS: $(if ($totalIssues -eq 0) { "READY" } else { "INCOMPLETE" })

IMPLEMENTATION STATUS:
- Dependency: $(if ($buildContent -match 'play-services-ads') { 'INSTALLED' } else { 'MISSING' })
- Configuration: $(if ($manifestContent -match 'APPLICATION_ID') { 'CONFIGURED' } else { 'MISSING' })
- Initialization: $(if ($hasInitialization) { 'IMPLEMENTED' } else { 'MISSING' })
- Ad Types: $(if ($implementedAds.Count -gt 0) { "$($implementedAds.Count) TYPES" } else { 'NONE' })
- Trigger Points: $(if ($triggerPoints.Count -gt 0) { "$($triggerPoints.Count) POINTS" } else { 'NONE' })

ERRORS:
$(if ($errors.Count -gt 0) { $errors | ForEach-Object { "- $_" } | Out-String } else { "None" })

MISSING:
$(if ($missing.Count -gt 0) { $missing | ForEach-Object { "- $_" } | Out-String } else { "None" })

WARNINGS:
$(if ($warnings.Count -gt 0) { $warnings | ForEach-Object { "- $_" } | Out-String } else { "None" })

STRATEGIC PLACEMENTS IDENTIFIED:
$(if ($strategicPlacements.Count -gt 0) { $strategicPlacements | ForEach-Object { "- $_" } | Out-String } else { "None identified" })

NEXT STEPS:
1. Fix all missing components
2. Implement ad initialization
3. Add ad implementations to strategic screens
4. Test ad loading and display
5. Configure production AdMob ID
6. Verify ad revenue tracking
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "`nDetailed report saved: $reportPath" -ForegroundColor Gray

exit $(if ($totalIssues -eq 0) { 0 } else { 1 })

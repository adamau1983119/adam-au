# AdMob Simple Check Script
Write-Host "=== AdMob Advertisement Check ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$passed = 0
$total = 6

Write-Host "`nChecking AdMob implementation..." -ForegroundColor Green

# 1. Check AdMob dependency
Write-Host "`n1. AdMob Dependency" -ForegroundColor Yellow
$buildFile = "app/build.gradle.kts"
if (Test-Path $buildFile) {
    $buildContent = Get-Content $buildFile -Raw
    
    if ($buildContent -match 'play-services-ads') {
        Write-Host "   PASS: AdMob dependency found" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: AdMob dependency missing" -ForegroundColor Red
    }
} else {
    Write-Host "   FAIL: build.gradle.kts not found" -ForegroundColor Red
}

# 2. Check Manifest configuration
Write-Host "`n2. Manifest Configuration" -ForegroundColor Yellow
$manifestFile = "app/src/main/AndroidManifest.xml"
if (Test-Path $manifestFile) {
    $manifestContent = Get-Content $manifestFile -Raw
    
    if ($manifestContent -match 'APPLICATION_ID') {
        Write-Host "   PASS: AdMob Application ID configured" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: AdMob Application ID missing" -ForegroundColor Red
    }
} else {
    Write-Host "   FAIL: AndroidManifest.xml not found" -ForegroundColor Red
}

# 3. Check for AdMob initialization
Write-Host "`n3. AdMob Initialization" -ForegroundColor Yellow
$javaFiles = Get-ChildItem "app/src/main/java" -Recurse -Name "*.kt" 2>$null

$hasInitialization = $false
foreach ($file in $javaFiles) {
    $filePath = "app/src/main/java/$file"
    if (Test-Path $filePath) {
        $fileContent = Get-Content $filePath -Raw
        if ($fileContent -match 'MobileAds\.initialize') {
            $hasInitialization = $true
            break
        }
    }
}

if ($hasInitialization) {
    Write-Host "   PASS: AdMob initialization found" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   FAIL: AdMob initialization missing" -ForegroundColor Red
}

# 4. Check for Ad implementations
Write-Host "`n4. Advertisement Implementation" -ForegroundColor Yellow

$hasAdImplementation = $false
foreach ($file in $javaFiles) {
    $filePath = "app/src/main/java/$file"
    if (Test-Path $filePath) {
        $fileContent = Get-Content $filePath -Raw
        if ($fileContent -match 'InterstitialAd|BannerAd|RewardedAd') {
            $hasAdImplementation = $true
            break
        }
    }
}

if ($hasAdImplementation) {
    Write-Host "   PASS: Ad implementation found" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   FAIL: No ad implementations found" -ForegroundColor Red
}

# 5. Check for Ad trigger points
Write-Host "`n5. Advertisement Triggers" -ForegroundColor Yellow

$hasTriggers = $false
foreach ($file in $javaFiles) {
    $filePath = "app/src/main/java/$file"
    if (Test-Path $filePath) {
        $fileContent = Get-Content $filePath -Raw
        if ($fileContent -match 'showAd|loadAd|AdRequest') {
            $hasTriggers = $true
            break
        }
    }
}

if ($hasTriggers) {
    Write-Host "   PASS: Ad triggers found" -ForegroundColor Green
    $passed++
} else {
    Write-Host "   FAIL: No ad triggers found" -ForegroundColor Red
}

# 6. Check strategic placement opportunities
Write-Host "`n6. Strategic Placements" -ForegroundColor Yellow
$screensFile = "app/src/main/java/com/wts/dsfortune/ui/screens/Screens.kt"
if (Test-Path $screensFile) {
    $screensContent = Get-Content $screensFile -Raw
    
    $hasHomeScreen = $screensContent -match 'fun HomeScreen'
    $hasContentScreen = $screensContent -match 'fun ContentScreen'
    $hasCupScreen = $screensContent -match 'fun CupScreen'
    
    if ($hasHomeScreen -and $hasContentScreen -and $hasCupScreen) {
        Write-Host "   PASS: Strategic screens available for ads" -ForegroundColor Green
        $passed++
    } else {
        Write-Host "   FAIL: Missing strategic screens" -ForegroundColor Red
    }
} else {
    Write-Host "   FAIL: Screens.kt not found" -ForegroundColor Red
}

# Results
Write-Host "`n=== RESULTS ===" -ForegroundColor Cyan
$percentage = ($passed / $total) * 100

Write-Host "Passed: $passed / $total" -ForegroundColor White
Write-Host "Completion: $([math]::Round($percentage, 1))%" -ForegroundColor $(if ($percentage -ge 80) { "Green" } elseif ($percentage -ge 50) { "Yellow" } else { "Red" })

if ($passed -eq $total) {
    Write-Host "`nSUCCESS: AdMob fully implemented!" -ForegroundColor Green
    Write-Host "Advertisement system is ready for production" -ForegroundColor Green
} elseif ($passed -ge 3) {
    Write-Host "`nPARTIAL: AdMob partially implemented" -ForegroundColor Yellow
    Write-Host "Core components present, need to add implementations" -ForegroundColor Yellow
} else {
    Write-Host "`nINCOMPLETE: AdMob not implemented" -ForegroundColor Red
    Write-Host "Significant work needed for advertisement system" -ForegroundColor Red
}

Write-Host "`nIMPLEMENTATION STATUS:" -ForegroundColor Cyan
Write-Host "- Dependency: $(if ($buildContent -match 'play-services-ads') { 'INSTALLED' } else { 'MISSING' })" -ForegroundColor $(if ($buildContent -match 'play-services-ads') { 'Green' } else { 'Red' })
Write-Host "- Configuration: $(if ($manifestContent -match 'APPLICATION_ID') { 'CONFIGURED' } else { 'MISSING' })" -ForegroundColor $(if ($manifestContent -match 'APPLICATION_ID') { 'Green' } else { 'Red' })
Write-Host "- Initialization: $(if ($hasInitialization) { 'IMPLEMENTED' } else { 'MISSING' })" -ForegroundColor $(if ($hasInitialization) { 'Green' } else { 'Red' })
Write-Host "- Ad Types: $(if ($hasAdImplementation) { 'IMPLEMENTED' } else { 'MISSING' })" -ForegroundColor $(if ($hasAdImplementation) { 'Green' } else { 'Red' })
Write-Host "- Triggers: $(if ($hasTriggers) { 'IMPLEMENTED' } else { 'MISSING' })" -ForegroundColor $(if ($hasTriggers) { 'Green' } else { 'Red' })
Write-Host "- Placements: $(if ($passed -eq $total) { 'READY' } else { 'NEEDS WORK' })" -ForegroundColor $(if ($passed -eq $total) { 'Green' } else { 'Red' })

Write-Host "`nNEXT STEPS:" -ForegroundColor Yellow
Write-Host "1. Add MobileAds.initialize() to Application class" -ForegroundColor White
Write-Host "2. Implement InterstitialAd for key actions" -ForegroundColor White
Write-Host "3. Add BannerAd to main screens" -ForegroundColor White
Write-Host "4. Create ad trigger points in user flows" -ForegroundColor White
Write-Host "5. Test ad loading and display" -ForegroundColor White
Write-Host "6. Replace test AdMob ID with production ID" -ForegroundColor White

exit $(if ($passed -ge 4) { 0 } else { 1 })

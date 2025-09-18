# DeepSeek Dependencies Check Script
Write-Host "=== DeepSeek Dependencies Check ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$errors = @()
$warnings = @()
$missing = @()

Write-Host "`nChecking DeepSeek API dependencies..." -ForegroundColor Green

# 1. Check build.gradle.kts for API configuration
Write-Host "`n1. Build Configuration Check" -ForegroundColor Yellow
if (Test-Path "app/build.gradle.kts") {
    $buildContent = Get-Content "app/build.gradle.kts" -Raw
    
    # Check for API_BASE_URL
    if ($buildContent -match 'API_BASE_URL') {
        Write-Host "   PASS: API_BASE_URL found in build config" -ForegroundColor Green
    } else {
        $missing += "API_BASE_URL not configured in build.gradle.kts"
        Write-Host "   MISSING: API_BASE_URL not found" -ForegroundColor Red
    }
    
    # Check for HTTP client dependencies
    $hasOkHttp = $buildContent -match 'okhttp'
    $hasRetrofit = $buildContent -match 'retrofit'
    $hasGson = $buildContent -match 'gson'
    
    if ($hasOkHttp) {
        Write-Host "   PASS: OkHttp dependency found" -ForegroundColor Green
    } else {
        $missing += "OkHttp dependency missing"
        Write-Host "   MISSING: OkHttp dependency" -ForegroundColor Red
    }
    
    if ($hasRetrofit) {
        Write-Host "   PASS: Retrofit dependency found" -ForegroundColor Green
    } else {
        $missing += "Retrofit dependency missing"
        Write-Host "   MISSING: Retrofit dependency" -ForegroundColor Red
    }
    
} else {
    $errors += "build.gradle.kts not found"
    Write-Host "   ERROR: build.gradle.kts not found" -ForegroundColor Red
}

# 2. Check for ServiceLocator
Write-Host "`n2. ServiceLocator Check" -ForegroundColor Yellow
$serviceLocatorPaths = @(
    "app/src/main/java/com/wts/dsfortune/data/ServiceLocator.kt",
    "app/src/main/java/com/example/wtsaskingforsignature/data/ServiceLocator.kt"
)

$serviceLocatorFound = $false
foreach ($path in $serviceLocatorPaths) {
    if (Test-Path $path) {
        Write-Host "   PASS: ServiceLocator found at $path" -ForegroundColor Green
        $serviceLocatorFound = $true
        
        # Check ServiceLocator content
        $content = Get-Content $path -Raw
        if ($content -match 'useRemote') {
            Write-Host "   PASS: useRemote flag found" -ForegroundColor Green
        } else {
            $warnings += "useRemote flag not found in ServiceLocator"
            Write-Host "   WARN: useRemote flag not found" -ForegroundColor Yellow
        }
        break
    }
}

if (-not $serviceLocatorFound) {
    $missing += "ServiceLocator.kt not found"
    Write-Host "   MISSING: ServiceLocator.kt not found" -ForegroundColor Red
}

# 3. Check for API interfaces
Write-Host "`n3. API Interface Check" -ForegroundColor Yellow
$apiPaths = @(
    "app/src/main/java/com/wts/dsfortune/data/api/",
    "app/src/main/java/com/example/wtsaskingforsignature/data/api/"
)

$apiFound = $false
foreach ($path in $apiPaths) {
    if (Test-Path $path) {
        $apiFiles = Get-ChildItem $path -Filter "*.kt"
        if ($apiFiles.Count -gt 0) {
            Write-Host "   PASS: API interfaces found ($($apiFiles.Count) files)" -ForegroundColor Green
            $apiFound = $true
            break
        }
    }
}

if (-not $apiFound) {
    $missing += "API interface files not found"
    Write-Host "   MISSING: API interface files not found" -ForegroundColor Red
}

# 4. Check network permissions
Write-Host "`n4. Network Permissions Check" -ForegroundColor Yellow
$manifestPath = "app/src/main/AndroidManifest.xml"
if (Test-Path $manifestPath) {
    $manifestContent = Get-Content $manifestPath -Raw
    
    if ($manifestContent -match 'android.permission.INTERNET') {
        Write-Host "   PASS: INTERNET permission found" -ForegroundColor Green
    } else {
        $missing += "INTERNET permission missing from manifest"
        Write-Host "   MISSING: INTERNET permission" -ForegroundColor Red
    }
    
    if ($manifestContent -match 'android.permission.ACCESS_NETWORK_STATE') {
        Write-Host "   PASS: ACCESS_NETWORK_STATE permission found" -ForegroundColor Green
    } else {
        $warnings += "ACCESS_NETWORK_STATE permission recommended"
        Write-Host "   WARN: ACCESS_NETWORK_STATE permission recommended" -ForegroundColor Yellow
    }
} else {
    $errors += "AndroidManifest.xml not found"
    Write-Host "   ERROR: AndroidManifest.xml not found" -ForegroundColor Red
}

# 5. Check for test files
Write-Host "`n5. Test Files Check" -ForegroundColor Yellow
$testFiles = @(
    "test_api.kt",
    "DEEPSEEK_API_CHECKLIST.md",
    "API_CONFIGURATION.md"
)

foreach ($file in $testFiles) {
    if (Test-Path $file) {
        Write-Host "   PASS: $file exists" -ForegroundColor Green
    } else {
        $warnings += "$file not found"
        Write-Host "   WARN: $file not found" -ForegroundColor Yellow
    }
}

# 6. Check current API status
Write-Host "`n6. Current API Status Check" -ForegroundColor Yellow
if (Test-Path "app/build.gradle.kts") {
    $buildContent = Get-Content "app/build.gradle.kts" -Raw
    
    # Look for buildConfigField
    if ($buildContent -match 'buildConfigField.*API') {
        Write-Host "   PASS: API config field found" -ForegroundColor Green
    } else {
        $warnings += "No API buildConfigField found"
        Write-Host "   WARN: No API buildConfigField found" -ForegroundColor Yellow
    }
}

# Results Summary
Write-Host "`n=== DEPENDENCY CHECK RESULTS ===" -ForegroundColor Cyan

$totalIssues = $errors.Count + $missing.Count
$hasWarnings = $warnings.Count -gt 0

if ($totalIssues -eq 0) {
    Write-Host "STATUS: DeepSeek dependencies READY" -ForegroundColor Green
    Write-Host "All critical dependencies are present" -ForegroundColor Green
} else {
    Write-Host "STATUS: DeepSeek dependencies INCOMPLETE" -ForegroundColor Red
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

Write-Host "`nRECOMMENDATIONS:" -ForegroundColor Cyan
Write-Host "1. Add missing dependencies to build.gradle.kts" -ForegroundColor White
Write-Host "2. Implement ServiceLocator with API configuration" -ForegroundColor White
Write-Host "3. Create API interface definitions" -ForegroundColor White
Write-Host "4. Add proper network permissions" -ForegroundColor White
Write-Host "5. Test API connectivity before release" -ForegroundColor White

# Generate detailed report
$reportPath = "deepseek_dependency_report_$(Get-Date -Format 'MMdd_HHmm').txt"
$report = @"
DeepSeek Dependencies Check Report
Generated: $(Get-Date)

SUMMARY:
- Errors: $($errors.Count)
- Missing Components: $($missing.Count)
- Warnings: $($warnings.Count)

STATUS: $(if ($totalIssues -eq 0) { "READY" } else { "INCOMPLETE" })

ERRORS:
$(if ($errors.Count -gt 0) { $errors | ForEach-Object { "- $_" } | Out-String } else { "None" })

MISSING:
$(if ($missing.Count -gt 0) { $missing | ForEach-Object { "- $_" } | Out-String } else { "None" })

WARNINGS:
$(if ($warnings.Count -gt 0) { $warnings | ForEach-Object { "- $_" } | Out-String } else { "None" })

NEXT STEPS:
1. Fix all errors and missing components
2. Address warnings for optimal functionality
3. Test API connectivity
4. Verify both Debug and Release builds
5. Test offline fallback functionality
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "`nDetailed report saved: $reportPath" -ForegroundColor Gray

exit $(if ($totalIssues -eq 0) { 0 } else { 1 })

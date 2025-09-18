# English-Only PowerShell Script Template
# Avoids all encoding issues by using only ASCII characters

Write-Host "=== Build Verification Script ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$errors = @()
$warnings = @()

Write-Host "`nChecking build configuration..." -ForegroundColor Green

# Example checks (English only)
Write-Host "`n1. File Existence Check" -ForegroundColor Yellow

$requiredFiles = @(
    "app/build.gradle.kts",
    "app/proguard-rules.pro",
    "app/src/main/AndroidManifest.xml"
)

foreach ($file in $requiredFiles) {
    if (Test-Path $file) {
        Write-Host "   PASS: $file exists" -ForegroundColor Green
    } else {
        Write-Host "   FAIL: $file missing" -ForegroundColor Red
        $errors += "Missing file: $file"
    }
}

# Results (English only)
Write-Host "`n=== RESULTS ===" -ForegroundColor Cyan

if ($errors.Count -eq 0) {
    Write-Host "SUCCESS: All checks passed" -ForegroundColor Green
    exit 0
} else {
    Write-Host "ERRORS FOUND: $($errors.Count)" -ForegroundColor Red
    foreach ($error in $errors) {
        Write-Host "  - $error" -ForegroundColor Red
    }
    exit 1
}

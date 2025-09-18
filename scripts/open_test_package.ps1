# Open Test Package Helper
# Helps access the hidden test package files

Write-Host "=== Hidden Test Package Access Helper ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$testPackage = "hidden_test_v4_0917_1351"

Write-Host "`nChecking hidden test package..." -ForegroundColor Green

if (Test-Path $testPackage) {
    Write-Host "✓ Test package found: $testPackage" -ForegroundColor Green
    
    # List all files
    Write-Host "`nPackage contents:" -ForegroundColor Yellow
    $files = Get-ChildItem $testPackage -Force
    
    foreach ($file in $files) {
        $sizeMB = [math]::Round($file.Length / 1024 / 1024, 1)
        Write-Host "  - $($file.Name) ($sizeMB MB)" -ForegroundColor White
    }
    
    # Copy files to desktop for easy access
    $desktopPath = [Environment]::GetFolderPath("Desktop")
    $copyPath = Join-Path $desktopPath "DS_Fortune_Test_v4"
    
    Write-Host "`nCopying files to desktop for easy access..." -ForegroundColor Yellow
    
    if (Test-Path $copyPath) {
        Remove-Item $copyPath -Recurse -Force
    }
    New-Item -ItemType Directory -Path $copyPath | Out-Null
    
    # Copy APK files
    foreach ($file in $files) {
        Copy-Item $file.FullName -Destination $copyPath
        Write-Host "  ✓ Copied: $($file.Name)" -ForegroundColor Green
    }
    
    Write-Host "`n=== FILES READY ===" -ForegroundColor Green
    Write-Host "Desktop location: $copyPath" -ForegroundColor Yellow
    Write-Host "Original location: $((Get-Location).Path)\$testPackage" -ForegroundColor Gray
    
    # Open desktop folder
    Write-Host "`nOpening desktop folder..." -ForegroundColor Cyan
    Start-Process "explorer.exe" -ArgumentList $copyPath
    
    Write-Host "`n=== UPLOAD READY FILES ===" -ForegroundColor Cyan
    Write-Host "For Google Play Console upload, use:" -ForegroundColor White
    Write-Host "📱 DS_Fortune_v4_Release_Test.apk (11.9 MB)" -ForegroundColor Green
    Write-Host "🔧 DS_Fortune_v4_Debug_Test.apk (36.9 MB) - for testing" -ForegroundColor Yellow
    Write-Host "📋 Hidden_Test_v4_Documentation.txt - read this first" -ForegroundColor Cyan
    
} else {
    Write-Host "✗ Test package not found: $testPackage" -ForegroundColor Red
    
    # List available packages
    Write-Host "`nAvailable packages:" -ForegroundColor Yellow
    $packages = Get-ChildItem . -Directory | Where-Object { $_.Name -like "*test*" -or $_.Name -like "*upload*" }
    
    if ($packages.Count -gt 0) {
        foreach ($pkg in $packages) {
            Write-Host "  - $($pkg.Name)" -ForegroundColor White
        }
    } else {
        Write-Host "  No test packages found" -ForegroundColor Gray
    }
}

Write-Host "`nDone!" -ForegroundColor Green

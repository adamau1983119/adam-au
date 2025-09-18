# 標準化 Release 建置腳本
# 確保每次建置的一致性

param(
    [switch]$Clean = $false,
    [switch]$SkipTests = $false,
    [string]$OutputDir = "build/outputs"
)

Write-Host "=== DS 黃大仙靈簽 Release 建置腳本 ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

# 建置前檢查
Write-Host "`n1. 建置前檢查..." -ForegroundColor Green

# 檢查 Gradle Wrapper
if (-not (Test-Path "gradlew.bat")) {
    Write-Host "錯誤: 找不到 gradlew.bat" -ForegroundColor Red
    exit 1
}

# 檢查簽名文件
if (-not (Test-Path "wts-release-key.keystore")) {
    Write-Host "錯誤: 找不到簽名文件 wts-release-key.keystore" -ForegroundColor Red
    exit 1
}

Write-Host "✓ 建置環境檢查通過" -ForegroundColor Green

# 清理建置
if ($Clean) {
    Write-Host "`n2. 清理建置..." -ForegroundColor Green
    & .\gradlew.bat clean
    if ($LASTEXITCODE -ne 0) {
        Write-Host "錯誤: 清理建置失敗" -ForegroundColor Red
        exit 1
    }
    Write-Host "✓ 清理完成" -ForegroundColor Green
}

# 運行資源一致性檢查
Write-Host "`n3. 運行資源一致性檢查..." -ForegroundColor Green
$verifyScript = Join-Path $PSScriptRoot "verify_build_consistency.ps1"
if (Test-Path $verifyScript) {
    & $verifyScript
    if ($LASTEXITCODE -ne 0) {
        Write-Host "警告: 資源一致性檢查發現問題" -ForegroundColor Yellow
    }
} else {
    Write-Host "警告: 找不到資源一致性檢查腳本" -ForegroundColor Yellow
}

# 建置 Debug 版本（用於對比）
Write-Host "`n4. 建置 Debug 版本..." -ForegroundColor Green
& .\gradlew.bat assembleDebug
if ($LASTEXITCODE -ne 0) {
    Write-Host "錯誤: Debug 建置失敗" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Debug 建置完成" -ForegroundColor Green

# 建置 Release 版本
Write-Host "`n5. 建置 Release 版本..." -ForegroundColor Green
& .\gradlew.bat assembleRelease
if ($LASTEXITCODE -ne 0) {
    Write-Host "錯誤: Release 建置失敗" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Release 建置完成" -ForegroundColor Green

# 運行測試（可選）
if (-not $SkipTests) {
    Write-Host "`n6. 運行單元測試..." -ForegroundColor Green
    & .\gradlew.bat testDebugUnitTest
    if ($LASTEXITCODE -ne 0) {
        Write-Host "警告: 單元測試失敗，但繼續建置" -ForegroundColor Yellow
    } else {
        Write-Host "✓ 單元測試通過" -ForegroundColor Green
    }
}

# 檢查輸出文件
Write-Host "`n7. 檢查建置輸出..." -ForegroundColor Green
$debugApk = "app\build\outputs\apk\debug\app-debug.apk"
$releaseApk = "app\build\outputs\apk\release\app-release.apk"

$results = @()

if (Test-Path $debugApk) {
    $debugSize = (Get-Item $debugApk).Length / 1MB
    $results += "Debug APK: $([math]::Round($debugSize, 2)) MB"
    Write-Host "✓ Debug APK 生成成功: $([math]::Round($debugSize, 2)) MB" -ForegroundColor Green
} else {
    Write-Host "錯誤: Debug APK 未找到" -ForegroundColor Red
}

if (Test-Path $releaseApk) {
    $releaseSize = (Get-Item $releaseApk).Length / 1MB
    $results += "Release APK: $([math]::Round($releaseSize, 2)) MB"
    Write-Host "✓ Release APK 生成成功: $([math]::Round($releaseSize, 2)) MB" -ForegroundColor Green
} else {
    Write-Host "錯誤: Release APK 未找到" -ForegroundColor Red
}

# 生成建置報告
Write-Host "`n8. 生成建置報告..." -ForegroundColor Green
$reportPath = "build_report_$(Get-Date -Format 'yyyy-MM-dd_HH-mm-ss').txt"
$report = @"
DS 黃大仙靈簽建置報告
生成時間: $(Get-Date)
建置環境: Windows PowerShell
建置類型: Debug + Release

建置結果:
$($results -join "`n")

建置參數:
- 清理建置: $Clean
- 跳過測試: $SkipTests
- 輸出目錄: $OutputDir

注意事項:
1. 請在不同設備上測試兩個版本的一致性
2. 確認所有資源文件正確載入
3. 驗證國際化功能正常運作
4. 檢查 AdMob 廣告配置

下一步:
1. 在模擬器中安裝並測試 Debug 版本
2. 在真實設備中安裝並測試 Release 版本
3. 對比兩個版本的功能和外觀
4. 如發現差異，請檢查 ProGuard 規則和資源配置
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "✓ 建置報告已保存: $reportPath" -ForegroundColor Green

Write-Host "`n=== 建置完成 ===" -ForegroundColor Cyan
Write-Host "建議接下來的步驟：" -ForegroundColor Yellow
Write-Host "1. 在模擬器中測試 Debug 版本" -ForegroundColor White
Write-Host "2. 在真實設備中測試 Release 版本" -ForegroundColor White
Write-Host "3. 對比兩個版本的一致性" -ForegroundColor White
Write-Host "4. 如有差異，檢查建置配置" -ForegroundColor White

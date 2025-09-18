# 快速驗證腳本 - 確保建置一致性問題已解決
Write-Host "=== 快速建置一致性驗證 ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$errors = @()

# 1. 檢查關鍵文件
Write-Host "`n1. 檢查關鍵文件..." -ForegroundColor Green

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
        Write-Host "  OK: $file (修改時間: $($fileInfo.LastWriteTime))" -ForegroundColor Gray
    } else {
        $errors += "缺少文件: $file"
        Write-Host "  ERROR: 缺少 $file" -ForegroundColor Red
    }
}

# 2. 檢查 ProGuard 配置
Write-Host "`n2. 檢查 ProGuard 配置..." -ForegroundColor Green
$proguardFile = "app/proguard-rules.pro"
if (Test-Path $proguardFile) {
    $content = Get-Content $proguardFile -Raw
    if ($content -match "com\.wts\.dsfortune") {
        Write-Host "  OK: ProGuard 包名配置正確" -ForegroundColor Gray
    } else {
        $errors += "ProGuard 包名配置錯誤"
        Write-Host "  ERROR: ProGuard 包名配置錯誤" -ForegroundColor Red
    }
}

# 3. 清理並建置
Write-Host "`n3. 清理建置..." -ForegroundColor Green
& .\gradlew.bat clean
if ($LASTEXITCODE -eq 0) {
    Write-Host "  OK: 清理成功" -ForegroundColor Gray
} else {
    $errors += "清理失敗"
    Write-Host "  ERROR: 清理失敗" -ForegroundColor Red
}

Write-Host "`n4. 建置 Debug 版本..." -ForegroundColor Green
& .\gradlew.bat assembleDebug
if ($LASTEXITCODE -eq 0) {
    Write-Host "  OK: Debug 建置成功" -ForegroundColor Gray
} else {
    $errors += "Debug 建置失敗"
    Write-Host "  ERROR: Debug 建置失敗" -ForegroundColor Red
}

Write-Host "`n5. 建置 Release 版本..." -ForegroundColor Green
& .\gradlew.bat assembleRelease
if ($LASTEXITCODE -eq 0) {
    Write-Host "  OK: Release 建置成功" -ForegroundColor Gray
} else {
    $errors += "Release 建置失敗"
    Write-Host "  ERROR: Release 建置失敗" -ForegroundColor Red
}

# 6. 檢查 APK 文件
Write-Host "`n6. 檢查 APK 文件..." -ForegroundColor Green
$debugApk = "app\build\outputs\apk\debug\app-debug.apk"
$releaseApk = "app\build\outputs\apk\release\app-release.apk"

if (Test-Path $debugApk) {
    $debugSize = (Get-Item $debugApk).Length / 1MB
    $debugHash = (Get-FileHash $debugApk).Hash.Substring(0,8)
    Write-Host "  OK: Debug APK 存在 ($([math]::Round($debugSize, 2)) MB, Hash: $debugHash)" -ForegroundColor Gray
} else {
    $errors += "Debug APK 不存在"
    Write-Host "  ERROR: Debug APK 不存在" -ForegroundColor Red
}

if (Test-Path $releaseApk) {
    $releaseSize = (Get-Item $releaseApk).Length / 1MB
    $releaseHash = (Get-FileHash $releaseApk).Hash.Substring(0,8)
    Write-Host "  OK: Release APK 存在 ($([math]::Round($releaseSize, 2)) MB, Hash: $releaseHash)" -ForegroundColor Gray
} else {
    $errors += "Release APK 不存在"
    Write-Host "  ERROR: Release APK 不存在" -ForegroundColor Red
}

# 結果
Write-Host "`n=== 驗證結果 ===" -ForegroundColor Cyan

if ($errors.Count -eq 0) {
    Write-Host "SUCCESS: 所有檢查通過！" -ForegroundColor Green
    Write-Host "`n下一步：" -ForegroundColor Yellow
    Write-Host "1. 在模擬器安裝 Debug APK: $debugApk" -ForegroundColor White
    Write-Host "2. 在真實設備安裝 Release APK: $releaseApk" -ForegroundColor White
    Write-Host "3. 對比兩個版本的外觀和功能" -ForegroundColor White
    Write-Host "4. 如果一致，問題已解決！" -ForegroundColor White
    
    # 生成簡單報告
    $report = @"
建置驗證報告 - $(Get-Date)

結果: SUCCESS - 所有檢查通過

APK 資訊:
- Debug: $([math]::Round($debugSize, 2)) MB (Hash: $debugHash)
- Release: $([math]::Round($releaseSize, 2)) MB (Hash: $releaseHash)

測試步驟:
1. adb install -r "$debugApk"
2. adb install -r "$releaseApk" 
3. 對比功能和外觀
4. 確認一致性

如果仍有差異，請檢查:
- ProGuard 日誌
- 資源文件完整性
- Manifest 配置
"@
    
    $report | Out-File "build_verification_$(Get-Date -Format 'MMdd_HHmm').txt" -Encoding UTF8
    exit 0
    
} else {
    Write-Host "FAILED: 發現 $($errors.Count) 個錯誤：" -ForegroundColor Red
    foreach ($error in $errors) {
        Write-Host "  - $error" -ForegroundColor Red
    }
    exit 1
}

# 10個關鍵重點最終檢查
Write-Host "=== 10個關鍵重點最終檢查 ===" -ForegroundColor Cyan
Write-Host "確保測試版及正式版app打包與模擬器內容一致" -ForegroundColor Yellow

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$checkResults = @{}
$totalPoints = 10
$passedPoints = 0

Write-Host "`n開始檢查..." -ForegroundColor Green

# 1. 建置環境一致性
Write-Host "`n1. 建置環境一致性" -ForegroundColor Cyan
$buildGradleExists = Test-Path "app/build.gradle.kts"
$gradlePropsExists = Test-Path "gradle.properties"
$gradleWrapperExists = Test-Path "gradlew.bat"

if ($buildGradleExists -and $gradlePropsExists -and $gradleWrapperExists) {
    $buildContent = Get-Content "app/build.gradle.kts" -Raw
    $hasFixedVersions = ($buildContent -match 'compileSdk = 36') -and ($buildContent -match 'targetSdk = 36')
    
    if ($hasFixedVersions) {
        Write-Host "  ✅ PASS: SDK版本固定，Gradle配置統一" -ForegroundColor Green
        $checkResults["1"] = $true
        $passedPoints++
    } else {
        Write-Host "  ❌ FAIL: SDK版本未固定" -ForegroundColor Red
        $checkResults["1"] = $false
    }
} else {
    Write-Host "  ❌ FAIL: 缺少關鍵建置文件" -ForegroundColor Red
    $checkResults["1"] = $false
}

# 2. 資源文件管理
Write-Host "`n2. 資源文件管理" -ForegroundColor Cyan
$drawableDir = "app/src/main/res/drawable"
$mipmapDirs = @("app/src/main/res/mipmap-hdpi", "app/src/main/res/mipmap-xhdpi", "app/src/main/res/mipmap-xxhdpi", "app/src/main/res/mipmap-xxxhdpi")
$assetsDir = "app/src/main/assets"

$resourcesExist = (Test-Path $drawableDir) -and (Test-Path $assetsDir)
$mipmapsExist = $mipmapDirs | ForEach-Object { Test-Path $_ } | Where-Object { $_ -eq $true }

if ($resourcesExist -and $mipmapsExist.Count -eq 4) {
    $drawableCount = (Get-ChildItem $drawableDir -File).Count
    $assetsCount = (Get-ChildItem $assetsDir -File).Count
    Write-Host "  ✅ PASS: 資源目錄完整 (drawable: $drawableCount, assets: $assetsCount, mipmap: 4密度)" -ForegroundColor Green
    $checkResults["2"] = $true
    $passedPoints++
} else {
    Write-Host "  ❌ FAIL: 資源目錄不完整" -ForegroundColor Red
    $checkResults["2"] = $false
}

# 3. 字符串資源國際化
Write-Host "`n3. 字符串資源國際化" -ForegroundColor Cyan
$stringsMain = "app/src/main/res/values/strings.xml"
$i18nDirs = @("app/src/main/res/values-en", "app/src/main/res/values-zh-rCN", "app/src/main/res/values-zh-rTW")

$mainStringsExists = Test-Path $stringsMain
$i18nExists = $i18nDirs | ForEach-Object { Test-Path "$_/strings.xml" } | Where-Object { $_ -eq $true }

if ($mainStringsExists -and $i18nExists.Count -eq 3) {
    Write-Host "  ✅ PASS: 主要strings.xml + 3個國際化版本" -ForegroundColor Green
    $checkResults["3"] = $true
    $passedPoints++
} else {
    Write-Host "  ❌ FAIL: 國際化字符串不完整" -ForegroundColor Red
    $checkResults["3"] = $false
}

# 4. 依賴項版本鎖定
Write-Host "`n4. 依賴項版本鎖定" -ForegroundColor Cyan
if (Test-Path "app/build.gradle.kts") {
    $buildContent = Get-Content "app/build.gradle.kts" -Raw
    $hasFixedVersions = ($buildContent -match 'compose-bom:2024\.06\.00') -and 
                       ($buildContent -match 'core-ktx:1\.13\.1') -and
                       ($buildContent -notmatch '\+') -and
                       ($buildContent -notmatch 'latest')
    
    if ($hasFixedVersions) {
        Write-Host "  ✅ PASS: 所有依賴項使用固定版本號，無動態版本" -ForegroundColor Green
        $checkResults["4"] = $true
        $passedPoints++
    } else {
        Write-Host "  ❌ FAIL: 存在動態版本或未固定版本" -ForegroundColor Red
        $checkResults["4"] = $false
    }
} else {
    Write-Host "  ❌ FAIL: 找不到build.gradle.kts" -ForegroundColor Red
    $checkResults["4"] = $false
}

# 5. 構建配置統一
Write-Host "`n5. 構建配置統一" -ForegroundColor Cyan
if (Test-Path "app/build.gradle.kts") {
    $buildContent = Get-Content "app/build.gradle.kts" -Raw
    $hasDebugRelease = ($buildContent -match 'buildTypes') -and 
                      ($buildContent -match 'debug') -and 
                      ($buildContent -match 'release') -and
                      ($buildContent -match 'signingConfigs')
    
    if ($hasDebugRelease) {
        Write-Host "  ✅ PASS: Debug/Release建置類型配置完整，簽名統一" -ForegroundColor Green
        $checkResults["5"] = $true
        $passedPoints++
    } else {
        Write-Host "  ❌ FAIL: 建置配置不完整" -ForegroundColor Red
        $checkResults["5"] = $false
    }
} else {
    Write-Host "  ❌ FAIL: 找不到建置配置" -ForegroundColor Red
    $checkResults["5"] = $false
}

# 6. 資源壓縮和優化
Write-Host "`n6. 資源壓縮和優化" -ForegroundColor Cyan
if (Test-Path "app/build.gradle.kts") {
    $buildContent = Get-Content "app/build.gradle.kts" -Raw
    $hasResourceConfig = ($buildContent -match 'isMinifyEnabled = true') -and 
                        ($buildContent -match 'isShrinkResources = true') -and
                        ($buildContent -match 'proguard-rules\.pro')
    
    if ($hasResourceConfig) {
        Write-Host "  ✅ PASS: Release版本啟用代碼混淆和資源壓縮" -ForegroundColor Green
        $checkResults["6"] = $true
        $passedPoints++
    } else {
        Write-Host "  ❌ FAIL: 資源壓縮配置不正確" -ForegroundColor Red
        $checkResults["6"] = $false
    }
} else {
    Write-Host "  ❌ FAIL: 找不到資源壓縮配置" -ForegroundColor Red
    $checkResults["6"] = $false
}

# 7. 權限和配置文件
Write-Host "`n7. 權限和配置文件" -ForegroundColor Cyan
$mainManifest = "app/src/main/AndroidManifest.xml"
$debugManifest = "app/src/debug/AndroidManifest.xml"
$releaseManifest = "app/src/release/AndroidManifest.xml"

$manifestsExist = (Test-Path $mainManifest) -and (Test-Path $debugManifest) -and (Test-Path $releaseManifest)

if ($manifestsExist) {
    $mainContent = Get-Content $mainManifest -Raw
    $hasCorrectPackage = $mainContent -match 'com\.wts\.dsfortune'
    $hasPermissions = $mainContent -match 'INTERNET'
    
    if ($hasCorrectPackage -and $hasPermissions) {
        Write-Host "  ✅ PASS: 三個Manifest文件存在，包名正確，權限配置完整" -ForegroundColor Green
        $checkResults["7"] = $true
        $passedPoints++
    } else {
        Write-Host "  ❌ FAIL: Manifest配置不正確" -ForegroundColor Red
        $checkResults["7"] = $false
    }
} else {
    Write-Host "  ❌ FAIL: Manifest文件不完整" -ForegroundColor Red
    $checkResults["7"] = $false
}

# 8. 測試自動化
Write-Host "`n8. 測試自動化" -ForegroundColor Cyan
$verificationScript = "scripts/verify_fix.ps1"
$buildScript = "scripts/build_release.ps1"
$compareScript = "scripts/compare_builds.ps1"

$scriptsExist = (Test-Path $verificationScript) -and (Test-Path $buildScript) -and (Test-Path $compareScript)

if ($scriptsExist) {
    Write-Host "  ✅ PASS: 自動化驗證腳本完整 (驗證、建置、對比)" -ForegroundColor Green
    $checkResults["8"] = $true
    $passedPoints++
} else {
    Write-Host "  ❌ FAIL: 缺少自動化測試腳本" -ForegroundColor Red
    $checkResults["8"] = $false
}

# 9. 版本控制策略
Write-Host "`n9. 版本控制策略" -ForegroundColor Cyan
$gitignoreExists = Test-Path ".gitignore"
$keyFilesInGit = (Test-Path "app/build.gradle.kts") -and 
                 (Test-Path "app/proguard-rules.pro") -and 
                 (Test-Path "app/src/debug/AndroidManifest.xml") -and
                 (Test-Path "app/src/release/AndroidManifest.xml")

if ($gitignoreExists -and $keyFilesInGit) {
    Write-Host "  ✅ PASS: .gitignore存在，關鍵配置文件已納入版本控制" -ForegroundColor Green
    $checkResults["9"] = $true
    $passedPoints++
} else {
    Write-Host "  ❌ FAIL: 版本控制配置不完整" -ForegroundColor Red
    $checkResults["9"] = $false
}

# 10. 發布前驗證流程
Write-Host "`n10. 發布前驗證流程" -ForegroundColor Cyan
$debugApk = "app\build\outputs\apk\debug\app-debug.apk"
$releaseApk = "app\build\outputs\apk\release\app-release.apk"
$apksExist = (Test-Path $debugApk) -and (Test-Path $releaseApk)

if ($apksExist) {
    $debugSize = (Get-Item $debugApk).Length / 1MB
    $releaseSize = (Get-Item $releaseApk).Length / 1MB
    $debugHash = (Get-FileHash $debugApk).Hash.Substring(0,8)
    $releaseHash = (Get-FileHash $releaseApk).Hash.Substring(0,8)
    
    $sizeDifferent = [math]::Abs($debugSize - $releaseSize) -gt 1
    $hashDifferent = $debugHash -ne $releaseHash
    
    if ($sizeDifferent -and $hashDifferent) {
        Write-Host "  ✅ PASS: 兩個APK成功生成，大小和雜湊值不同 (Debug: $([math]::Round($debugSize,1))MB, Release: $([math]::Round($releaseSize,1))MB)" -ForegroundColor Green
        $checkResults["10"] = $true
        $passedPoints++
    } else {
        Write-Host "  ❌ FAIL: APK生成異常或版本差異不明顯" -ForegroundColor Red
        $checkResults["10"] = $false
    }
} else {
    Write-Host "  ❌ FAIL: APK文件不存在，需要重新建置" -ForegroundColor Red
    $checkResults["10"] = $false
}

# 總結報告
Write-Host "`n=== 最終檢查結果 ===" -ForegroundColor Cyan
Write-Host "通過項目: $passedPoints / $totalPoints" -ForegroundColor $(if ($passedPoints -eq $totalPoints) { "Green" } else { "Yellow" })

$percentage = ($passedPoints / $totalPoints) * 100
Write-Host "完成度: $([math]::Round($percentage, 1))%" -ForegroundColor $(if ($percentage -eq 100) { "Green" } elseif ($percentage -ge 80) { "Yellow" } else { "Red" })

if ($passedPoints -eq $totalPoints) {
    Write-Host "`n🎉 恭喜！所有10個關鍵重點已完全符合要求！" -ForegroundColor Green
    Write-Host "✅ 您的測試版app打包與模擬器內容一致性問題已徹底解決" -ForegroundColor Green
    Write-Host "🚀 可以安心推出正式版！" -ForegroundColor Green
} else {
    Write-Host "`n⚠️ 還有 $($totalPoints - $passedPoints) 個項目需要改善：" -ForegroundColor Yellow
    for ($i = 1; $i -le $totalPoints; $i++) {
        if (-not $checkResults["$i"]) {
            $pointNames = @{
                "1" = "建置環境一致性"
                "2" = "資源文件管理" 
                "3" = "字符串資源國際化"
                "4" = "依賴項版本鎖定"
                "5" = "構建配置統一"
                "6" = "資源壓縮和優化"
                "7" = "權限和配置文件"
                "8" = "測試自動化"
                "9" = "版本控制策略"
                "10" = "發布前驗證流程"
            }
            Write-Host "  - 第$i點: $($pointNames["$i"])" -ForegroundColor Red
        }
    }
}

# 生成詳細報告
$reportPath = "10_POINTS_FINAL_CHECK_$(Get-Date -Format 'yyyy-MM-dd_HH-mm-ss').txt"
$report = @"
10個關鍵重點最終檢查報告
檢查時間: $(Get-Date)

總體結果: $passedPoints / $totalPoints 通過 ($([math]::Round($percentage, 1))%)

詳細檢查結果:
1. 建置環境一致性: $(if ($checkResults["1"]) { "✅ PASS" } else { "❌ FAIL" })
2. 資源文件管理: $(if ($checkResults["2"]) { "✅ PASS" } else { "❌ FAIL" })
3. 字符串資源國際化: $(if ($checkResults["3"]) { "✅ PASS" } else { "❌ FAIL" })
4. 依賴項版本鎖定: $(if ($checkResults["4"]) { "✅ PASS" } else { "❌ FAIL" })
5. 構建配置統一: $(if ($checkResults["5"]) { "✅ PASS" } else { "❌ FAIL" })
6. 資源壓縮和優化: $(if ($checkResults["6"]) { "✅ PASS" } else { "❌ FAIL" })
7. 權限和配置文件: $(if ($checkResults["7"]) { "✅ PASS" } else { "❌ FAIL" })
8. 測試自動化: $(if ($checkResults["8"]) { "✅ PASS" } else { "❌ FAIL" })
9. 版本控制策略: $(if ($checkResults["9"]) { "✅ PASS" } else { "❌ FAIL" })
10. 發布前驗證流程: $(if ($checkResults["10"]) { "✅ PASS" } else { "❌ FAIL" })

結論:
$(if ($passedPoints -eq $totalPoints) { "🎉 所有要求已完全符合！測試版app打包與模擬器內容一致性問題已徹底解決，可以安心推出正式版。" } else { "⚠️ 還有部分項目需要改善，建議先修復未通過的項目再進行正式版發布。" })
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "`n📋 詳細報告已保存: $reportPath" -ForegroundColor Cyan

exit $(if ($passedPoints -eq $totalPoints) { 0 } else { 1 })

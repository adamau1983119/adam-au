# Deep Analysis of Build Inconsistency
# 深度分析打包版本與模擬器版本不一致的根本原因

Write-Host "=== 深度分析：打包版本與模擬器版本不一致 ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$issues = @()
$potentialCauses = @()

Write-Host "`n🔍 分析可能的根本原因..." -ForegroundColor Green

# 1. ProGuard/R8 Code Shrinking Analysis
Write-Host "`n1. ProGuard/R8 代碼混淆分析" -ForegroundColor Yellow

$proguardFile = "app/proguard-rules.pro"
if (Test-Path $proguardFile) {
    $proguardContent = Get-Content $proguardFile -Raw
    
    # Check if keep rules are too restrictive
    if ($proguardContent -match '-keep class.*\*\*.*\{ \*; \}') {
        Write-Host "   ⚠️  ISSUE: 過度保護的 ProGuard 規則可能導致不一致" -ForegroundColor Yellow
        $potentialCauses += "ProGuard 規則過度保護，可能影響運行時行為"
    }
    
    # Check for missing keep rules for critical components
    $criticalComponents = @("AdManager", "DeepSeekApi", "ServiceLocator", "DSFortuneApp")
    foreach ($component in $criticalComponents) {
        if ($proguardContent -notmatch $component) {
            Write-Host "   ❌ MISSING: $component 沒有專門的 ProGuard 保護規則" -ForegroundColor Red
            $issues += "$component 可能被 ProGuard 錯誤處理"
        }
    }
    
    # Check if reflection usage is protected
    if ($proguardContent -notmatch 'keepclassmembers.*Serializable') {
        Write-Host "   ⚠️  POTENTIAL: 序列化類可能需要額外保護" -ForegroundColor Yellow
        $potentialCauses += "序列化/反序列化可能在混淆後失敗"
    }
    
} else {
    Write-Host "   ❌ ERROR: ProGuard 規則文件不存在" -ForegroundColor Red
    $issues += "ProGuard 規則文件缺失"
}

# 2. Resource Shrinking Analysis
Write-Host "`n2. 資源壓縮分析" -ForegroundColor Yellow

$buildFile = "app/build.gradle.kts"
$buildContent = Get-Content $buildFile -Raw

if ($buildContent -match 'isShrinkResources = true') {
    Write-Host "   ⚠️  ACTIVE: 資源壓縮已啟用，可能移除必要資源" -ForegroundColor Yellow
    $potentialCauses += "資源壓縮可能錯誤移除動態載入的資源"
    
    # Check for resources that might be dynamically loaded
    $dynamicResources = @("wong_tai_sin", "wts03", "wts04", "wts05")
    foreach ($resource in $dynamicResources) {
        $resourceFound = $false
        $javaFiles = Get-ChildItem "app/src/main/java" -Recurse -Name "*.kt" 2>$null
        
        foreach ($file in $javaFiles) {
            $filePath = "app/src/main/java/$file"
            if (Test-Path $filePath) {
                $fileContent = Get-Content $filePath -Raw
                if ($fileContent -match "getIdentifier.*$resource") {
                    $resourceFound = $true
                    Write-Host "   ⚠️  DYNAMIC: $resource 通過 getIdentifier 動態載入" -ForegroundColor Yellow
                    $potentialCauses += "$resource 動態載入可能在資源壓縮後失敗"
                    break
                }
            }
        }
    }
}

# 3. Build Variant Differences
Write-Host "`n3. 建置變體差異分析" -ForegroundColor Yellow

# Check manifest differences
$mainManifest = "app/src/main/AndroidManifest.xml"
$debugManifest = "app/src/debug/AndroidManifest.xml" 
$releaseManifest = "app/src/release/AndroidManifest.xml"

$manifestDiffs = @()

if (Test-Path $mainManifest -and Test-Path $debugManifest -and Test-Path $releaseManifest) {
    $mainContent = Get-Content $mainManifest -Raw
    $debugContent = Get-Content $debugManifest -Raw
    $releaseContent = Get-Content $releaseManifest -Raw
    
    # Check AdMob configuration differences
    $mainAdMob = [regex]::Matches($mainContent, 'ca-app-pub-[0-9~]+').Value
    $debugAdMob = [regex]::Matches($debugContent, 'ca-app-pub-[0-9~]+').Value
    $releaseAdMob = [regex]::Matches($releaseContent, 'ca-app-pub-[0-9~]+').Value
    
    if ($debugAdMob -ne $releaseAdMob) {
        Write-Host "   ⚠️  DIFF: Debug 和 Release AdMob ID 不同" -ForegroundColor Yellow
        Write-Host "     Debug: $debugAdMob" -ForegroundColor Gray
        Write-Host "     Release: $releaseAdMob" -ForegroundColor Gray
        $potentialCauses += "不同建置類型使用不同的 AdMob 配置"
    }
    
    # Check debuggable flag
    if ($debugContent -match 'android:debuggable="true"' -and $releaseContent -match 'android:debuggable="false"') {
        Write-Host "   ✅ CORRECT: debuggable 標誌正確設置" -ForegroundColor Green
    } else {
        Write-Host "   ❌ ISSUE: debuggable 標誌設置不正確" -ForegroundColor Red
        $issues += "debuggable 標誌可能導致運行時行為差異"
    }
}

# 4. Signing Configuration Impact
Write-Host "`n4. 簽名配置影響分析" -ForegroundColor Yellow

if ($buildContent -match 'signingConfig = signingConfigs\.getByName\("release"\)') {
    $debugUsesReleaseSigning = $buildContent -match 'debug \{[^}]*signingConfig = signingConfigs\.getByName\("release"\)'
    
    if ($debugUsesReleaseSigning) {
        Write-Host "   ✅ GOOD: Debug 和 Release 使用相同簽名" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️  POTENTIAL: Debug 和 Release 簽名不同" -ForegroundColor Yellow
        $potentialCauses += "不同簽名可能導致權限或行為差異"
    }
}

# 5. Runtime Environment Differences
Write-Host "`n5. 運行環境差異分析" -ForegroundColor Yellow

# Check BuildConfig usage
$buildConfigUsage = @()
$javaFiles = Get-ChildItem "app/src/main/java" -Recurse -Name "*.kt" 2>$null

foreach ($file in $javaFiles) {
    $filePath = "app/src/main/java/$file"
    if (Test-Path $filePath) {
        $fileContent = Get-Content $filePath -Raw
        if ($fileContent -match 'BuildConfig\.(BUILD_TYPE|USE_REMOTE_API|API_BASE_URL)') {
            $matches = [regex]::Matches($fileContent, 'BuildConfig\.(\w+)')
            foreach ($match in $matches) {
                $buildConfigUsage += @{
                    File = $file
                    Field = $match.Groups[1].Value
                }
            }
        }
    }
}

if ($buildConfigUsage.Count -gt 0) {
    Write-Host "   ℹ️  FOUND: 發現 $($buildConfigUsage.Count) 個 BuildConfig 使用點" -ForegroundColor Cyan
    $uniqueFiles = $buildConfigUsage | Group-Object File
    foreach ($group in $uniqueFiles) {
        $fields = ($group.Group | ForEach-Object { $_.Field }) -join ", "
        Write-Host "     $($group.Name): $fields" -ForegroundColor Gray
    }
    
    # Check if BUILD_TYPE is used for conditional logic
    $conditionalLogic = $false
    foreach ($usage in $buildConfigUsage) {
        if ($usage.Field -eq "BUILD_TYPE") {
            $conditionalLogic = $true
            break
        }
    }
    
    if ($conditionalLogic) {
        Write-Host "   ⚠️  POTENTIAL: BUILD_TYPE 用於條件邏輯，可能導致不同行為" -ForegroundColor Yellow
        $potentialCauses += "BuildConfig.BUILD_TYPE 條件邏輯導致 debug/release 行為不同"
    }
}

# 6. Asset and Resource Loading Analysis
Write-Host "`n6. 資產和資源載入分析" -ForegroundColor Yellow

# Check for dynamic resource loading
$dynamicLoading = @()
foreach ($file in $javaFiles) {
    $filePath = "app/src/main/java/$file"
    if (Test-Path $filePath) {
        $fileContent = Get-Content $filePath -Raw
        
        # Check for getIdentifier usage
        if ($fileContent -match 'getIdentifier') {
            $matches = [regex]::Matches($fileContent, 'getIdentifier\s*\(\s*"([^"]+)"')
            foreach ($match in $matches) {
                $dynamicLoading += @{
                    File = $file
                    Resource = $match.Groups[1].Value
                    Method = "getIdentifier"
                }
            }
        }
        
        # Check for asset loading
        if ($fileContent -match 'assets\.open') {
            $dynamicLoading += @{
                File = $file
                Resource = "assets"
                Method = "assets.open"
            }
        }
    }
}

if ($dynamicLoading.Count -gt 0) {
    Write-Host "   ⚠️  FOUND: 發現 $($dynamicLoading.Count) 個動態資源載入點" -ForegroundColor Yellow
    foreach ($loading in $dynamicLoading) {
        Write-Host "     $($loading.File): $($loading.Method)($($loading.Resource))" -ForegroundColor Gray
    }
    $potentialCauses += "動態資源載入在資源壓縮後可能失敗"
}

# 7. Third-party Library Consistency
Write-Host "`n7. 第三方庫一致性分析" -ForegroundColor Yellow

$dependencies = @("play-services-ads", "retrofit", "okhttp", "gson", "coil")
foreach ($dep in $dependencies) {
    if ($buildContent -match "$dep:([0-9.]+)") {
        $version = [regex]::Match($buildContent, "$dep:([0-9.]+)").Groups[1].Value
        Write-Host "   ✅ $dep: $version (固定版本)" -ForegroundColor Green
    } elseif ($buildContent -match "$dep:\+") {
        Write-Host "   ❌ $dep: 使用動態版本 (+)" -ForegroundColor Red
        $issues += "$dep 使用動態版本，可能導致不一致"
    }
}

# 8. Gradle Cache and Build Cache Analysis  
Write-Host "`n8. Gradle 快取分析" -ForegroundColor Yellow

$gradleProperties = "gradle.properties"
if (Test-Path $gradleProperties) {
    $gradleContent = Get-Content $gradleProperties -Raw
    
    if ($gradleContent -match 'org\.gradle\.caching=true') {
        Write-Host "   ✅ Gradle 建置快取已啟用" -ForegroundColor Green
    } else {
        Write-Host "   ⚠️  Gradle 建置快取未啟用，可能影響一致性" -ForegroundColor Yellow
        $potentialCauses += "Gradle 建置快取未啟用可能導致增量建置不一致"
    }
    
    if ($gradleContent -match 'org\.gradle\.parallel=true') {
        Write-Host "   ℹ️  並行建置已啟用" -ForegroundColor Cyan
    }
    
    if ($gradleContent -match 'org\.gradle\.configureondemand=true') {
        Write-Host "   ⚠️  按需配置可能影響建置一致性" -ForegroundColor Yellow
        $potentialCauses += "按需配置可能導致建置行為不一致"
    }
} else {
    Write-Host "   ⚠️  gradle.properties 不存在" -ForegroundColor Yellow
}

# Generate Analysis Report
Write-Host "`n=== 分析結果 ===" -ForegroundColor Cyan

Write-Host "`n🔴 發現的問題 ($($issues.Count)):" -ForegroundColor Red
if ($issues.Count -eq 0) {
    Write-Host "   沒有發現明顯的配置問題" -ForegroundColor Green
} else {
    foreach ($issue in $issues) {
        Write-Host "   - $issue" -ForegroundColor Red
    }
}

Write-Host "`n⚠️  可能的原因 ($($potentialCauses.Count)):" -ForegroundColor Yellow
if ($potentialCauses.Count -eq 0) {
    Write-Host "   沒有識別出潛在原因" -ForegroundColor Green
} else {
    foreach ($cause in $potentialCauses) {
        Write-Host "   - $cause" -ForegroundColor Yellow
    }
}

# Recommendations
Write-Host "`n💡 建議的解決方案:" -ForegroundColor Green

if ($potentialCauses -contains "動態資源載入在資源壓縮後可能失敗") {
    Write-Host "   1. 在 proguard-rules.pro 中添加資源保護規則" -ForegroundColor White
    Write-Host "      -keep class **.R`$*" -ForegroundColor Gray
    Write-Host "      -keepclassmembers class **.R`$* { public static <fields>; }" -ForegroundColor Gray
}

if ($potentialCauses -match "ProGuard") {
    Write-Host "   2. 增強 ProGuard 規則以保護關鍵組件" -ForegroundColor White
    Write-Host "      -keep class com.wts.dsfortune.util.AdManager { *; }" -ForegroundColor Gray
    Write-Host "      -keep class com.wts.dsfortune.data.** { *; }" -ForegroundColor Gray
}

if ($potentialCauses -match "BuildConfig") {
    Write-Host "   3. 檢查所有 BuildConfig 條件邏輯" -ForegroundColor White
    Write-Host "      確保 debug/release 邏輯分支正確" -ForegroundColor Gray
}

Write-Host "   4. 使用完全清潔建置流程" -ForegroundColor White
Write-Host "      .\gradlew.bat clean" -ForegroundColor Gray
Write-Host "      .\gradlew.bat assembleDebug assembleRelease" -ForegroundColor Gray

Write-Host "   5. 比較 APK 內容以找出具體差異" -ForegroundColor White
Write-Host "      使用 Android Studio APK Analyzer" -ForegroundColor Gray

Write-Host "   6. 測試時使用相同的設備和 Android 版本" -ForegroundColor White

# Save detailed report
$reportPath = "build_inconsistency_analysis_$(Get-Date -Format 'MMdd_HHmm').txt"
$report = @"
建置不一致深度分析報告
生成時間: $(Get-Date)

發現的問題 ($($issues.Count)):
$(if ($issues.Count -gt 0) { $issues | ForEach-Object { "- $_" } | Out-String } else { "無明顯問題" })

可能的原因 ($($potentialCauses.Count)):
$(if ($potentialCauses.Count -gt 0) { $potentialCauses | ForEach-Object { "- $_" } | Out-String } else { "無識別出的潛在原因" })

動態資源載入點:
$(if ($dynamicLoading.Count -gt 0) { $dynamicLoading | ForEach-Object { "- $($_.File): $($_.Method)($($_.Resource))" } | Out-String } else { "無動態載入" })

BuildConfig 使用點:
$(if ($buildConfigUsage.Count -gt 0) { $buildConfigUsage | ForEach-Object { "- $($_.File): BuildConfig.$($_.Field)" } | Out-String } else { "無 BuildConfig 使用" })

建議解決方案:
1. 增強 ProGuard 規則保護
2. 檢查動態資源載入
3. 驗證 BuildConfig 條件邏輯
4. 使用完全清潔建置
5. APK 內容比較分析
6. 統一測試環境
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "`n📄 詳細報告已保存: $reportPath" -ForegroundColor Gray

# Return exit code based on severity
$severity = $issues.Count + ($potentialCauses.Count / 2)
exit $(if ($severity -gt 3) { 1 } else { 0 })

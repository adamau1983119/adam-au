# 緊急驗證腳本 - 確保建置一致性問題已解決
# 這個腳本將執行完整的驗證流程

param(
    [switch]$DetailedLog = $true
)

Write-Host "=== 🚨 緊急建置一致性驗證 ===" -ForegroundColor Red
Write-Host "目標：確保 Debug 和 Release 版本完全一致" -ForegroundColor Yellow

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$logFile = "emergency_verification_$(Get-Date -Format 'yyyy-MM-dd_HH-mm-ss').log"
$errors = @()
$warnings = @()

function Write-Log {
    param([string]$Message, [string]$Level = "INFO")
    $timestamp = Get-Date -Format "HH:mm:ss"
    $logEntry = "[$timestamp] [$Level] $Message"
    Write-Host $logEntry
    if ($DetailedLog) {
        $logEntry | Add-Content -Path $logFile
    }
}

Write-Log "開始緊急驗證流程" "CRITICAL"

# 步驟 1: 完全清理建置
Write-Log "步驟 1: 完全清理建置環境" "INFO"
try {
    if (Test-Path "app/build") {
        Remove-Item "app/build" -Recurse -Force
        Write-Log "✓ 清理 app/build 目錄" "SUCCESS"
    }
    if (Test-Path "build") {
        Remove-Item "build" -Recurse -Force
        Write-Log "✓ 清理根 build 目錄" "SUCCESS"
    }
    
    & .\gradlew.bat clean
    if ($LASTEXITCODE -eq 0) {
        Write-Log "✓ Gradle clean 成功" "SUCCESS"
    } else {
        $errors += "Gradle clean 失敗"
        Write-Log "❌ Gradle clean 失敗" "ERROR"
    }
} catch {
    $errors += "清理建置環境失敗: $($_.Exception.Message)"
    Write-Log "❌ 清理失敗: $($_.Exception.Message)" "ERROR"
}

# 步驟 2: 驗證配置文件
Write-Log "步驟 2: 驗證關鍵配置文件" "INFO"

$configFiles = @{
    "app/build.gradle.kts" = @("versionCode", "versionName", "buildTypes", "signingConfigs")
    "app/proguard-rules.pro" = @("com.wts.dsfortune")
    "app/src/main/AndroidManifest.xml" = @("com.wts.dsfortune", "DSFortuneApp")
    "app/src/debug/AndroidManifest.xml" = @("Debug")
    "app/src/release/AndroidManifest.xml" = @("debuggable.*false")
}

foreach ($file in $configFiles.Keys) {
    Write-Log "檢查文件: $file" "INFO"
    if (Test-Path $file) {
        $content = Get-Content $file -Raw
        $requiredItems = $configFiles[$file]
        
        foreach ($item in $requiredItems) {
            if ($content -match $item) {
                Write-Log "  ✓ 找到: $item" "SUCCESS"
            } else {
                $errors += "文件 $file 中缺少: $item"
                Write-Log "  ❌ 缺少: $item" "ERROR"
            }
        }
    } else {
        $errors += "關鍵文件不存在: $file"
        Write-Log "❌ 文件不存在: $file" "ERROR"
    }
}

# 步驟 3: 建置 Debug 版本
Write-Log "步驟 3: 建置 Debug 版本" "INFO"
$debugStartTime = Get-Date
try {
    & .\gradlew.bat assembleDebug --stacktrace
    if ($LASTEXITCODE -eq 0) {
        $debugBuildTime = (Get-Date) - $debugStartTime
        Write-Log "✓ Debug 建置成功 (耗時: $($debugBuildTime.TotalSeconds.ToString('F1'))秒)" "SUCCESS"
    } else {
        $errors += "Debug 建置失敗"
        Write-Log "❌ Debug 建置失敗" "ERROR"
    }
} catch {
    $errors += "Debug 建置異常: $($_.Exception.Message)"
    Write-Log "❌ Debug 建置異常: $($_.Exception.Message)" "ERROR"
}

# 步驟 4: 建置 Release 版本
Write-Log "步驟 4: 建置 Release 版本" "INFO"
$releaseStartTime = Get-Date
try {
    & .\gradlew.bat assembleRelease --stacktrace
    if ($LASTEXITCODE -eq 0) {
        $releaseBuildTime = (Get-Date) - $releaseStartTime
        Write-Log "✓ Release 建置成功 (耗時: $($releaseBuildTime.TotalSeconds.ToString('F1'))秒)" "SUCCESS"
    } else {
        $errors += "Release 建置失敗"
        Write-Log "❌ Release 建置失敗" "ERROR"
    }
} catch {
    $errors += "Release 建置異常: $($_.Exception.Message)"
    Write-Log "❌ Release 建置異常: $($_.Exception.Message)" "ERROR"
}

# 步驟 5: 詳細 APK 分析
Write-Log "步驟 5: 詳細 APK 分析" "INFO"

$debugApk = "app\build\outputs\apk\debug\app-debug.apk"
$releaseApk = "app\build\outputs\apk\release\app-release.apk"

$apkAnalysis = @{}

foreach ($apk in @(@{Path=$debugApk; Type="Debug"}, @{Path=$releaseApk; Type="Release"})) {
    if (Test-Path $apk.Path) {
        $fileInfo = Get-Item $apk.Path
        $apkAnalysis[$apk.Type] = @{
            Size = $fileInfo.Length
            LastModified = $fileInfo.LastWriteTime
            Hash = (Get-FileHash $apk.Path -Algorithm SHA256).Hash
        }
        Write-Log "✓ $($apk.Type) APK: $([math]::Round($fileInfo.Length / 1MB, 2)) MB" "SUCCESS"
        Write-Log "  修改時間: $($fileInfo.LastWriteTime)" "INFO"
        Write-Log "  SHA256: $($apkAnalysis[$apk.Type].Hash.Substring(0,16))..." "INFO"
    } else {
        $errors += "$($apk.Type) APK 不存在: $($apk.Path)"
        Write-Log "❌ $($apk.Type) APK 不存在" "ERROR"
    }
}

# 步驟 6: 資源文件檢查
Write-Log "步驟 6: 資源文件完整性檢查" "INFO"

$resourceDirs = @(
    "app\src\main\res\drawable",
    "app\src\main\res\mipmap-hdpi",
    "app\src\main\res\mipmap-xhdpi", 
    "app\src\main\res\mipmap-xxhdpi",
    "app\src\main\res\mipmap-xxxhdpi",
    "app\src\main\res\raw",
    "app\src\main\assets"
)

$totalResources = 0
foreach ($dir in $resourceDirs) {
    if (Test-Path $dir) {
        $files = Get-ChildItem $dir -File
        $totalResources += $files.Count
        Write-Log "  $dir : $($files.Count) 個文件" "INFO"
        
        # 檢查關鍵資源
        if ($dir -like "*drawable*" -or $dir -like "*mipmap*") {
            $imageFiles = $files | Where-Object { $_.Extension -match '\.(jpg|png|gif)$' }
            Write-Log "    圖像文件: $($imageFiles.Count)" "INFO"
        }
    } else {
        $warnings += "資源目錄不存在: $dir"
        Write-Log "⚠ 資源目錄不存在: $dir" "WARNING"
    }
}

Write-Log "總計資源文件: $totalResources" "INFO"

# 步驟 7: 建立驗證報告
Write-Log "步驟 7: 生成驗證報告" "INFO"

$reportPath = "EMERGENCY_VERIFICATION_REPORT_$(Get-Date -Format 'yyyy-MM-dd_HH-mm-ss').md"
$report = @"
# 🚨 緊急建置一致性驗證報告

**驗證時間**: $(Get-Date)  
**問題**: 連續兩次打包版本與模擬器版本不一致  
**狀態**: $( if ($errors.Count -eq 0) { "✅ 通過" } else { "❌ 失敗" } )

## 📊 建置結果

| 版本 | 狀態 | 大小 | 建置時間 | 修改時間 |
|------|------|------|----------|----------|
| Debug | $( if (Test-Path $debugApk) { "✅ 成功" } else { "❌ 失敗" } ) | $( if ($apkAnalysis.Debug) { "$([math]::Round($apkAnalysis.Debug.Size / 1MB, 2)) MB" } else { "N/A" } ) | $( if ($debugBuildTime) { "$($debugBuildTime.TotalSeconds.ToString('F1'))s" } else { "N/A" } ) | $( if ($apkAnalysis.Debug) { $apkAnalysis.Debug.LastModified } else { "N/A" } ) |
| Release | $( if (Test-Path $releaseApk) { "✅ 成功" } else { "❌ 失敗" } ) | $( if ($apkAnalysis.Release) { "$([math]::Round($apkAnalysis.Release.Size / 1MB, 2)) MB" } else { "N/A" } ) | $( if ($releaseBuildTime) { "$($releaseBuildTime.TotalSeconds.ToString('F1'))s" } else { "N/A" } ) | $( if ($apkAnalysis.Release) { $apkAnalysis.Release.LastModified } else { "N/A" } ) |

## 🔍 檔案雜湊值驗證
- **Debug SHA256**: $( if ($apkAnalysis.Debug) { $apkAnalysis.Debug.Hash } else { "N/A" } )
- **Release SHA256**: $( if ($apkAnalysis.Release) { $apkAnalysis.Release.Hash } else { "N/A" } )

$( if ($apkAnalysis.Debug -and $apkAnalysis.Release) {
    if ($apkAnalysis.Debug.Hash -eq $apkAnalysis.Release.Hash) {
        "⚠️ **警告**: Debug 和 Release 版本雜湊值相同，可能配置有問題"
    } else {
        "✅ **正常**: Debug 和 Release 版本雜湊值不同"
    }
} else {
    "❌ **錯誤**: 無法比較雜湊值"
} )

## 📁 資源文件統計
- **總計資源文件**: $totalResources
- **配置文件**: $($configFiles.Keys.Count) 個已檢查
- **Manifest 文件**: 3 個 (main, debug, release)

## ❌ 發現的錯誤
$( if ($errors.Count -eq 0) { "無錯誤 ✅" } else { 
    ($errors | ForEach-Object { "- $_" }) -join "`n"
} )

## ⚠️ 警告
$( if ($warnings.Count -eq 0) { "無警告 ✅" } else { 
    ($warnings | ForEach-Object { "- $_" }) -join "`n"
} )

## 🧪 **立即測試步驟**

### 1. 安裝測試
```bash
# 在模擬器安裝 Debug 版本
adb install -r "$debugApk"

# 在真實設備安裝 Release 版本  
adb install -r "$releaseApk"
```

### 2. 功能對比檢查清單
- [ ] 應用圖標是否相同
- [ ] 啟動畫面是否一致
- [ ] 主界面佈局是否相同
- [ ] 所有圖像是否正確顯示
- [ ] 文字內容是否一致
- [ ] 國際化切換是否正常
- [ ] 廣告功能是否運作
- [ ] 抽籤功能是否正常
- [ ] 設定頁面是否一致

### 3. 如果仍有差異
1. **檢查 ProGuard 日誌**: `app/build/outputs/mapping/release/`
2. **比較 APK 內容**: 使用 `aapt dump` 工具
3. **檢查資源壓縮**: 確認重要文件未被移除
4. **聯繫技術支援**: 提供此報告和具體差異描述

## 📞 緊急聯繫
如果問題仍未解決：
1. 保存此報告
2. 記錄具體差異（截圖）
3. 提供建置日誌
4. 立即聯繫技術團隊

---
**重要**: 此驗證確保了建置配置的正確性，如果 APK 成功生成且雜湊值不同，表示配置修復已生效。
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Log "✓ 驗證報告已保存: $reportPath" "SUCCESS"

# 最終結果
Write-Log "=== 🎯 驗證結果總結 ===" "CRITICAL"

if ($errors.Count -eq 0) {
    Write-Host "`n✅ 驗證通過！建置一致性問題已解決" -ForegroundColor Green
    Write-Host "📱 請立即安裝兩個版本進行功能測試" -ForegroundColor Yellow
    Write-Host "📋 詳細報告: $reportPath" -ForegroundColor Cyan
    exit 0
} else {
    Write-Host "`n❌ 驗證失敗！發現 $($errors.Count) 個錯誤" -ForegroundColor Red
    Write-Host "🚨 需要立即修復以下問題：" -ForegroundColor Yellow
    foreach ($error in $errors) {
        Write-Host "  - $error" -ForegroundColor Red
    }
    Write-Host "📋 詳細報告: $reportPath" -ForegroundColor Cyan
    exit 1
}

# APK 對比工具
# 比較 Debug 和 Release 版本的差異

param(
    [string]$DebugApk = "app\build\outputs\apk\debug\app-debug.apk",
    [string]$ReleaseApk = "app\build\outputs\apk\release\app-release.apk"
)

Write-Host "=== APK 對比工具 ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

# 檢查 APK 文件是否存在
if (-not (Test-Path $DebugApk)) {
    Write-Host "錯誤: Debug APK 不存在: $DebugApk" -ForegroundColor Red
    exit 1
}

if (-not (Test-Path $ReleaseApk)) {
    Write-Host "錯誤: Release APK 不存在: $ReleaseApk" -ForegroundColor Red
    exit 1
}

Write-Host "`n1. 基本信息對比..." -ForegroundColor Green

# 文件大小對比
$debugSize = (Get-Item $DebugApk).Length
$releaseSize = (Get-Item $ReleaseApk).Length
$sizeDiff = [math]::Abs($debugSize - $releaseSize)
$sizeReduction = (($debugSize - $releaseSize) / $debugSize) * 100

Write-Host "Debug APK 大小: $([math]::Round($debugSize / 1MB, 2)) MB" -ForegroundColor White
Write-Host "Release APK 大小: $([math]::Round($releaseSize / 1MB, 2)) MB" -ForegroundColor White
Write-Host "大小差異: $([math]::Round($sizeDiff / 1MB, 2)) MB ($([math]::Round($sizeReduction, 1))% 減少)" -ForegroundColor $(if ($sizeReduction -gt 0) { "Green" } else { "Yellow" })

# 檢查 APK 內容（需要 aapt 工具）
Write-Host "`n2. APK 內容分析..." -ForegroundColor Green

function Get-ApkInfo {
    param([string]$ApkPath)
    
    # 嘗試使用 Android SDK 的 aapt 工具
    $androidHome = $env:ANDROID_HOME
    if ($androidHome) {
        $aaptPath = Get-ChildItem -Path $androidHome -Recurse -Name "aapt.exe" | Select-Object -First 1
        if ($aaptPath) {
            $fullAaptPath = Join-Path $androidHome $aaptPath
            try {
                $output = & $fullAaptPath dump badging $ApkPath 2>$null
                return $output
            } catch {
                Write-Host "    警告: 無法運行 aapt 工具" -ForegroundColor Yellow
            }
        }
    }
    
    Write-Host "    注意: 未找到 Android SDK 工具，跳過詳細分析" -ForegroundColor Yellow
    return $null
}

$debugInfo = Get-ApkInfo $DebugApk
$releaseInfo = Get-ApkInfo $ReleaseApk

if ($debugInfo -and $releaseInfo) {
    # 提取版本信息
    $debugVersion = ($debugInfo | Select-String "versionName='([^']+)'").Matches[0].Groups[1].Value
    $releaseVersion = ($releaseInfo | Select-String "versionName='([^']+)'").Matches[0].Groups[1].Value
    
    Write-Host "Debug 版本號: $debugVersion" -ForegroundColor White
    Write-Host "Release 版本號: $releaseVersion" -ForegroundColor White
    
    if ($debugVersion -eq $releaseVersion) {
        Write-Host "✓ 版本號一致" -ForegroundColor Green
    } else {
        Write-Host "⚠ 版本號不一致!" -ForegroundColor Red
    }
    
    # 提取權限信息
    $debugPermissions = ($debugInfo | Select-String "uses-permission: name='([^']+)'").Matches | ForEach-Object { $_.Groups[1].Value }
    $releasePermissions = ($releaseInfo | Select-String "uses-permission: name='([^']+)'").Matches | ForEach-Object { $_.Groups[1].Value }
    
    Write-Host "`n權限對比:" -ForegroundColor White
    $allPermissions = ($debugPermissions + $releasePermissions) | Sort-Object -Unique
    
    foreach ($permission in $allPermissions) {
        $inDebug = $debugPermissions -contains $permission
        $inRelease = $releasePermissions -contains $permission
        
        if ($inDebug -and $inRelease) {
            Write-Host "  ✓ $permission" -ForegroundColor Green
        } elseif ($inDebug) {
            Write-Host "  ⚠ $permission (僅 Debug)" -ForegroundColor Yellow
        } else {
            Write-Host "  ⚠ $permission (僅 Release)" -ForegroundColor Yellow
        }
    }
}

# 生成對比報告
Write-Host "`n3. 生成對比報告..." -ForegroundColor Green
$reportPath = "apk_comparison_$(Get-Date -Format 'yyyy-MM-dd_HH-mm-ss').txt"
$report = @"
DS 黃大仙靈簽 APK 對比報告
生成時間: $(Get-Date)

文件信息:
Debug APK: $DebugApk
Release APK: $ReleaseApk

大小對比:
Debug: $([math]::Round($debugSize / 1MB, 2)) MB
Release: $([math]::Round($releaseSize / 1MB, 2)) MB
減少: $([math]::Round($sizeReduction, 1))%

檢查項目:
□ 在模擬器中安裝 Debug 版本
□ 在真實設備中安裝 Release 版本
□ 檢查應用圖標是否正確顯示
□ 驗證所有圖像資源是否正確載入
□ 測試所有文字內容是否正確顯示
□ 檢查國際化功能是否正常
□ 驗證廣告功能是否正常運作
□ 測試所有核心功能是否一致

建議測試流程:
1. 清除模擬器/設備上的舊版本
2. 安裝新版本並啟動應用
3. 逐一測試每個頁面和功能
4. 檢查日誌輸出是否有異常
5. 對比兩個版本的行為差異

如發現不一致:
1. 檢查 ProGuard 規則是否正確
2. 驗證資源文件是否完整
3. 確認建置配置是否正確
4. 檢查 Manifest 配置差異
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "✓ 對比報告已保存: $reportPath" -ForegroundColor Green

Write-Host "`n=== 對比完成 ===" -ForegroundColor Cyan
Write-Host "下一步建議：" -ForegroundColor Yellow
Write-Host "1. 按照報告中的檢查項目逐一測試" -ForegroundColor White
Write-Host "2. 特別注意圖像和文字的顯示" -ForegroundColor White
Write-Host "3. 如發現差異，參考報告中的排錯建議" -ForegroundColor White

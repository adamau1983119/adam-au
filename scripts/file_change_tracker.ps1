# 檔案變更追蹤工具
# 確保實際檔案已更新，不只是表面更新

param(
    [switch]$CreateBaseline = $false,
    [switch]$ShowDetails = $true
)

Write-Host "=== 📁 檔案變更追蹤驗證 ===" -ForegroundColor Cyan
Write-Host "目標：確保檔案實際已更新，非表面更新" -ForegroundColor Yellow

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$baselineFile = "file_baseline.json"
$currentScan = @{}

# 要追蹤的關鍵檔案
$keyFiles = @(
    "app/build.gradle.kts",
    "app/proguard-rules.pro", 
    "app/src/main/AndroidManifest.xml",
    "app/src/debug/AndroidManifest.xml",
    "app/release/AndroidManifest.xml",
    "app/src/main/java/com/example/wtsaskingforsignature/MainActivity.kt",
    "app/src/main/java/com/example/wtsaskingforsignature/WtsApp.kt"
)

function Get-FileFingerprint {
    param([string]$FilePath)
    
    if (-not (Test-Path $FilePath)) {
        return @{
            Exists = $false
            Hash = $null
            Size = 0
            LastModified = $null
            ContentSample = $null
        }
    }
    
    $fileInfo = Get-Item $FilePath
    $hash = (Get-FileHash $FilePath -Algorithm SHA256).Hash
    
    # 讀取前100個字符作為內容樣本
    $contentSample = ""
    try {
        $content = Get-Content $FilePath -Raw -Encoding UTF8
        $contentSample = if ($content.Length -gt 100) { 
            $content.Substring(0, 100) 
        } else { 
            $content 
        }
        # 移除換行符以便比較
        $contentSample = $contentSample -replace "`r`n|`n|`r", " "
    } catch {
        $contentSample = "無法讀取內容"
    }
    
    return @{
        Exists = $true
        Hash = $hash
        Size = $fileInfo.Length
        LastModified = $fileInfo.LastWriteTime
        ContentSample = $contentSample
    }
}

Write-Host "`n掃描關鍵檔案..." -ForegroundColor Green

foreach ($file in $keyFiles) {
    Write-Host "掃描: $file" -ForegroundColor Gray
    $currentScan[$file] = Get-FileFingerprint $file
}

if ($CreateBaseline) {
    Write-Host "`n建立基準線檔案..." -ForegroundColor Green
    $currentScan | ConvertTo-Json -Depth 3 | Out-File $baselineFile -Encoding UTF8
    Write-Host "✓ 基準線已保存: $baselineFile" -ForegroundColor Green
    Write-Host "下次運行時請移除 -CreateBaseline 參數進行比較" -ForegroundColor Yellow
    exit 0
}

if (-not (Test-Path $baselineFile)) {
    Write-Host "❌ 找不到基準線檔案！" -ForegroundColor Red
    Write-Host "請先運行: .\scripts\file_change_tracker.ps1 -CreateBaseline" -ForegroundColor Yellow
    exit 1
}

# 讀取基準線
Write-Host "`n讀取基準線..." -ForegroundColor Green
try {
    $baseline = Get-Content $baselineFile -Raw | ConvertFrom-Json -AsHashtable
} catch {
    Write-Host "❌ 無法讀取基準線檔案！" -ForegroundColor Red
    exit 1
}

# 比較變更
Write-Host "`n比較檔案變更..." -ForegroundColor Green

$changes = @()
$noChanges = @()

foreach ($file in $keyFiles) {
    $current = $currentScan[$file]
    $base = $baseline[$file]
    
    if (-not $base) {
        $changes += @{
            File = $file
            Type = "新增"
            Details = "基準線中不存在此檔案"
        }
        continue
    }
    
    if (-not $current.Exists -and $base.Exists) {
        $changes += @{
            File = $file
            Type = "刪除"
            Details = "檔案已被刪除"
        }
        continue
    }
    
    if (-not $current.Exists -and -not $base.Exists) {
        $noChanges += $file
        continue
    }
    
    $hasChanges = $false
    $changeDetails = @()
    
    if ($current.Hash -ne $base.Hash) {
        $hasChanges = $true
        $changeDetails += "內容雜湊值變更"
    }
    
    if ($current.Size -ne $base.Size) {
        $hasChanges = $true
        $changeDetails += "檔案大小變更 ($($base.Size) → $($current.Size) bytes)"
    }
    
    if ($current.LastModified -ne $base.LastModified) {
        $hasChanges = $true
        $changeDetails += "修改時間變更 ($($base.LastModified) → $($current.LastModified))"
    }
    
    if ($current.ContentSample -ne $base.ContentSample) {
        $hasChanges = $true
        $changeDetails += "內容樣本變更"
    }
    
    if ($hasChanges) {
        $changes += @{
            File = $file
            Type = "修改"
            Details = $changeDetails -join "; "
        }
    } else {
        $noChanges += $file
    }
}

# 顯示結果
Write-Host "`n變更分析結果" -ForegroundColor Cyan

if ($changes.Count -gt 0) {
    Write-Host "`n✅ 發現 $($changes.Count) 個檔案有實際變更：" -ForegroundColor Green
    foreach ($change in $changes) {
        Write-Host "  📝 $($change.File)" -ForegroundColor White
        Write-Host "     類型: $($change.Type)" -ForegroundColor Gray
        if ($ShowDetails) {
            Write-Host "     詳情: $($change.Details)" -ForegroundColor Gray
        }
    }
} else {
    Write-Host "`n⚠️ 沒有發現任何實際檔案變更！" -ForegroundColor Yellow
    Write-Host "這可能表示：" -ForegroundColor Yellow
    Write-Host "  1. 檔案只是表面更新但內容沒變" -ForegroundColor Gray
    Write-Host "  2. 變更沒有正確保存" -ForegroundColor Gray
    Write-Host "  3. 基準線過舊需要更新" -ForegroundColor Gray
}

if ($noChanges.Count -gt 0) {
    Write-Host "`n📋 $($noChanges.Count) 個檔案無變更：" -ForegroundColor Gray
    if ($ShowDetails) {
        foreach ($file in $noChanges) {
            Write-Host "  - $file" -ForegroundColor DarkGray
        }
    }
}

# 建議操作
Write-Host "`n💡 建議操作：" -ForegroundColor Yellow

if ($changes.Count -eq 0) {
    Write-Host "❗ 緊急：檔案可能沒有實際更新！" -ForegroundColor Red
    Write-Host "1. 檢查編輯器是否正確保存檔案" -ForegroundColor White
    Write-Host "2. 手動驗證關鍵檔案內容" -ForegroundColor White
    Write-Host "3. 重新執行修改操作" -ForegroundColor White
    Write-Host "4. 檢查檔案權限問題" -ForegroundColor White
} else {
    Write-Host "1. 檔案確實已更新，可以繼續建置" -ForegroundColor White
    Write-Host "2. 運行建置驗證腳本" -ForegroundColor White
    Write-Host "3. 更新基準線以記錄新狀態" -ForegroundColor White
}

# 生成追蹤報告
$reportPath = "file_tracking_report_$(Get-Date -Format 'yyyy-MM-dd_HH-mm-ss').txt"
$report = @"
檔案變更追蹤報告
生成時間: $(Get-Date)

變更統計:
- 有變更的檔案: $($changes.Count)
- 無變更的檔案: $($noChanges.Count)
- 總計追蹤檔案: $($keyFiles.Count)

詳細變更:
$($changes | ForEach-Object { "- $($_.File): $($_.Type) - $($_.Details)" } | Out-String)

無變更檔案:
$($noChanges | ForEach-Object { "- $_" } | Out-String)

結論:
$(if ($changes.Count -gt 0) { "✅ 檔案已實際更新，可以繼續建置流程" } else { "❌ 檔案可能沒有實際更新，需要檢查" })
"@

$report | Out-File -FilePath $reportPath -Encoding UTF8
Write-Host "`n📄 追蹤報告已保存: $reportPath" -ForegroundColor Cyan

exit $(if ($changes.Count -gt 0) { 0 } else { 1 })

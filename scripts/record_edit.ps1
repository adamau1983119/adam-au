param(
    [Parameter(Mandatory = $true)] [string]$Content,
    [string]$Date = $(Get-Date -Format 'yyyy-MM-dd'),
    [string]$File = "開發改善方案執行記錄.md"
)

Write-Host "=== 📘 追加執行記錄 ===" -ForegroundColor Cyan

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

if (-not (Test-Path $File)) {
    Write-Host "❌ 找不到記錄檔：$File" -ForegroundColor Red
    exit 1
}

$header = "### $Date"
$lines = Get-Content $File -Raw -Encoding UTF8

# 若當天標題不存在，插入於 "## 📝 執行記錄" 之後的第一行
$execHeader = "## 📝 執行記錄"
if ($lines -notmatch [regex]::Escape($header)) {
    if ($lines -match [regex]::Escape($execHeader)) {
        $parts = $lines -split [regex]::Escape($execHeader), 2
        $newSection = "`n$header`n- [x] $Content`n"
        $updated = $parts[0] + $execHeader + "`n" + $newSection + $parts[1]
        Set-Content -Path $File -Value $updated -Encoding UTF8
        Write-Host "✓ 已新增日期區塊並寫入記錄" -ForegroundColor Green
    } else {
        Add-Content -Path $File -Value "`n$execHeader`n$header`n- [x] $Content" -Encoding UTF8
        Write-Host "✓ 已建立執行記錄區塊並寫入記錄" -ForegroundColor Green
    }
} else {
    # 已有當天區塊，直接在其下一行追加
    $pattern = [regex]::Escape($header)
    $updated = $lines -replace "($pattern[\s\S]*?)(\n### |\z)", { param($m) $m.Groups[1].Value + "`n- [x] $Content`n" + $m.Groups[2].Value }
    Set-Content -Path $File -Value $updated -Encoding UTF8
    Write-Host "✓ 已在現有日期區塊追加記錄" -ForegroundColor Green
}

Write-Host "📄 已更新：$File" -ForegroundColor Cyan
exit 0



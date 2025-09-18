# 生成并显示WTS APP终极管理员密码
# Generate and Display WTS APP Master Administrator Password

param(
    [switch]$ShowPassword,
    [switch]$SaveToFile,
    [string]$OutputFile = "master_password_backup.txt"
)

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "🔐 WTS APP 终极管理员密码生成器" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# 安全警告
Write-Host "⚠️  安全警告:" -ForegroundColor Yellow
Write-Host "• 此密码具有最高管理权限"
Write-Host "• 请妥善保管，不要泄露给任何人"
Write-Host "• 仅在紧急情况下使用夺回管理权"
Write-Host "• 密码一旦丢失将无法找回"
Write-Host ""

# 生成终极密码
function Generate-MasterPassword {
    $chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#`$%^&*"
    $password = ""
    $random = New-Object System.Random

    for ($i = 0; $i -lt 32; $i++) {
        $password += $chars[$random.Next(0, $chars.Length)]
    }

    return $password
}

# 显示密码信息
function Show-PasswordInfo {
    param([string]$Password)

    Write-Host "📋 终极管理员密码信息:" -ForegroundColor Green
    Write-Host "--------------------------------" -ForegroundColor Green
    Write-Host "密码: $Password" -ForegroundColor White
    Write-Host "长度: $($Password.Length) 字符" -ForegroundColor White
    Write-Host "生成时间: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor White
    Write-Host "用途: 夺回APP管理权" -ForegroundColor White
    Write-Host ""

    # 密码强度分析
    Write-Host "🔍 密码强度分析:" -ForegroundColor Yellow
    $hasUpper = $Password -match "[A-Z]"
    $hasLower = $Password -match "[a-z]"
    $hasDigit = $Password -match "[0-9]"
    $hasSpecial = $Password -match "[!@#`$%^&*]"

    Write-Host "✓ 大写字母: $(if ($hasUpper) { '是' } else { '否' })" -ForegroundColor $(if ($hasUpper) { 'Green' } else { 'Red' })
    Write-Host "✓ 小写字母: $(if ($hasLower) { '是' } else { '否' })" -ForegroundColor $(if ($hasLower) { 'Green' } else { 'Red' })
    Write-Host "✓ 数字: $(if ($hasDigit) { '是' } else { '否' })" -ForegroundColor $(if ($hasDigit) { 'Green' } else { 'Red' })
    Write-Host "✓ 特殊字符: $(if ($hasSpecial) { '是' } else { '否' })" -ForegroundColor $(if ($hasSpecial) { 'Green' } else { 'Red' })
    Write-Host ""
}

# 保存密码到文件
function Save-PasswordToFile {
    param([string]$Password, [string]$FilePath)

    $content = @"
=========================================
🔐 WTS APP 终极管理员密码备份
=========================================

⚠️ 重要安全信息:
• 此密码具有最高管理权限
• 请妥善保管此文件
• 不要将此文件存储在云端
• 密码一旦丢失将无法找回

📋 密码信息:
密码: $Password
长度: $($Password.Length) 字符
生成时间: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
用途: 夺回APP管理权

🔍 密码强度:
$(if ($Password -match "[A-Z]") { "✓" } else { "✗" }) 大写字母
$(if ($Password -match "[a-z]") { "✓" } else { "✗" }) 小写字母
$(if ($Password -match "[0-9]") { "✓" } else { "✗" }) 数字
$(if ($Password -match "[!@#`$%^&*]") { "✓" } else { "✗" }) 特殊字符

🚨 使用说明:
1. 仅在忘记默认密码时使用
2. 输入此密码可夺回管理权
3. 系统会生成新的默认密码
4. 验证成功后立即修改密码

=========================================
"@

    try {
        $content | Out-File -FilePath $FilePath -Encoding UTF8
        Write-Host "✅ 密码已保存到文件: $FilePath" -ForegroundColor Green
    } catch {
        Write-Host "❌ 保存失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 主程序
$masterPassword = Generate-MasterPassword

if ($ShowPassword) {
    Show-PasswordInfo -Password $masterPassword
}

if ($SaveToFile) {
    Save-PasswordToFile -Password $masterPassword -FilePath $OutputFile
    Write-Host ""
    Write-Host "📁 文件保存位置: $OutputFile" -ForegroundColor Cyan
    Write-Host "⚠️  请将此文件存储在安全的地方！" -ForegroundColor Yellow
}

# 显示使用说明
Write-Host "📖 使用说明:" -ForegroundColor Cyan
Write-Host "1. 复制并安全保存此密码"
Write-Host "2. 仅在忘记默认密码时使用"
Write-Host "3. 在APP中选择'夺回管理权'功能"
Write-Host "4. 输入此终极密码验证身份"
Write-Host "5. 系统会生成新的默认密码"
Write-Host ""

if (-not $ShowPassword -and -not $SaveToFile) {
    Write-Host "💡 使用参数:" -ForegroundColor Yellow
    Write-Host "  -ShowPassword    显示详细密码信息"
    Write-Host "  -SaveToFile      保存到文件"
    Write-Host "  -OutputFile      指定输出文件路径"
    Write-Host ""
    Write-Host "示例:" -ForegroundColor Gray
    Write-Host "  .\generate_master_password.ps1 -ShowPassword -SaveToFile"
    Write-Host ""
}

# 显示生成的密码
Write-Host "🔑 终极管理员密码:" -ForegroundColor Green
Write-Host "----------------------------------------" -ForegroundColor Green
Write-Host "$masterPassword" -ForegroundColor White
Write-Host "----------------------------------------" -ForegroundColor Green
Write-Host ""

Write-Host "✅ 密码生成完成！" -ForegroundColor Green
Write-Host "⚠️  请务必安全保存此密码！" -ForegroundColor Yellow

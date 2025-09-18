# Generate WTS APP Master Administrator Password

Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "WTS APP Master Password Generator" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host ""

# Security Warning
Write-Host "SECURITY WARNING:" -ForegroundColor Yellow
Write-Host "- This password has the highest admin privileges"
Write-Host "- Keep it secure and don't share it with anyone"
Write-Host "- Use only in emergency situations to recover admin rights"
Write-Host "- Password cannot be recovered if lost"
Write-Host ""

# Generate Master Password
function Generate-MasterPassword {
    $chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#`$%^&*"
    $password = ""
    $random = New-Object System.Random

    for ($i = 0; $i -lt 32; $i++) {
        $password += $chars[$random.Next(0, $chars.Length)]
    }

    return $password
}

# Save password to file
function Save-PasswordToFile {
    param([string]$Password, [string]$FilePath)

    $content = @"
=========================================
WTS APP Master Administrator Password Backup
=========================================

IMPORTANT SECURITY INFORMATION:
- This password has the highest admin privileges
- Keep this file secure and don't store in cloud
- Password cannot be recovered if lost

Password Information:
Password: $Password
Length: $($Password.Length) characters
Generated: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
Purpose: Recover APP admin rights

Password Strength:
$(if ($Password -match "[A-Z]") { "YES" } else { "NO" }) Uppercase Letters
$(if ($Password -match "[a-z]") { "YES" } else { "NO" }) Lowercase Letters
$(if ($Password -match "[0-9]") { "YES" } else { "NO" }) Digits
$(if ($Password -match "[!@#`$%^&*]") { "YES" } else { "NO" }) Special Characters

Usage Instructions:
1. Use only when you forget the default password
2. Enter this password to recover admin rights
3. System will generate a new default password
4. Change to your password immediately after verification

=========================================
"@

    try {
        $content | Out-File -FilePath $FilePath -Encoding UTF8
        Write-Host "Password saved to file: $FilePath" -ForegroundColor Green
    } catch {
        Write-Host "Save failed: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Main program
$masterPassword = Generate-MasterPassword

# Save to file
Save-PasswordToFile -Password $masterPassword -FilePath "master_password_backup.txt"
Write-Host ""
Write-Host "File saved: master_password_backup.txt" -ForegroundColor Cyan
Write-Host "WARNING: Store this file in a secure location!" -ForegroundColor Yellow

# Display usage instructions
Write-Host ""
Write-Host "Usage Instructions:" -ForegroundColor Cyan
Write-Host "1. Copy and securely save this password"
Write-Host "2. Use only when you forget the default password"
Write-Host "3. In the APP, select 'Admin Rights Recovery' function"
Write-Host "4. Enter this master password to verify identity"
Write-Host "5. System will generate a new default password"
Write-Host ""

# Display generated password
Write-Host "MASTER ADMINISTRATOR PASSWORD:" -ForegroundColor Green
Write-Host "----------------------------------------" -ForegroundColor Green
Write-Host "$masterPassword" -ForegroundColor White
Write-Host "----------------------------------------" -ForegroundColor Green
Write-Host ""

Write-Host "Password generation completed!" -ForegroundColor Green
Write-Host "WARNING: Keep this password secure!" -ForegroundColor Yellow

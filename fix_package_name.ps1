# Fix package name script
$sourceDir = "app\src\main\java\com\example\wtsaskingforsignature"
$targetDir = "app\src\main\java\com\wts\dsfortune"

# Create target directory
New-Item -ItemType Directory -Path $targetDir -Force

# Copy all files to new directory
Copy-Item -Path "$sourceDir\*" -Destination $targetDir -Recurse -Force

# Only replace package declarations, not string content
Get-ChildItem -Path $targetDir -Recurse -Include "*.kt","*.java" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    # Only replace package declaration lines
    $content = $content -replace '^package com\.example\.wtsaskingforsignature', 'package com.wts.dsfortune'
    # Only replace import statements
    $content = $content -replace 'import com\.example\.wtsaskingforsignature', 'import com.wts.dsfortune'
    Set-Content -Path $_.FullName -Value $content -NoNewline
}

Write-Host "Package name modification completed!"
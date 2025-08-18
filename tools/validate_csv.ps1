$ErrorActionPreference='Stop'
$p='app/src/main/assets/fortunes_source.csv'
if(!(Test-Path $p)){ Write-Output ('ERROR=MISSING '+$p); exit 1 }
$rows=Import-Csv -Path $p -Encoding UTF8
Write-Output ('COUNT='+$rows.Count)
if($rows.Count -gt 0){
  Write-Output ('HEADERS='+($rows[0].PSObject.Properties.Name -join ','))
}
$badId = $rows | Where-Object { -not $_.id -or -not ($_.id -match '^(\d+)$') -or ([int]$_.id -lt 1) -or ([int]$_.id -gt 100) }
Write-Output ('BAD_ID='+$badId.Count)
$missing = $rows | Where-Object { [string]::IsNullOrWhiteSpace($_.title) -or [string]::IsNullOrWhiteSpace($_.summary) -or [string]::IsNullOrWhiteSpace($_.content) }
Write-Output ('MISSING_FIELDS='+$missing.Count)
$dup = $rows.id | Group-Object | Where-Object { $_.Count -gt 1 } | ForEach-Object { $_.Name }
Write-Output ('DUP_IDS='+(($dup -join ',') -replace '^$','<none>'))
$present = $rows.id | ForEach-Object { [int]$_ } | Sort-Object -Unique
$missingIds = (1..100) | Where-Object { $present -notcontains $_ }
Write-Output ('MISSING_IDS='+(($missingIds -join ',') -replace '^$','<none>'))
if($rows.Count -gt 0){
  $sample = $rows | Select-Object -First 1
  Write-Output ('SAMPLE_ID='+$sample.id)
  Write-Output ('SAMPLE_TITLE='+$sample.title)
  $lenTitle = if ($null -ne $sample.title) { ($sample.title).ToString().Length } else { 0 }
  $lenSummary = if ($null -ne $sample.summary) { ($sample.summary).ToString().Length } else { 0 }
  $lenContent = if ($null -ne $sample.content) { ($sample.content).ToString().Length } else { 0 }
  Write-Output ("SAMPLE_LEN_TITLE=$lenTitle,SUMMARY=$lenSummary,CONTENT=$lenContent")
}


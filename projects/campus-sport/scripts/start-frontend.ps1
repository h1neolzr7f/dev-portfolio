$ErrorActionPreference = 'Stop'

$projectRoot = Resolve-Path (Join-Path $PSScriptRoot '..')
$frontDir = Join-Path $projectRoot 'front'

if (Get-NetTCPConnection -LocalPort 7000 -State Listen -ErrorAction SilentlyContinue) {
    Write-Host 'Frontend port 7000 is already listening.'
    exit 0
}

Start-Process -FilePath 'npm.cmd' -ArgumentList @('run', 'dev', '--', '--host', '0.0.0.0') -WorkingDirectory $frontDir -WindowStyle Hidden
Start-Sleep -Seconds 5
Write-Host 'Frontend started at http://localhost:7000.'

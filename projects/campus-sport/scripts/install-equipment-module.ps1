$ErrorActionPreference = 'Stop'

. (Join-Path $PSScriptRoot 'Resolve-Mysql.ps1')
$mysql = Get-MysqlExe
$sqlFile = Join-Path $PSScriptRoot 'install-equipment-module.sql'

cmd /c "`"$mysql`" -uroot -proot --default-character-set=utf8mb4 sport-manage < `"$sqlFile`""
if ($LASTEXITCODE -ne 0) {
    throw "Failed to install equipment module. MySQL exit code: $LASTEXITCODE"
}
Write-Host 'Equipment module database objects installed.'

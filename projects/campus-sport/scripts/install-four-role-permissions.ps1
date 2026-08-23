$ErrorActionPreference = 'Stop'

. (Join-Path $PSScriptRoot 'Resolve-Mysql.ps1')
$mysql = Get-MysqlExe
$sql = Join-Path $PSScriptRoot 'install-four-role-permissions.sql'

cmd /c "`"$mysql`" -uroot -proot --default-character-set=utf8mb4 < `"$sql`""
if ($LASTEXITCODE -ne 0) {
    throw 'Failed to install four-role permissions.'
}
Write-Host 'Four-role permissions installed.'

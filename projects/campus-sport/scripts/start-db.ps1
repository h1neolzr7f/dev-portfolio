$ErrorActionPreference = 'Stop'

if (Get-NetTCPConnection -LocalPort 3306 -State Listen -ErrorAction SilentlyContinue) {
    Write-Host 'Database port 3306 is already listening.'
    exit 0
}

Write-Error '未检测到 3306 端口。请先在本机启动 MySQL 或 MariaDB，再导入 sport-manage 数据库。'

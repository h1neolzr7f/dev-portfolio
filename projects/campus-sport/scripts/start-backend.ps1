$ErrorActionPreference = 'Stop'

$projectRoot = Resolve-Path (Join-Path $PSScriptRoot '..')
$java = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin\java.exe' } else { 'java' }
$jarCandidates = @(
    (Join-Path $projectRoot 'admin\target\boot.jar'),
    (Join-Path $projectRoot 'admin\target\admin-0.0.1-SNAPSHOT.jar')
)
$jar = $jarCandidates | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1

if (-not $jar) {
    Write-Error '未找到后端 jar。请先在 admin 目录执行 mvn -DskipTests package。'
    exit 1
}

if (Get-NetTCPConnection -LocalPort 9090 -State Listen -ErrorAction SilentlyContinue) {
    Write-Host 'Backend port 9090 is already listening.'
    exit 0
}

Start-Process -FilePath $java -ArgumentList @('-jar', $jar) -WorkingDirectory $projectRoot -WindowStyle Hidden
Start-Sleep -Seconds 8
Write-Host 'Backend started at http://localhost:9090.'

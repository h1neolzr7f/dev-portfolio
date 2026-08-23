$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

if (-not (Test-Path -LiteralPath 'db.properties')) {
    Copy-Item -LiteralPath 'db.properties.example' -Destination 'db.properties'
    Write-Host '已生成 db.properties，请按本机 MySQL 账号修改后再运行。'
}

$lib = Get-ChildItem -Path 'lib' -Filter '*.jar' -ErrorAction SilentlyContinue | Select-Object -First 1
if (-not $lib) {
    Write-Error '请先把 MySQL Connector/J 放到 lib/ 目录，例如 lib/mysql-connector-j.jar'
}

New-Item -ItemType Directory -Force -Path 'out' | Out-Null
$sources = Get-ChildItem -Path 'src' -Filter '*.java' -Recurse | ForEach-Object { $_.FullName }
& javac -encoding UTF-8 -cp $lib.FullName -d out @sources
if ($LASTEXITCODE -ne 0) {
    throw '编译失败'
}

$classpath = "out;$($lib.FullName)"
& java -cp $classpath com.tzz.student.StudentManagerApp

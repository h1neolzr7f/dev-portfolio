$ErrorActionPreference = 'Stop'

. (Join-Path $PSScriptRoot 'Resolve-Mysql.ps1')
$mysql = Get-MysqlExe
$demoDate = (Get-Date).AddDays(7).ToString('yyyy-MM-dd')
$expireTime = "$demoDate 23:59:59"
$available = 'CONVERT(0xE58FAFE9A284E7BAA6 USING utf8mb4)'

$sql = @"
UPDATE timeslot
SET book_date = '$demoDate', expire_time = '$expireTime', state_radio = $available;

DELETE FROM prepared WHERE user_id = 22;

DELETE FROM timeslot
WHERE area_id IS NULL OR timetable_id IS NULL;

INSERT INTO timeslot(area_id, book_date, timetable_id, expire_time, state_radio)
SELECT a.id, '$demoDate', t.id, '$expireTime', $available
FROM area a
JOIN timetable t
WHERE NOT EXISTS (
  SELECT 1
  FROM timeslot s
  WHERE s.area_id = a.id AND s.timetable_id = t.id
);
"@

$sql | & $mysql -uroot -proot --default-character-set=utf8mb4 sport-manage
if ($LASTEXITCODE -ne 0) {
    throw "Failed to reset demo data. MySQL exit code: $LASTEXITCODE"
}

Write-Host "Demo time slots reset to $demoDate."

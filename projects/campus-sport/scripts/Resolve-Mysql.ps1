function Get-MysqlExe {
    foreach ($name in @('mariadb', 'mysql')) {
        $cmd = Get-Command $name -ErrorAction SilentlyContinue
        if ($cmd) {
            return $cmd.Source
        }
    }

    throw '未找到 mysql 或 mariadb 命令。请先安装 MySQL/MariaDB，并把它加入 PATH。'
}

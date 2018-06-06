#
class leoz::mysql (
    $dev = false ) {
  # masc20140721. mysql server setup
  class { ::mysql::server:
    root_password => 'root',
    override_options => { 
      'mysqld' => {
        'bind-address' => '0.0.0.0',
        'sql-mode' => 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION'
       }
    },
    restart => true
  }

  mysql_user { 'root@%':
    ensure => 'present',
    password_hash => mysql_password('root')
  }

  mysql_grant { 'root@%/*.*':
    ensure     => 'present',
    options    => ['GRANT'],
    privileges => ['ALL'],
    table      => '*.*',
    user       => 'root@%'
  }

# masc20140721. mysql database setup
  ::mysql::db { 'dekuclient':
    user => 'leoz',
    password => 'leoz',
    host => '%'
  }
}
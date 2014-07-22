#
class leo2::mysql {
  # masc20140721. mysql server setup
  class { ::mysql::server:
    root_password => 'root',
    override_options => { 
      'mysqld' => {
        'bind-address' => '0.0.0.0' 
       }
    },
    restart => true
  }

  # masc20140721. mysql database setup
  ::mysql::db { 'leo2':
    user => 'leo2',
    password => 'leo2',
    host => '%',
    sql => '/vagrant/mysql/leo2.sql'
  }
}
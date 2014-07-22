node default {
  Exec { path => ['/bin/', '/sbin/', '/usr/bin/', '/usr/sbin/'] }

  #exec { 'apt-get-update':
  #  command => 'apt-get update'
  #}

  # masc20140722. set timezone
  class { 'timezone':
    timezone => 'Europe/Berlin'
  }

  # masc20140721. make sure apt-get-update is executed before any package is pulled in
  # Exec['apt-get-update'] -> Package <| |>
  class { 'apt::update':
  } -> Package <| |>

  # masc20140721. packages installed explicitly
  package { ['vim', 'htop']:
    ensure => 'installed',
  }

  # masc20140721. mysql server setup
  class { '::mysql::server':
    root_password => 'root',
    override_options => { 
      'mysqld' => {
        'bind-address' => '0.0.0.0' 
       }
    },
    restart => true
  }

  # masc20140721. mysql database setup
  mysql::db { 'leo2':
    user => 'leo2',
    password => 'leo2',
    host => '%',
    sql => '/vagrant/mysql/leo2.sql'
  }

  # masc20140721. setup oracle java PPA
  class { java::oracle: 
    # it's an ubuntu PPA, but trusty is ok for debian wheezy
    release => 'trusty'
  }

  # masc20140721. setup java
  class {
	   java::oracle_1_8_0: 
  }

  # masc20140721. setup java environment var
  $java_home = $::java::oracle_1_8_0::home
  file { '/etc/profile.d/set_java_home.sh':
    require => Class['java::oracle_1_8_0'],
    ensure => present,
    content => sprintf("JAVA_HOME=\"%s\"\nexport JAVA_HOME\n", $java_home)
  }

  # masc20140721. setup tomcat
  class { 'tomcat::params':
    version => 7
  }

  class { tomcat:
    require => [ 
      Class['java::oracle_1_8_0'],
      Class['Tomcat::Params'] ]
  }

  # masc20140722. amend tomcat default configuration
  file_line { 'set_tomcat_home':
    before => Service['tomcat7'],
    require => Package['tomcat'],
    path => '/etc/default/tomcat7',
    match => ".*JAVA_HOME=.*",
    line => sprintf("JAVA_HOME=\"%s\"", $java_home)
  }

  package { ['tomcat7-admin']:
    require => Package['tomcat'],
    ensure => 'installed'
  }

  # masc20140722. add tomcat user for manager gui
  augeas { 'tomcat-users.xml':
    before => Service['tomcat7'],
    require => Package['tomcat'],
    lens => 'Xml.lns',
    incl => '/etc/tomcat7/tomcat-users.xml',
    changes => [
      "set tomcat-users/role/#attribute/rolename manager-gui",
      "set tomcat-users/user/#attribute/username tomcat",
      "set tomcat-users/user[#attribute/username='tomcat']/#attribute/password tomcat",
      "set tomcat-users/user[#attribute/username='tomcat']/#attribute/roles manager-gui"
    ]
  }
}

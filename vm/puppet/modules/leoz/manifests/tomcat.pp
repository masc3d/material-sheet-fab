#
class leoz::tomcat (
  $debug = false 
) {
  contain leoz::java

  # masc20140721. setup tomcat
  class { ::tomcat::params:
    version => 7
  }

  class { ::tomcat:
    require => Class['Tomcat::Params']
  }

  # masc20140722. amend tomcat default configuration
  file_line { 'set_tomcat_home':
    before => Service['tomcat7'],
    require => Package['tomcat'],
    path => '/etc/default/tomcat7',
    match => ".*JAVA_HOME=.*",
    line => sprintf("JAVA_HOME=\"%s\"", $::java::oracle_1_8_0::home)
  }

  # masc20140724. set java opts with optional debug support
  $java_opts = '-Djava.awt.headless=true -Xmx1024m -XX:+UseConcMarkSweepGC'
  if $debug {
    $java_debug_opts += ' -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n'
  }

  file_line { 'set_tomcat_java_opts':
    before => Service['tomcat7'],
    require => Package['tomcat'],
    path => '/etc/default/tomcat7',
    match => "^JAVA_OPTS=.*",
    line => sprintf("JAVA_OPTS=\"%s\"", "${java_opts}${java_debug_opts}")
  }

  package { 'tomcat7-admin':
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
      "set tomcat-users/role/#attribute/rolename manager-script",
      "set tomcat-users/user/#attribute/username tomcat",
      "set tomcat-users/user[#attribute/username='tomcat']/#attribute/password tomcat",
      "set tomcat-users/user[#attribute/username='tomcat']/#attribute/roles manager-gui,manager-script" ]
  }
}
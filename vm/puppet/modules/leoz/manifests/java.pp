#
class leoz::java {
  include ::java::oracle_1_8_0

  # masc20140721. setup java environment var
  file { '/etc/profile.d/set_java_home.sh':
    require => Class['java::oracle_1_8_0'],
    ensure => 'present',
    content => sprintf("JAVA_HOME=\"%s\"\nexport JAVA_HOME\n", $::java::oracle_1_8_0::home)
  }  
}

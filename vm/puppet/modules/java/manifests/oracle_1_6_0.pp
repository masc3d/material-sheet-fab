class java::oracle_1_6_0 (
    $package = $java::oracle_1_6_0::params::package,
    $home    = $java::oracle_1_6_0::params::home) inherits java::oracle_1_6_0::params {
    include oracle

    if (!defined(Package[$package])) {
        package { $package:
            ensure  => installed,
            require => Class['oracle']
        }
    }

    exec { "${package}accepted-oracle-license-v1-1":
        command => "/bin/echo ${package} shared/accepted-oracle-license-v1-1 select true | /usr/bin/sudo /usr/bin/debconf-set-selections",
        unless  => "/usr/bin/debconf-show ${package} | grep 'shared/accepted-oracle-license-v1-1: true'",
    }
}

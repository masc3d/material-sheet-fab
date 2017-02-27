class java::openjdk_1_7_0 (
    $package = $java::openjdk_1_7_0::params::package) inherits java::openjdk_1_7_0::params {
    if (!defined(Package[$package])) {
        package { $package: ensure => installed }
    }
}

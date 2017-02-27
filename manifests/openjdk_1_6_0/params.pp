# == Class: java::openjdk_1_6_0::params
#
# This class manages parameters for the java::openjdk_1_6_0 Puppet class in this module.
#
# === Parameters
#
# === Variables
#
# === Examples
#
# === Authors
#
# Sander Bilo <sander@proteon.nl>
#
# === Copyright
#
# Copyright 2013 Proteon.
#
class java::openjdk_1_6_0::params {
    $home = "/usr/lib/jvm/java-1.6.0-openjdk-${::architecture}"
    $package = 'openjdk-6-jre'
}

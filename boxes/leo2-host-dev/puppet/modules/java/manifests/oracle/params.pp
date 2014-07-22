# == Class: java::oracle::params
#
# This class manages parameters for the java::oracle Puppet class in this module.
#
# === Parameters
#
# === Variables
#
# === Examples
#
#  class { java::params
#  }
#
# === Authors
#
# Sander Bilo <sander@proteon.nl>
#
# === Copyright
#
# Copyright 2013 Proteon.
#
class java::oracle::params {
    $location = 'http://ppa.launchpad.net/webupd8team/java/ubuntu'
    $repository_name = 'webupd8team'
    $release = $::lsbdistcodename
    $repos = 'main'
    $key = 'EEA14886'
    $key_server = 'keyserver.ubuntu.com'
}

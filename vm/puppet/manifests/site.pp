# masc20140723 leoz site definition

# globals
Exec { path => ['/bin/', '/sbin/', '/usr/bin/', '/usr/sbin/'] }

# masc20140722. set timezone
class { timezone:
  timezone => 'Europe/Berlin'
}

# masc20140721. make sure apt-get-update is executed before any package is pulled in
class { apt::update:
} -> Package <| |>

# masc20140721. packages to install
package { ['vim', 'htop']:
  ensure => 'installed',
}

import 'nodes.pp'

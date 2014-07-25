# leo2 base system
node leo2-base {
	include leo2::mysql
	include leo2::java
}

# leo2 developer configuration
node leo2-dev inherits leo2-base {
  class { leo2::tomcat:
  	debug => true 
  }
}

# leo2 productive configuration
node leo2-prod inherits leo2-base {
	include leo2::tomcat
}

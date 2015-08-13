# leo2 base system
node leo2-base {
	include leo2::mysql
	include leo2::java
}

# leoz developer configuration
node leo2-dev inherits leo2-base {
  class { leo2::tomcat:
  	debug => true 
  }
}

# leoz productive configuration
node leo2-prod inherits leo2-base {
	include leo2::tomcat
}

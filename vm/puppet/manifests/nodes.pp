# leoz base system
node leoz-base {
	include leoz::mysql
	include leoz::java
}

# leoz developer configuration
node leoz-dev inherits leoz-base {
  class { leoz::tomcat:
  	debug => true 
  }
}

# leoz productive configuration
node leoz-prod inherits leoz-base {
	include leoz::tomcat
}

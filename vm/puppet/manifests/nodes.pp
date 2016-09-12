# leoz developer configuration
node leoz-dev {
	include leoz::mysql
	include leoz::java
}

# leoz productive configuration
node leoz-prod {
	include leoz::mysql
	include leoz::java
}

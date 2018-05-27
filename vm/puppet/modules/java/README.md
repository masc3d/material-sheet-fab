puppet-java
===========

Puppet code for deploying and configuring Oracle Java on Debian-like servers. Uses the webupd8.com Java packages by default. 
See: http://www.webupd8.org/2012/01/install-oracle-java-jdk-7-in-ubuntu-via.html for details.

Depencendies:

    'puppetlabs/apt', '>=1.1.0'

Basic usage
-------------------------
To install Oracle Java JDK 1.6

    class { 'java::oracle_1_6_0': }

To install Oracle JAVA JDK 1.8

    class { 'java::oracle_1_8_0':}
    
To install OpenJDK JAVA 1.7

    class { 'java::openjdk_1_7_0':}


Include the module in your own applications
-------------------------
Include the class into your application if not defined elsewhere

    if (!defined(Class['java::oracle_1_7_0'])) {
        class { 'java::oracle_1_7_0': }
    }
    
Make the JAVA_HOME variable available to your application templates
    
    $java_home = "::java::${java_version}::home"
    
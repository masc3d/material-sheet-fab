# Leoz #

## Development Environment ##

#### Required software ####
* [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html)
* [Idea Intellij](https://www.jetbrains.com/idea/)
* [Sourcetree](https://www.atlassian.com/software/sourcetree)

#### Optional ####
* [JavaFX Scene Builder](http://gluonhq.com/open-source/scene-builder/)

#### Get up and running ####
* Clone GIT repository using URL [ssh://git@git.derkurier.de:13020/leoz/leoz.git](ssh://git@git.derkurier.de:13020/leoz/leoz.git)
  <pre>git clone --recursive ssh://git@git.derkurier.de:13020/leoz/leoz.git leoz</pre>
* Open project directory with IntelliJ

## VM ##
Contains development host resources, eg. MYSQL and application server

#### Required software ####
* [Virtual Box](https://www.virtualbox.org)
* [Vagrant](http://www.vagrantup.com)

#### Get up and running ####
* Open terminal/console and change directory to `<project directory>/vm`
* Start VM with default provider (`virtualbox`)
   <pre>vagrant up</pre>
* Alternatively the provider can be overriden, eg. for Parallels on OSX
   <pre>vagrant up --provider parallels</pre>

## Contribution guidelines ##

* Writing tests
* Code review
* Other guidelines

## Who do I talk to? ##

* Repo owner or admin
* Other community or team contact
# Prepare #

## Download the source ##

  * This is a Maven 2 project, so we need to install a maven 2 environment. Get it at [Apache Maven](http://maven.apache.org/) project site.
  * Download [gdata-java-client](http://code.google.com/p/gdata-java-client/) library, this time we download [gdata.java-1.41.3.zip](http://gdata-java-client.googlecode.com/files/gdata.java-1.41.3.zip). The number 1.41 is the version what we installed, if you get a newest version, maybe you can save this reversion number for latter.
  * Get the rss2blogspot source, you can download from [Google Code](http://code.google.com/p/rss2blogspot/downloads/list), unpack it and we can get a directory called rss2blogspot.
  * All right! these all we need.

## Install the GDATA Java Client ##

  * The GDATA Java Client's version is 1.41.3
  * Unpack gdata.java-1.41.3.zip, and we will get a new directory called gdata. We can find a directory named lib at gdata/java directory, the full path name is **gdata/java/lib** !
  * At gdata/java/lib, 4 jar files we need to install by self, there are **gdata-core-1.0.jar**, **gdata-base-1.0.jar**, **gdata-blogger-2.0.jar**, and **gdata-media-1.0.jar** .
> [![](http://farm3.static.flickr.com/2498/3993329206_32442c86e1_o.png)](http://www.flickr.com/photos/adahsu/3993329206/)
  * Use the following code to install these jars into maven 2 local repository.
```
  # Remember change your gdata-java-client version correctly.
  $ mvn install:install-file -DgroupId=com.google.gdata -DartifactId=gdata-core  \
                                    -Dversion=1.0 -Dfile=gdata-core-1.0.jar \
                                    -Dpackaging=jar -DgeneratePom=true

  $ mvn install:install-file -DgroupId=com.google.gdata -DartifactId=gdata-base  \
                                    -Dversion=1.0 -Dfile=gdata-base-1.0.jar \
                                    -Dpackaging=jar -DgeneratePom=true

  $ mvn install:install-file -DgroupId=com.google.gdata -DartifactId=gdata-blogger  \
                                    -Dversion=2.0 -Dfile=gdata-blogger-2.0.jar \
                                    -Dpackaging=jar -DgeneratePom=true

  $ mvn install:install-file -DgroupId=com.google.gdata -DartifactId=gdata-media  \
                                    -Dversion=1.0 -Dfile=gdata-media-1.0.jar \
                                    -Dpackaging=jar -DgeneratePom=true
```
  * Or, use the bundled resource file to install them:
    1. ~~If you use Windows platform, copy rss2blogspot/src/main/resources/installGDATA.cmd to gdata/java/lib and run it to install these 4 jars. **BUT be careful** , must be sure these 4 jars are installed.~~
    1. If you use Linux platform with bash, copy rss2blogspot/src/main/resources/installGDATA.sh to gdata/java/lib and turn it to install all jars.

# Compile #

Compile is easy, just run the following code at rss2blogspot/ , maven will compile and package it as a single, runnable jar file.

```
  $ mvn clean package
```

# Execute #

Check wiki page to execute it !
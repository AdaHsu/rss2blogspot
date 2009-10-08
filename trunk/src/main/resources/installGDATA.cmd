@echo off
mvn install:install-file -DgroupId=com.google.gdata -DartifactId=gdata-core -Dversion=1.0-1.38 -Dfile=gdata-core-1.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.google.gdata -DartifactId=gdata-base -Dversion=1.0-1.38 -Dfile=gdata-base-1.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.google.gdata -DartifactId=gdata-blogger -Dversion=2.0-1.38 -Dfile=gdata-blogger-2.0.jar -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -DgroupId=com.google.gdata -DartifactId=gdata-media -Dversion=1.0-1.38 -Dfile=gdata-media-1.0.jar -Dpackaging=jar -DgeneratePom=true

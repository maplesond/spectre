#!/bin/bash

#REM installs not-public-available dependencies in local maven repository

mvn install:install-file -DgroupId=joptimizer -DartifactId=joptimizer -Dversion=3.2.0 -Dpackaging=jar -Dfile=joptimizer-3.2.0.jar


#!/bin/bash

# Assumes that $GUROBI_HOME and $GRB_LICENSE_FILE are set and that maven is installed and on the path

GRB_VER_LINE=$(grep VERSION $GRB_LICENSE_FILE)
GRB_VER_PARTS=(${GRB_VER_LINE//=/ })
GRB_VER=${GRB_VER_PARTS[1]}

echo "Found Gurobi Version: $GRB_VER\n"

mvn install:install-file -Dfile=$GUROBI_HOME/lib/gurobi.jar -DgroupId=gurobi -DartifactId=gurobi -Dversion=$GRB_VER -Dpackaging=jar

echo "Added gurobi.jar to local maven repository.  Phygen can now integrate with Gurobi optimiser\n"


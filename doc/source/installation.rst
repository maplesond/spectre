.. _installation:

Installation
============

Spectre can be installed either from a distributable tarball, or from source via a `git clone`. These steps for both
methods are described in the following sections.

Some of the tools in spectre use external mathematical optimizers for solving linear and quadratic problems.  Should you
install from tarball working version of Apache Maths and JOptimizer is included.  However, some users may want to use
optimizers from other vendors or sources such as GLPK or Gurobi you will need to manually install another tool called
*metaopt* first, and then install from source.  Metaopt can be obtained from https://github.com/maplesond/metaopt.  Please
follow the instructions in the metaopt README for how to add other optimizers.

From tarball
------------

Before starting the installation please ensure that the Java Runtime Environment (JRE) V1.8+ is installed and configured
for your environment.  You can check this by typing the following at the command line: ``java -version``.  Double check
the version number exceeds V1.8.

The installation process from tarball is simple.  The first step is acquire the tarball from https://github.com/maplesond/spectre/releases.
Then unpacking the compressed tarball to a directory of your choice.  The unpack command is: ``tar -xvf spectre-<version>-<platform>.tar.gz``.
This will create a sub-directory called ``spectre-<version>`` and in there should be the following further sub-directories:

* bin - contains scripts allowing the user to easily run all the tools.  In general, the scripts are all command line tools except for ``spectre`` suffix.  Scripts for all platforms are available, in general, those with no extension should work on linux and mac platforms, and those with a ``.bat`` extension should run on windows.
* doc - a html, pdf and text copy of the complete manual
* etc - contains examples and configuration files
* examples - Example files to help you get started with the spectre tools
* repo - contains the java classes used by spectre
* support_jars - contains source and javadocs for the spectre codebase

Should you want to run the tools without referring to their paths, you should ensure the `bin` directory is on your
PATH environment variable.

Unfortunately, as we cannot bundle Gurobi resources with SPECTRE, if you wish to enable Gurobi support you must install SPECTRE
from source (see below).

From source
-----------

Spectre is a java 1.8 / maven project. Before compiling the source code, please make sure the following tools are installed:

* GIT
* Maven (make sure you set the m2_home environment variable to point at your Maven directory) https://maven.apache.org/
* JDK v1.8+  (make sure you set the JAVA_HOME environment variable to point at your JDK directory)
* Make
* Sphinx (may require you to install python, also make sure the sphinx-build is on the path environment variable) http://www.sphinx-doc.org/en/stable/

You also need to make sure that the system to are compiling on has internet access, as it will try to automatically
incorporate any required java dependencies via maven. Because spectre is a maven project, almost all the other
dependencies (not mentioned here) will be downloaded automatically
as part of the Maven buildcycle.  However, the one exception to this is a java library called metaopt (described at the
beginning of this section), which provides a common interface to several open source and commercial optimizers.  Metaopt
can be obtained from: https://github.com/maplesond/metaopt. Please follow the instructions in the metaopt README and
make sure the metaopt library has been added to your local maven repository.  After this, you can proceed with the
spectre installation.

Now type the following::

  git clone https://github.com/maplesond/spectre.git
  cd spectre

Then type::

    mvn clean install

or, if you wish to enable gurobi optimizer support::

    mvn clean install -P gurobi


Note: If you cannot clone the git repositories using “https”, please try “ssh” instead. Consult github to obtain the
specific URLs.

Assuming there were no compilation errors. The build, hopefully the same as that described in the previous section, can
now be found in ./build/spectre-<version>. There should also be a dist sub directory which will contain a tarball suitable
for installing spectre on other systems.


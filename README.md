Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
==============================================================

The aim of this project was to create a single project that contains a number of tools and reusable tools for
manipulating and creating phylogenetic trees and networks.


Dependencies
============

Spectre is a maven project, so almost all the dependencies will be downloaded automatically as part of the Maven
buildcycle.  However, the one exception to this is a java library called metaopt, which provides a common interface to
several open source and commercial optimizers.  Metaopt can be obtained from: https://github.com/maplesond/metaopt.
Please follow the instructions in the metaopt README and make sure the metaopt library has been added to your local
maven repository.  After this, you can proceed with the installation instructions for spectre.


Installing
==========

Before installing spectre please ensure that the Java Runtime Environment (JRE) V1.7+ is installed. Also please consult
the `dependencies' section and ensure the instructions described there are carried out first.

Spectre can be installed either from a distributable tarball, or from source via a git clone. These steps for both
methods are described below.

From tarball
------------

Spectre is available as a distributable tarball. The installation process is simply involves unpacking the compressed
tarball to a directory of your choice: ``tar -xvf spectre-<version>.tar.gz``. This will create a directory called
``spectre-<version>`` and in there should be the following sub-directories:

* bin - contains scripts allowing the user to easily run all the tools.  In general, the scripts are all command line tools except for those having a ``-gui`` suffix.  Scripts for all platforms are available, in general, those with no extension should work on linux and mac platforms, and those with a ``.bat`` extension should run on windows.
* doc - a html, pdf and text copy of the complete manual
* etc - contains examples and configuration files
* repo - contains the java classes used by spectre
* support_jars - contains source and javadocs for the spectre codebase

Should you want to run the tools without referring to their paths, you should ensure the ‘bin’ sub-directory is on your
PATH environment variable.

From source
-----------

Spectre is a java 1.7 / maven project. Before compiling the source code, please make sure the following tools are installed:

* GIT
* Maven (make sure you set the m2_home environment variable to point at your Maven directory)
* JDK v1.7+  (make sure you set the JAVA_HOME environment variable to point at your JDK directory)
* Make
* Sphinx   (may require you to install phyton, also make sure the sphinx-build is on the path environment variable)

You also need to make sure that the system to are compiling on has internet access, as it will try to automatically
incorporate any required java dependencies via maven. Now type the following::

  git clone https://github.com/maplesond/spectre.git
  cd spectre
  mvn clean install

Note: If you cannot clone the git repositories using “https”, please try “ssh” instead. Consult github to obtain the
specific URLs.

Assuming there were no compilation errors. The build, hopefully the same as that described in the previous section, can
now be found in ./build/spectre-<version>. There should also be a dist sub directory which will contain a tarball
suitable for installing spectre on other systems.



Developing:
===========

Most modern IDEs support maven project structures, so there should be no reason to change IDEs, however, they may need a
maven plugin installed.  The exact details of how to open a maven project will vary from IDE to IDE but it should be a
simple process of just opening an existing project and pointing the IDE to the pom.xml in the root directory of the
spectre.  This should open all child modules within the master project.


Modules:
========

Within the parent maven project for spectre there is a single “pom.xml” which describes common properties for all child
modules.  This file contains details such as project details, developer list, compiler settings, unit test configuration,
common dependencies and some common jar packaging settings. Beyond the pom.xml there are the child modules themselves,
which each have their own pom.xml describing their specific configuration.  A short summary of each module follows:


Core:
-----

Contains classes that are used by other modules, that contain some kind of general functionality which means they can be
used in different situations.  These classes were broken down into sub groups based on their specific kind of
functionality as follows:

* ds - Data structures - Commonly used phylogenetic data structures relating to concepts such as: Splits, Trees, Networks, Distances and Quartets
* io - Input and Output - Classes that help loading and saving common phylogenetic file formats.  Specifically, Nexus and Phylip format.
* math - Maths - Classes related to common mathematical data structures and algorithms such as basic statistics, matrix algebra, and storing of tuples.
* ui - User interface - Supporting classes to help with both command line interfaces and graphical user interfaces
* util - Miscellaneous utilities - Anything we might conceivably want to reuse that doesn’t fit elsewhere.

Core is designed to contain most of the core functionality of the tools within spectre.  The idea being that other
developers can design there own tools whilst leveraging the functionality in this library.


FlatNJ:
-------

Constructs a flat split system from quardruples.



Net:
----

A set of tools that create or use phylogenetic networks.  These tools include:

* NetMake - Creates a compatible split system and a circular ordering from a distance matrix and either a single weighting or a hybrid weighting configuration.
* NetME - Constructs a minimum evolution tree from the specified network with its implied circular order.


Misc:
-----

Miscellaneous tools that might be useful for the user, and might be used directly by other modules.  These include:

* Convertor - Converts phylip to nexus format and vice versa (note this can be a lossy conversion)
* PhylipCorrector - Modifies phylip files.... ???
* Random Distance Generator Tool - Creates phylip or nexus files with a randomly generated distance matrix


Qtools:
-------

Contains a number of tools related to creating and manipulating quartets.  These include:

* QMaker - Create quartet systems from a set of trees
* QNet - A tool for creating a circular weighted split network from a quartet system
* SuperQ - Generates a circular split network from a set of trees.  SuperQ is a pipeline that incorporates QMaker and QNet.



Quick Start:
============

Assuming the user has access to the compiled executable jars for spectre, then they should only need JRE 1.7+ installed
on their system.  The tools can be found in the bin subfolder.



Availability:
=============

Open source code available on github: https://github.com/maplesond/spectre.git

License: GPL v3

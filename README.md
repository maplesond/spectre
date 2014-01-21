Phylogenetics Bundled Resources
===============================

The aim of this project was to create a single project that contains a number of tools and reusable tools for manipulating and creating phylogenetic trees and networks.


Dependencies
============

Phybre is a maven project, so almost all the dependencies will be downloaded automatically as part of the Maven buildcycle.  However, the one exception to this is a java library called metaopt, which provides a common interface to several open source and commerical optimizers.  Metaopt can be obtained from: https://github.com/maplesond/metaopt.  Please follow the instructions in the metaopt README and make sure the metaopt library has been added to your local maven repository.  After this, you can proceed with the installation instructions for phybre.


Installing
==========

Before installing PhyGen please ensure that the Java Runtime Environment (JRE) V1.7+ is installed.

PhyGen can be installed either from a distributable tarball, or from source via a git clone. These steps for both methods are described below.

From tarball
------------

PhyGen is available as a distributable tarball. The installation process is simply involves unpacking the compressed tarball to a directory of your choice: ``tar -xvf phybre-<version>.tar.gz``. This will create a directory called
``phybre-<version>`` and in there should be the following sub-directories:
• bin - contains scripts allowing the user to easily run all the tools.  In general, the scripts are all command line tools except for those having a ``-gui`` suffix.  Scripts for all platforms are available, in general, those with no extension should work on linux and mac platforms, and those with a ``.bat`` extension should run on windows.
• doc - a html, pdf and text copy of the complete manual
• etc - contains examples and configuration files
• repo - contains the java classes used by phybre
• support_jars - contains source and javadocs for the phybre codebase

Should you want to run the tools without referring to their paths, you should ensure the ‘bin’ sub-directory is on your PATH environment variable.

From source
-----------

Phygen is a java 1.7 / maven project. Before compiling the source code, please make sure the following tools are installed::

* GIT
* Maven 3
* JDK v1.7+
* Sphinx (If you would like to compile this documentation)

You also need to make sure that the system to are compiling on has internet access, as it will try to automatically incorporate any required java dependencies via maven. Now type the following::

  git clone https://github.com/maplesond/phybre.git
  cd phybre
  mvn clean install

Note: If you cannot clone the git repositories using “https”, please try “ssh” instead. Consult github to obtain the specific URLs.

Assuming there were no compilation errors. The build, hopefully the same as that described in the previous section, can now be found in ./build/phybre-<version>. There should also be a dist sub directory which will contain a tarball suitable for installing phybre on other systems.



Developing:
===========

Most modern IDEs support maven project structures, so there should be no reason to change IDEs, however, they may need a maven plugin installed.  The exact details of how to open a maven project will vary from IDE to IDE but it should be a simple process of just opening an existing project and pointing the IDE to the pom.xml in the root directory of the PhygenSuite.  This should open all child modules within the master project.


Modules:
========

Within the parent maven project for phybre there is a single “pom.xml” which describes common properties for all child modules.  This file contains details such as project details, developer list, compiler settings, unit test configuration, common dependencies and some common jar packaging settings. Beyond the pom.xml there are the child modules themselves, which each have their own pom.xml describing their specific configuration.  A short summary of each module follows:


Core:
-----

Contains classes that are used by other modules, that contain some kind of general functionality which means they can be used in different situations.  These classes were broken down into sub groups based on their specific kind of functionality as follows:

ds - Data structures - Commonly used phylogenetic data structures relating to concepts such as: Splits, Trees, Networks, Distances and Quartets
io - Input and Output - Classes that help loading and saving common phylogenetic file formats.  Specifically, Nexus and Phylip format.
math - Maths - Classes related to common mathematical data structures and algorithms such as basic statistics, matrix algebra, and storing of tuples.
ui - User interface - Supporting classes to help with both command line interfaces and graphical user interfaces
util - Miscellaneous utilities - Anything we might conceivably want to reuse that doesn’t fit elsewhere.


FlatNJ: 
-------

Constructs a flat split system from quardruples.



Netmake:
--------

Creates a compatible split system and a circular ordering from a distance matrix and either a single weighting or a hybrid weighting configuration.


Netme:
------

Constructs a minimum evolution tree from the specified network with its implied circular order.


Phygentools:
------------

Miscellaneous tools that might be useful for the user, and might be used directly by other modules.  These include:
Chopper - Breaks down trees into Quartets
Convertor - Converts phylip to nexus format and vice versa (note this can be a lossy conversion)
PhylipCorrector - Modifies phylip files.... ???
Random Distance Generator Tool - Creates phylip or nexus files with a randomly generated distance matrix
Scaler - Scales trees within a set of trees


Qnet:
-----

Constructs a circular split system from a set of quartets.


SuperQ:
-------

Constructs a circular split system from a set of weighted or unweighted partial trees by using quartets.



Quick Start:
============

Assuming the user has access to the compiled executable jars for phybre, then they should only need JRE 1.7+ installed on their system.  To run any of the jars type: “java -jar <jar_name>-<jar_version>.jar <options>”.  To discover the options available for each jar just type “java -jar <jar_name>-<jar_version>.jar --help”



Availability:
=============

Open source code available on github: https://github.com/maplesond/phybre.git

License: GPL v3

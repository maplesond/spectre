.. _developing:

Developing:
===========

Most modern IDEs support maven project structures, so there should be no reason to change IDEs, however, they may need a maven plugin installed.  The exact details of how to open a maven project will vary from IDE to IDE but it should be a simple process of just opening an existing project and pointing the IDE to the pom.xml in the root directory of the PhygenSuite.  This should open all child modules within the master project.

Within the parent maven project for the PhygenSuite there is a single “pom.xml” which describes common properties for all child modules.  This file contains details such as project details, developer list, compiler settings, unit test configuration, common dependencies and some common jar packaging settings. Beyond the pom.xml there are the child modules themselves, which each have their own pom.xml describing their specific configuration.  A short summary of each module follows:


Core:
-----

Contains classes that are used by other modules, that contain some kind of general functionality which means they can be used in different situations.  These classes were broken down into sub groups based on their specific kind of functionality as follows:

ds - Data structures - Commonly used phylogenetic data structures relating to concepts such as: Splits, Trees, Networks, Distances and Quartets
io - Input and Output - Classes that help loading and saving common phylogenetic file formats.  Specifically, Nexus and Phylip format.
math - Maths - Classes related to common mathematical data structures and algorithms such as basic statistics, matrix algebra, and storing of tuples.
ui - User interface - Supporting classes to help with both command line interfaces and graphical user interfaces
util - Miscellaneous utilities - Anything we might conceivably want to reuse that doesn’t fit elsewhere.


FlatNJ:
--------

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

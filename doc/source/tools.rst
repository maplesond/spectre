
.. _tools:

Running the Tools
=================

Unless specified otherwise all tools listed here are java programs that, for ease of use, have been wrapped in automatically generated
executable bash scripts for unix-based system (linux, mac, cygwin) and batch scripts for windows.  There is no direct way
of altering the JVM memory settings for these scripts.  Therefore we recommend, if you require additional memory,
that you use the ``JAVA_OPTS`` environment variable.  An example command that alters the max memory available to a program
may therefore look like this on a unix terminal::

  JAVA_OPTS='-Xmx4g'; ./superq-gui


FlatNJ:
--------

Constructs a flat split system from quardruples.


Netmake:
--------


Creates a compatible split system and a circular ordering from a distance matrix and either a single weighting or a hybrid weighting configuration.



Netme:
------


Constructs a minimum evolution tree from the specified network with its implied circular order.


Chopper
-------

Breaks down trees into Quartets

Convertor
---------

Converts phylip to nexus format and vice versa (note this can be a lossy conversion)


PhylipCorrector
---------------
Modifies phylip files.... ???


Random Distance Generator
-------------------------

Creates phylip or nexus files with a randomly generated distance matrix

Scaler
------

Scales trees within a set of trees

Qnet
----

Constructs a circular split system from a set of quartets.


SuperQ
------

Constructs a circular split system from a set of weighted or unweighted partial trees by using quartets.

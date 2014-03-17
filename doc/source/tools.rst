
.. _tools:

Running the Tools
=================

Unless specified otherwise all tools listed here are java programs that, for ease of use, have been wrapped in automatically generated
executable bash scripts for unix-based system (linux, mac, cygwin) and batch scripts for windows.  There is no direct way
of altering the JVM memory settings for these scripts.  Therefore we recommend, if you require additional memory, that
you use the ``JAVA_OPTS`` environment variable, which is recognised by all the scripts.  If you want to set the Xmx value
permanently to 4GB you would type something like this on a bash shell::

  export JAVA_OPTS='-Xmx4g'

Should you only wish to set the memory for a particular instance of a spectre program (in this case ``superq-gui`` your
command would like this on a bash shell::

  JAVA_OPTS='-Xmx4g'; ./superq-gui

You may require a different syntax in alternate shells or operating systems.  Any java VM option can be set using this
environment variable.


Wrapping Scripts and Platform Support
-------------------------------------

We have tried to make all tools in spectre as platform independent as possible.  However, in order to use the java wrapping
scripts which simplify callling of the tools, we need to use different languages on different platforms.  On unix and mac
platforms, these scripts have been generated without any extension, to make user experience of running the tools as close
to that of running a native binary as possible.  However, on windows the scripts require a ``.bat`` extension.  Please keep
this in mind when reading the tool specific documentation.  All examples are assumed to be running on a unix or mac system,
so if you are running on windows please add a ``.bat`` suffix to the script name.


Optimisers
----------

A number of tools use external optimisers ...

Consider linear / quadratic programming.
Free / Commerical license.

Talk about metaopt briefly.


Gurobi
~~~~~~

JOptimiser
~~~~~~~~~~

GLPK
~~~~

Apache
~~~~~~


Tool Groups
-----------

The tools within spectre are divided into sub-groups.  Those sub groups include:

.. toctree::
    :maxdepth: 1

    flatnj
    network_tools
    quartet_tools
    padre
    misc_tools

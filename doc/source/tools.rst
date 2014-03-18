.. _tools:

The Tools
=================

Before discussing the tools available in spectre in detail, we will run through some spectre specific considerations
that you should be aware of such as where to find the tools, how to change the memory limits.


Wrapping scripts and platform support
-------------------------------------

We have tried to make all tools in spectre as platform independent and as simple to use as possible.  We have made
wrapping scripts for each tool in spectre to save the use typing ``java -jar <path to executable jar`` everytime they
wish to run a tool.  On unix, mac and cygwin platforms these scripts have been generated without any extension, to make user
experience of running the tools as close to that of running a native binary as possible.  However, on windows the scripts
have a ``.bat`` extension.  Please keep this in mind when reading the tool specific documentation.  All examples in this
documentation are assumed to be running on a unix or mac system, so if you are running on windows please add a ``.bat`` suffix
to the script name.

All these executable scripts can be found in the `bin` subdirectory of the spectre installation.  If compiling spectre
from source code the scripts can be found in `<project_dir>/build/spectre-<version>/bin`.  In order to run spectre tools
without specifying the full path add the bin directory onto your PATH environment variable.


Changing the JVM memory limits
------------------------------

Normally 64-bit java processes are capped to 2GB of Heap Space, although you can find out the actual limit on your system
by typing on a linux, mac or cygwin machine::

  ``java -XX:+PrintFlagsFinal -version | grep MaxHeapSize``.

On windows just type::

  ``java -XX:+PrintFlagsFinal -version``

And then manually find the line containing MaxHeapSize.  Sometimes some of the
tools may need more memory than this when processing large datasets.  If you encounter an OutOfMemory error while running
a spectre tool and you have more memory available on your system then you might want to consider increasing max heap size
of your process.

To do this with the spectre scripts then recommended way is to modify an environment variable called ``JAVA_OPTS`` with
the JVM options you wish to change.  This environment variable should be recognised by all the spectre scripts.  So, for
example, if you want to set the Xmx value (the value associated with max heap size) permanently to 4GB you would type
something like this on a bash shell::

  export JAVA_OPTS='-Xmx4g'

Should you only wish to set the memory for a particular instance of a spectre program (in this case ``superq-gui``) your
command would like this on a bash shell::

  JAVA_OPTS='-Xmx4g'; ./superq-gui

Any java VM option can be set using the JAVA_OPTS environment variable.

NOTE: you will require a slightly different syntax on windows if not using cygwin.



Optimisers
----------

Some tools within spectre use optimizers to maximise or minimise an objective function given a set of constraints.  In
order to provide flexibility to test out different optimizers with spectre tools we enable the developer to write their
problems to be solved by an optimizer using our own data types.  These are then translated to the data types for the
optimizer selected by the user at runtime.  This way the developer can keep their code optimizer agnostic.

The code for managing optimizers has been extracted from spectre and is available in a separate project called *metaopt*.
This is freely available from https://github.com/maplesond/metaopt.  Metaopt should be installed and configured with
the optimisers you wish to use prior to compilation of spectre.

Currently the following optimizers are supported by metaopt, each of which has its own pros and cons:

+-----------------+------------+---------------+------+-------------------------------------------------------------+
| Optimiser       | Quadratic? | Configuration | Free | Description                                                 |
+=================+============+===============+======+=============================================================+
| Apache Math 3   | No         | Built in      | Yes  | Part of Apache Math 3, available through maven central      |
+-----------------+------------+---------------+------+-------------------------------------------------------------+
| GLPK            | No         | External      | Yes  | GNU Linear Programming Toolkit                              |
+-----------------+------------+---------------+------+-------------------------------------------------------------+
| JOptimizer      | Yes        | Bundled       | Yes  | Pure java implementation but some dependencies not in maven |
+-----------------+------------+---------------+------+-------------------------------------------------------------+
| Gurobi          | Yes        | External      | No   | A popular and fast but commercial optimizer                 |
+-----------------+------------+---------------+------+-------------------------------------------------------------+

See :ref:`installation` for more details.  Also please refer to the metaopt README file.


Tool groups
-----------

The tools within spectre are divided into sub-groups based on their functionality.  These sub groups are:

.. toctree::
    :maxdepth: 1

    flatnj
    network_tools
    quartet_tools
    padre
    misc_tools

.. _quartet_tools:

Quartet Tools
=============

The quartet tools are all built around the concept of creating circular split systems from a set of weighted or unweighted
partial trees using quartet systems.  SuperQ is a pipeline that brings together all the tools in this package and in most
cases SuperQ will be the only Quartet tool you need to use.  However, all the subtools within superq are separately
available in case you wish to conduct a more detailed and controlled analysis.

SuperQ
------

Constructs a phylogenetic supernetwork from a set of weighted or unweighted partial trees by using quartets. SuperQ is a pipeline
that incorporates QMaker, Scaler, QNet and SFilter, which are all described in more detail later in this section.  These
tools are all available for you to use individually in case you wish to conduct a more detailed and controlled experiment
for generating circular split systems from your partial trees, however in most cases SuperQ will be the only Quartet tool
you need to use.

To run SuperQ use the ``superq`` script like this::

  superq -o <outfile> [-s <scaling_optimiser> -x <primary_optimiser>
         -y <secondary_optimiser> -b <objective> -f <filter_threshold>] <input_file> [<input_file>]...

Alternatively, SuperQ has a graphical interface that can be accessed via the Tools menu in the Spectre viewer.  This interface
provides all the same options as available through the command line.

.. image:: images/superq-gui.png
    :scale: 100 %

When running SuperQ for large datasets, it is recommended to allocate more RAM for the heap space. Otherwise an
OutOfMemoryError may occur. Heap space can be increased using the Java VM options ``-Xms`` or ``-Xmx``. See :ref:`running` for
more information on how to adjust these options.



QMaker
------

QMaker enables conversion of several types of phylogenetic data into quartet phylogeny information.

Currently supported input types include:

* qweights files, see syntax below - simple, monopurpose internal format used by QNet and QMaker, originally appeared in ([10])
* Nexus files with st quartets (old format) or Quartets blocks ([6], [5])
* Nexus files with st splits (old format) or Splits blocks ([6], [5])
* Nexus files with distance blocks ([6], [5])
* Treebase syntax Nexus files with TREES blocks ([6])
* Newick trees, with or without branch lengths [8]

To run QMaker use the ``qmaker`` script like this::

  qmaker [--output_prefix <output_prefix> --optimiser <scaling optimiser>] <input_file> [<input_file>]...

QMaker will automatically determine the file type based on the filename extension.

If a ``newick`` file is input, the loader expects a file containing a single line describing a standard Newick format tree.

If file type is ``qweight``, the loader expects a standard qweights file (see below for specifications).

If file type is ”nexus:st quartets”, ”nexus:distances”, ”nexus:trees” or ”nexus:st splits”,
Nexus files with st quartets, distances, trees or st splits (alternately
Splits) blocks are expected.
<outfile> is the name of the resulting qweights file.



Scaler
------

Scales trees within a set of trees


SFilter
-------

Once a phylogenetic supernetwork has been computed, it may be desirable to filter out weakly supported splits that may
result from numerical error or noise. This can be done using the SFilter tool::

    sfilter [--output <outfile> --min_threshold <threshold>] <infile>

<infile> and <outfile> should be Nexus files with st splits blocks, whereas <threshold> is a real number. Only those splits
whose weight is higher than the threshold number times the weight of the most highly weighted conflicting split are
retained from <infile> to <outfile>.



Qnet
----

QNet, short for Quartet Network, is an algorithm to combine quartet phylogenies into a phylogenetic network, as well as
the name of the software suite implementing the method. It functions by first finding a cyclic ordering between the taxa
in the input set, then estimate via non-negative least squares the set of split weights that best recreates the original
set of quartet weights. Its running time and memory requirements are on the order of number of possible quartets, that is,

After creating a QNet network, a subapplication, Filterer, may be used to exclude splits that are poorly supported for
ease of visualisation. This is done by requiring that for a split to be displayed, its weight must exceed a user-defined
threshold ratio times the weight of the strongest split that conflicts with this split.

To run QNet use the ``qnet`` script like this::

  qnet sfilter -o <outfile> [--log --tolerance <threshold> --optimiser <optimiser>] <infile>

Alternatively, QNet has a simple GUI which can be started by simply running ``qnet-gui`` without any arguments.




Visualising the results
-----------------------

The final output will be a Nexus file, which can be viewed by the SPECTRE network viewing tool, or by external tools
such as SplitsTree ([1]).


QWeights example file
---------------------

The following is an example QWeights file::

  taxanumber: 6;
  description: artificial data;
  sense: max;
  taxon: 001 name: a;
  taxon: 002 name: b;
  taxon: 003 name: c;
  taxon: 004 name: d;
  taxon: 005 name: e;
  taxon: 006 name: f;
  quartet: 001 002 003 004 weights: 200 0 200;
  quartet: 001 002 003 005 weights: 200 0 200;
  quartet: 001 002 003 006 weights: 200 0 200;
  quartet: 001 002 004 005 weights: 210 0 210;
  quartet: 001 002 004 006 weights: 210 0 210;
  quartet: 001 002 005 006 weights: 410 0 410;
  quartet: 001 003 004 005 weights: 10 0 10;
  quartet: 001 003 004 006 weights: 10 0 10;
  quartet: 001 003 005 006 weights: 210 0 210;
  quartet: 001 004 005 006 weights: 200 0 200;
  quartet: 002 003 004 005 weights: 10 0 10;
  quartet: 002 003 004 006 weights: 10 0 10;
  quartet: 002 003 005 006 weights: 210 0 210;
  quartet: 002 004 005 006 weights: 200 0 200;
  quartet: 003 004 005 006 weights: 200 0 200;


Credits
-------

The original version QNet and the original set of quartet tools were developed by:

* Stephan Grunewald
* Kristoffer Forslund

The original version of SuperQ was developed by:

* Sarah Bastkowski

The tools have been reengineered, optimised and integrated into SPECTRE by:

* Daniel Mapleson

All the tools have been developed, since inception, under the supervision of:

* Andreas Spillner
* Vincent Moulton

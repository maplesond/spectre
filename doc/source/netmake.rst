.. _netmake:

Netmake
=======

Netmake creates a compatible split system with circular ordering from a distance matrix.  Netmake's default mode uses the
well-known Neighbor-Net algorithm (Bryant and Moulton, 2004) for quickly (O(n^3)) constructing circular split systems.
Neighbor-Net is an extension of the Neighbor Joining (NJ) algorithm that is used to create trees, with the difference that instead
of agglomerating two neighbors into a new node immediately, Neighbor-Net pairs up another set of candidate nodes before agglomerating
into a new node.  This process generates a collection of splits for which it might not be possible to represent in a single
tree, hence can be used to create split networks.  Running Neighbor-Net via netmake is straight forward. The only option
the user might wish to specify is the output_prefix. The input file can be either nexus format file containing a distances
block, a phylip format distance matrix or a emboss format distance matrix.  Note if you only have multiple sequence alignment data
we recommend converting to a distance matrix using a tool like emboss or phylip.  Netmake will produce a circular split network in nexus format.  So a typical
Neighbor-Net run producing an output file called "bees_out.network.nex" looks like this::

  netmake -o bees_out bees.nex


Netmake's alternate mode is based partially on work by Levy and Pachter (Levy and Pachter, 2008), where we construct a
circular split system by greedily optimising the minimum evolution criterion.  For more information on this mode
please see :download:`Thesis Chapter 4 <assets/spectre_bastkowskis_thesis.pdf>`.  Netmake in this mode requires that the
user specify the ``-alt`` switch.  Additional options maybe specified to select the runmode for the tool.  The full list
of options takes this form::

  netmake -alt [-o <output_prefix> -w <weighting_1> -x <weighting_2> -z <tree_weighting> <input_file>

A simple command line to run using the travelling sales man circular ordering configuration (the default option in alt mode) looks like this::

  netmake -alt -o bees_tsp_out bees.nex

And the command line run netmake in hybrid greedy minimum evolution would look like this::

  netmake -alt -o bees.t -w GREEDY_ME -x TREE bees.nex


Alternatively, Neighbor-Net and the netmake alternative can be invoked through the Tools menu in Spectre Viewer.


References
----------

* D. Bryant and V. Moulton. Neighbor-net: an agglomerative method for the construction of phylogenetic networks. Mol. Biol. Evol., 21:255–265, 2004.
* D. Levy and L. Pachter. The Neighbor-Net Algorithm. ArXiv. 2008.
* S. Bastkowski. From Trees to Networks and Back.  :download:`PhD Thesis <assets/spectre_bastkowskis_thesis.pdf>`. 2013
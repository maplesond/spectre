.. _netmake:

Netmake
=======

Netmake creates a compatible split system with circular ordering from a distance matrix.  Netmake's default mode uses the
well-known Neighbor-Net algorithm (Bryant and Moulton, 2004) for quickly (O(n^3)) constructing circular split systems.
Neighbor-Net is an extension of the Neighbor Joining (NJ) algorithm that is used to create trees, with the difference that instead
of agglomerating two neighbors into a new node immediately, Neighbor-Net pairs up another set of candidate nodes before agglomerating
into a new node.  This process generates a collection of splits for which it might not be possible to represent in a single
tree, hence can be used to create split networks.

Neighbor-Net via netmake takes in either a distance matrix in nexus, phylip or emboss format, or an MSA in fasta or nexus format.
If an MSA is provided netmake calculates the distance matrix from the MSA using the Jukes Cantor method by default, although the user
can user an alternate distance calculation using the '-dc' option.  Netmake will produce a circular split network in nexus format so
a typical Neighbor-Net run from a nexus file (bees.nex) containing a distance matrix would produce a circular split system
in an output file called "bees_out.network.nex" with the following command::

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
.. _tools:

The Tools
=========

The main graphical interface for the software is accessed via the :ref:`spectre`.  From here you can visualise and modify trees and
networks and launch most of the main tools within the toolkit.

The main tools and their workflows are listed here.

:ref:`netmake` - Creates a circular split system from a distance matrix producing an outer-planar network.
Allows the user to select either NeighborNet or NetMake's own method for constructing the circular ordering of taxa.
The distance matrix can be presented in any of the following formats:

1. Nexus format.  Must contain both "taxa" and "distances" block.
2. Phylip format.

:ref:`flatnj` - Creates a flat planar split network where labels are not forced to the edges of the network.  FlatNJ can
therefore produce richer and less distorted networks that Neighbornet if supported by the data.  FlatNJ however cannot be
driven from a distance matrix alone, requiring quartet data instead.  Fortunately, FlatNJ can automatically generate
quartets from the following input types:

1. Multiple sequence alignment in Fasta or Nexus format.  If Nexus format is used and contains a "distances" block, this information can help guide split weight estimation.
2. Geographical coordinates in Nexus format
3. Weighted split system in Nexus format

:ref:`superq` - Creates a Supernetwork from a collection of partial input trees.  Input trees can be derived from several different input formats:

1. Qweights files, see syntax below - simple, monopurpose internal format used by QNet and QMaker.
2. Nexus files with "st quartets" (old format) or Quartets blocks
3. Nexus files with "st splits" (old format) or Splits blocks
4. Nexus files with distance blocks
5. Treebase syntax Nexus files with TREES blocks
6. Newick trees, with or without branch lengths

The tools within spectre are divided into sub-groups based on their functionality.  More detail about the specific tools are listed in these sub-groups:


.. toctree::
        :maxdepth: 1

        netmake
        netme
        flatnj
        superq
        viewer
        misc_tools

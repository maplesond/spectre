.. _tools:

The Tools
=========

The tools within spectre are divided into sub-groups based on their functionality.  These sub groups are:

.. toctree::
    :maxdepth: 1

    network_tools
    flatnj
    quartet_tools
    netview
    misc_tools


The tools in spectre focus around generating phylogenetic networks from a variety of inputs.  The main workflows are listed here.

*NetMake* - Creates a circular split system from a distance matrix producing an outer-planar network.
Allows the user to select either NeighborNet or NetMake's own method for constructing the circular ordering of taxa.
The distance matrix can be presented in any of the following formats:

1. Nexus format.  Must contain both "taxa" and "distances" block.
2. Phylip format.

*FlatNJ* - Creates a flat planar split network where labels are not forced to the edges of the network.  FlatNJ can
therefore produce richer and less distorted networks that Neighbornet if supported by the data.  FlatNJ however cannot be
driven from a distance matrix alone, requiring quartet data instead.  Fortunatly, FlatNJ can automatically generate
quartets from the following input types:

1. Multiple sequence alignment in Fasta or Nexus format.  If Nexus format is used and contains a "distances" block, this information can help guide split weight estimation.
2. Geographical coordinates in Nexus format
3. Weighted split system in Nexus format

*SuperQ* - Creates a Supernetwork from a collection of partial input trees.  Input trees can be derived from several different input formats:

1. Qweights files, see syntax below - simple, monopurpose internal format used by QNet and QMaker.
2. Nexus files with "st quartets" (old format) or Quartets blocks
3. Nexus files with "st splits" (old format) or Splits blocks
4. Nexus files with distance blocks
5. Treebase syntax Nexus files with TREES blocks
6. Newick trees, with or without branch lengths


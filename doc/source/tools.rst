.. _tools:

The Tools
=========

The tools within spectre are divided into sub-groups based on their functionality.  These sub groups are:

.. toctree::
    :maxdepth: 1

    flatnj
    network_tools
    quartet_tools
    netview
    misc_tools


The tools in spectre focus around generating phylogenetic networks from a variety of inputs.  The main workflows are listed here.

*FlatNJ* - Creates a flat planar split network from quadruples (systems of 4-splits).  FlatNJ can automatically generate quadruples from the following input types:

1. Multiple sequence alignment in Fasta or Nexus format.  If Nexus format is used and contains a "distances" block, this information can help guide split weight estimation.
2. Geographical coordinates in Nexus format
3. Weighted split system in Nexus format

*SuperQ* - Creates a Supernetwork from a collection of partial input trees.  Input trees can be derived from several different input formats:

1. Qweights files, see syntax below - simple, monopurpose internal format used by QNet and QMaker.
2. Nexus files with st quartets (old format) or Quartets blocks
3. Nexus files with st splits (old format) or Splits blocks
4. Nexus files with distance blocks
5. Treebase syntax Nexus files with TREES blocks
6. Newick trees, with or without branch lengths

*NetMake* - Creates a circular split system from a distance matrix.  Allows the user to select either NeighborNet or NetMake's own method for constructing the circular ordering of taxa.  The distance matrix can be presented in any of the following formats:

1. Nexus format.  Must contain both "taxa" and "distances" block.
2. Phylip format.
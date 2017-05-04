.. _misc_tools:

Miscellaneous Tools
===================

These tools were developed for particular tasks during the development of the Spectre tools and are included here in case
they are of use to others.


SFilter
-------

Sometimes it is useful to filter out splits in a split system that are weakly supported, i.e. have a low weight.  Sometimes
these splits may be the result from numerical error or noise. The sfilter tool can filter splits from a nexus file that have
a weight below a certain threshold.  Example usage::

    sfilter [--output <outfile> --min_threshold <threshold>] <infile>

<infile> and <outfile> should be Nexus files with st splits blocks, whereas <threshold> is a real number. Only those splits
whose weight is higher than the threshold number times the weight of the most highly weighted conflicting split are
retained from <infile> to <outfile>.


Distance Matrix Generator
-------------------------

This tool can be run in one of two ways.  The first mode is determined by the positional argument passed in.  If that argument
 is an integer then this tool creates one or more phylip or nexus files with a randomly generated distance matrix using the
 integer value representing the number of taxa.  Example usage::

    distmatgen -s 10 -t nexus -o randomdm 20

So this will create 10 nexus files containing a "distances" block containing 20 taxa each, with distances between each taxa randomly
generated separately for each file.  Output filenames will be of the format "randomdm-<sample_index>.nex".

Alternatively, if the argument is an MSA file in Fasta or Nexus format then we generate a distance matrix based on the
specified calculator.  By default this is Jukes Cantor.  Example usage::

    distmatgen -t phylip -o bees bees.fa

This command line would generate a distance matrix output to a phylip file called "bees.phy" from an MSA stored in fasta format.

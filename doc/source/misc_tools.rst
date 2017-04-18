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


Random Distance Generator
-------------------------

Creates one or more phylip or nexus files with a randomly generated distance matrix given a defined number of taxa.  Example usage::

    random_distmat -s 10 -t nexus -o randomdm 20

This will create 10 nexus files containing a "distances" block containing 20 taxa each, with distances between each taxa randomly
generated separately for each file.  Output filenames will be of the format "randomdm-<sample_index>.nex".

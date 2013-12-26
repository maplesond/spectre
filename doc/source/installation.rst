.. _installing:

Installing
==========

Before installing PhyGen please ensure that the Java Runtime Environment (JRE) V1.7+ is installed.

PhyGen can be installed either from a distributable tarball, or from source via a git clone. These steps for both methods are described below.

From tarball
------------

PhyGen is available as a distributable tarball. The installation process is simply involves unpacking the compressed tarball to a directory of your choice: ``tar -xvf phygen-<version>.tar.gz``. This will create a directory called
``phygen-<version>`` and in there should be the following sub-directories:
• bin - contains scripts allowing the user to easily run all the tools.  In general, the scripts are all command line tools except for those having a ``-gui`` suffix.  Scripts for all platforms are available, in general, those with no extension should work on linux and mac platforms, and those with a ``.bat`` extension should run on windows.
• doc - a html, pdf and text copy of the complete manual
• etc - contains examples and configuration files
• repo - contains the java classes used by phygen
• support_jars - contains source and javadocs for the phygen codebase

Should you want to run the tools without referring to their paths, you should ensure the ‘bin’ sub-directory is on your PATH environment variable.

From source
-----------

Phygen is a java 1.7 / maven project. Before compiling the source code, please make sure the following tools are installed::

* GIT
* Maven 3
* JDK v1.7+
* Sphinx (If you would like to compile this documentation)

You also need to make sure that the system to are compiling on has internet access, as it will try to automatically incorporate any required java dependencies via maven. Now type the following::

  git clone https://github.com/maplesond/phygen.git
  cd phygen
  mvn clean install

Note: If you cannot clone the git repositories using “https”, please try “ssh” instead. Consult github to obtain the specific URLs.

Assuming there were no compilation errors. The build, hopefully the same as that described in the previous section, can now be found in ./build/phygen-<version>. There should also be a dist sub directory which will contain a tarball suitable for installing phygen on other systems.


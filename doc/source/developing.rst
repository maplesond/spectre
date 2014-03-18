.. _developing:

Developing the codebase
=======================

Spectre is designed to be open source and easy to extend, maintain and develop by the community.  This section of the
manual will help you work with the codebase, whether you just plan to use our core library in your own java application
or want to modify or extend our codebase.


Making your own apps using our core library
-------------------------------------------

IMPORTANT NOTE: We have not deployed the core library onto a maven server yet, so this particular section should be read as a wish
list for the future... it may be complicated by metaopt as we can't easily get JOptimiser, GLPK or gurobi into maven.

If you wish to use our core library within your own applications then we recommend that you use maven for our own project.
You can then include our library as a maven dependency by adding the following snippet into the dependencies section in
your pom.xml::

  <dependency>
    <groupId>uk.ac.uea.cmp.spectre</groupId>
    <artifactId>core</artifactId>
    <version>[PUT-VERSION-HERE]</version>
  </dependency>

Currently, you will also need to add our public maven repository to your pom as well.  This is a temporary measure until
we can get our core library in maven central (at which point you will not need to add our public facing maven server).
This snippet should be added to the repositories section of your pom.xml (if the repositories section does not already
exist then create it)::

  <repository>
    <id>tgac-repo</id>
    <name>TGAC Maven Repository</name>
    <url>https://repos.tgac.ac.uk/maven/repo</url>
  </repository>


And that's it!  You should now automatically download the library when you import maven changes or run your maven build
cycle.

The rest of this section assumes you want to modify or extend the spectre codebase.


Version Control
---------------

The source code for spectre is version controlled using GIT.  The public repository is hosted on github at
https://github.com/maplesond/spectre.git

If you plan to make contributions directly to the spectre codebase and want to work closely with us on a new tool or
feature then please email daniel.mapleson@tgac.ac.uk about your planned changes.  He can grant you write access to the
codebase.  We use the `Git-Flow <http://nvie.com/posts/a-successful-git-branching-model/>`_ branching
model in order to make it easier to work on the codebase as a team.  The main takeaway message here is do NOT commit
changes directly to the master branch as this might effect the stability of the suite for everyone!  To make managing the
branches easier we recommend a gitflow aware client tool, such as `SmartGIT <http://www.syntevo.com/smartgithg/>`_.

However, for most external developers we recommend you `fork <https://help.github.com/articles/fork-a-repo/>`_
our github repository.  You are then free to use whichever branching model you like in your own fork.  If you want to
merge back changes to the original codebase then do so using the `pull request <https://help.github.com/articles/using-pull-requests>`_
mechanism.



Integrated Development Environments
-----------------------------------

Spectre was developed in the Java programming language and has become a relatively large project.  Because Java is a relatively
verbose language (as compared to a language like python), we strongly recommend using an Integrated Development Environment
(IDE).  This will enable you to easily visualise the project structure, navigate around the code, refactor code and generally be
productive.

Spectre was developed using the `IntelliJ <http://www.jetbrains.com/idea/>`_ IDE, but, while this is our preferred IDE, we do not store the
IntelliJ project files in the repository.  Instead we use maven to manage the project structure, making the spectre
codebase IDE agnostic, so you should be able to use whichever Java IDE you are most familiar with.  Most modern Java IDEs,
and all those that have a wide user base, will also support maven project object models (POMs).  The exact details of how to
load spectre will vary from IDE to IDE but it should be as simple as opening an existing project and selecting the pom.xml
in the root directory of spectre.  The IDE should then load the project structure.  You should now be ready to view, modify
or extend the codebase.


Project Structure
-----------------

Within the parent maven project for spectre there is a single “pom.xml” which describes common properties for all child
modules.  This file contains details such as project details, developer list, compiler settings, unit test configuration,
common dependencies and some common jar packaging settings. Beyond the pom.xml there are the child modules themselves,
which each have their own pom.xml describing their specific configuration.  Broadly speaking the project is structured
into two main areas: *core* and *apps*.

**Core** Contains classes that are used by other modules, that contain some kind of general functionality which means they can be
used in different situations.  These classes were broken down into sub groups based on their specific kind of functionality
as follows:

+--------------------+--------------+------------------------------------------------------------------+
| Class Group        | Package name | Description                                                      |
+====================+==============+==================================================================+
| Algorithms         | alg          | Implementations of Phylogenetic Algorithms                       |
+--------------------+--------------+----------+-------------------------------------------------------+
| Data structures    | ds           | Phylogenetic data structures relating to concepts such as:       |
|                    |              |  Splits, Trees, Networks, Distances and Quartets                 |
+--------------------+--------------+------------------------------------------------------------------+
| File Handling      | io           | Loading and saving common phylogenetic file formats.             |
|                    |              | Specifically, Nexus and Phylip format.                           |
+--------------------+--------------+------------------------------------------------------------------+
| Mathematics        | math         | Math related functionality such as basic statistics, matrix      |
|                    |              | algebra, and storing of tuples.                                  |
+--------------------+--------------+------------------------------------------------------------------+
| User Interface     | ui           | Functionality to help with Command Line Interfaces and Graphical |
|                    |              | interfaces                                                       |
+--------------------+--------------+------------------------------------------------------------------+
| Misc Utils         | util         | Miscellaneous functionality                                      |
+--------------------+--------------+------------------------------------------------------------------+

**Apps** Contains all the applications managed by spectre.  Most of these apps rely heavily on the *core* library.





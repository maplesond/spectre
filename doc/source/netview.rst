.. _misc_tools:

Network Viewer
==============

The Network Viewing tool allows users to visualise networks that are stored as "network" blocks within a nexus file.


Opening a file
--------------

There are several ways a user can open a valid nexus file:

 1. Use the file menu
 2. Drag and drop file into window
 3. Launch netview from commandline providing the nexus file as an argument


Saving an image
---------------

Users can save an image of the network to disk in multiple formats via the menu item in the File menu.  Supported formats are:

 1. PDF - Contains vector graphics, suitable for publications.
 2. PNG - Rasterised image, lossless compression
 3. GIF - Rasterised image, lossless compression
 4. JPG - Rasterised image, lossy compression

When typing a file name please ensure that it has one of the extensions listed above in order to for netview to save to
the correct format.

Selecting nodes
---------------

The user can select nodes either by selecting via mouse click or by dragging a box over groups of nodes.  In addition, the
Edit menu provides options for either selecting all nodes, or by selecting nodes through a text search of labels.


Layout Options
--------------

Users can orientate the image via the Layout menu.  The clicking the rotation option (or holding the Ctrl key) puts the
window in rotation mode.  Then by holding the left-mouse button and moving the mouse the user can alter the rotation of
the image.  The user can also flip the image either vertically or horizontally via the menu button.

The user can also collapse trivial split in the network in order to simplify the image via the menu button in the Layout menu


Labelling Options
-----------------

The viewer provides multiple options for controlling the look, feel and positioning of labelled nodes in the network.  These
options are controlled via the Labelling menu.

Show labels - Turns on or off whether node labels should be visible

Color labels - Turns on or off whether the labels color should be inverted (i.e. Black text on white background, or white
text in a black box).  This option is only relevant if show labels is switched on.

Format selected nodes - Clicking on this option brings up a dialog box that allows users to modify the shape, size and color
of the currently selected nodes.  In addition, they can modify the size and style of the current selected nodes' label text.

Fix all label positions - This forces the labels to stay in the same proximity to the node as current viewed, even after
the image is rotated or flipped.

Optimise labels - If the label positions are fixed this will relocate them to automatically determined optimial locations.

Leaders - Labels of internal nodes are often located towards the edge of the window, outside the network itself in order
to aid readability.  In this case "leaders" are used to link the label to the node.  With this sub-menu the user can control
the look and feel of the leaders.



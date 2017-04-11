/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2017  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.viewer;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.Identifier;
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.network.FlatNetwork;
import uk.ac.uea.cmp.spectre.core.ds.network.Network;
import uk.ac.uea.cmp.spectre.core.ds.network.NetworkLabel;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.PermutationSequenceDraw;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.ViewerConfig;
import uk.ac.uea.cmp.spectre.core.io.nexus.Nexus;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.spectre.core.ui.cli.CommandLineHelper;
import uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel;
import uk.ac.uea.cmp.spectre.core.ui.gui.geom.Leaders;
import uk.ac.uea.cmp.spectre.core.util.LogConfig;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author balvociute and maplesond
 */
public class NetView extends javax.swing.JFrame implements DropTargetListener {

    private static Logger log = LoggerFactory.getLogger(NetView.class);

    private static String BIN_NAME = "netview";

    private static String OPT_VERBOSE = "verbose";
    private static String OPT_DISPOSE = "dispose_on_close";
    private final String TITLE = "NetView";

    private Point startPoint;
    private JFrame format;
    private JFrame formatLabels;
    private DropTarget dt;
    private Window drawing;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenuItem mnuFileOpen;
    private javax.swing.JMenuItem mnuFileSave;
    private javax.swing.JMenuItem mnuFileSaveas;
    private javax.swing.JMenuItem mnuFileSaveimage;
    private javax.swing.JMenuItem mnuFileExit;
    private javax.swing.JMenuItem mnuLabelingFormat;
    private javax.swing.JMenu mnuEdit;
    private javax.swing.JMenuItem mnuEditCopy;
    private javax.swing.JMenuItem mnuEditSelectall;
    private javax.swing.JMenuItem mnuEditFind;
    private javax.swing.JMenu mnuView;
    private javax.swing.JMenuItem mnuViewRotateleft;
    private javax.swing.JMenuItem mnuViewRotateright;
    private javax.swing.JMenuItem mnuViewZoomin;
    private javax.swing.JMenuItem mnuViewZoomout;
    private javax.swing.JMenuItem mnuViewFliphorizontal;
    private javax.swing.JMenuItem mnuViewFlipvertical;
    private javax.swing.JMenuItem mnuViewOptimiseLayout;
    private javax.swing.JCheckBoxMenuItem mnuViewShowTrivial;
    private javax.swing.JCheckBoxMenuItem mnuViewShowRange;
    private javax.swing.JMenu mnuLabeling;
    private javax.swing.JCheckBoxMenuItem mnuLabelingColor;
    private javax.swing.JCheckBoxMenuItem mnuLabelingFix;
    private javax.swing.JCheckBoxMenuItem mnuLabelingShow;
    private javax.swing.JMenu mnuLabelingLeaders;
    private javax.swing.JMenuItem mnuLabelingLeadersColor;
    private javax.swing.JRadioButtonMenuItem mnuLabelingLeadersBended;
    private javax.swing.JRadioButtonMenuItem mnuLabelingLeadersDashed;
    private javax.swing.JRadioButtonMenuItem mnuLabelingLeadersDotted;
    private javax.swing.JRadioButtonMenuItem mnuLabelingLeadersNo;
    private javax.swing.JRadioButtonMenuItem mnuLabelingLeadersSlanted;
    private javax.swing.JRadioButtonMenuItem mnuLabelingLeadersSolid;
    private javax.swing.JRadioButtonMenuItem mnuLabelingLeadersStraight;

    private javax.swing.JPopupMenu popupMenu;


    private File networkFile = null;


    private void prepareViewer() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(TITLE);
        this.setPreferredSize(new Dimension(800, 600));
        this.setMinimumSize(new Dimension(400, 300));
        getContentPane().setBackground(Color.white); // TODO Allow user to control background color
        setForeground(java.awt.Color.white);
        setIconImage((new ImageIcon("logo.png")).getImage());

        prepareDrawing();
        prepareMenu();
        preparePopupMenu();
    }

    /**
     * Creates new netview instance without any input data.
     * Normal initialisation.
     */
    public NetView() throws IOException {
        prepareViewer();
        initConfig();
        mnuFileSave.setEnabled(false);
        drawing.repaintOnResize();
    }

    /**
     * Creates a netview instance with the given file loaded at startup
     * Intended to be used from CLI
     *
     * @param inFile
     * @throws IOException
     */
    public NetView(File inFile) throws IOException {
        prepareViewer();
        openNetwork(inFile);
    }

    /**
     * Creates a newview instance with the given network loaded at startup
     * Intended to be used from other SPECTRE GUIs.
     *
     * @param network
     * @param inFile
     * @param taxa
     */
    public NetView(Network network, File inFile, IdentifierList taxa) {
        prepareViewer();
        directory = inFile.getPath();
        this.network = network;
        this.taxa = taxa;
        setTitle(TITLE + ": " + inFile.getAbsolutePath());
        drawNetwork();
        drawing.repaint();
    }

    private void prepareDrawing() {
        drawing = new Window(this);
        drawing.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                drawing.zoom(evt.getPreciseWheelRotation() * drawing.getRatio() / 50.0);
            }
        });
        drawing.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                java.awt.Point clickedPoint = evt.getPoint();
                if (SwingUtilities.isRightMouseButton(evt)) {
                    popupMenu.show(drawing, clickedPoint.x, clickedPoint.y);
                } else {
                    drawing.setSelection(clickedPoint, evt.isControlDown());
                }
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (SwingUtilities.isRightMouseButton(evt)) {
                    drawing.activateRotation(true);
                }
                startPoint = evt.getPoint();
                if (!drawing.markLabel(startPoint)) {
                    drawing.markPoint(startPoint);
                }
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (drawing.rotate) {
                    drawing.activateRotation(false);
                }
                drawing.removeSelectionRectangle();
            }
        });
        drawing.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                drawing.repaintOnResize();
            }
        });
        drawing.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                java.awt.Point endPoint = evt.getPoint();
                if (!drawing.rotate) {
                    if (network != null && drawing.isOnLabel()) {
                        drawing.moveLabels(endPoint);
                    } else if (network != null && drawing.isOnPoint()) {
                        drawing.moveTheVertex(endPoint);
                    } else {
                        drawing.setSelection(startPoint,
                                endPoint,
                                evt.isControlDown());
                    }
                } else {
                    if (network != null) {
                        drawing.rotate(startPoint, endPoint);
                    }
                }
            }
        });

        javax.swing.GroupLayout drawingLayout = new javax.swing.GroupLayout(drawing);
        drawing.setLayout(drawingLayout);
        drawingLayout.setHorizontalGroup(
                drawingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 513, Short.MAX_VALUE)
        );
        drawingLayout.setVerticalGroup(
                drawingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 322, Short.MAX_VALUE)
        );

        dt = new DropTarget(drawing, this);
        format = new Formating(drawing);
        formatLabels = new FormatLabels(drawing);
    }

    @SuppressWarnings("unchecked")
    private void prepareMenu() {

        menuBar = new javax.swing.JMenuBar();

        mnuView = new javax.swing.JMenu();
        mnuViewRotateleft = new javax.swing.JMenuItem();
        mnuViewRotateright = new javax.swing.JMenuItem();
        mnuViewZoomin = new javax.swing.JMenuItem();
        mnuViewZoomout = new javax.swing.JMenuItem();
        mnuViewFliphorizontal = new javax.swing.JMenuItem();
        mnuViewFlipvertical = new javax.swing.JMenuItem();
        mnuViewOptimiseLayout = new javax.swing.JMenuItem();
        mnuViewShowTrivial = new javax.swing.JCheckBoxMenuItem();
        mnuViewShowRange = new javax.swing.JCheckBoxMenuItem();

        mnuLabeling = new javax.swing.JMenu();
        mnuLabelingFormat = new javax.swing.JMenuItem();
        mnuLabelingShow = new javax.swing.JCheckBoxMenuItem();
        mnuLabelingColor = new javax.swing.JCheckBoxMenuItem();
        mnuLabelingFix = new javax.swing.JCheckBoxMenuItem();
        mnuLabelingLeaders = new javax.swing.JMenu();
        mnuLabelingLeadersNo = new javax.swing.JRadioButtonMenuItem();
        mnuLabelingLeadersStraight = new javax.swing.JRadioButtonMenuItem();
        mnuLabelingLeadersSlanted = new javax.swing.JRadioButtonMenuItem();
        mnuLabelingLeadersBended = new javax.swing.JRadioButtonMenuItem();
        mnuLabelingLeadersSolid = new javax.swing.JRadioButtonMenuItem();
        mnuLabelingLeadersDashed = new javax.swing.JRadioButtonMenuItem();
        mnuLabelingLeadersDotted = new javax.swing.JRadioButtonMenuItem();
        mnuLabelingLeadersColor = new javax.swing.JMenuItem();

        mnuFile = new javax.swing.JMenu();
        mnuFile.setText("File");
        mnuFile.setMnemonic('F');

        mnuFileOpen = new javax.swing.JMenuItem();
        mnuFileOpen.setText("Open network...");
        mnuFileOpen.setMnemonic('O');
        mnuFileOpen.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,
                java.awt.Event.CTRL_MASK));
        mnuFileOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileActionPerformed(evt);
            }
        });
        mnuFile.add(mnuFileOpen);
        mnuFile.addSeparator();

        mnuFileSave = new javax.swing.JMenuItem();
        mnuFileSave.setText("Save network");
        mnuFileSave.setMnemonic('S');
        mnuFileSave.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
                java.awt.Event.CTRL_MASK));
        mnuFileSave.setEnabled(false);
        mnuFileSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNetworkActionPerformed(evt);
            }
        });
        mnuFileSave.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mnuFile.add(mnuFileSave);

        mnuFileSaveas = new javax.swing.JMenuItem();
        mnuFileSaveas.setText("Save network as...");
        mnuFileSaveas.setMnemonic('A');
        mnuFileSaveas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNetworkAsActionPerformed(evt);
            }
        });
        mnuFile.add(mnuFileSaveas);

        mnuFileSaveimage = new javax.swing.JMenuItem();
        mnuFileSaveimage.setText("Save image as...");
        mnuFileSaveimage.setMnemonic('I');
        mnuFileSaveimage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImageActionPerformed(evt);
            }
        });
        mnuFile.add(mnuFileSaveimage);
        mnuFile.addSeparator();

        mnuFileExit = new javax.swing.JMenuItem();
        mnuFileExit.setText("Exit");
        mnuFileExit.setMnemonic('X');
        mnuFileExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                log.info("Shutting down netview");
                System.exit(0);
            }
        });
        mnuFile.add(mnuFileExit);

        menuBar.add(mnuFile);

        mnuEdit = new javax.swing.JMenu();
        mnuEdit.setText("Edit");
        mnuEdit.setMnemonic('E');

        mnuEditCopy = new javax.swing.JMenuItem();
        mnuEditCopy.setText("Copy");
        mnuEditCopy.setMnemonic('C');
        mnuEditCopy.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C,
                java.awt.Event.CTRL_MASK));
        mnuEditCopy.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                drawing.copySelectedTaxa();
            }
        });
        mnuEdit.add(mnuEditCopy);

        mnuEdit.addSeparator();

        mnuEditSelectall = new javax.swing.JMenuItem();
        mnuEditSelectall.setText("Select all");
        mnuEditSelectall.setMnemonic('S');
        mnuEditSelectall.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
                java.awt.Event.CTRL_MASK));
        mnuEditSelectall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSelectAllActionPerformed(evt);
            }
        });
        mnuEdit.add(mnuEditSelectall);

        mnuEdit.addSeparator();

        mnuEditFind = new javax.swing.JMenuItem();
        mnuEditFind.setText("Find...");
        mnuEditFind.setMnemonic('F');
        mnuEditFind.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F,
                java.awt.Event.CTRL_MASK));
        mnuEditFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                find.setVisible(true);
            }
        });
        mnuEdit.add(mnuEditFind);


        menuBar.add(mnuEdit);

        mnuView.setText("View");
        mnuView.setMnemonic('V');

        mnuViewRotateleft.setText("Rotate Left");
        mnuViewRotateleft.setMnemonic('L');
        mnuViewRotateleft.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
                java.awt.Event.CTRL_MASK));
        mnuViewRotateleft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.rotate(-0.1);
            }
        });
        mnuView.add(mnuViewRotateleft);

        mnuViewRotateright.setText("Rotate Right");
        mnuViewRotateright.setMnemonic('R');
        mnuViewRotateright.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_RIGHT,
                java.awt.Event.CTRL_MASK));
        mnuViewRotateright.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.rotate(0.1);
            }
        });
        mnuView.add(mnuViewRotateright);

        mnuViewZoomin.setText("Zoom in");
        mnuViewZoomin.setMnemonic('I');
        mnuViewZoomin.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP,
                java.awt.Event.CTRL_MASK));
        mnuViewZoomin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.zoom(-drawing.getRatio() / 10.0);
            }
        });
        mnuView.add(mnuViewZoomin);

        mnuViewZoomout.setText("Zoom out");
        mnuViewZoomout.setMnemonic('O');
        mnuViewZoomout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
                java.awt.Event.CTRL_MASK));
        mnuViewZoomout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.zoom(drawing.getRatio() / 10.0);
            }
        });
        mnuView.add(mnuViewZoomout);

        mnuViewFliphorizontal.setText("Flip Horizontal");
        mnuViewFliphorizontal.setMnemonic('H');
        mnuViewFliphorizontal.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H,
                java.awt.Event.CTRL_MASK + Event.SHIFT_MASK));
        mnuViewFliphorizontal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.flipNetwork(true);
            }
        });
        mnuView.add(mnuViewFliphorizontal);

        mnuViewFlipvertical.setText("Flip Vertical");
        mnuViewFlipvertical.setMnemonic('V');
        mnuViewFlipvertical.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V,
                java.awt.Event.CTRL_MASK + Event.SHIFT_MASK));
        mnuViewFlipvertical.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.flipNetwork(false);
            }
        });
        mnuView.add(mnuViewFlipvertical);

        mnuView.addSeparator();

        mnuViewOptimiseLayout.setText("Optimize layout");
        mnuViewOptimiseLayout.setMnemonic('P');
        mnuViewOptimiseLayout.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P,
                java.awt.Event.CTRL_MASK));
        mnuViewOptimiseLayout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (network != null) {
                    drawing.resetLabelPositions(true);
                    drawing.repaintOnResize();
                }
            }
        });
        mnuView.add(mnuViewOptimiseLayout);

        mnuView.addSeparator();

        mnuViewShowTrivial.setSelected(true);
        mnuViewShowTrivial.setMnemonic('T');
        mnuViewShowTrivial.setText("Show trivial splits");
        mnuViewShowTrivial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boolean showTrivial = mnuViewShowTrivial.isSelected();
                drawing.showTrivial(showTrivial);
                config.setShowTrivial(showTrivial);
            }
        });
        mnuView.add(mnuViewShowTrivial);

        mnuViewShowRange.setSelected(true);
        mnuViewShowRange.setMnemonic('R');
        mnuViewShowRange.setText("Show range");
        mnuViewShowRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boolean showRange = mnuViewShowRange.isSelected();
                drawing.showRange(showRange);
                config.setShowRange(showRange);
                drawing.repaint();
            }
        });
        mnuView.add(mnuViewShowRange);

        menuBar.add(mnuView);

        mnuLabeling.setText("Labeling");
        mnuLabeling.setMnemonic('B');

        mnuLabelingShow.setSelected(true);
        mnuLabelingShow.setText("Show labels");
        mnuLabelingShow.setMnemonic('S');
        mnuLabelingShow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.repaintOnResize();
            }
        });
        mnuLabeling.add(mnuLabelingShow);

        mnuLabelingColor.setText("Color labels");
        mnuLabelingColor.setMnemonic('C');
        mnuLabelingColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.repaint();
            }
        });
        mnuLabeling.add(mnuLabelingColor);

        mnuLabeling.addSeparator();

        mnuLabelingFormat.setText("Format selected nodes ...");
        mnuLabelingFormat.setMnemonic('F');
        mnuLabelingFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formatNodesActionPerformed(evt);
            }
        });
        mnuLabeling.add(mnuLabelingFormat);

        mnuLabeling.addSeparator();

        mnuLabelingFix.setText("Fix all label positions");
        mnuLabelingFix.setMnemonic('X');
        mnuLabelingFix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFixLabelPositionsActionPerformed(evt);
            }
        });
        mnuLabeling.add(mnuLabelingFix);


        mnuLabeling.addSeparator();

        mnuLabelingLeaders.setText("Leaders");
        mnuLabelingLeaders.setMnemonic('L');

        ButtonGroup leaderConnectorGroup1 = new ButtonGroup();

        mnuLabelingLeadersNo.setText("None");
        mnuLabelingLeadersNo.setMnemonic('N');
        mnuLabelingLeadersNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.repaint();
                config.setLeaderType("none");
            }
        });
        leaderConnectorGroup1.add(mnuLabelingLeadersNo);
        mnuLabelingLeaders.add(mnuLabelingLeadersNo);

        mnuLabelingLeadersStraight.setText("Straight");
        mnuLabelingLeadersStraight.setMnemonic('S');
        mnuLabelingLeadersStraight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.repaint();
                config.setLeaderType("straight");
            }
        });
        leaderConnectorGroup1.add(mnuLabelingLeadersStraight);
        mnuLabelingLeaders.add(mnuLabelingLeadersStraight);

        mnuLabelingLeadersSlanted.setSelected(true);
        mnuLabelingLeadersSlanted.setText("Slanted");
        mnuLabelingLeadersSlanted.setMnemonic('N');
        mnuLabelingLeadersSlanted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.repaint();
                config.setLeaderType("slanted");
            }
        });
        leaderConnectorGroup1.add(mnuLabelingLeadersSlanted);
        mnuLabelingLeaders.add(mnuLabelingLeadersSlanted);

        mnuLabelingLeadersBended.setText("Bended");
        mnuLabelingLeadersBended.setMnemonic('B');
        mnuLabelingLeadersBended.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.repaint();
                config.setLeaderType("bended");
            }
        });
        leaderConnectorGroup1.add(mnuLabelingLeadersBended);
        mnuLabelingLeaders.add(mnuLabelingLeadersBended);

        mnuLabelingLeaders.addSeparator();

        ButtonGroup leaderConnectorGroup2 = new ButtonGroup();

        mnuLabelingLeadersSolid.setText("Solid");
        mnuLabelingLeadersSolid.setMnemonic('O');
        mnuLabelingLeadersSolid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                config.setLeaderStroke("solid");
                drawing.repaint();
            }
        });
        leaderConnectorGroup2.add(mnuLabelingLeadersSolid);
        mnuLabelingLeaders.add(mnuLabelingLeadersSolid);

        mnuLabelingLeadersDashed.setSelected(true);
        mnuLabelingLeadersDashed.setText("Dashed");
        mnuLabelingLeadersDashed.setMnemonic('D');
        mnuLabelingLeadersDashed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                config.setLeaderStroke("dashed");
                drawing.repaint();
            }
        });
        leaderConnectorGroup2.add(mnuLabelingLeadersDashed);
        mnuLabelingLeaders.add(mnuLabelingLeadersDashed);

        mnuLabelingLeadersDotted.setText("Dotted");
        mnuLabelingLeadersDotted.setMnemonic('E');
        mnuLabelingLeadersDotted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                config.setLeaderType("dotted");
                drawing.repaint();
            }
        });
        leaderConnectorGroup2.add(mnuLabelingLeadersDotted);
        mnuLabelingLeaders.add(mnuLabelingLeadersDotted);

        mnuLabelingLeaders.addSeparator();

        mnuLabelingLeadersColor.setText("Set color");
        mnuLabelingLeadersColor.setMnemonic('C');
        mnuLabelingLeadersColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdLeaderColorSelectActionPerformed(evt);
            }
        });
        mnuLabelingLeaders.add(mnuLabelingLeadersColor);

        mnuLabeling.add(mnuLabelingLeaders);

        menuBar.add(mnuLabeling);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(drawing, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(drawing, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }


    private void cmdLeaderColorSelectActionPerformed(ActionEvent evt) {
        Color newLeaderColor = JColorChooser.showDialog(this, "Font color", drawing.leaderColor);
        if (newLeaderColor != null) {
            config.setLeaderColor(newLeaderColor);
            drawing.leaderColor = newLeaderColor;
            drawing.repaint();
        }
    }

    private void openFileActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser(directory);
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File inFile = fileChooser.getSelectedFile();
            directory = inFile.getPath();
            try {
                openNetwork(inFile);
            } catch (IOException e) {
                errorMessage("Error opening file", e);
            }
        }
    }

    private void saveImageActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser(directory);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Portable Document Format (.pdf)", "pdf"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image File(.png, .jpg, .gif)", "png", "jpg", "gif"));
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File image_out = fileChooser.getSelectedFile();
            String ext = FilenameUtils.getExtension(image_out.getName());

            if (ext.equalsIgnoreCase("pdf")) {
                directory = image_out.getPath();
                try {
                    savePDF(image_out);
                } catch (DocumentException ex) {
                    errorMessage("Error saving PDF", ex);
                }
            } else if (ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("gif")) {
                try {
                    saveImage(image_out);
                } catch (IOException ex) {
                    errorMessage("Error saving PNG", ex);
                }
            } else {
                errorMessage("No recognised extension specified in filename.  Please type an appropriate extension for image.");
            }
        }
    }

    private void saveNetworkAsActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser(directory);
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (fileToSave != null) {
                directory = fileToSave.getPath();
                int confirm = JOptionPane.YES_OPTION;
                if (fileToSave.exists()) {
                    confirm = JOptionPane.showConfirmDialog(this, "File " + fileToSave.getName() +
                                    " already exists.\nOverwrite?", "Overwrite?",
                            JOptionPane.YES_NO_OPTION);
                }
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        saveNetworkAs(fileToSave);
                    } catch (IOException ioe) {
                        errorMessage("Problem occured while trying to save network", ioe);
                    }
                }
            }
        }
    }

    private void formatNodesActionPerformed(java.awt.event.ActionEvent evt) {
        if (drawing.isSelected()) {
            ((Formating) format).setVisible();
        } else {
            JOptionPane.showMessageDialog(this, "No nodes are selected.");
        }
    }

    private void saveNetworkActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            this.saveNetwork(networkFile);
        } catch (IOException ioe) {
            errorMessage("Problem occured while trying to save network", ioe);
        }
    }

    private void jMenuItemSelectAllActionPerformed(java.awt.event.ActionEvent evt) {
        drawing.selectAll();
    }

    private void jCheckBoxFixLabelPositionsActionPerformed(java.awt.event.ActionEvent evt) {
        boolean fix = mnuLabelingFix.isSelected();
        drawing.fixLabels(!fix);
    }

    protected static void errorMessage(String message, Exception ex) {
        log.error(message, ex);
        JOptionPane.showMessageDialog(null, message + "\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected static void errorMessage(String message) {
        log.error(message);
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }





    private Network network;
    private IdentifierList taxa;
    private static Leaders leaders;
    private static String directory = ".";
    private FindDIalog find = new FindDIalog(this, true);
    private ViewerConfig config = new ViewerConfig();

    private void drawNetwork() {
        drawing.setGraph(network, (config != null) ? config.getRatio() : null);
        drawing.showTrivial(config.showTrivial());
        drawing.repaint();
    }

    private void savePDF(File pdfFile) throws DocumentException {
        try {
            Rectangle domensions = new Rectangle(0, 0, drawing.getWidth(), drawing.getHeight());
            Document d = new Document(domensions);
            PdfWriter writer = PdfWriter.getInstance(d, new FileOutputStream(pdfFile));

            d.open();
            PdfContentByte cb = writer.getDirectContent();

            DefaultFontMapper mapper = new DefaultFontMapper();

            Graphics2D g2d = new PdfGraphics2D(cb, drawing.getWidth(), drawing.getHeight(), mapper, false, false, (float) 1.0);

            drawing.paint(g2d);
            g2d.dispose();

            d.close();
        } catch (IOException ex) {
            errorMessage("Error writing image to the file.", ex);
        } catch (DocumentException de) {
            errorMessage("Document Error");
        }
    }

    private void saveImage(File image_file) throws IOException {

        // Create image
        BufferedImage bImage = new BufferedImage(drawing.getWidth(), drawing.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bImage.createGraphics();
        drawing.paint(g2d);

        // Save image to disk
        String ext = FilenameUtils.getExtension(image_file.getName());
        ImageIO.write(bImage, ext, image_file);
    }

    private void saveNetworkAs(File fileToSave) throws IOException {

        this.saveNetwork(fileToSave);

        setTitle(TITLE + ": " + fileToSave.getAbsolutePath());
        mnuFileSave.setEnabled(true);
        networkFile = fileToSave;
    }

    private void saveNetwork() throws IOException {
        this.saveNetwork(networkFile);
    }

    private void saveNetwork(File file) throws IOException {
        ViewerNexusWriter writer = new ViewerNexusWriter();
        writer.appendHeader();
        writer.appendLine();
        writer.append(taxa);
        writer.appendLine();
        writer.append(network);
        writer.appendLine();
        writer.append(config);
        writer.write(file);
    }

    public boolean fixedLabelPositions() {
        return mnuLabeling.isSelected();
    }

    private void preparePopupMenu() {

        popupMenu = new javax.swing.JPopupMenu();

        JMenuItem copySelectedTaxa = new JMenuItem("Copy selected labels");
        copySelectedTaxa.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C,
                java.awt.Event.CTRL_MASK));
        copySelectedTaxa.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                drawing.copySelectedTaxa();
            }
        });
        popupMenu.add(copySelectedTaxa);

        JMenuItem selectGroup = new JMenuItem("Select group");
        selectGroup.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G,
                java.awt.Event.CTRL_MASK));
        selectGroup.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                drawing.selectGroup();
            }
        });
        popupMenu.add(selectGroup);

        JMenuItem group = new JMenuItem("Group selected");
        group.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                drawing.makeGroup();
            }
        });
        popupMenu.add(group);

        JMenuItem remove = new JMenuItem("Remove from group");
        remove.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                drawing.removeFromGroup();
            }
        });
        popupMenu.add(remove);
    }

    private void openNetwork(File inFile) throws IOException {
        directory = inFile.getPath();

        Nexus nexus = new NexusReader().parse(inFile);

        // If no network was defined but there is a split system then convert the split system to a network
        if (nexus.getNetwork() == null && nexus.getSplitSystem() != null) {

            // Create network
            network = new FlatNetwork(new PermutationSequenceDraw(nexus.getSplitSystem()).drawSplitSystem(-1.0));

            // Setup labels
            for (Vertex v : network.getAllVertices()) {
                if (v.getTaxa().size() > 0) {
                    String label = new String();
                    for (Identifier i : v.getTaxa()) {
                        label = (i.getName() + ", ").concat(label);
                    }
                    label = label.substring(0, label.length() - 2);
                    v.setLabel(new NetworkLabel(label));
                }
            }
        } else {
            network = nexus.getNetwork();
        }
        this.taxa = nexus.getTaxa();
        this.network.setTaxa(this.taxa);

        if (nexus.getViewerConfig() != null) {
            this.config = nexus.getViewerConfig();
            applyConfig(this.config);
        } else {
            initConfig();
        }

        networkFile = inFile;
        mnuFileSave.setEnabled(true);
        setTitle(TITLE + ": " + inFile.getAbsolutePath());
        drawNetwork();
        drawing.repaint();
    }

    public Window getDrawing() {
        return drawing;
    }

    boolean showLabels() {
        return mnuLabelingShow.isSelected();
    }

    boolean colorLabels() {
        return mnuLabelingColor.isSelected();
    }

    private void initConfig() {
        String leaderType = null;
        if (mnuLabelingLeadersBended.isSelected()) {
            leaderType = "bended";
        } else if (mnuLabelingLeadersSlanted.isSelected()) {
            leaderType = "slanted";
        } else if (mnuLabelingLeadersStraight.isSelected()) {
            leaderType = "straight";
        }
        String leaderStroke = "solid";
        if (mnuLabelingLeadersDashed.isSelected()) {
            leaderStroke = "dashed";
        } else if (mnuLabelingLeadersDotted.isSelected()) {
            leaderStroke = "dotted";
        }

        config = new ViewerConfig(drawing.getSize(),
                leaderType,
                leaderStroke,
                drawing.leaderColor,
                mnuViewShowTrivial.isSelected(),
                mnuViewShowRange.isSelected(),
                mnuLabelingShow.isSelected(),
                mnuLabelingColor.isSelected(),
                new HashSet<Integer>(),
                null,
                network == null ? null : network.getLabeledVertices());
    }

    boolean dashedLeaders() {
        return mnuLabelingLeadersDashed.isSelected();
    }

    boolean dottedLeaders() {
        return mnuLabelingLeadersDotted.isSelected();
    }

    private void applyConfig(ViewerConfig config) {
        String leaderType = config.getLeaderType();
        switch (leaderType) {
            case "straight":
                mnuLabelingLeadersStraight.setSelected(true);
                break;
            case "bended":
                mnuLabelingLeadersBended.setSelected(true);
                break;
            case "slanted":
                mnuLabelingLeadersSlanted.setSelected(true);
                break;
            default:
                mnuLabelingLeadersNo.setSelected(true);
                break;
        }

        String stroke = config.getLeaderStroke();
        switch (stroke) {
            case "dashed":
                mnuLabelingLeadersDashed.setSelected(true);
                break;
            case "dotted":
                mnuLabelingLeadersDotted.setSelected(true);
                break;
            default:
                mnuLabelingLeadersSolid.setSelected(true);
                break;
        }

        Color leaderColor = config.getLeaderColor();
        if (leaderColor != null) {
            drawing.leaderColor = leaderColor;
        }

        mnuLabelingColor.setSelected(config.colorLabels());
        mnuLabelingShow.setSelected(config.showLabels());
        mnuViewShowTrivial.setSelected(config.showTrivial());
        mnuViewShowRange.setSelected(config.isShowRange());

        Set<Integer> fixed = config.getFixed();
        for (Vertex vertex : network.getAllVertices()) {
            if (fixed.contains(vertex.getNxnum())) {
                vertex.getLabel().movable = false;
            }
        }

        this.drawing.range = config.isShowRange();
        this.drawing.showTrivial(config.showTrivial());
    }

    boolean leadersVisible() {
        return !mnuLabelingLeadersNo.isSelected();
    }

    boolean straightLeaders() {
        return mnuLabelingLeadersStraight.isSelected();
    }

    boolean bendedLeaders() {
        return mnuLabelingLeadersBended.isSelected();
    }

    boolean slantedLeaders() {
        return mnuLabelingLeadersSlanted.isSelected();
    }

    void setRatio(double ratio) {
        config.setRatio(ratio);
    }


    protected void processDrag(DropTargetDragEvent dtde) {
        try {
            if (dtde.isDataFlavorSupported(new DataFlavor("text/uri-list;class=java.lang.String"))) {
                dtde.acceptDrag(DnDConstants.ACTION_COPY);
            } else {
                dtde.rejectDrag();
            }
        } catch (ClassNotFoundException e) {
            dtde.rejectDrag();
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        processDrag(dtde);
        repaint();
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        processDrag(dtde);
        repaint();
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        repaint();
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {

        try {
            DataFlavor df = new DataFlavor("text/uri-list;class=java.lang.String");
            Transferable transferable = dtde.getTransferable();
            if (dtde.isDataFlavorSupported(df)) {
                dtde.acceptDrop(dtde.getDropAction());

                String data = (String) transferable.getTransferData(df);
                for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens(); ) {
                    String token = st.nextToken().trim();
                    if (token.startsWith("#") || token.isEmpty()) {
                        // comment line, by RFC 2483
                        continue;
                    }

                    File file = new File(new URI(token));
                    directory = file.getPath();
                    openNetwork(file);
                }
                dtde.dropComplete(true);
            }
        } catch (IOException e) {
            errorMessage("File not found", e);
        } catch (URISyntaxException e) {
            errorMessage("File not found", e);
        } catch (UnsupportedFlavorException e) {
            errorMessage("Unsupported item", e);
        } catch (ClassNotFoundException e) {
            errorMessage("Unsupported item", e);
        }
    }

    private static Options createOptions() {

        Options options = new Options();
        options.addOption(CommandLineHelper.HELP_OPTION);
        options.addOption(OptionBuilder.withLongOpt(OPT_DISPOSE).hasArg(false)
                .withDescription("Whether to just close this window when closing netview.  By default we close all linked applications and windows when closing netview.")
                .isRequired(false).create("d"));
        options.addOption(OptionBuilder.withLongOpt(OPT_VERBOSE).isRequired(false).hasArg(false)
                .withDescription("Whether to output extra information").create("v"));
        return options;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {
            // Configure logging
            LogConfig.defaultConfig();

            LookAndFeel.setLookAndFeel(LookAndFeel.NIMBUS);

            // Parse command line args
            final CommandLine commandLine = CommandLineHelper.startApp(createOptions(), "netview [options] <input>",
                    "Visualises a network in nexus format.  The nexus file can contain a pre-drawn network in " +
                            "a network block, or a split system to be drawn.\n" +
                            "The viewer can be passed the nexus file as a command line argument at startup, selected by " +
                            "the user via a file dialog or through a file being dragged and dropped into the window.", args, false);

            // If we didn't return a command line object then just return.  Probably the user requested help or
            // input invalid args
            if (commandLine == null) {
                return;
            }

            final File inputfile = commandLine == null || commandLine.getArgs().length == 0 ? null : new File(commandLine.getArgs()[0]);

            if (commandLine.getArgs().length > 1) {
                throw new IOException("Expected only a single input file.");
            } else if (commandLine.getArgs().length == 1) {
                log.info("Opening netview with input file: " + inputfile.getAbsolutePath());
            } else {
                log.info("Opening netview with no input");
            }


            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        NetView nv = inputfile == null ? new NetView() : new NetView(inputfile);
                        log.info("Viewer initalised");
                        nv.setVisible(true);
                        nv.setDefaultCloseOperation(commandLine.hasOption(OPT_DISPOSE) ? WindowConstants.DISPOSE_ON_CLOSE : javax.swing.WindowConstants.EXIT_ON_CLOSE);
                        nv.revalidate();
                        nv.repaint();
                    } catch (Exception e) {
                        errorMessage("Unexpected problem occured while running NetView", e);
                        System.exit(4);
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("\nException: " + e.toString());
            System.err.println("\nStack trace:");
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(3);
        }
    }
}

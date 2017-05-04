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
import uk.ac.uea.cmp.spectre.core.ds.IdentifierList;
import uk.ac.uea.cmp.spectre.core.ds.network.Network;
import uk.ac.uea.cmp.spectre.core.ds.network.Vertex;
import uk.ac.uea.cmp.spectre.core.ds.network.draw.*;
import uk.ac.uea.cmp.spectre.core.io.nexus.Nexus;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusReader;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusWriter;
import uk.ac.uea.cmp.spectre.core.ui.cli.CommandLineHelper;
import uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel;
import uk.ac.uea.cmp.spectre.core.util.LogConfig;
import uk.ac.uea.cmp.spectre.flatnj.FlatNJGUI;
import uk.ac.uea.cmp.spectre.net.netmake.NetMakeGUI;
import uk.ac.uea.cmp.spectre.net.netme.NetMEGUI;
import uk.ac.uea.cmp.spectre.qtools.superq.SuperQGUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * The main SPECTRE GUI Frame.
 *
 * This frame mainly just manages the menu and initial UI for drawing networks.
 *
 * Most of the heavy lifting is done in the Window class, which represents the
 * drawing canvas for the network.
 *
 * @author balvociute and maplesond
 */
public class Spectre extends javax.swing.JFrame implements DropTargetListener {

    private static Logger log = LoggerFactory.getLogger(Spectre.class);

    // Constants
    private static final String BIN_NAME = "netview";
    private static final String OPT_VERBOSE = "verbose";
    private static final String OPT_DISPOSE = "dispose_on_close";
    private static final String TITLE = "SPECTRE";

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    // The data
    private Network network;
    private IdentifierList taxa;

    // Record of current working directory and currently open file
    private static String directory = ".";
    private File networkFile = null;

    // Dialog for finding labels
    private FindDialog find = new FindDialog(this, true);

    // Viewer configuration
    //private ViewerConfig config = new ViewerConfig();

    private JFrame format;
    private JFrame formatLabels;

    // Handles file drops onto drawing canvas or initial opening canvas
    private DropTarget dt;

    private Window drawing;                 // Network drawing canvas

    private javax.swing.JPanel pnlOpen;     // Initial panel containing help message
    private javax.swing.JLabel lblOpenMsg;  // Initial help message

    private javax.swing.JPanel pnlStatus;   // Status bar
    private javax.swing.JPanel pnlNetCoords;
    private javax.swing.JLabel lblNetCoords;
    private javax.swing.JPanel pnlScreenCoords;
    private javax.swing.JLabel lblScreenCoords;
    private javax.swing.JPanel pnlOffset;
    private javax.swing.JLabel lblOffset;
    private javax.swing.JPanel pnlRatio;
    private javax.swing.JLabel lblZoomRatio;
    private javax.swing.JPanel pnlAngle;
    private javax.swing.JLabel lblAngle;

    // Main menu
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
    private javax.swing.JMenu mnuTools;
    private javax.swing.JMenuItem mnuToolsNeighbornet;
    private javax.swing.JMenuItem mnuToolsNetmake;
    private javax.swing.JMenuItem mnuToolsNetme;
    private javax.swing.JMenuItem mnuToolsFlatnj;
    private javax.swing.JMenuItem mnuToolsSuperq;



    /**
     * Creates new netview instance without any input data.
     * Normal initialisation.
     */
    public Spectre() throws IOException {
        prepareViewer();
        prepareOpenPane();
    }

    /**
     * Creates a netview instance with the given file loaded at startup
     * Intended to be used from CLI
     *
     * @param inFile
     * @throws IOException
     */
    public Spectre(File inFile) throws IOException {
        prepareViewer();
        openNetwork(inFile);
    }

    private void prepareViewer() {

        prepareMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(TITLE);
        setPreferredSize(new Dimension(800, 600));
        setMinimumSize(new Dimension(700, 500));
        getContentPane().setBackground(Color.white); // TODO Allow user to control background color
        setForeground(java.awt.Color.white);

        try {
            setIconImage((new ImageIcon(LookAndFeel.getLogoFilePath()).getImage()));
        } catch (URISyntaxException e) {
            errorMessage("Couldn't load logo.", e);
        }

        setLayout(new BorderLayout());


        prepareStatus();

    }



    private void prepareStatus() {
        this.pnlStatus = new JPanel();
        this.pnlStatus.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.pnlStatus.setPreferredSize(new Dimension(this.getWidth(), 30));
        this.pnlStatus.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        this.pnlNetCoords = new JPanel();
        this.pnlNetCoords.setPreferredSize(new Dimension(140, 20));
        this.lblNetCoords = new JLabel("Net:");
        this.lblNetCoords.setHorizontalAlignment(SwingConstants.LEFT);
        this.pnlNetCoords.add(this.lblNetCoords);
        this.pnlStatus.add(this.pnlNetCoords);

        this.pnlScreenCoords = new JPanel();
        this.pnlScreenCoords.setPreferredSize(new Dimension(140, 20));
        this.lblScreenCoords = new JLabel("Screen:");
        this.lblScreenCoords.setHorizontalAlignment(SwingConstants.LEFT);
        this.pnlScreenCoords.add(this.lblScreenCoords);
        this.pnlStatus.add(this.pnlScreenCoords);

        this.pnlRatio = new JPanel();
        this.pnlRatio.setPreferredSize(new Dimension(120, 20));
        this.lblZoomRatio = new JLabel("Zoom:");
        this.lblZoomRatio.setHorizontalAlignment(SwingConstants.LEFT);
        this.pnlRatio.add(this.lblZoomRatio);
        this.pnlStatus.add(this.pnlRatio);

        this.pnlAngle = new JPanel();
        this.pnlAngle.setPreferredSize(new Dimension(100, 20));
        this.lblAngle = new JLabel("Angle:");
        this.lblAngle.setHorizontalAlignment(SwingConstants.LEFT);
        this.pnlAngle.add(this.lblAngle);
        this.pnlStatus.add(this.pnlAngle);

        this.pnlOffset = new JPanel();
        this.pnlOffset.setPreferredSize(new Dimension(150, 20));
        this.pnlStatus.add(this.pnlOffset);
        this.lblOffset = new JLabel("Offset:");
        this.lblOffset.setHorizontalAlignment(SwingConstants.LEFT);
        this.pnlOffset.add(this.lblOffset);

        this.add(this.pnlStatus, BorderLayout.SOUTH);
    }

    private void prepareOpenPane() {
        pnlOpen = new JPanel();
        pnlOpen.setLayout(new GridBagLayout());

        lblOpenMsg = new JLabel("<html><div style='text-align: center;'>To open a network or split system, use the File menu or drop the file into this pane.<br>Alternatively, run a SPECTRE tool via the Tools menu.</html>");
        pnlOpen.add(lblOpenMsg);

        dt = new DropTarget(pnlOpen, this);
        pnlOpen.setVisible(true);

        this.add(pnlOpen, BorderLayout.CENTER);
    }

    private void prepareDrawing() {
        drawing = new Window();

        dt = new DropTarget(drawing, this);
        format = new Formating(drawing);
        formatLabels = new FormatLabels(drawing);


        drawing.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                updateStatus(e.getX(), e.getY());
            }
        });

        drawing.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateStatus(e.getX(), e.getY());
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                updateStatus(e.getX(), e.getY());
            }
        });

        this.add(drawing, BorderLayout.CENTER);
    }

    private void updateStatus(int x, int y) {
        Point2D net = drawing.getTranslatedPoint(x, y);
        lblNetCoords.setText("Net: " + df2.format(net.getX()) + "," + df2.format(net.getY()));
        lblScreenCoords.setText("Screen: " + x + "," + y);
        lblOffset.setText("Offset: " + df2.format(drawing.getOffset().getX()) + "," + df2.format(drawing.getOffset().getY()));
        lblZoomRatio.setText("Zoom: " + df2.format(drawing.config.getRatio()));
        lblAngle.setText("Angle: " + df2.format(drawing.config.getAngle() * 180.0 / Math.PI));
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
                try {
                    saveNetwork(networkFile);
                } catch (IOException ioe) {
                    errorMessage("Problem occured while trying to save network", ioe);
                }
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
                drawing.selectAll();
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
                find.clearText();
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
                drawing.zoom(-drawing.config.getRatio() / 10.0);
            }
        });
        mnuView.add(mnuViewZoomin);

        mnuViewZoomout.setText("Zoom out");
        mnuViewZoomout.setMnemonic('O');
        mnuViewZoomout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
                java.awt.Event.CTRL_MASK));
        mnuViewZoomout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.zoom(drawing.config.getRatio() / 10.0);
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
                    drawing.optimiseScale(true);
                }
            }
        });
        mnuView.add(mnuViewOptimiseLayout);

        mnuView.addSeparator();

        mnuViewShowTrivial.setSelected(true);
        mnuViewShowTrivial.setMnemonic('T');
        mnuViewShowTrivial.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T,
                java.awt.Event.CTRL_MASK));
        mnuViewShowTrivial.setText("Show trivial splits");
        mnuViewShowTrivial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.showTrivial(mnuViewShowTrivial.isSelected());
            }
        });
        mnuView.add(mnuViewShowTrivial);

        mnuViewShowRange.setSelected(true);
        mnuViewShowRange.setMnemonic('R');
        mnuViewShowRange.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R,
                java.awt.Event.CTRL_MASK));
        mnuViewShowRange.setText("Show range");
        mnuViewShowRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.showRange(mnuViewShowRange.isSelected());
            }
        });
        mnuView.add(mnuViewShowRange);

        menuBar.add(mnuView);

        mnuLabeling.setText("Labeling");
        mnuLabeling.setMnemonic('B');

        mnuLabelingShow.setSelected(true);
        mnuLabelingShow.setText("Show labels");
        mnuLabelingShow.setMnemonic('S');
        mnuLabelingShow.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L,
                java.awt.Event.CTRL_MASK));
        mnuLabelingShow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.config.setShowLabels(mnuLabelingShow.isSelected());
                drawing.repaint();
            }
        });
        mnuLabeling.add(mnuLabelingShow);

        mnuLabelingColor.setText("Color labels");
        mnuLabelingColor.setMnemonic('C');
        mnuLabelingColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.config.setColorLabels(mnuLabelingColor.isSelected());
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
                drawing.fixLabels(!mnuLabelingFix.isSelected());
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
                drawing.setLeaderType(Leader.LeaderType.NONE);
            }
        });
        leaderConnectorGroup1.add(mnuLabelingLeadersNo);
        mnuLabelingLeaders.add(mnuLabelingLeadersNo);

        mnuLabelingLeadersStraight.setText("Straight");
        mnuLabelingLeadersStraight.setMnemonic('S');
        mnuLabelingLeadersStraight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.setLeaderType(Leader.LeaderType.STRAIGHT);
            }
        });
        leaderConnectorGroup1.add(mnuLabelingLeadersStraight);
        mnuLabelingLeaders.add(mnuLabelingLeadersStraight);

        mnuLabelingLeadersSlanted.setSelected(true);
        mnuLabelingLeadersSlanted.setText("Slanted");
        mnuLabelingLeadersSlanted.setMnemonic('N');
        mnuLabelingLeadersSlanted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.setLeaderType(Leader.LeaderType.SLANTED);
            }
        });
        leaderConnectorGroup1.add(mnuLabelingLeadersSlanted);
        mnuLabelingLeaders.add(mnuLabelingLeadersSlanted);

        mnuLabelingLeadersBended.setText("Bended");
        mnuLabelingLeadersBended.setMnemonic('B');
        mnuLabelingLeadersBended.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.setLeaderType(Leader.LeaderType.BENDED);
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
                drawing.setLeaderStroke(Leader.LeaderStroke.SOLID);
            }
        });
        leaderConnectorGroup2.add(mnuLabelingLeadersSolid);
        mnuLabelingLeaders.add(mnuLabelingLeadersSolid);

        mnuLabelingLeadersDashed.setSelected(true);
        mnuLabelingLeadersDashed.setText("Dashed");
        mnuLabelingLeadersDashed.setMnemonic('D');
        mnuLabelingLeadersDashed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.setLeaderStroke(Leader.LeaderStroke.DASHED);
            }
        });
        leaderConnectorGroup2.add(mnuLabelingLeadersDashed);
        mnuLabelingLeaders.add(mnuLabelingLeadersDashed);

        mnuLabelingLeadersDotted.setText("Dotted");
        mnuLabelingLeadersDotted.setMnemonic('E');
        mnuLabelingLeadersDotted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawing.setLeaderStroke(Leader.LeaderStroke.DOTTED);
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

        mnuTools = new javax.swing.JMenu();
        mnuTools.setText("Tools");
        mnuTools.setMnemonic('T');

        mnuToolsNeighbornet = new javax.swing.JMenuItem();
        mnuToolsNeighbornet.setText("Neighbor-Net");
        mnuToolsNeighbornet.setMnemonic('N');
        mnuToolsNeighbornet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {

                            NetMakeGUI nn = new NetMakeGUI();
                            nn.neighbornetConfig();
                            nn.addPropertyChangeListener("done", new PropertyChangeListener() {
                                @Override
                                public void propertyChange(PropertyChangeEvent evt) {
                                    if (evt.getNewValue() != null) {
                                        File f = (File)evt.getNewValue();
                                        if (f.exists()) {
                                            try {
                                                openNetwork(f);
                                            }
                                            catch (Exception e) {
                                                errorMessage("Error trying to view network", e);
                                            }
                                        }
                                    }
                                }
                            });
                            nn.setVisible(true);
                        }
                    });
                    return;
                } catch (Exception ex) {
                    errorMessage("Unexpected problem occurred with Neighbor-Net", ex);
                }
            }
        });
        mnuTools.add(mnuToolsNeighbornet);

        mnuToolsNetmake = new javax.swing.JMenuItem();
        mnuToolsNetmake.setText("Netmake");
        mnuToolsNetmake.setMnemonic('K');
        mnuToolsNetmake.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {

                            NetMakeGUI nm = new NetMakeGUI();
                            nm.netmakeConfig();
                            nm.addPropertyChangeListener("done", new PropertyChangeListener() {
                                @Override
                                public void propertyChange(PropertyChangeEvent evt) {
                                    if (evt.getNewValue() != null) {
                                        File f = (File)evt.getNewValue();
                                        if (f.exists()) {
                                            try {
                                                openNetwork(f);
                                            }
                                            catch (Exception e) {
                                                errorMessage("Error trying to view network", e);
                                            }
                                        }
                                    }
                                }
                            });
                            nm.setVisible(true);
                        }
                    });
                    return;
                } catch (Exception ex) {
                    errorMessage("Unexpected problem occurred with Netmake", ex);
                }
            }
        });
        mnuTools.add(mnuToolsNetmake);

        mnuToolsNetme = new javax.swing.JMenuItem();
        mnuToolsNetme.setText("NetME");
        mnuToolsNetme.setMnemonic('M');
        mnuToolsNetme.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {

                            NetMEGUI nm = new NetMEGUI();
                            /*nm.addPropertyChangeListener("done", new PropertyChangeListener() {
                                @Override
                                public void propertyChange(PropertyChangeEvent evt) {
                                    if (evt.getNewValue() != null) {
                                        File f = (File)evt.getNewValue();
                                        if (f.exists()) {
                                            try {
                                                openNetwork(f);
                                            }
                                            catch (Exception e) {
                                                errorMessage("Error trying to view network", e);
                                            }
                                        }
                                    }
                                }
                            });*/
                            nm.setVisible(true);
                        }
                    });
                    return;
                } catch (Exception ex) {
                    errorMessage("Unexpected problem occurred with NetME", ex);
                }
            }
        });
        mnuTools.add(mnuToolsNetme);

        mnuToolsFlatnj = new javax.swing.JMenuItem();
        mnuToolsFlatnj.setText("Flat Neighbor Joining (FlatNJ)");
        mnuToolsFlatnj.setMnemonic('F');
        mnuToolsFlatnj.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {

                            FlatNJGUI fnj = new FlatNJGUI();
                            fnj.addPropertyChangeListener("done", new PropertyChangeListener() {
                                @Override
                                public void propertyChange(PropertyChangeEvent evt) {
                                    if (evt.getNewValue() != null) {
                                        File f = (File)evt.getNewValue();
                                        if (f.exists()) {
                                            try {
                                                openNetwork(f);
                                            }
                                            catch (Exception e) {
                                                errorMessage("Error trying to view network", e);
                                            }
                                        }
                                    }
                                }
                            });
                            fnj.setVisible(true);
                        }
                    });
                    return;
                } catch (Exception ex) {
                    errorMessage("Unexpected problem occurred with Neighbor-Net", ex);
                }
            }
        });
        mnuTools.add(mnuToolsFlatnj);

        mnuToolsSuperq = new javax.swing.JMenuItem();
        mnuToolsSuperq.setText("SuperQ");
        mnuToolsSuperq.setMnemonic('S');
        mnuToolsSuperq.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {

                            SuperQGUI sq = new SuperQGUI();
                            sq.addPropertyChangeListener("done", new PropertyChangeListener() {
                                @Override
                                public void propertyChange(PropertyChangeEvent evt) {
                                    if (evt.getNewValue() != null) {
                                        File f = (File)evt.getNewValue();
                                        if (f.exists()) {
                                            try {
                                                openNetwork(f);
                                            }
                                            catch (Exception e) {
                                                errorMessage("Error trying to view network", e);
                                            }
                                        }
                                    }
                                }
                            });
                            sq.setVisible(true);
                        }
                    });
                    return;
                } catch (Exception ex) {
                    errorMessage("Unexpected problem occurred with Neighbor-Net", ex);
                }
            }
        });
        mnuTools.add(mnuToolsSuperq);

        menuBar.add(mnuTools);

        setJMenuBar(menuBar);
    }


    private void cmdLeaderColorSelectActionPerformed(ActionEvent evt) {
        Color newLeaderColor = JColorChooser.showDialog(this, "Font color", drawing.config.getLeaderColor());
        if (newLeaderColor != null) {
            drawing.setLeaderColour(newLeaderColor);
        }
    }

    private void openFileActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser(directory);
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                openNetwork(fileChooser.getSelectedFile());
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

    protected static void errorMessage(String message, Exception ex) {
        log.error(message, ex);
        JOptionPane.showMessageDialog(null, message + "\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected static void errorMessage(String message) {
        log.error(message);
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
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

    private void saveNetwork(File file) throws IOException {
        NexusWriter writer = new NexusWriter();
        writer.appendHeader();
        writer.appendLine();
        writer.append(taxa);
        writer.appendLine();
        writer.append(network);
        writer.appendLine();
        writer.append(drawing.config);
        writer.write(file);
    }



    private void openNetwork(File inFile) throws IOException {

        // Setup drawing pane if it's not already setup
        if (drawing == null) {
            prepareDrawing();
        }

        // Record this directory in case we open any file dialogs in the future
        directory = inFile.getPath();

        // Load the given nexus file
        Nexus nexus = new NexusReader().parse(inFile);

        // Extract taxa from nexus file
        this.taxa = nexus.getTaxa();

        // If no network was defined but there is a split system then convert the split system to a network
        this.network = nexus.getNetwork() == null && nexus.getSplitSystem() != null ?
                new PermutationSequenceDraw(nexus.getSplitSystem()).createOptimisedNetwork() :
                nexus.getNetwork();

        // Assign taxa to network
        this.network.setTaxa(this.taxa);

        // Load config if present in the nexus file otherwise initialise with defaults
        boolean optimiseLayout = nexus.getViewerConfig() == null;
        ViewerConfig config = optimiseLayout ? createDefaultConfig() : nexus.getViewerConfig();

        // Apply config
        this.applyConfig(config);

        // If we've got this far then the file loaded correctly.
        networkFile = inFile;           // Record the file for future saving etc
        mnuFileSave.setEnabled(true);   // Ensure we can save menu is enabled
        setTitle(TITLE + ": " + inFile.getAbsolutePath());  // Update title with the filename

        // If the open panel was used, make sure the opening panel is invisible... not required any more.
        if (pnlOpen != null) {
            pnlOpen.setVisible(false);
        }

        // Now draw the network into the drawing canvas
        drawing.drawNetwork(config, this.network, optimiseLayout);
    }

    public Window getDrawing() {
        return drawing;
    }

    private ViewerConfig createDefaultConfig() {

        Leader.LeaderType leaderType = Leader.LeaderType.NONE;
        if (mnuLabelingLeadersBended.isSelected()) {
            leaderType = Leader.LeaderType.BENDED;
        } else if (mnuLabelingLeadersSlanted.isSelected()) {
            leaderType = Leader.LeaderType.SLANTED;
        } else if (mnuLabelingLeadersStraight.isSelected()) {
            leaderType = Leader.LeaderType.STRAIGHT;
        }
        Leader.LeaderStroke leaderStroke = Leader.LeaderStroke.SOLID;
        if (mnuLabelingLeadersDashed.isSelected()) {
            leaderStroke = Leader.LeaderStroke.DASHED;
        } else if (mnuLabelingLeadersDotted.isSelected()) {
            leaderStroke = Leader.LeaderStroke.DOTTED;
        }

        return new ViewerConfig(drawing.getSize(),
                leaderType,
                leaderStroke,
                drawing != null ? drawing.config.getLeaderColor() : new Color(0,0,0),
                mnuViewShowTrivial.isSelected(),
                mnuViewShowRange.isSelected(),
                mnuLabelingShow.isSelected(),
                mnuLabelingColor.isSelected(),
                new HashSet<Integer>(),
                1.0,
                0.0,
                network == null ? null : network.getLabeledVertices());
    }

    boolean dashedLeaders() {
        return mnuLabelingLeadersDashed.isSelected();
    }

    boolean dottedLeaders() {
        return mnuLabelingLeadersDotted.isSelected();
    }

    private void applyConfig(ViewerConfig config) {

        switch (config.getLeaderType()) {
            case STRAIGHT:
                mnuLabelingLeadersStraight.setSelected(true);
                break;
            case BENDED:
                mnuLabelingLeadersBended.setSelected(true);
                break;
            case SLANTED:
                mnuLabelingLeadersSlanted.setSelected(true);
                break;
            default:
                mnuLabelingLeadersNo.setSelected(true);
                break;
        }

        switch (config.getLeaderStroke()) {
            case DASHED:
                mnuLabelingLeadersDashed.setSelected(true);
                break;
            case DOTTED:
                mnuLabelingLeadersDotted.setSelected(true);
                break;
            default:
                mnuLabelingLeadersSolid.setSelected(true);
                break;
        }

        if (config.getLeaderColor() != null) {
            drawing.config.setLeaderColor(config.getLeaderColor());
        }

        mnuLabelingColor.setSelected(config.colorLabels());
        this.drawing.config.setColorLabels(config.colorLabels());

        mnuLabelingShow.setSelected(config.showLabels());
        this.drawing.config.setShowLabels(config.showLabels());

        mnuViewShowTrivial.setSelected(config.showTrivial());
        this.drawing.config.setShowTrivial(config.showTrivial());

        mnuViewShowRange.setSelected(config.showRange());
        this.drawing.config.setShowRange(config.showRange());

        Set<Integer> fixed = config.getFixed();
        for (Vertex vertex : network.getAllVertices()) {
            if (fixed.contains(vertex.getNxnum())) {
                vertex.getLabel().movable = false;
            }
        }
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

                    openNetwork(new File(new URI(token)));
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
                        Spectre nv = inputfile == null ? new Spectre() : new Spectre(inputfile);
                        log.info("Viewer initialised");
                        nv.setDefaultCloseOperation(commandLine.hasOption(OPT_DISPOSE) ? WindowConstants.DISPOSE_ON_CLOSE : javax.swing.WindowConstants.EXIT_ON_CLOSE);
                        nv.setVisible(true);
                    } catch (Exception e) {
                        errorMessage("Unexpected problem occurred while running Spectre", e);
                        System.exit(4);
                    }
                }
            });
            return;
        } catch (Exception e) {
            System.err.println("\nException: " + e.toString());
            System.err.println("\nStack trace:");
            System.err.println(StringUtils.join(e.getStackTrace(), "\n"));
            System.exit(3);
        }
    }
}

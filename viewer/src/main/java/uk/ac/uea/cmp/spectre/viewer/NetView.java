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
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
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
 * @author balvociute
 */
public class NetView extends javax.swing.JFrame implements DropTargetListener {
    private static Logger log = LoggerFactory.getLogger(NetView.class);

    private static String BIN_NAME = "netview";

    private static String OPT_HELP = "help";
    private static String OPT_VERBOSE = "verbose";

    private Point startPoint;
    private JFrame format;
    private JFrame formatLabels;
    private DropTarget dt;


    private String mainTitle = "NetView";
    private boolean resetlabelPositions = false;
    private NexusReader read;

    private File networkFile = null;
    int nDots = 40;

    private double filteringThr = 0.15;

    /**
     * Creates new form NetVi
     */
    public NetView() throws IOException {
        prepareViewer();
    }

    private void prepareViewer() {
        initComponents();
        preparePopupMenu();
        getContentPane().setBackground(Color.white);
        dt = new DropTarget(drawing, this);
        setIconImage((new ImageIcon("logo.png")).getImage());
        ButtonGroup leaderButtons = new ButtonGroup();
        leaderButtons.add(jRadioButtonBendedLeaders);
        leaderButtons.add(jRadioButtonSlantedLeaders);
        leaderButtons.add(jRadioButtonStraightLeaders);
        leaderButtons.add(jRadioButtonNoLeaders);
        format = new Formating(drawing);
        formatLabels = new FormatLabels(drawing);
        //setLocationRelativeTo(null);
        ButtonGroup leaderLineTypes = new ButtonGroup();
        leaderLineTypes.add(jRadioButtonSolid);
        leaderLineTypes.add(jRadioButtonDashed);
        leaderLineTypes.add(jRadioButtonDotted);
    }

    public NetView(File inFile) throws IOException {
        prepareViewer();
        directory = inFile.getPath();
        openNetwork(inFile);
    }

    public NetView(Network network, File inFile, IdentifierList taxa) {
        prepareViewer();
        directory = inFile.getPath();
        this.network = network;
        this.taxa = taxa;
        showNetwork(inFile);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu = new javax.swing.JPopupMenu();
        drawing = new Window(this);
        menuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        openFile = new javax.swing.JMenuItem();
        saveNetwork = new javax.swing.JMenuItem();
        saveNetworkAs = new javax.swing.JMenuItem();
        saveImage = new javax.swing.JMenuItem();
        exitProgram = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemSelectAll = new javax.swing.JMenuItem();
        jMenuItemFind = new javax.swing.JMenuItem();
        formatNodes = new javax.swing.JMenuItem();
        jMenuLayout = new javax.swing.JMenu();
        jMenuItemRotate = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItemFlipHorizontal = new javax.swing.JMenuItem();
        jMenuItemFlipVertical = new javax.swing.JMenuItem();
        jCheckBoxShowTrivial = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxShowRange = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxShowLabels = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxColorLabels = new javax.swing.JCheckBoxMenuItem();
        jMenuLabeling = new javax.swing.JMenu();
        jCheckBoxFixLabelPositions = new javax.swing.JCheckBoxMenuItem();
        optimizeLabels = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jRadioButtonNoLeaders = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonStraightLeaders = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonSlantedLeaders = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonBendedLeaders = new javax.swing.JRadioButtonMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jRadioButtonSolid = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonDashed = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonDotted = new javax.swing.JRadioButtonMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemChangeLeaderColor = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("NetVi");
        setForeground(java.awt.Color.white);
        this.setPreferredSize(new Dimension(800,600));
        this.setMinimumSize(new Dimension(400,300));

        drawing.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                drawingMouseWheelMoved(evt);
            }
        });
        drawing.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                drawingMouseClicked(evt);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                drawingMousePressed(evt);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                drawingMouseReleased(evt);
            }
        });
        drawing.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                drawingComponentResized(evt);
            }
        });
        drawing.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                drawingMouseDragged(evt);
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

        jMenuFile.setText("File");

        openFile.setText("Open network...");
        openFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileActionPerformed(evt);
            }
        });
        jMenuFile.add(openFile);
        jMenuFile.addSeparator();

        saveNetwork.setText("Save network");
        saveNetwork.setEnabled(false);
        saveNetwork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNetworkActionPerformed(evt);
            }
        });
        jMenuFile.add(saveNetwork);

        saveNetworkAs.setText("Save network as...");
        saveNetworkAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNetworkAsActionPerformed(evt);
            }
        });
        jMenuFile.add(saveNetworkAs);

        saveImage.setText("Save image as...");
        saveImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveImageActionPerformed(evt);
            }
        });
        jMenuFile.add(saveImage);
        jMenuFile.addSeparator();

        exitProgram.setText("Exit");
        exitProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitProgramActionPerformed(evt);
            }
        });
        jMenuFile.add(exitProgram);

        menuBar.add(jMenuFile);

        jMenuEdit.setText("Edit");

        jMenuItemSelectAll.setText("Select all");
        jMenuItemSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSelectAllActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemSelectAll);

        jMenuItemFind.setText("Find...");
        jMenuItemFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFindActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemFind);



        menuBar.add(jMenuEdit);

        jMenuLayout.setText("Layout");

        jMenuItemRotate.setText("Rotate");
        jMenuItemRotate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRotateActionPerformed(evt);
            }
        });
        jMenuLayout.add(jMenuItemRotate);

        jMenu6.setText("Flip");

        jMenuItemFlipHorizontal.setText("Horizontal");
        jMenuItemFlipHorizontal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFlipHorizontalActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemFlipHorizontal);

        jMenuItemFlipVertical.setText("Vertical");
        jMenuItemFlipVertical.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFlipVerticalActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemFlipVertical);

        jMenuLayout.add(jMenu6);
        jMenuLayout.addSeparator();

        jCheckBoxShowTrivial.setSelected(true);
        jCheckBoxShowTrivial.setText("Show trivial splits");
        jCheckBoxShowTrivial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxShowTrivialActionPerformed(evt);
            }
        });
        jMenuLayout.add(jCheckBoxShowTrivial);

        jCheckBoxShowRange.setSelected(true);
        jCheckBoxShowRange.setText("Show range");
        jCheckBoxShowRange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxShowRangeActionPerformed(evt);
            }
        });
        jMenuLayout.add(jCheckBoxShowRange);



        menuBar.add(jMenuLayout);

        jMenuLabeling.setText("Labeling");

        jCheckBoxShowLabels.setSelected(true);
        jCheckBoxShowLabels.setText("Show labels");
        jCheckBoxShowLabels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxShowLabelsActionPerformed(evt);
            }
        });
        jMenuLabeling.add(jCheckBoxShowLabels);

        jCheckBoxColorLabels.setText("Color labels");
        jCheckBoxColorLabels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxColorLabelsActionPerformed(evt);
            }
        });
        jMenuLabeling.add(jCheckBoxColorLabels);
        jMenuLabeling.addSeparator();

        formatNodes.setText("Format selected nodes ...");
        formatNodes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formatNodesActionPerformed(evt);
            }
        });
        jMenuLabeling.add(formatNodes);

        jMenuLabeling.addSeparator();

        jCheckBoxFixLabelPositions.setText("Fix all label positions");
        jCheckBoxFixLabelPositions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxFixLabelPositionsActionPerformed(evt);
            }
        });
        jMenuLabeling.add(jCheckBoxFixLabelPositions);

        optimizeLabels.setText("Optimize labels");
        optimizeLabels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optimizeLabelsActionPerformed(evt);
            }
        });
        jMenuLabeling.add(optimizeLabels);

        jMenuLabeling.addSeparator();

        jMenu4.setText("Leaders");

        jRadioButtonNoLeaders.setText("None");
        jRadioButtonNoLeaders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonNoLeadersActionPerformed(evt);
            }
        });
        jMenu4.add(jRadioButtonNoLeaders);

        jRadioButtonStraightLeaders.setText("Straight");
        jRadioButtonStraightLeaders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonStraightLeadersActionPerformed(evt);
            }
        });
        jMenu4.add(jRadioButtonStraightLeaders);

        jRadioButtonSlantedLeaders.setSelected(true);
        jRadioButtonSlantedLeaders.setText("Slanted");
        jRadioButtonSlantedLeaders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonSlantedLeadersActionPerformed(evt);
            }
        });
        jMenu4.add(jRadioButtonSlantedLeaders);

        jRadioButtonBendedLeaders.setText("Bended");
        jRadioButtonBendedLeaders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonBendedLeadersActionPerformed(evt);
            }
        });
        jMenu4.add(jRadioButtonBendedLeaders);
        jMenu4.add(jSeparator1);


        jRadioButtonSolid.setText("Solid");
        jRadioButtonSolid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonSolidActionPerformed(evt);
            }
        });
        jMenu4.add(jRadioButtonSolid);

        jRadioButtonDashed.setSelected(true);
        jRadioButtonDashed.setText("Dashed");
        jRadioButtonDashed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonDashedActionPerformed(evt);
            }
        });
        jMenu4.add(jRadioButtonDashed);

        jRadioButtonDotted.setText("Dotted");
        jRadioButtonDotted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonDottedActionPerformed(evt);
            }
        });
        jMenu4.add(jRadioButtonDotted);
        jMenu4.add(jSeparator2);

        jMenuItemChangeLeaderColor.setText("Set color");
        jMenuItemChangeLeaderColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChangeLeaderColorActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItemChangeLeaderColor);

        jMenuLabeling.add(jMenu4);

        menuBar.add(jMenuLabeling);

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
    }// </editor-fold>//GEN-END:initComponents

    private void openFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openFileActionPerformed
    {//GEN-HEADEREND:event_openFileActionPerformed
        JFileChooser fileChooser = new JFileChooser(directory);
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File inFile = fileChooser.getSelectedFile();
            directory = inFile.getPath();
            openNetwork(inFile);
        }
    }//GEN-LAST:event_openFileActionPerformed

    private void saveImageActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveImageActionPerformed
    {//GEN-HEADEREND:event_saveImageActionPerformed
        JFileChooser fileChooser = new JFileChooser(directory);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Portable Document Format (.pdf)", "pdf"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image File(.png, .jpg, .gif)", "png","jpg","gif"));
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
            }
            else if (ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("gif")) {
                try {
                    saveImage(image_out);
                } catch (IOException ex) {
                    errorMessage("Error saving PNG", ex);
                }
            }
            else {
                errorMessage("No recognised extension specified in filename.  Please type an appropriate extension for image.");
            }
        }
    }//GEN-LAST:event_saveImageActionPerformed

    private void saveNetworkAsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveNetworkAsActionPerformed
    {//GEN-HEADEREND:event_saveNetworkAsActionPerformed
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
                    }
                    catch(IOException ioe) {
                        errorMessage("Problem occured while trying to save network", ioe);
                    }
                }
            }
        }
    }//GEN-LAST:event_saveNetworkAsActionPerformed

    private void optimizeLabelsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_optimizeLabelsActionPerformed
    {//GEN-HEADEREND:event_optimizeLabelsActionPerformed
        if (network != null) {
            drawing.resetLabelPositions(true);
            drawing.repaintOnResize();
        }
    }//GEN-LAST:event_optimizeLabelsActionPerformed

    private void jRadioButtonStraightLeadersActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonStraightLeadersActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonStraightLeadersActionPerformed
        drawing.repaint();
        config.setLeaderType("straight");
    }//GEN-LAST:event_jRadioButtonStraightLeadersActionPerformed

    private void jRadioButtonSlantedLeadersActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonSlantedLeadersActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonSlantedLeadersActionPerformed
        drawing.repaint();
        config.setLeaderType("slanted");
    }//GEN-LAST:event_jRadioButtonSlantedLeadersActionPerformed

    private void formatNodesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_formatNodesActionPerformed
    {//GEN-HEADEREND:event_formatNodesActionPerformed
        if (drawing.isSelected()) {
            ((Formating) format).setVisible();
        } else {
            JOptionPane.showMessageDialog(this, "No nodes are selected.");
        }
    }//GEN-LAST:event_formatNodesActionPerformed

    private void drawingMouseDragged(java.awt.event.MouseEvent evt)//GEN-FIRST:event_drawingMouseDragged
    {//GEN-HEADEREND:event_drawingMouseDragged
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
    }//GEN-LAST:event_drawingMouseDragged

    private void drawingComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_drawingComponentResized
    {//GEN-HEADEREND:event_drawingComponentResized
        drawing.repaintOnResize();
        config.setDimensions(this.getSize());
    }//GEN-LAST:event_drawingComponentResized

    private void drawingMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_drawingMouseReleased
    {//GEN-HEADEREND:event_drawingMouseReleased
        if (drawing.rotate) {
            drawing.activateRotation(false);
        }
        drawing.removeSelectionRectangle();
        //drawing.repaintOnResize();
    }//GEN-LAST:event_drawingMouseReleased

    private void drawingMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_drawingMousePressed
    {//GEN-HEADEREND:event_drawingMousePressed
        startPoint = evt.getPoint();
        if (!drawing.markLabel(startPoint)) {
            drawing.markPoint(startPoint);
        }
    }//GEN-LAST:event_drawingMousePressed

    private void drawingMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_drawingMouseClicked
    {//GEN-HEADEREND:event_drawingMouseClicked
        java.awt.Point clickedPoint = evt.getPoint();
        if (SwingUtilities.isRightMouseButton(evt)) {
            popupMenu.show(drawing, clickedPoint.x, clickedPoint.y);
        } else {
            drawing.setSelection(clickedPoint, evt.isControlDown());
        }
    }//GEN-LAST:event_drawingMouseClicked

    private void drawingMouseWheelMoved(java.awt.event.MouseWheelEvent evt)//GEN-FIRST:event_drawingMouseWheelMoved
    {//GEN-HEADEREND:event_drawingMouseWheelMoved
        /*drawing.zoom(evt.getPreciseWheelRotation(),
            MouseInfo.getPointerInfo().getLocation());*/
    }//GEN-LAST:event_drawingMouseWheelMoved

    private void jMenuItemRotateActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonRotateActionPerformed
    {//GEN-HEADEREND:event_jButtonRotateActionPerformed
        drawing.activateRotation(true);
    }//GEN-LAST:event_jButtonRotateActionPerformed

    private void jMenuItemFlipHorizontalActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItemFlipHorizontalActionPerformed
    {//GEN-HEADEREND:event_jMenuItemFlipHorizontalActionPerformed
        drawing.flipNetwork(true);
    }//GEN-LAST:event_jMenuItemFlipHorizontalActionPerformed

    private void jMenuItemFlipVerticalActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItemFlipVerticalActionPerformed
    {//GEN-HEADEREND:event_jMenuItemFlipVerticalActionPerformed
        drawing.flipNetwork(false);
    }//GEN-LAST:event_jMenuItemFlipVerticalActionPerformed

    private void jRadioButtonBendedLeadersActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonBendedLeadersActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonBendedLeadersActionPerformed
        drawing.repaint();
        config.setLeaderType("bended");
    }//GEN-LAST:event_jRadioButtonBendedLeadersActionPerformed

    private void jMenuItemFindActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItemFindActionPerformed
    {//GEN-HEADEREND:event_jMenuItemFindActionPerformed
        find.setVisible(true);
    }//GEN-LAST:event_jMenuItemFindActionPerformed

    private void jCheckBoxShowLabelsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxShowLabelsActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxShowLabelsActionPerformed
        drawing.repaintOnResize();
    }//GEN-LAST:event_jCheckBoxShowLabelsActionPerformed

    private void jCheckBoxColorLabelsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxColorLabelsActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxColorLabelsActionPerformed
        drawing.repaint();
    }//GEN-LAST:event_jCheckBoxColorLabelsActionPerformed

    private void jCheckBoxShowTrivialActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxShowTrivialActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxShowTrivialActionPerformed
        Boolean showTrivial = jCheckBoxShowTrivial.isSelected();
        drawing.showTrivial(showTrivial);
        config.setShowTrivial(showTrivial);
    }//GEN-LAST:event_jCheckBoxShowTrivialActionPerformed

    private void jCheckBoxShowRangeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxShowTrivialActionPerformed
    {
        Boolean showRange = jCheckBoxShowRange.isSelected();
        drawing.showRange(showRange);
        config.setShowRange(showRange);
        drawing.repaint();
    }

    private void jRadioButtonNoLeadersActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonNoLeadersActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonNoLeadersActionPerformed
        drawing.repaint();
        config.setLeaderType("none");
    }//GEN-LAST:event_jRadioButtonNoLeadersActionPerformed

    private void exitProgramActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exitProgramActionPerformed
    {//GEN-HEADEREND:event_exitProgramActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitProgramActionPerformed

    private void saveNetworkActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveNetworkActionPerformed
    {//GEN-HEADEREND:event_saveNetworkActionPerformed
        try {
            this.saveNetwork(networkFile);
        }
        catch (IOException ioe) {
            log.error("Problem occured while trying to save network", ioe);
        }
    }//GEN-LAST:event_saveNetworkActionPerformed

    private void jRadioButtonSolidActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonSolidActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonSolidActionPerformed
        config.setLeaderStroke("solid");
        drawing.repaint();
    }//GEN-LAST:event_jRadioButtonSolidActionPerformed

    private void jRadioButtonDashedActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonDashedActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonDashedActionPerformed
        config.setLeaderStroke("dashed");
        drawing.repaint();
    }//GEN-LAST:event_jRadioButtonDashedActionPerformed

    private void jRadioButtonDottedActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jRadioButtonDottedActionPerformed
    {//GEN-HEADEREND:event_jRadioButtonDottedActionPerformed
        config.setLeaderType("dotted");
        drawing.repaint();
    }//GEN-LAST:event_jRadioButtonDottedActionPerformed

    private void jMenuItemChangeLeaderColorActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItemChangeLeaderColorActionPerformed
    {//GEN-HEADEREND:event_jMenuItemChangeLeaderColorActionPerformed
        Color newLeaderColor = JColorChooser.showDialog(this, "Font color", drawing.leaderColor);
        if (newLeaderColor != null) {
            config.setLeaderColor(newLeaderColor);
            drawing.leaderColor = newLeaderColor;
            drawing.repaint();
        }
    }//GEN-LAST:event_jMenuItemChangeLeaderColorActionPerformed

    private void jMenuItemSelectAllActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jMenuItemSelectAllActionPerformed
    {//GEN-HEADEREND:event_jMenuItemSelectAllActionPerformed
        drawing.selectAll();
    }//GEN-LAST:event_jMenuItemSelectAllActionPerformed

    private void jCheckBoxFixLabelPositionsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBoxFixLabelPositionsActionPerformed
    {//GEN-HEADEREND:event_jCheckBoxFixLabelPositionsActionPerformed
        boolean fix = jCheckBoxFixLabelPositions.isSelected();
        drawing.fixLabels(!fix);
    }//GEN-LAST:event_jCheckBoxFixLabelPositionsActionPerformed


    /**
     * @param input the input file to load and display
     */
    public static void startWithInput(final File input) {
        LookAndFeel.setLookAndFeel(LookAndFeel.NIMBUS);

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
            try {
                new NetView(input).setVisible(true);
            } catch (IOException ex) {
                errorMessage("Problem occured while running NetView", ex);
            }
            }
        });
    }

    protected static void errorMessage(String message, Exception ex) {
        log.error(message, ex);
        JOptionPane.showMessageDialog(null, message + "\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected static void errorMessage(String message) {
        log.error(message);
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }


    private static Options createOptions() {

        Options options = new Options();
        options.addOption(CommandLineHelper.HELP_OPTION);
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
            BasicConfigurator.configure(new ConsoleAppender(new PatternLayout("%d{HH:mm:ss} %p: %m%n")));

            LookAndFeel.setLookAndFeel(LookAndFeel.NIMBUS);

            // Parse command line args
            CommandLine commandLine = CommandLineHelper.startApp(createOptions(), "netview [options] <input>",
                    "Visualises a network.  Can take a nexus file as input.", args, false);

            // If we didn't return a command line object then just return.  Probably the user requested help or
            // input invalid args
            if (commandLine == null) {
                return;
            }

            final File inputfile = commandLine == null || commandLine.getArgs().length == 0 ? null : new File(commandLine.getArgs()[0]);

            if (commandLine.getArgs().length > 1) {
                throw new IOException("Expected only a single input file.");
            }


            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (inputfile == null) {
                            new NetView().setVisible(true);
                        } else {
                            new NetView(inputfile).setVisible(true);
                        }
                    } catch (IOException ex) {
                        errorMessage("Problem occured while running NetView", ex);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Window drawing;
    private javax.swing.JMenuItem exitProgram;
    private javax.swing.JMenuItem formatNodes;
    private javax.swing.JCheckBoxMenuItem jCheckBoxColorLabels;
    private javax.swing.JCheckBoxMenuItem jCheckBoxFixLabelPositions;
    private javax.swing.JCheckBoxMenuItem jCheckBoxShowLabels;
    private javax.swing.JCheckBoxMenuItem jCheckBoxShowTrivial;
    private javax.swing.JCheckBoxMenuItem jCheckBoxShowRange;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemChangeLeaderColor;
    private javax.swing.JMenuItem jMenuItemFind;
    private javax.swing.JMenuItem jMenuItemFlipHorizontal;
    private javax.swing.JMenuItem jMenuItemFlipVertical;
    private javax.swing.JMenuItem jMenuItemRotate;
    private javax.swing.JMenuItem jMenuItemSelectAll;
    private javax.swing.JMenu jMenuLabeling;
    private javax.swing.JMenu jMenuLayout;
    private javax.swing.JRadioButtonMenuItem jRadioButtonBendedLeaders;
    private javax.swing.JRadioButtonMenuItem jRadioButtonDashed;
    private javax.swing.JRadioButtonMenuItem jRadioButtonDotted;
    private javax.swing.JRadioButtonMenuItem jRadioButtonNoLeaders;
    private javax.swing.JRadioButtonMenuItem jRadioButtonSlantedLeaders;
    private javax.swing.JRadioButtonMenuItem jRadioButtonSolid;
    private javax.swing.JRadioButtonMenuItem jRadioButtonStraightLeaders;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openFile;
    private javax.swing.JMenuItem optimizeLabels;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JMenuItem saveImage;
    private javax.swing.JMenuItem saveNetwork;
    private javax.swing.JMenuItem saveNetworkAs;

    // End of variables declaration//GEN-END:variables
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

        setTitle(mainTitle + ": " + fileToSave.getAbsolutePath());
        saveNetwork.setEnabled(true);
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
        return jMenuLabeling.isSelected();
    }

    private void preparePopupMenu() {
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

    private void openNetwork(File inFile) {
        directory = inFile.getPath();
        try {
            Nexus nexus = new NexusReader().parse(inFile);

            // If no network was defined but there is a split system then convert the split system to a network
            if (nexus.getNetwork() == null && nexus.getSplitSystem() != null) {

                // Create network
                network = new FlatNetwork(new PermutationSequenceDraw(nexus.getSplitSystem()).drawSplitSystem(-1.0));

                // Setup labels
                for(Vertex v : network.getAllVertices()) {
                    if (v.getTaxa().size() > 0) {
                        String label = new String();
                        for(Identifier i : v.getTaxa()) {
                            label = (i.getName() + ", ").concat(label);
                        }
                        label = label.substring(0, label.length() - 2);
                        v.setLabel(new NetworkLabel(label));
                    }
                }
            }
            else {
                network = nexus.getNetwork();
            }
            this.taxa = nexus.getTaxa();
            this.network.setTaxa(this.taxa);

            if (nexus.getViewerConfig() != null) {
                this.config = nexus.getViewerConfig();
                applyConfig(this.config);
            }
            else {
                initConfig();
            }

            networkFile = inFile;
            saveNetwork.setEnabled(true);
            showNetwork(inFile);
        } catch (IOException e) {
            errorMessage("Problem occured while loading Nexus file containing Network: " + inFile, e);
        }
    }

    private void showNetwork(File inFile) {
        if (network != null) {
            setTitle(mainTitle + ": " + inFile.getAbsolutePath());
            drawNetwork();
            drawing.repaint();
        }
    }

    public Window getDrawing() {
        return drawing;
    }

    boolean showLabels() {
        return jCheckBoxShowLabels.isSelected();
    }

    boolean colorLabels() {
        return jCheckBoxColorLabels.isSelected();
    }

    private void initConfig() {
        resetlabelPositions = true;
        String leaderType = null;
        if (jRadioButtonBendedLeaders.isSelected()) {
            leaderType = "bended";
        } else if (jRadioButtonSlantedLeaders.isSelected()) {
            leaderType = "slanted";
        } else if (jRadioButtonStraightLeaders.isSelected()) {
            leaderType = "straight";
        }
        String leaderStroke = "solid";
        if (jRadioButtonDashed.isSelected()) {
            leaderStroke = "dashed";
        } else if (jRadioButtonDotted.isSelected()) {
            leaderStroke = "dotted";
        }

        config = new ViewerConfig(drawing.getSize(),
                leaderType,
                leaderStroke,
                drawing.leaderColor,
                jCheckBoxShowTrivial.isSelected(),
                jCheckBoxShowRange.isSelected(),
                jCheckBoxShowLabels.isSelected(),
                jCheckBoxColorLabels.isSelected(),
                new HashSet<Integer>(),
                null,
                network.getLabeledVertices());
    }

    boolean dashedLeaders() {
        return jRadioButtonDashed.isSelected();
    }

    boolean dottedLeaders() {
        return jRadioButtonDotted.isSelected();
    }

    private void applyConfig(ViewerConfig config) {
        String leaderType = config.getLeaderType();
        switch (leaderType) {
            case "straight":
                jRadioButtonStraightLeaders.setSelected(true);
                break;
            case "bended":
                jRadioButtonBendedLeaders.setSelected(true);
                break;
            case "slanted":
                jRadioButtonSlantedLeaders.setSelected(true);
                break;
            default:
                jRadioButtonNoLeaders.setSelected(true);
                break;
        }

        String stroke = config.getLeaderStroke();
        switch (stroke) {
            case "dashed":
                jRadioButtonDashed.setSelected(true);
                break;
            case "dotted":
                jRadioButtonDotted.setSelected(true);
                break;
            default:
                jRadioButtonSolid.setSelected(true);
                break;
        }

        Color leaderColor = config.getLeaderColor();
        if (leaderColor != null) {
            drawing.leaderColor = leaderColor;
        }

        jCheckBoxColorLabels.setSelected(config.colorLabels());
        jCheckBoxShowLabels.setSelected(config.showLabels());
        jCheckBoxShowTrivial.setSelected(config.showTrivial());
        jCheckBoxShowRange.setSelected(config.isShowRange());

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
        return !jRadioButtonNoLeaders.isSelected();
    }

    boolean straightLeaders() {
        return jRadioButtonStraightLeaders.isSelected();
    }

    boolean bendedLeaders() {
        return jRadioButtonBendedLeaders.isSelected();
    }

    boolean slantedLeaders() {
        return jRadioButtonSlantedLeaders.isSelected();
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
        }
        catch (ClassNotFoundException e) {
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
}

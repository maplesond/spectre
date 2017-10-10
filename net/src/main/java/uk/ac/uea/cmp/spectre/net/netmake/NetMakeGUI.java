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

package uk.ac.uea.cmp.spectre.net.netmake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ds.distance.DistanceCalculatorFactory;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.CircularOrderingAlgorithms;
import uk.ac.uea.cmp.spectre.core.ds.split.circular.ordering.nm.weighting.Weightings;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusFileFilter;
import uk.ac.uea.cmp.spectre.core.ui.gui.JobController;
import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTrackerWithView;
import uk.ac.uea.cmp.spectre.core.ui.gui.ToolHost;
import uk.ac.uea.cmp.spectre.core.util.LogConfig;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URISyntaxException;

import static uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel.NIMBUS;
import static uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel.setLookAndFeel;

public class NetMakeGUI extends JFrame implements ToolHost {

    private static Logger log = LoggerFactory.getLogger(NetMakeGUI.class);

    private static final String TITLE = "NetMake";

    // ***** GUI components *****

    private JPanel pnlOptions;

    private JPanel pnlInput;
    private JPanel pnlSelectInput;
    private JLabel lblInput;
    private JTextField txtInput;
    private JButton cmdInput;

    private JPanel pnlAlgorithm;
    private JLabel lblCoAlgorithm;
    private JComboBox<CircularOrderingAlgorithms> cboCoAlgorithm;
    private JLabel lblDmAlgorithm;
    private JComboBox<DistanceCalculatorFactory> cboDmAlgorithm;

    private JPanel pnlWeightings;
    private JLabel lblWeighting1;
    private JComboBox<Weightings> cboWeighting1;
    private JLabel lblWeighting2;
    private JComboBox<String> cboWeighting2;
    private JLabel lblWeight;
    private JTextField txtWeight;

    private JPanel pnlOutput;
    private JPanel pnlSelectOutputNetwork;
    private JLabel lblOutputNetwork;
    private JTextField txtOutputNetwork;
    private JButton cmdOutputNetwork;
    private JPanel pnlSelectOutputTree;
    private JLabel lblOutputTree;
    private JTextField txtOutputTree;
    private JButton cmdOutputTree;

    private JPanel pnlStatus;
    private JPanel pnlControlButtons;
    private JButton cmdCancel;
    private JButton cmdRun;
    private JButton cmdViewOutput;
    private JLabel lblStatus;
    private JProgressBar progStatus;


    private JDialog dialog = new JDialog(this, TITLE);
    private JFrame gui = new JFrame(TITLE);
    private JobController go_control;
    private NetMakeRunner netMakeRunner;

    private File lastOutput;
    private File cwd;

    public NetMakeGUI() {
        initComponents();
        setTitle(TITLE);

        try {
            setIconImage((new ImageIcon(uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel.getLogoFilePath()).getImage()));
        } catch (URISyntaxException e) {
            showErrorDialog("Couldn't load logo.");
        }

        this.lastOutput = null;
        this.cwd = null;

        cmdRun.setEnabled(true);

        this.netMakeRunner = new NetMakeRunner(this);

        this.go_control = new JobController(this.cmdRun, this.cmdCancel);
        setRunningStatus(false);
    }

    /**
     * Input options
     */
    private void initInputComponents() {

        lblInput = new JLabel();
        txtInput = new JTextField();
        cmdInput = new JButton();

        cmdInput.setText("...");
        cmdInput.setToolTipText(NetMakeOptions.DESC_INPUT);
        cmdInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdInputActionPerformed(evt);
            }
        });

        txtInput.setPreferredSize(new Dimension(200, 25));
        txtInput.setToolTipText(NetMakeOptions.DESC_INPUT);

        lblInput.setText("Input file:");
        lblInput.setToolTipText(NetMakeOptions.DESC_INPUT);

        pnlSelectInput = new JPanel();
        pnlSelectInput.setLayout(new BoxLayout(pnlSelectInput, BoxLayout.LINE_AXIS));
        pnlSelectInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlSelectInput.add(Box.createHorizontalGlue());
        pnlSelectInput.add(lblInput);
        pnlSelectInput.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectInput.add(txtInput);
        pnlSelectInput.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectInput.add(cmdInput);

        pack();

        pnlInput = new JPanel();
        pnlInput.setLayout(new BoxLayout(pnlInput, BoxLayout.PAGE_AXIS));
        pnlInput.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Input:"));
        pnlInput.add(Box.createVerticalGlue());
        pnlInput.add(pnlSelectInput);

        pack();
    }

    private void initAlgorithmComponents() {

        cboCoAlgorithm = new JComboBox<>();
        cboDmAlgorithm = new JComboBox<>();
        lblCoAlgorithm = new JLabel();
        lblDmAlgorithm = new JLabel();

        cboCoAlgorithm.setModel(new DefaultComboBoxModel<>(CircularOrderingAlgorithms.values()));
        cboCoAlgorithm.setToolTipText(NetMakeOptions.DESC_CO_ALG);
        cboCoAlgorithm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSelectAlgorithmActionPerformed(evt);
            }
        });

        lblCoAlgorithm.setText("Select circular ordering algorithm:");
        lblCoAlgorithm.setToolTipText(NetMakeOptions.DESC_CO_ALG);

        cboDmAlgorithm.setModel(new DefaultComboBoxModel<>(DistanceCalculatorFactory.values()));
        cboDmAlgorithm.setToolTipText(NetMakeOptions.DESC_DIST_CALC);
        cboDmAlgorithm.setSelectedItem(DistanceCalculatorFactory.JUKES_CANTOR);

        lblDmAlgorithm.setText("Select distance matrix calculator:");
        lblDmAlgorithm.setToolTipText(NetMakeOptions.DESC_DIST_CALC);

        pnlAlgorithm = new JPanel();

        GroupLayout algLayout = new GroupLayout(pnlAlgorithm);

        algLayout.setAutoCreateGaps(true);
        algLayout.setAutoCreateContainerGaps(true);

        algLayout.setHorizontalGroup(
                algLayout.createSequentialGroup()
                        .addGroup(algLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblCoAlgorithm)
                                        .addComponent(lblDmAlgorithm)
                        )
                        .addGroup(algLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(cboCoAlgorithm)
                                        .addComponent(cboDmAlgorithm)
                        )

        );
        algLayout.setVerticalGroup(
                algLayout.createSequentialGroup()
                        .addGroup(algLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblCoAlgorithm)
                                        .addComponent(cboCoAlgorithm)
                        )
                        .addGroup(algLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblDmAlgorithm)
                                .addComponent(cboDmAlgorithm)
                        )
        );

        pnlAlgorithm.setLayout(algLayout);
        pnlAlgorithm.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Algorithms:"));

        pack();
    }


    /**
     * Optimiser options
     */
    private void initWeightingComponents() {

        cboWeighting1 = new JComboBox<>();
        lblWeighting1 = new JLabel();

        cboWeighting2 = new JComboBox<>();
        lblWeighting2 = new JLabel();

        txtWeight = new JTextField();
        lblWeight = new JLabel();

        cboWeighting1.setModel(new DefaultComboBoxModel<>(Weightings.values()));
        cboWeighting1.setToolTipText(NetMakeOptions.DESC_WEIGHTINGS_1);

        lblWeighting1.setText("Select weight type 1:");
        lblWeighting1.setToolTipText(NetMakeOptions.DESC_WEIGHTINGS_1);

        cboWeighting2.setModel(new DefaultComboBoxModel<>(Weightings.stringValuesWithNone()));
        cboWeighting2.setToolTipText(NetMakeOptions.DESC_WEIGHTINGS_2);

        lblWeighting2.setText("Select weight type 2:");
        lblWeighting2.setToolTipText(NetMakeOptions.DESC_WEIGHTINGS_2);


        lblWeight.setText("Set the tree weight:");
        lblWeight.setToolTipText(NetMakeOptions.DESC_TREE_PARAM);

        txtWeight.setText(Double.toString(NetMakeOptions.DEFAULT_TREE_WEIGHT));
        txtWeight.setPreferredSize(new Dimension(200, 25));
        txtWeight.setToolTipText(NetMakeOptions.DESC_TREE_PARAM);


        pnlWeightings = new JPanel();


        GroupLayout layout = new GroupLayout(pnlWeightings);

        pnlWeightings.setLayout(layout);
        pnlWeightings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Weighting Setup:"));

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(lblWeighting1)
                                .addComponent(lblWeighting2)
                                .addComponent(lblWeight)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(cboWeighting1)
                                .addComponent(cboWeighting2)
                                .addComponent(txtWeight)
                        )

        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblWeighting1)
                                .addComponent(cboWeighting1)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblWeighting2)
                                .addComponent(cboWeighting2)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblWeight)
                                .addComponent(txtWeight)
                        )
        );

        pack();

    }


    /**
     * Output options
     */
    private void initOutputComponents() {


        pnlOutput = new JPanel(new BorderLayout());

        lblOutputNetwork = new JLabel();
        txtOutputNetwork = new JTextField();
        cmdOutputNetwork = new JButton();

        lblOutputTree = new JLabel();
        txtOutputTree = new JTextField();
        cmdOutputTree = new JButton();

        txtOutputNetwork.setPreferredSize(new Dimension(200, 25));
        txtOutputNetwork.setToolTipText(NetMakeOptions.DESC_OUTPUT_NETWORK);

        lblOutputNetwork.setText("Output network file:");
        lblOutputNetwork.setToolTipText(NetMakeOptions.DESC_OUTPUT_NETWORK);

        cmdOutputNetwork.setText("...");
        cmdOutputNetwork.setToolTipText(NetMakeOptions.DESC_OUTPUT_NETWORK);
        cmdOutputNetwork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOutputNetworkActionPerformed(evt);
            }
        });

        lblOutputTree.setText("Output tree file:");
        lblOutputTree.setToolTipText(NetMakeOptions.DESC_OUTPUT_TREE);

        txtOutputTree.setPreferredSize(new Dimension(200, 25));
        txtOutputTree.setToolTipText(NetMakeOptions.DESC_OUTPUT_TREE);

        cmdOutputTree.setText("...");
        cmdOutputTree.setToolTipText(NetMakeOptions.DESC_OUTPUT_TREE);
        cmdOutputTree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOutputTreeActionPerformed(evt);
            }
        });

        pnlSelectOutputNetwork = new JPanel();
        pnlSelectOutputNetwork.setLayout(new BoxLayout(pnlSelectOutputNetwork, BoxLayout.LINE_AXIS));
        pnlSelectOutputNetwork.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlSelectOutputNetwork.add(Box.createHorizontalGlue());
        pnlSelectOutputNetwork.add(lblOutputNetwork);
        pnlSelectOutputNetwork.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOutputNetwork.add(txtOutputNetwork);
        pnlSelectOutputNetwork.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOutputNetwork.add(cmdOutputNetwork);

        pnlSelectOutputTree = new JPanel();
        pnlSelectOutputTree.setLayout(new BoxLayout(pnlSelectOutputTree, BoxLayout.LINE_AXIS));
        pnlSelectOutputTree.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlSelectOutputTree.add(Box.createHorizontalGlue());
        pnlSelectOutputTree.add(lblOutputTree);
        pnlSelectOutputTree.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOutputTree.add(txtOutputTree);
        pnlSelectOutputTree.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOutputTree.add(cmdOutputTree);

        pnlOutput = new JPanel();
        pnlOutput.setLayout(new BoxLayout(pnlOutput, BoxLayout.PAGE_AXIS));
        pnlOutput.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Output:"));
        pnlOutput.add(Box.createVerticalGlue());
        pnlOutput.add(pnlSelectOutputNetwork);
        pnlOutput.add(pnlSelectOutputTree);

        pack();
    }

    /**
     * Program status setup
     */
    private void initStatusComponents() {

        progStatus = new JProgressBar();

        lblStatus = new JLabel();
        lblStatus.setText("Status:");
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlStatus = new JPanel(new BorderLayout(0, 5));
        pnlStatus.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        pnlStatus.add(lblStatus, BorderLayout.LINE_START);
        pnlStatus.add(progStatus, BorderLayout.SOUTH);
    }

    /**
     * Execution controls
     */
    private void initControlComponents() {


        // ***** Run control and feedback *****

        cmdRun = new JButton();
        cmdRun.setText("Run");
        cmdRun.setToolTipText("Run Algorithm");
        cmdRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRunActionPerformed(evt);
            }
        });

        cmdCancel = new JButton();
        cmdCancel.setText("Cancel run");

        cmdViewOutput = new JButton();
        cmdViewOutput.setText("View Network");
        cmdViewOutput.setToolTipText("Visualise the produced network in Spectre");
        cmdViewOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdViewActionPerformed(evt);
            }
        });
        cmdViewOutput.setEnabled(false);

        pnlControlButtons = new JPanel();
        pnlControlButtons.setLayout(new BoxLayout(pnlControlButtons, BoxLayout.LINE_AXIS));
        pnlControlButtons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        pnlControlButtons.add(Box.createHorizontalGlue());
        pnlControlButtons.add(cmdViewOutput);
        pnlControlButtons.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlControlButtons.add(cmdRun);
        pnlControlButtons.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlControlButtons.add(cmdCancel);

        pack();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        this.initInputComponents();
        this.initAlgorithmComponents();
        this.initWeightingComponents();
        this.initOutputComponents();
        this.initStatusComponents();
        this.initControlComponents();

        pnlOptions = new JPanel();
        pnlOptions.setLayout(new BoxLayout(pnlOptions, BoxLayout.PAGE_AXIS));
        pnlOptions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlOptions.add(pnlInput);
        pnlOptions.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlOptions.add(pnlAlgorithm);
        pnlOptions.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlOptions.add(pnlWeightings);
        pnlOptions.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlOptions.add(pnlOutput);

        // Set algorithm to neighbornet
        cboCoAlgorithm.setSelectedItem(CircularOrderingAlgorithms.NEIGHBORNET);
        this.enableWeightingsPanel(false);


        // ***** Layout *****

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        getContentPane().add(Box.createVerticalGlue());
        getContentPane().add(pnlOptions);
        getContentPane().add(pnlStatus);
        getContentPane().add(pnlControlButtons, BorderLayout.PAGE_END);

        pack();
    }


    private void cmdSelectAlgorithmActionPerformed(ActionEvent evt) {

        if (evt.getSource() == cboCoAlgorithm) {

            CircularOrderingAlgorithms alg = (CircularOrderingAlgorithms) cboCoAlgorithm.getSelectedItem();

            if (alg == CircularOrderingAlgorithms.NEIGHBORNET) {
                this.enableWeightingsPanel(false);
                this.enableTreeOutput(false);
            }
            else if (alg == CircularOrderingAlgorithms.NETMAKE) {
                this.enableWeightingsPanel(true);
                this.enableTreeOutput(true);
            }
        }
    }

    private void enableTreeOutput(boolean b) {
        this.lblOutputTree.setEnabled(b);
        this.txtOutputTree.setEnabled(b);
        this.cmdOutputTree.setEnabled(b);
    }

    private void cmdViewActionPerformed(ActionEvent evt) {
        this.firePropertyChange("done", null, this.lastOutput);
    }

    /**
     * Choose file for output
     *
     * @param evt
     */
    private void cmdOutputNetworkActionPerformed(java.awt.event.ActionEvent evt) {

        final JFileChooser fc = cwd == null ? new JFileChooser() : new JFileChooser(cwd);
        if (evt.getSource() == cmdOutputNetwork) {
            int returnVal = fc.showSaveDialog(NetMakeGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();
                String z = file.getAbsolutePath();
                txtOutputNetwork.setText(z);
                this.cwd = file.getParentFile();
            } else {
                log.debug("Open command cancelled by user.");
            }
        }
    }

    /**
     * Choose file for output
     *
     * @param evt
     */
    private void cmdOutputTreeActionPerformed(java.awt.event.ActionEvent evt) {

        final JFileChooser fc = cwd == null ? new JFileChooser() : new JFileChooser(cwd);
        if (evt.getSource() == cmdOutputTree) {
            int returnVal = fc.showSaveDialog(NetMakeGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();
                String z = file.getAbsolutePath();
                txtOutputTree.setText(z);
                this.cwd = file.getParentFile();
            } else {
                log.debug("Open command cancelled by user.");
            }
        }
    }

    /**
     * Choose a file for input
     *
     * @param evt
     */
    private void cmdInputActionPerformed(java.awt.event.ActionEvent evt) {

        final JFileChooser fc = cwd == null ? new JFileChooser() : new JFileChooser(cwd);
        fc.addChoosableFileFilter(new NexusFileFilter());
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Phylip", "phylip", "phy"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Emboss", "emb", "emboss", "ems"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("FastA", "fa", "faa", "fasta"));

        if (evt.getSource() == cmdInput) {
            int returnVal = fc.showOpenDialog(NetMakeGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String z = "";
                z = file.getAbsolutePath();
                txtInput.setText(z);
                this.cwd = file.getParentFile();
            } else {
                log.debug("Open command cancelled by user.");
            }
        }
    }

    /**
     * Start
     *
     * @param evt
     */
    private void cmdRunActionPerformed(java.awt.event.ActionEvent evt) {

        NetMakeOptions options = buildNetMakeOptions();

        if (options != null)
            if (options.getOutputNetwork() == null || options.getOutputNetwork().getName().isEmpty()) {
                showErrorDialog("Can't run without circular ordering file specified.");
                return;
            }

            this.lastOutput = options.getOutputNetwork();
            this.netMakeRunner.runNetMake(options, new StatusTrackerWithView(this.progStatus, this.lblStatus, this.cmdViewOutput));
    }


    private void enableWeightingsPanel(boolean enabled) {

        this.pnlWeightings.setEnabled(enabled);
        this.lblWeighting1.setEnabled(enabled);
        this.cboWeighting1.setEnabled(enabled);
        this.lblWeighting2.setEnabled(enabled);
        this.cboWeighting2.setEnabled(enabled);
        this.lblWeight.setEnabled(enabled);
        this.txtWeight.setEnabled(enabled);
    }

    private void enableAlgPanel(boolean enabled) {

        this.pnlAlgorithm.setEnabled(enabled);
        this.lblCoAlgorithm.setEnabled(enabled);
        this.cboCoAlgorithm.setEnabled(enabled);
    }

    /**
     * Setup configuration using values specified in the GUI
     *
     * @return configuration
     */
    private NetMakeOptions buildNetMakeOptions() {

        NetMakeOptions options = new NetMakeOptions();

        options.setInput(this.txtOutputTree.getText().trim() == null ? null : new File(this.txtInput.getText().replaceAll("(^\")|(\"$)", "")));
        options.setOutputNetwork(this.txtOutputTree.getText().trim() == null ? null : new File(this.txtOutputNetwork.getText().replaceAll("(^\")|(\"$)", "")));
        options.setOutputTree(this.txtOutputTree.getText().trim() == null ? null : new File(this.txtOutputTree.getText().replaceAll("(^\")|(\"$)", "")));

        try {
            options.setTreeParam(Double.parseDouble(this.txtWeight.getText()));
        } catch (Exception e) {
            showErrorDialog("Tree param must be a real number.");
            return null;
        }

        options.setCoAlg(this.cboCoAlgorithm.getSelectedItem().toString());
        options.setDc(this.cboDmAlgorithm.getSelectedItem().toString());

        // May need some more validation here.
        options.setWeighting1(this.cboWeighting1.getSelectedItem().toString());
        options.setWeighting2(this.cboWeighting2.getSelectedItem().toString());

        // Always draw
        options.setDraw(true);

        return options;
    }


    @Override
    public void update() {
        // Nothing to do... I think
    }

    @Override
    public void setRunningStatus(boolean running) {
        if (this.go_control != null) {
            this.go_control.setRunning(running);
        }
    }

    @Override
    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Net Make Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void neighbornetConfig() {
        this.enableWeightingsPanel(false);
        this.enableTreeOutput(false);
        this.cboCoAlgorithm.setSelectedItem(CircularOrderingAlgorithms.NEIGHBORNET);
        this.enableAlgPanel(false);
    }

    public void netmakeConfig() {
        this.enableWeightingsPanel(true);
        this.enableTreeOutput(true);
        this.cboCoAlgorithm.setSelectedItem(CircularOrderingAlgorithms.NETMAKE);
        this.enableAlgPanel(false);
    }

    /**
     * Main entry point when running in GUI mode.
     *
     * @param args Program arguments... we expect nothing to be here.
     */
    public static void main(String args[]) {

        // Configure logging
        LogConfig.defaultConfig();

        setLookAndFeel(NIMBUS);

        try {
            log.info("Started netmake GUI");

            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    new NetMakeGUI().setVisible(true);
                }
            });
            return;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}

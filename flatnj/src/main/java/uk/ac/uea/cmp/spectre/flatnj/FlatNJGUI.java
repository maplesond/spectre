/*
 * Suite of PhylogEnetiC Tools for Reticulate Evolution (SPECTRE)
 * Copyright (C) 2014  UEA School of Computing Sciences
 *
 * This program is free software: you can redistribute it and/or modify it under the term of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package uk.ac.uea.cmp.spectre.flatnj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.earlham.metaopt.Objective;
import uk.ac.earlham.metaopt.OptimiserException;
import uk.ac.earlham.metaopt.OptimiserFactory;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusFileFilter;
import uk.ac.uea.cmp.spectre.core.ui.gui.JobController;
import uk.ac.uea.cmp.spectre.core.ui.gui.JobControllerWithView;
import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTrackerWithView;
import uk.ac.uea.cmp.spectre.core.ui.gui.ToolHost;
import uk.ac.uea.cmp.spectre.core.util.LogConfig;
import uk.ac.uea.cmp.spectre.core.util.ProjectProperties;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Vector;

import static uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel.NIMBUS;
import static uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel.setLookAndFeel;

public class FlatNJGUI extends JFrame implements ToolHost {

    private static Logger log = LoggerFactory.getLogger(FlatNJGUI.class);

    private static final String TITLE = "FlatNJ";

    // ***** GUI components *****

    private JPanel pnlOptions;

    private JPanel pnlInput;
    private JPanel pnlSelectInput;
    private JLabel lblInput;
    private JTextField txtInput;
    private JButton cmdInput;

    private JPanel pnlConfig;
    private JLabel lblSelectSolver;
    private JComboBox<String> cboSelectSolver;
    private JTextField txtThreshold;

    private JPanel pnlOutput;
    private JPanel pnlSelectOutput;
    private JPanel pnlOutputOptions;
    private JLabel lblSave;
    private JTextField txtSave;
    private JButton cmdSave;
    private JCheckBox chkSaveStages;
    private JLabel lblThreshold;

    private JPanel pnlStatus;
    private JPanel pnlControlButtons;
    private JButton cmdCancel;
    private JButton cmdRun;
    private JButton cmdViewOutput;
    private JLabel lblStatus;
    private JProgressBar progStatus;


    private JDialog dialog;
    private JFrame gui;
    private JobController go_control;
    private FlatNJRunner runner;
    private File lastOutput;
    private File cwd;

    public FlatNJGUI() {

        this.dialog = new JDialog(this, TITLE);
        this.gui = new JFrame(TITLE);
        this.lastOutput = null;
        this.cwd = null;

        this.setPreferredSize(new Dimension(500, 420));

        initComponents();
        setTitle(TITLE);

        setIconImage((new ImageIcon(ProjectProperties.getResourceFile("logo.png")).getImage()));

        this.runner = new FlatNJRunner(this);

        this.go_control = new JobControllerWithView(this.cmdRun, this.cmdCancel, this.cmdViewOutput);
        setRunningStatus(false);


        // Overridden this... this should work without gurobi :s
        // Only enable scaling if Gurobi is available
        //this.chkScaleInput.setEnabled(Optimiser.isOperational("GUROBI"));
    }

    /**
     * Input options
     */
    private void initInputComponents() {

        lblInput = new JLabel();
        txtInput = new JTextField();
        cmdInput = new JButton();

        final String inputTip = "REQUIRED: The input files containing trees, or paths to many trees if input format is " +
                "script.  Supports NEWICK, NEXUS, QWEIGHT and script files.  Multiple files are separated with semi-colons. " +
                "Paths containing spaces should be wrapped in quotes.";

        cmdInput.setText("...");
        cmdInput.setToolTipText(inputTip);
        cmdInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cmdInputActionPerformed(evt);
            }
        });

        txtInput.setToolTipText(inputTip);
        //txtInput.setMinimumSize(new Dimension(400, txtInput.getHeight()));

        lblInput.setText("Input files:");
        lblInput.setToolTipText(inputTip);

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

    /**
     * Optimiser options
     */
    private void initConfigComponents() {

        cboSelectSolver = new JComboBox<>();
        lblSelectSolver = new JLabel();

        java.util.List<String> primarySolvers = OptimiserFactory.getInstance().listOperationalOptimisers(Objective.ObjectiveType.QUADRATIC);

        cboSelectSolver.setModel(new DefaultComboBoxModel<>(new Vector<>(primarySolvers)));
        cboSelectSolver.setToolTipText(FlatNJOptions.DESC_OPTIMISER);

        lblSelectSolver.setText("Select optimiser:");
        lblSelectSolver.setToolTipText(FlatNJOptions.DESC_OPTIMISER);

        txtThreshold = new JTextField();
        txtThreshold.setToolTipText(FlatNJOptions.DESC_THRESHOLD);
        txtThreshold.setText(Double.toString(FlatNJOptions.DEFAULT_THRESHOLD));

        lblThreshold = new JLabel();
        lblThreshold.setText("Filter threshold:");
        lblThreshold.setToolTipText(FlatNJOptions.DESC_THRESHOLD);

        pnlConfig = new JPanel();


        GroupLayout layout = new GroupLayout(pnlConfig);

        pnlConfig.setLayout(layout);
        pnlConfig.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Configuration:"));

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(lblSelectSolver)
                                .addComponent(lblThreshold)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(cboSelectSolver)
                                .addComponent(txtThreshold)
                        )
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblSelectSolver)
                                .addComponent(cboSelectSolver)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblThreshold)
                                .addComponent(txtThreshold)
                        )
        );

        pack();
    }

    /**
     * Output options
     */
    private void initOutputComponents() {


        pnlOutput = new JPanel(new BorderLayout());

        lblSave = new JLabel();
        txtSave = new JTextField();
        cmdSave = new JButton();

        chkSaveStages = new JCheckBox();

        txtSave.setToolTipText(FlatNJOptions.DESC_OUTPUT);

        lblSave.setText("Save to file:");
        lblSave.setToolTipText(FlatNJOptions.DESC_OUTPUT);

        cmdSave.setText("...");
        cmdSave.setToolTipText(FlatNJOptions.DESC_OUTPUT);
        cmdSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cmdSaveActionPerformed(evt);
            }
        });

        chkSaveStages.setText("Save stages");
        chkSaveStages.setToolTipText(FlatNJOptions.DESC_STAGES);

        pnlSelectOutput = new JPanel();
        pnlSelectOutput.setLayout(new BoxLayout(pnlSelectOutput, BoxLayout.LINE_AXIS));
        pnlSelectOutput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlSelectOutput.add(Box.createHorizontalGlue());
        pnlSelectOutput.add(lblSave);
        pnlSelectOutput.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOutput.add(txtSave);
        pnlSelectOutput.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOutput.add(cmdSave);

        pack();

        pnlOutputOptions = new JPanel();
        pnlOutputOptions.setLayout(new BoxLayout(pnlOutputOptions, BoxLayout.LINE_AXIS));
        pnlOutputOptions.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        pnlOutputOptions.add(Box.createHorizontalGlue());
        pnlOutputOptions.add(chkSaveStages);

        pack();

        pnlOutput = new JPanel();
        pnlOutput.setLayout(new BoxLayout(pnlOutput, BoxLayout.PAGE_AXIS));
        pnlOutput.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Output:"));
        pnlOutput.add(Box.createVerticalGlue());
        pnlOutput.add(pnlSelectOutput);
        pnlOutput.add(pnlOutputOptions);

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
        cmdRun.setText("Run FlatNJ");
        cmdRun.setToolTipText("Run the FlatNJ Algorithm");
        cmdRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cmdRunActionPerformed(evt);
            }
        });
        cmdRun.setEnabled(true);

        cmdCancel = new JButton();
        cmdCancel.setText("Cancel run");
        cmdCancel.setToolTipText("Stop FlatNJ at next opportunity");

        cmdViewOutput = new JButton();
        cmdViewOutput.setText("View Network");
        cmdViewOutput.setToolTipText("Visualise the produced network in Spectre");
        cmdViewOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
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
        this.initConfigComponents();
        this.initOutputComponents();
        this.initStatusComponents();
        this.initControlComponents();

        pnlOptions = new JPanel();
        pnlOptions.setLayout(new BoxLayout(pnlOptions, BoxLayout.PAGE_AXIS));
        pnlOptions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlOptions.add(pnlInput);
        pnlOptions.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlOptions.add(pnlConfig);
        pnlOptions.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlOptions.add(pnlOutput);


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

    /**
     * Choose file for output
     *
     * @param evt
     */
    private void cmdSaveActionPerformed(ActionEvent evt) {

        final JFileChooser fc = cwd == null ? new JFileChooser() : new JFileChooser(cwd);
        if (evt.getSource() == cmdSave) {
            fc.addChoosableFileFilter(new NexusFileFilter());
            fc.setSelectedFile(new File("outfile.nex"));
            int returnVal = fc.showSaveDialog(FlatNJGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();
                String z = file.getAbsolutePath();
                txtSave.setText(z);
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
    private void cmdInputActionPerformed(ActionEvent evt) {

        if (evt.getSource() == cmdInput) {
            final JFileChooser fc = cwd != null ? new JFileChooser(cwd) : new JFileChooser();
            fc.addChoosableFileFilter(new NexusFileFilter());
            fc.addChoosableFileFilter(new FileNameExtensionFilter("FastA", "fa", "faa", "fna", "fasta", "fas"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Nexus", "nexus", "nex", "nxs"));
            fc.setMultiSelectionEnabled(true);
            int returnVal = fc.showOpenDialog(FlatNJGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] files = fc.getSelectedFiles();
                if (files.length > 0) {
                    txtInput.setText(files[0].getAbsolutePath().toString());
                    cwd = files[0].getParentFile();
                }
            } else {
                log.debug("Open command cancelled by user.");
            }
        }
    }

    /**
     * Start Super Q
     *
     * @param evt
     */
    private void cmdRunActionPerformed(ActionEvent evt) {

        FlatNJOptions options = buildFlatNJOptions();

        if (options != null) {

            if (options.getOutputFile() == null || options.getOutputFile().getName().isEmpty()) {
                showErrorDialog("Can't run without output file specified.");
                return;
            }

            this.lastOutput = options.getOutputFile();
            this.runner.runFlatNJ(options, new StatusTrackerWithView(this.progStatus, this.lblStatus, this.cmdViewOutput));
        }

    }

    /**
     * Start netviewer on superq output
     *
     * @param evt
     */
    private void cmdViewActionPerformed(ActionEvent evt) {
        this.firePropertyChange("done", null, this.lastOutput);
        //Spectre.main(new String[]{this.lastOutput.getAbsolutePath(), "--dispose_on_close"});
    }

    private FlatNJOptions buildFlatNJOptions() {

        FlatNJOptions options = new FlatNJOptions();

        try {

            options.setInFile(new File(this.txtInput.getText().replaceAll("(^\")|(\"$)", "")));
            options.setOutputFile(new File(this.txtSave.getText().replaceAll("(^\")|(\"$)", "")));


            double filter = Double.MAX_VALUE;
            try {
                filter = Double.valueOf(txtThreshold.getText());
            } catch (NumberFormatException e) {
                showErrorDialog("Filter threshold must be a non-negative real number");
                return null;
            }
            options.setThreshold(filter);

            try {

                options.setOptimiser(
                        OptimiserFactory.getInstance().createOptimiserInstance(
                                (String) this.cboSelectSolver.getSelectedItem(), Objective.ObjectiveType.QUADRATIC));

            } catch (OptimiserException oe) {
                showErrorDialog("Could not create requested optimiser: " + (String) this.cboSelectSolver.getSelectedItem());
                return null;
            }
        } catch (Exception e) {
            showErrorDialog("Unexpected error occurred configuring SuperQ: " + e.getMessage());
            return null;
        }

        return options;
    }

    @Override
    public void update() {
        // Nothing to do... I think
    }

    @Override
    public void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "FlatNJ Error",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void setRunningStatus(boolean running) {
        if (this.go_control != null) {
            this.go_control.setRunning(running);
        }
    }


    /**
     * Main entry point for SuperQ when running in GUI mode.
     *
     * @param args Program arguments... we expect nothing to be here.
     */
    public static void main(String args[]) {

        LogConfig.defaultConfig();

        setLookAndFeel(NIMBUS);

        try {
            log.info("Running in GUI mode");

            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    new FlatNJGUI().setVisible(true);
                }
            });
            return;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}

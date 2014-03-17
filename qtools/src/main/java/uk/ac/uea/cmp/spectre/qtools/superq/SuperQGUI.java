/*
 * Phylogenetics Tool suite
 * Copyright (C) 2013  UEA CMP Phylogenetics Group
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
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
package uk.ac.uea.cmp.spectre.qtools.superq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.tgac.metaopt.Objective;
import uk.ac.tgac.metaopt.Optimiser;
import uk.ac.tgac.metaopt.OptimiserException;
import uk.ac.tgac.metaopt.OptimiserFactory;
import uk.ac.uea.cmp.spectre.core.io.nexus.NexusFileFilter;
import uk.ac.uea.cmp.spectre.core.ui.gui.JobController;
import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.spectre.core.ui.gui.ToolHost;
import uk.ac.uea.cmp.spectre.qtools.superq.problems.SecondaryProblemFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel.NIMBUS;
import static uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel.setLookAndFeel;

public class SuperQGUI extends JFrame implements ToolHost {

    private static Logger log = LoggerFactory.getLogger(SuperQGUI.class);

    // ***** GUI components *****

    private JPanel pnlOptions;

    private JPanel pnlInput;
    private JPanel pnlSelectInput;
    private JLabel lblInput;
    private JTextField txtInput;
    private JButton cmdInput;

    private JPanel pnlOptimisers;
    private JLabel lblSelectScalingSolver;
    private JComboBox<String> cboSelectScalingSolver;
    private JLabel lblSelectPrimarySolver;
    private JComboBox<String> cboSelectPrimarySolver;
    private JLabel lblSelectSecondarySolver;
    private JComboBox<String> cboSelectSecondarySolver;
    private JLabel lblSelectSecondaryObjective;
    private JComboBox<String> cboSelectSecondaryObjective;

    private JPanel pnlOutput;
    private JPanel pnlSelectOutput;
    private JPanel pnlOutputOptions;
    private JLabel lblSave;
    private JTextField txtSave;
    private JButton cmdSave;
    private JTextField txtFilter;
    private JCheckBox chkFilter;

    private JPanel pnlStatus;
    private JPanel pnlControlButtons;
    private JButton cmdCancel;
    private JButton cmdRun;
    private JLabel lblStatus;
    private JProgressBar progStatus;

    

    private JDialog dialog = new JDialog(this, "SUPERQ");
    private JFrame gui = new JFrame("SUPERQ");
    private JobController go_control;
    private SuperQRunner superqRunner;

    public SuperQGUI() {

        this.setPreferredSize(new Dimension(600, 500));

        initComponents();
        setTitle("SUPERQ");

        cmdRun.setEnabled(true);
        txtFilter.setEnabled(false);

        this.superqRunner = new SuperQRunner(this);

        this.go_control = new JobController(this.cmdRun, this.cmdCancel);
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
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
    private void initOptimiserComponents() {

        cboSelectScalingSolver = new JComboBox<>();
        lblSelectScalingSolver = new JLabel();

        cboSelectPrimarySolver = new JComboBox<>();
        lblSelectPrimarySolver = new JLabel();

        cboSelectSecondarySolver = new JComboBox<>();
        lblSelectSecondarySolver = new JLabel();

        cboSelectSecondaryObjective = new JComboBox<>();
        lblSelectSecondaryObjective = new JLabel();

        java.util.List<String> scalingSolvers = OptimiserFactory.getInstance().listOperationalOptimisers(Objective.ObjectiveType.QUADRATIC);
        scalingSolvers.add(0, "Off");

        cboSelectScalingSolver.setModel(new DefaultComboBoxModel(scalingSolvers.toArray()));
        cboSelectScalingSolver.setToolTipText(SuperQOptions.DESC_SCALING_SOLVER);

        lblSelectScalingSolver.setText("Select scaling optimiser:");
        lblSelectScalingSolver.setToolTipText(SuperQOptions.DESC_SCALING_SOLVER);


        java.util.List<String> primarySolvers = OptimiserFactory.getInstance().listOperationalOptimisers(Objective.ObjectiveType.QUADRATIC);
        primarySolvers.add(0, "Internal");

        cboSelectPrimarySolver.setModel(new DefaultComboBoxModel(primarySolvers.toArray()));
        cboSelectPrimarySolver.setToolTipText(SuperQOptions.DESC_PRIMARY_SOLVER);

        lblSelectPrimarySolver.setText("Select primary optimiser:");
        lblSelectPrimarySolver.setToolTipText(SuperQOptions.DESC_PRIMARY_SOLVER);

        java.util.List<String> secondarySolvers = OptimiserFactory.getInstance().listOperationalOptimisers();
        secondarySolvers.add(0, "Off");

        cboSelectSecondarySolver.setModel(new DefaultComboBoxModel(secondarySolvers.toArray()));
        cboSelectSecondarySolver.setToolTipText(SuperQOptions.DESC_SECONDARY_SOLVER);
        cboSelectSecondarySolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSelectSecondaryObjectiveActionPerformed(evt);
            }
        });

        lblSelectSecondarySolver.setText("Select secondary optimiser:");
        lblSelectSecondarySolver.setToolTipText(SuperQOptions.DESC_SECONDARY_SOLVER);


        cboSelectSecondaryObjective.setToolTipText(SuperQOptions.DESC_SECONDARY_OBJECTIVE);

        lblSelectSecondaryObjective.setText("Select secondary objective:");
        lblSelectSecondaryObjective.setToolTipText(SuperQOptions.DESC_SECONDARY_OBJECTIVE);

        this.enableSecondaryObjective(false);

        pnlOptimisers = new JPanel();


        GroupLayout layout = new GroupLayout(pnlOptimisers);

        pnlOptimisers.setLayout(layout);
        pnlOptimisers.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Optimiser Setup:"));

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(lblSelectScalingSolver)
                                .addComponent(lblSelectPrimarySolver)
                                .addComponent(lblSelectSecondarySolver)
                                .addComponent(lblSelectSecondaryObjective)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(cboSelectScalingSolver)
                                .addComponent(cboSelectPrimarySolver)
                                .addComponent(cboSelectSecondarySolver)
                                .addComponent(cboSelectSecondaryObjective)
                        )
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblSelectScalingSolver)
                                .addComponent(cboSelectScalingSolver)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblSelectPrimarySolver)
                                .addComponent(cboSelectPrimarySolver)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblSelectSecondarySolver)
                                .addComponent(cboSelectSecondarySolver)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblSelectSecondaryObjective)
                                .addComponent(cboSelectSecondaryObjective)
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
        txtFilter = new JTextField();
        chkFilter = new JCheckBox();


        txtSave.setToolTipText(SuperQOptions.DESC_OUTPUT);

        lblSave.setText("Save to file:");
        lblSave.setToolTipText(SuperQOptions.DESC_OUTPUT);

        cmdSave.setText("...");
        cmdSave.setToolTipText(SuperQOptions.DESC_OUTPUT);
        cmdSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveActionPerformed(evt);
            }
        });

        chkFilter.setText("Filter:");
        chkFilter.setToolTipText(SuperQOptions.DESC_FILTER);
        chkFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFilterActionPerformed(evt);
            }
        });

        txtFilter.setToolTipText(SuperQOptions.DESC_FILTER);

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
        pnlOutputOptions.add(chkFilter);
        pnlOutputOptions.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlOutputOptions.add(txtFilter);

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
        cmdRun.setText("Run SUPERQ");
        cmdRun.setToolTipText("Run the SUPERQ Algorithm");
        cmdRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRunActionPerformed(evt);
            }
        });

        cmdCancel = new JButton();
        cmdCancel.setText("Cancel");

        pnlControlButtons = new JPanel();
        pnlControlButtons.setLayout(new BoxLayout(pnlControlButtons, BoxLayout.LINE_AXIS));
        pnlControlButtons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        pnlControlButtons.add(Box.createHorizontalGlue());
        pnlControlButtons.add(cmdRun);
        pnlControlButtons.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlControlButtons.add(cmdCancel);

        pack();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        this.initInputComponents();
        this.initOptimiserComponents();
        this.initOutputComponents();
        this.initStatusComponents();
        this.initControlComponents();

        pnlOptions = new JPanel();
        pnlOptions.setLayout(new BoxLayout(pnlOptions, BoxLayout.PAGE_AXIS));
        pnlOptions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlOptions.add(pnlInput);
        pnlOptions.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlOptions.add(pnlOptimisers);
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
     * @param evt
     */
    private void cmdSaveActionPerformed(java.awt.event.ActionEvent evt) {

        final JFileChooser fc = new JFileChooser();
        if (evt.getSource() == cmdSave) {
            fc.addChoosableFileFilter(new NexusFileFilter());
            fc.setSelectedFile(new File("outfile.nex"));
            int returnVal = fc.showSaveDialog(SuperQGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();
                String z = file.getAbsolutePath();
                txtSave.setText(z);
            } else {
                log.debug("Open command cancelled by user.");
            }
        }
    }

    private void chkFilterActionPerformed(java.awt.event.ActionEvent evt) {
        txtFilter.setEnabled(chkFilter.isSelected());
    }

    private void cboSelectSecondaryObjectiveActionPerformed(java.awt.event.ActionEvent evt) {
        this.enableSecondaryObjective(!((String) this.cboSelectSecondarySolver.getSelectedItem()).equalsIgnoreCase("off"));

        String secondarySolver = (String) this.cboSelectSecondarySolver.getSelectedItem();

        if (!secondarySolver.equalsIgnoreCase("off")) {

            Optimiser opt = null;
            try {
                opt = OptimiserFactory.getInstance().createOptimiserInstance(secondarySolver, null);
            }
            catch (OptimiserException oe) {
                showErrorDialog("Error trying to create an instance of the selected optimiser");
                return;
            }

            cboSelectSecondaryObjective.setModel(
                    new DefaultComboBoxModel(
                            SecondaryProblemFactory.getInstance().listObjectivesByIdentifier(
                                    opt.acceptsObjectiveType(Objective.ObjectiveType.QUADRATIC) ?
                                            Objective.ObjectiveType.QUADRATIC :
                                            Objective.ObjectiveType.LINEAR
                            ).toArray()));
        }
        else {
            cboSelectSecondaryObjective.setModel(new DefaultComboBoxModel());
        }
    }

    /**
     * Choose a file for input
     * @param evt
     */
    private void cmdInputActionPerformed(java.awt.event.ActionEvent evt) {

        if (evt.getSource() == cmdInput) {
            final JFileChooser fc = new JFileChooser();
            fc.addChoosableFileFilter(new NexusFileFilter());
            fc.addChoosableFileFilter(new FileNameExtensionFilter("QWeight", "qw"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Phylip/Newick", "phylip", "newick", "tree", "tre"));
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Script", "script", "scr"));
            fc.setMultiSelectionEnabled(true);
            int returnVal = fc.showOpenDialog(SuperQGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] files = fc.getSelectedFiles();

                StringBuilder sb = new StringBuilder();
                for(File f : files) {
                    sb.append(f.getAbsolutePath());
                    sb.append("; ");
                }
                txtInput.setText(sb.toString());
            } else {
                log.debug("Open command cancelled by user.");
            }
        }
    }

    /**
     * Start Super Q
     * @param evt
     */
    private void cmdRunActionPerformed(java.awt.event.ActionEvent evt) {

        SuperQOptions options = buildSuperQOptions();

        if (options != null)
            this.superqRunner.runSuperQ(options, new StatusTracker(this.progStatus, this.lblStatus));

    }

    /**
     * Setup SuperQ configuration using values specified in the GUI
     * @return SuperQ configuration
     */
    private SuperQOptions buildSuperQOptions() {

        SuperQOptions options;

        try {
            options = new SuperQOptions();
        } catch (OptimiserException oe) {
            showErrorDialog("Error occurred configuring the optimiser.  Check you have selected an operational optimiser and set an appropriate objective.");
            return null;
        }

        try {

            String[] files = this.txtInput.getText().split(";");

            java.util.List<File> inputFiles = new ArrayList<>();
            for(int i = 0; i < files.length; i++) {
                String path = files[i].trim();

                if (!path.isEmpty()) {
                    path = path.replaceAll("(^\")|(\"$)", "");
                    inputFiles.add(new File(path));
                }
            }

            options.setInputFiles(inputFiles.toArray(new File[inputFiles.size()]));

            File output_file = new File(this.txtSave.getText().replaceAll("(^\")|(\"$)", ""));
            options.setOutputFile(output_file);


            if (chkFilter.isSelected()) {
                double filter = Double.MAX_VALUE;
                try {
                    filter = Double.valueOf(txtFilter.getText());
                } catch (NumberFormatException e) {
                    showErrorDialog("Filter threshold must be a non-negative real number");
                    return null;
                }
                options.setFilter(filter);
            }

            if (this.cboSelectSecondaryObjective.isEnabled()) {
                options.setSecondaryProblem(
                        SecondaryProblemFactory.getInstance().createSecondaryObjective(
                                (String)this.cboSelectSecondaryObjective.getSelectedItem()));
            }

            if (((String) this.cboSelectScalingSolver.getSelectedItem()).equalsIgnoreCase("off")) {
                options.setScalingSolver(null);
            }
            if (((String) this.cboSelectPrimarySolver.getSelectedItem()).equalsIgnoreCase("internal")) {
                options.setPrimarySolver(null);
            }
            if (((String) this.cboSelectSecondarySolver.getSelectedItem()).equalsIgnoreCase("off")) {
                options.setSecondarySolver(null);
            }
            try {

                options.setScalingSolver(
                        OptimiserFactory.getInstance().createOptimiserInstance(
                                (String) this.cboSelectScalingSolver.getSelectedItem(), Objective.ObjectiveType.QUADRATIC));

                options.setPrimarySolver(
                        OptimiserFactory.getInstance().createOptimiserInstance(
                                (String) this.cboSelectPrimarySolver.getSelectedItem(), Objective.ObjectiveType.QUADRATIC));

                if (!((String) this.cboSelectSecondarySolver.getSelectedItem()).equalsIgnoreCase("off")) {
                    options.setSecondarySolver(
                            OptimiserFactory.getInstance().createOptimiserInstance(
                                    (String) this.cboSelectSecondarySolver.getSelectedItem(), options.getSecondaryProblem().getObjectiveType()));
                }

            } catch (OptimiserException oe) {
                showErrorDialog("Could not create requested optimiser: " + (String) this.cboSelectSecondarySolver.getSelectedItem());
                return null;
            }
        }
        catch(Exception e) {
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
                "SuperQ Error",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void setRunningStatus(boolean running) {
        if (this.go_control != null) {
            this.go_control.setRunning(running);
        }
    }


    private void enableSecondaryObjective(boolean enabled) {

        lblSelectSecondaryObjective.setEnabled(enabled);
        cboSelectSecondaryObjective.setEnabled(enabled);
    }



    /**
     * Main entry point for SuperQ when running in GUI mode.
     * @param args Program arguments... we expect nothing to be here.
     */
    public static void main(String args[]) {

        SuperQ.configureLogging();

        setLookAndFeel(NIMBUS);

        try {
            log.info("Running in GUI mode");

            java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    new SuperQGUI().setVisible(true);
                }
            });
            return;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}

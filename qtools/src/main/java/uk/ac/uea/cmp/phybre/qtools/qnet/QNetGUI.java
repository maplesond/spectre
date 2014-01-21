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
package uk.ac.uea.cmp.phybre.qtools.qnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.tgac.metaopt.Objective;
import uk.ac.tgac.metaopt.OptimiserException;
import uk.ac.tgac.metaopt.OptimiserFactory;
import uk.ac.uea.cmp.phybre.core.io.qweight.QWeightFileFilter;
import uk.ac.uea.cmp.phybre.core.ui.gui.JobController;
import uk.ac.uea.cmp.phybre.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.phybre.core.ui.gui.ToolHost;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class QNetGUI extends JFrame implements ToolHost {

    private static Logger log = LoggerFactory.getLogger(QNetGUI.class);

    private static final String TITLE = "QNet";

    // ***** GUI components *****

    private JPanel pnlOptions;

    private JPanel pnlInput;
    private JPanel pnlSelectInput;
    private JPanel pnlInputOptions;
    private JLabel lblInput;
    private JTextField txtInput;
    private JButton cmdInput;
    private JCheckBox chkLogNormalise;

    private JPanel pnlOptimisers;
    private JLabel lblSolver;
    private JComboBox<String> cboSolver;
    private JLabel lblTolerance;
    private JTextField txtTolerance;

    private JPanel pnlOutput;
    private JPanel pnlSelectOutput;
    private JLabel lblSave;
    private JTextField txtSave;
    private JButton cmdSave;


    private JPanel pnlStatus;
    private JPanel pnlControlButtons;
    private JButton cmdCancel;
    private JButton cmdRun;
    private JLabel lblStatus;
    private JProgressBar progStatus;



    private JDialog dialog = new JDialog(this, TITLE);
    private JFrame gui = new JFrame(TITLE);
    private JobController go_control;
    private QNetRunner qnetRunner;

    public QNetGUI() {
        initComponents();
        setTitle(TITLE);

        cmdRun.setEnabled(true);

        this.qnetRunner = new QNetRunner(this);

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
        chkLogNormalise = new JCheckBox();

        cmdInput.setText("...");
        cmdInput.setToolTipText(QNetOptions.DESC_INPUT);
        cmdInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdInputActionPerformed(evt);
            }
        });

        txtInput.setPreferredSize(new Dimension(200, 25));
        txtInput.setToolTipText(QNetOptions.DESC_INPUT);

        lblInput.setText("Input file:");
        lblInput.setToolTipText(QNetOptions.DESC_INPUT);

        chkLogNormalise.setText("Log normalise input");
        chkLogNormalise.setToolTipText(QNetOptions.DESC_LOG);

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

        pnlInputOptions = new JPanel();
        pnlInputOptions.setLayout(new BoxLayout(pnlInputOptions, BoxLayout.LINE_AXIS));
        pnlInputOptions.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        pnlInputOptions.add(Box.createHorizontalGlue());
        pnlInputOptions.add(chkLogNormalise);

        pack();

        pnlInput = new JPanel();
        pnlInput.setLayout(new BoxLayout(pnlInput, BoxLayout.PAGE_AXIS));
        pnlInput.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Input:"));
        pnlInput.add(Box.createVerticalGlue());
        pnlInput.add(pnlSelectInput);
        pnlInput.add(pnlInputOptions);

        pack();
    }

    /**
     * Optimiser options
     */
    private void initOptimiserComponents() {

        cboSolver = new JComboBox();
        lblSolver = new JLabel();

        txtTolerance = new JTextField();
        lblTolerance = new JLabel();

        java.util.List<String> optimisers = OptimiserFactory.getInstance().listOperationalOptimisers(Objective.ObjectiveType.QUADRATIC);
        optimisers.add(0, "Internal");

        cboSolver.setModel(new DefaultComboBoxModel(optimisers.toArray()));
        cboSolver.setToolTipText(QNetOptions.DESC_OPTIMISER);
        cboSolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSolverActionPerformed(evt);
            }
        });

        lblSolver.setText("Select optimiser:");
        lblSolver.setToolTipText(QNetOptions.DESC_OPTIMISER);


        lblTolerance.setText("Set the tolerance:");
        lblTolerance.setToolTipText(QNetOptions.DESC_TOLERANCE);

        txtTolerance.setText("");
        txtTolerance.setPreferredSize(new Dimension(200, 25));
        txtTolerance.setToolTipText(QNetOptions.DESC_TOLERANCE);

        pnlOptimisers = new JPanel();


        GroupLayout layout = new GroupLayout(pnlOptimisers);

        pnlOptimisers.setLayout(layout);
        pnlOptimisers.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Optimiser Setup:"));

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(lblSolver)
                                .addComponent(lblTolerance)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(cboSolver)
                                .addComponent(txtTolerance)
                        )
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblSolver)
                                .addComponent(cboSolver)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblTolerance)
                                .addComponent(txtTolerance)
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

        txtSave.setPreferredSize(new Dimension(200, 25));
        txtSave.setToolTipText(QNetOptions.DESC_OUTPUT);

        lblSave.setText("Save to file:");
        lblSave.setToolTipText(QNetOptions.DESC_OUTPUT);

        cmdSave.setText("...");
        cmdSave.setToolTipText(QNetOptions.DESC_OUTPUT);
        cmdSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveActionPerformed(evt);
            }
        });

        pnlSelectOutput = new JPanel();
        pnlSelectOutput.setLayout(new BoxLayout(pnlSelectOutput, BoxLayout.LINE_AXIS));
        pnlSelectOutput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlSelectOutput.add(Box.createHorizontalGlue());
        pnlSelectOutput.add(lblSave);
        pnlSelectOutput.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOutput.add(txtSave);
        pnlSelectOutput.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOutput.add(cmdSave);

        pnlOutput = new JPanel();
        pnlOutput.setLayout(new BoxLayout(pnlOutput, BoxLayout.PAGE_AXIS));
        pnlOutput.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Output:"));
        pnlOutput.add(Box.createVerticalGlue());
        pnlOutput.add(pnlSelectOutput);

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
        cmdRun.setText("Run QNet");
        cmdRun.setToolTipText("Run the QNet Algorithm");
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

        this.enableTolerance(true);

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
            fc.setSelectedFile(new File("outfile"));
            int returnVal = fc.showSaveDialog(QNetGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();
                String z = file.getAbsolutePath();
                txtSave.setText(z);
            } else {
                log.debug("Open command cancelled by user.");
            }
        }
    }

    /**
     * Choose a file for input
     * @param evt
     */
    private void cmdInputActionPerformed(java.awt.event.ActionEvent evt) {

        final JFileChooser fc = new JFileChooser();
        if (evt.getSource() == cmdInput) {
            fc.addChoosableFileFilter(new QWeightFileFilter());
            int returnVal = fc.showOpenDialog(QNetGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String z = "";
                z = file.getAbsolutePath();
                txtInput.setText(z);
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

        QNetOptions options = buildQNetOptions();

        if (options != null)
            this.qnetRunner.runSuperQ(options, new StatusTracker(this.progStatus, this.lblStatus));

    }

    private void cboSolverActionPerformed(ActionEvent evt) {

        enableTolerance((((String) this.cboSolver.getSelectedItem()).equalsIgnoreCase("internal")));
    }

    private void enableTolerance(boolean enabled) {

        lblTolerance.setEnabled(enabled);
        txtTolerance.setEnabled(enabled);
    }


    /**
     * Setup SuperQ configuration using values specified in the GUI
     * @return SuperQ configuration
     */
    private QNetOptions buildQNetOptions() {

        QNetOptions options= new QNetOptions();

        File input_file = new File(this.txtInput.getText().replaceAll("(^\")|(\"$)", ""));
        options.setInput(input_file);

        File output_file = new File(this.txtSave.getText().replaceAll("(^\")|(\"$)", ""));
        options.setOutput(output_file);

        options.setLogNormalise(this.chkLogNormalise.isSelected());

        if (((String) this.cboSolver.getSelectedItem()).equalsIgnoreCase("internal")) {
            options.setOptimiser(null);

            double tolerance = -1.0;
            try {
                tolerance = Double.parseDouble(this.txtTolerance.getText());
            }
            catch(Exception e) {
                showErrorDialog("Tolerance must be a real number.");
                return null;
            }
            options.setTolerance(tolerance);
        }
        else {
            try {
                options.setOptimiser(
                        OptimiserFactory.getInstance().createOptimiserInstance(
                                (String) this.cboSolver.getSelectedItem(), Objective.ObjectiveType.QUADRATIC));

            } catch (OptimiserException oe) {
                showErrorDialog("Could not create requested optimiser: " + this.cboSolver.getSelectedItem());
                return null;
            }
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
                "QNet Error",
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
     * @param args Program arguments... we expect nothing to be here.
     */
    public static void main(String args[]) {

        // Configure logging
        QNet.configureLogging();

        try {
            log.info("Running in GUI mode");

            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    new QNetGUI().setVisible(true);
                }
            });
            return;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}

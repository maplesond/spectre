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
package uk.ac.uea.cmp.phygen.superq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.tgac.metaopt.OptimiserException;
import uk.ac.tgac.metaopt.OptimiserFactory;
import uk.ac.uea.cmp.phygen.core.ui.gui.JobController;
import uk.ac.uea.cmp.phygen.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.phygen.core.ui.gui.ToolHost;
import uk.ac.uea.cmp.phygen.superq.problems.SecondaryProblem;
import uk.ac.uea.cmp.phygen.superq.problems.SecondaryProblemFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SuperQGUI extends JFrame implements ToolHost {

    private static Logger log = LoggerFactory.getLogger(SuperQGUI.class);

    // ***** GUI components *****

    private JPanel pnlInput;
    private JPanel pnlSelectInput;
    private JPanel pnlInputOptions;
    private JComboBox cboInputFormat;
    private JLabel lblInput;
    private JTextField txtInput;
    private JButton cmdInput;
    private JCheckBox chkScaleInput;

    private JPanel pnlOptimisers;
    private JLabel lblSelectPrimarySolver;
    private JComboBox cboSelectPrimarySolver;
    private JLabel lblSelectSecondarySolver;
    private JComboBox cboSelectSecondarySolver;
    private JLabel lblSelectSecondaryObjective;
    private JComboBox cboSelectSecondaryObjective;

    private JPanel pnlOutput;
    private JPanel pnlSelectOutput;
    private JPanel pnlOutputOptions;
    private JLabel lblSave;
    private JTextField txtSave;
    private JButton cmdSave;
    private JTextField txtFilter;
    private JCheckBox chkFilter;

    private JPanel pnlControl;
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


    private void initInputComponents() {

        // ***** Input setup *****


        lblInput = new JLabel();
        txtInput = new JTextField();
        cmdInput = new JButton();
        cboInputFormat = new JComboBox();
        chkScaleInput = new JCheckBox();

        cmdInput.setText("...");
        cmdInput.setToolTipText("Select the input file to process");
        cmdInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdInputActionPerformed(evt);
            }
        });

        txtInput.setToolTipText("Path to the input file containing trees to process");

        lblInput.setText("Input file:");

        cboInputFormat.setModel(new DefaultComboBoxModel(new String[]{"Choose input file format:", "script", "newick"}));

        chkScaleInput.setText("Scale tree weights");
        chkScaleInput.setToolTipText("Scale weights from input trees");
        chkScaleInput.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chkScaleInputMouseClicked(evt);
            }
        });
        chkScaleInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkScaleInputActionPerformed(evt);
            }
        });

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
        pnlInputOptions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlInputOptions.add(Box.createHorizontalGlue());
        pnlInputOptions.add(cboInputFormat);
        pnlInputOptions.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlInputOptions.add(chkScaleInput);

        pack();

        pnlInput = new JPanel();
        pnlInput.setLayout(new BoxLayout(pnlInput, BoxLayout.PAGE_AXIS));
        pnlInput.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Input:"));
        pnlInput.add(Box.createVerticalGlue());
        pnlInput.add(pnlSelectInput);
        pnlInput.add(pnlInputOptions);

        pack();
    }


    private void initOptimiserComponents() {

        // ***** Optimiser options *****

        cboSelectPrimarySolver = new JComboBox();
        lblSelectPrimarySolver = new JLabel();

        cboSelectSecondarySolver = new JComboBox();
        lblSelectSecondarySolver = new JLabel();

        cboSelectSecondaryObjective = new JComboBox();
        lblSelectSecondaryObjective = new JLabel();


        cboSelectPrimarySolver.setModel(new DefaultComboBoxModel(
                OptimiserFactory.getInstance().listOperationalOptimisers().toArray()));
        cboSelectPrimarySolver.setToolTipText("Select primary optimiser");
        cboSelectPrimarySolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSelectSolverActionPerformed(evt);
            }
        });

        lblSelectPrimarySolver.setText("Select primary optimiser:");

        cboSelectSecondarySolver.setModel(new DefaultComboBoxModel(
                OptimiserFactory.getInstance().listOperationalOptimisers().toArray()));
        cboSelectSecondarySolver.setToolTipText("Select secondary optimiser");
        cboSelectSecondarySolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSelectSolverActionPerformed(evt);
            }
        });

        lblSelectSecondarySolver.setText("Select secondary optimiser:");

        cboSelectSecondaryObjective.setModel(new DefaultComboBoxModel(SecondaryProblemFactory.getInstance().listObjectivesByIdentifier().toArray()));
        cboSelectSecondaryObjective.setToolTipText("Select secondary objective");
        cboSelectSecondaryObjective.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSelectObjectiveActionPerformed(evt);
            }
        });

        lblSelectSecondaryObjective.setText("Select secondary objective:");

        pnlOptimisers = new JPanel();


        GroupLayout layout = new GroupLayout(pnlOptimisers);

        pnlOptimisers.setLayout(layout);
        pnlOptimisers.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Optimiser Setup:"));

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(lblSelectPrimarySolver)
                                .addComponent(lblSelectSecondarySolver)
                                .addComponent(lblSelectSecondaryObjective)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(cboSelectPrimarySolver)
                                .addComponent(cboSelectSecondarySolver)
                                .addComponent(cboSelectSecondaryObjective)
                        )
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
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


    private void initOutputComponents() {


        // ***** Output setup *****

        pnlOutput = new JPanel(new BorderLayout());

        lblSave = new JLabel();
        txtSave = new JTextField();
        cmdSave = new JButton();
        txtFilter = new JTextField();
        chkFilter = new JCheckBox();


        txtSave.setToolTipText("Choose the output file");
        txtSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSaveActionPerformed(evt);
            }
        });

        lblSave.setText("Save to file:");

        cmdSave.setText("...");
        cmdSave.setToolTipText("Choose the output file");
        cmdSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveActionPerformed(evt);
            }
        });

        chkFilter.setText("Filter:");
        chkFilter.setToolTipText("filter");
        chkFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFilterActionPerformed(evt);
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

        pack();

        pnlOutputOptions = new JPanel();
        pnlOutputOptions.setLayout(new BoxLayout(pnlOutputOptions, BoxLayout.LINE_AXIS));
        pnlOutputOptions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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

    private void initControlComponents() {


        // ***** Run control and feedback *****

        cmdRun = new JButton();
        progStatus = new JProgressBar();
        lblStatus = new JLabel();
        cmdCancel = new JButton();

        cmdRun.setText("Run SUPERQ");
        cmdRun.setToolTipText("Run the SUPERQ Algorithm");
        cmdRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRunActionPerformed(evt);
            }
        });

        lblStatus.setText("Status:");
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmdCancel.setText("Cancel");

        pnlControlButtons = new JPanel();
        pnlControlButtons.setLayout(new BoxLayout(pnlControlButtons, BoxLayout.LINE_AXIS));
        pnlControlButtons.add(Box.createHorizontalGlue());
        pnlControlButtons.add(cmdRun);
        pnlControlButtons.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlControlButtons.add(cmdCancel);

        pack();

        pnlControl = new JPanel();
        pnlControl.setLayout(new BoxLayout(pnlControl, BoxLayout.PAGE_AXIS));
        pnlControl.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        pnlControl.add(Box.createVerticalGlue());
        pnlControl.add(lblStatus, BorderLayout.LINE_START);
        pnlControl.add(Box.createVerticalGlue());
        pnlControl.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlControl.add(progStatus);
        pnlControl.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlControl.add(pnlControlButtons, BorderLayout.PAGE_END);

        pack();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        this.initInputComponents();
        this.initOptimiserComponents();
        this.initOutputComponents();
        this.initControlComponents();


        // ***** Layout *****

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        getContentPane().add(Box.createVerticalGlue());
        getContentPane().add(Box.createRigidArea(new Dimension(0, 10)));
        getContentPane().add(pnlInput);
        getContentPane().add(Box.createRigidArea(new Dimension(0, 10)));
        getContentPane().add(pnlOptimisers);
        getContentPane().add(Box.createRigidArea(new Dimension(0, 10)));
        getContentPane().add(pnlOutput);
        getContentPane().add(Box.createRigidArea(new Dimension(0, 10)));
        getContentPane().add(pnlControl);

        pack();
    }



    private void txtSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaveActionPerformed

    private void cmdSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdSaveActionPerformed
        //Choose file for output
        final JFileChooser fc = new JFileChooser();
        if (evt.getSource() == cmdSave) {
            fc.setSelectedFile(new File("outfile"));
            int returnVal = fc.showSaveDialog(SuperQGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();
                String z = file.getAbsolutePath();
                txtSave.setText(z);
            } else {
                log.debug("Open command cancelled by user.");
            }
        }
    }//GEN-LAST:event_cmdSaveActionPerformed

    private void chkScaleInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkScaleInputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkScaleInputActionPerformed

    private void chkScaleInputMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chkScaleInputMouseClicked
    }//GEN-LAST:event_chkScaleInputMouseClicked

    private void chkFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFilterActionPerformed
        if (chkFilter.isSelected()) {
            txtFilter.setEnabled(true);
        } else {
            txtFilter.setEnabled(false);
        }
    }//GEN-LAST:event_chkFilterActionPerformed

    private void cmdInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdInputActionPerformed
        //Create a file chooser
        final JFileChooser fc = new JFileChooser();
        if (evt.getSource() == cmdInput) {
            int returnVal = fc.showOpenDialog(SuperQGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String z = "";
                z = file.getAbsolutePath();
                txtInput.setText(z);
            } else {
                log.debug("Open command cancelled by user.");
            }
        }
    }//GEN-LAST:event_cmdInputActionPerformed

    private void cmdRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRunActionPerformed

        SuperQOptions options = buildSuperQOptions();

        if (options != null)
            this.superqRunner.runSuperQ(options, new StatusTracker(this.progStatus, this.lblStatus));

    }//GEN-LAST:event_cmdRunActionPerformed

    private void cboSelectObjectiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSelectObjectiveActionPerformed
        SecondaryProblem newObjective = SecondaryProblemFactory.getInstance().createSecondaryObjective(this.cboSelectSecondaryObjective.getSelectedItem().toString());
        if (newObjective == null) {
            this.lblSelectSecondarySolver.setEnabled(false);
            this.cboSelectSecondarySolver.setEnabled(false);
        } else {
            this.lblSelectSecondarySolver.setEnabled(true);
            this.cboSelectSecondarySolver.setEnabled(true);
        }
    }//GEN-LAST:event_cboSelectObjectiveActionPerformed

    private void cboSelectSolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSelectSolverActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboSelectSolverActionPerformed

    private SuperQOptions buildSuperQOptions() {

        SuperQOptions options;

        try {
            options = new SuperQOptions();
        } catch (OptimiserException oe) {
            showErrorDialog("Error occurred configuring the optimiser.  Check you have selected an operational optimiser and set an appropriate objective.");
            return null;
        }

        String type = (String) cboInputFormat.getSelectedItem();
        options.setInputFileFormat(SuperQOptions.InputFormat.valueOf(type.toUpperCase()));

        File input_file = new File(this.txtInput.getText().replaceAll("(^\")|(\"$)", ""));
        options.setInputFile(input_file);

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

        options.setSecondaryProblem((SecondaryProblem) this.cboSelectSecondaryObjective.getSelectedItem());

        try {
            options.setSecondarySolver(
                    OptimiserFactory.getInstance().createOptimiserInstance(
                            (String) this.cboSelectSecondarySolver.getSelectedItem(), options.getSecondaryProblem().getObjectiveType()));
        } catch (OptimiserException oe) {
            showErrorDialog("Could not create requested optimiser: " + (String) this.cboSelectSecondarySolver.getSelectedItem());
            return null;
        }

        options.setScaleInputTree(this.chkScaleInput.isEnabled() && this.chkScaleInput.isSelected());

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

    


    public static void main(String args[]) {

        SuperQ.configureLogging();


        // If there are no args we assume that we're in GUI mode
        try {
            if (args.length == 0) {

                log.info("Running in GUI mode");

                java.awt.EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        new SuperQGUI().setVisible(true);
                    }
                });
                return;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}

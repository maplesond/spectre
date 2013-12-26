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
import java.io.File;

public class SuperQGUI extends JFrame implements ToolHost {

    private static Logger log = LoggerFactory.getLogger(SuperQGUI.class);

    JDialog dialog = new JDialog(this, "SUPERQ");
    JFrame gui = new JFrame("SUPERQ");
    JobController go_control;
    SuperQRunner superqRunner;

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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        cmdInput = new javax.swing.JButton();
        txtInput = new javax.swing.JTextField();
        cmdRun = new javax.swing.JButton();
        txtFilter = new javax.swing.JTextField();
        chkFilter = new javax.swing.JCheckBox();
        txtSave = new javax.swing.JTextField();
        lblSave = new javax.swing.JLabel();
        cmdSave = new javax.swing.JButton();
        lblInput = new javax.swing.JLabel();
        cboInputFormat = new javax.swing.JComboBox();
        chkScaleInput = new javax.swing.JCheckBox();
        cboSelectObjective = new javax.swing.JComboBox();
        lblSelectObjective = new javax.swing.JLabel();
        progStatus = new javax.swing.JProgressBar();
        lblStatus = new javax.swing.JLabel();
        cmdCancel = new javax.swing.JButton();
        cboSelectSolver = new javax.swing.JComboBox();
        lblSelectSolver = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        cmdInput.setText("...");
        cmdInput.setToolTipText("Choose the Input");
        cmdInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdInputActionPerformed(evt);
            }
        });

        txtInput.setToolTipText("Choose the Input");

        cmdRun.setText("Run SUPERQ");
        cmdRun.setToolTipText("Run the SUPERQ Algorithm");
        cmdRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRunActionPerformed(evt);
            }
        });

        chkFilter.setText("Filter:");
        chkFilter.setToolTipText("filter");
        chkFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFilterActionPerformed(evt);
            }
        });

        txtSave.setToolTipText("Choose the output file");
        txtSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSaveActionPerformed(evt);
            }
        });

        lblSave.setText("Save to file:");

        cmdSave.setText("jButton1");
        cmdSave.setToolTipText("Choose the output file");
        cmdSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdSaveActionPerformed(evt);
            }
        });

        lblInput.setText("Choose input file:");

        cboInputFormat.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"choose format of input file", "script", "newick"}));

        chkScaleInput.setText("Scale input");
        chkScaleInput.setToolTipText("Scale input trees");
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

        cboSelectObjective.setModel(new javax.swing.DefaultComboBoxModel(SecondaryProblemFactory.getInstance().listObjectivesByIdentifier().toArray()));
        cboSelectObjective.setToolTipText("Select Function");
        cboSelectObjective.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSelectObjectiveActionPerformed(evt);
            }
        });

        lblSelectObjective.setText("Select secondary objective:");

        lblStatus.setText("Status:");

        cmdCancel.setText("Cancel");

        cboSelectSolver.setModel(new javax.swing.DefaultComboBoxModel(
                OptimiserFactory.getInstance().listOperationalOptimisers().toArray()));
        cboSelectSolver.setToolTipText("Select Function");
        cboSelectSolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSelectSolverActionPerformed(evt);
            }
        });

        lblSelectSolver.setText("Select secondary solver:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(chkFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(6, 6, 6)
                                                .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(234, 234, 234)
                                                                .addComponent(cmdRun, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(cmdCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(lblStatus)
                                                                        .addComponent(chkScaleInput))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(progStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                                .addComponent(lblInput, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                                                                .addComponent(lblSave, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                        .addComponent(lblSelectObjective, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(txtInput)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(cmdInput, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(cboInputFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(txtSave)
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addComponent(cboSelectObjective, 0, 178, Short.MAX_VALUE)
                                                                                                .addGap(18, 18, 18)
                                                                                                .addComponent(lblSelectSolver, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                        .addComponent(cboSelectSolver, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addComponent(cmdSave, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                .addGap(157, 157, 157)))))))
                                                .addGap(30, 30, 30))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cboInputFormat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblInput, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cmdInput))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblSave, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cmdSave))
                                .addGap(13, 13, 13)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(lblSelectObjective, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(cboSelectObjective, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(lblSelectSolver, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(cboSelectSolver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(chkFilter))
                                .addGap(18, 18, 18)
                                .addComponent(chkScaleInput)
                                .addGap(35, 35, 35)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(progStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblStatus))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(cmdRun, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                                        .addComponent(cmdCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        SecondaryProblem newObjective = SecondaryProblemFactory.getInstance().createSecondaryObjective(this.cboSelectObjective.getSelectedItem().toString());
        if (newObjective == null) {
            this.lblSelectSolver.setEnabled(false);
            this.cboSelectSolver.setEnabled(false);
        } else {
            this.lblSelectSolver.setEnabled(true);
            this.cboSelectSolver.setEnabled(true);
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

        options.setSecondaryProblem((SecondaryProblem) this.cboSelectObjective.getSelectedItem());

        try {
            options.setSecondarySolver(
                    OptimiserFactory.getInstance().createOptimiserInstance(
                        (String) this.cboSelectSolver.getSelectedItem(), options.getSecondaryProblem().getObjectiveType()));
        } catch (OptimiserException oe) {
            showErrorDialog("Could not create requested optimiser: " + (String) this.cboSelectSolver.getSelectedItem());
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox cboInputFormat;
    private javax.swing.JComboBox cboSelectObjective;
    private javax.swing.JComboBox cboSelectSolver;
    private javax.swing.JCheckBox chkFilter;
    private javax.swing.JCheckBox chkScaleInput;
    private javax.swing.JButton cmdCancel;
    private javax.swing.JButton cmdInput;
    private javax.swing.JButton cmdRun;
    private javax.swing.JButton cmdSave;
    private javax.swing.JLabel lblInput;
    private javax.swing.JLabel lblSave;
    private javax.swing.JLabel lblSelectObjective;
    private javax.swing.JLabel lblSelectSolver;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JProgressBar progStatus;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtInput;
    private javax.swing.JTextField txtSave;
    // End of variables declaration//GEN-END:variables



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

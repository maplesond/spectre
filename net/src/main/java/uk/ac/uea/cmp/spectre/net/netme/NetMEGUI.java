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

package uk.ac.uea.cmp.spectre.net.netme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ui.gui.JobController;
import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTrackerWithView;
import uk.ac.uea.cmp.spectre.core.ui.gui.ToolHost;
import uk.ac.uea.cmp.spectre.core.util.LogConfig;
import uk.ac.uea.cmp.spectre.net.netmake.NetMakeOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URISyntaxException;

import static uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel.NIMBUS;
import static uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel.setLookAndFeel;

public class NetMEGUI extends JFrame implements ToolHost {

    private static Logger log = LoggerFactory.getLogger(NetMEGUI.class);

    private static final String TITLE = "NetME";

    // ***** GUI components *****

    private JPanel pnlOptions;

    private JPanel pnlInput;
    private JPanel pnlSelectInput;
    private JLabel lblInput;
    private JTextField txtInput;
    private JButton cmdInput;

    private JPanel pnlOutput;
    private JPanel pnlSelectOutput;
    private JLabel lblOutputPrefix;
    private JTextField txtOutputPrefix;
    private JButton cmdOutputPrefix;

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
    private NetMERunner netMERunner;

    private File lastOutput;
    private File cwd;


    public NetMEGUI() {
        initComponents();
        setTitle(TITLE);

        try {
            setIconImage((new ImageIcon(uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel.getLogoFilePath()).getImage()));
        } catch (URISyntaxException e) {
            showErrorDialog("Couldn't load logo.");
        }

        cmdRun.setEnabled(true);

        this.lastOutput = null;
        this.cwd = null;

        this.netMERunner = new NetMERunner(this);

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
        cmdInput.setToolTipText(NetMEOptions.DESC_INPUT);
        cmdInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdInputDistancesActionPerformed(evt);
            }
        });

        txtInput.setPreferredSize(new Dimension(200, 25));
        txtInput.setToolTipText(NetMEOptions.DESC_INPUT);

        lblInput.setText("Input nexus file:");
        lblInput.setToolTipText(NetMEOptions.DESC_INPUT);

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
     * Output options
     */
    private void initOutputComponents() {


        pnlOutput = new JPanel(new BorderLayout());

        lblOutputPrefix = new JLabel();
        txtOutputPrefix = new JTextField();
        cmdOutputPrefix = new JButton();

        lblOutputPrefix = new JLabel();
        txtOutputPrefix = new JTextField();

        txtOutputPrefix.setPreferredSize(new Dimension(200, 25));
        txtOutputPrefix.setToolTipText(NetMEOptions.DESC_OUTPUT_PREFIX);

        lblOutputPrefix.setText("Output prefix:");
        lblOutputPrefix.setToolTipText(NetMEOptions.DESC_OUTPUT_PREFIX);

        cmdOutputPrefix.setText("...");
        cmdOutputPrefix.setToolTipText(NetMEOptions.DESC_OUTPUT_PREFIX);
        cmdOutputPrefix.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOutputDirActionPerformed(evt);
            }
        });

        pnlSelectOutput = new JPanel();
        pnlSelectOutput.setLayout(new BoxLayout(pnlSelectOutput, BoxLayout.LINE_AXIS));
        pnlSelectOutput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlSelectOutput.add(Box.createHorizontalGlue());
        pnlSelectOutput.add(lblOutputPrefix);
        pnlSelectOutput.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOutput.add(txtOutputPrefix);
        pnlSelectOutput.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOutput.add(cmdOutputPrefix);

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
        cmdRun.setText("Run NetME");
        cmdRun.setToolTipText("Run NetME");
        cmdRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRunActionPerformed(evt);
            }
        });

        cmdCancel = new JButton();
        cmdCancel.setText("Cancel");

        cmdViewOutput = new JButton();
        cmdViewOutput.setText("View Tree");
        cmdViewOutput.setToolTipText("Visualise the produced ME tree in Spectre");
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
        this.initOutputComponents();
        this.initStatusComponents();
        this.initControlComponents();

        pnlOptions = new JPanel();
        pnlOptions.setLayout(new BoxLayout(pnlOptions, BoxLayout.PAGE_AXIS));
        pnlOptions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlOptions.add(pnlInput);
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

    private void cmdViewActionPerformed(ActionEvent evt) {
        this.firePropertyChange("done", null, this.lastOutput);
    }

    /**
     * Choose file for output
     *
     * @param evt
     */
    private void cmdOutputDirActionPerformed(java.awt.event.ActionEvent evt) {

        final JFileChooser fc = cwd == null ? new JFileChooser() : new JFileChooser(cwd);
        if (evt.getSource() == cmdOutputPrefix) {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showSaveDialog(NetMEGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();
                String z = file.getAbsolutePath();
                txtOutputPrefix.setText(z);
                this.cwd = file.getParentFile();
            } else {
                log.debug("Open output directory command cancelled by user.");
            }
        }
    }

    /**
     * Choose a file for input
     *
     * @param evt
     */
    private void cmdInputDistancesActionPerformed(java.awt.event.ActionEvent evt) {

        final JFileChooser fc = cwd == null ? new JFileChooser() : new JFileChooser(cwd);
        if (evt.getSource() == cmdInput) {
            int returnVal = fc.showOpenDialog(NetMEGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String z = "";
                z = file.getAbsolutePath();
                txtInput.setText(z);
                this.cwd = file.getParentFile();
            } else {
                log.debug("Open distance matrix command cancelled by user.");
            }
        }
    }


    /**
     * Start
     *
     * @param evt
     */
    private void cmdRunActionPerformed(java.awt.event.ActionEvent evt) {

        NetMEOptions options = buildNetMEOptions();

        if (options != null)
            this.lastOutput = options.getMETreeOutput();
            this.netMERunner.runNetME(options, new StatusTrackerWithView(this.progStatus, this.lblStatus, this.cmdViewOutput));

    }

    /**
     * Setup configuration using values specified in the GUI
     *
     * @return configuration
     */
    private NetMEOptions buildNetMEOptions() {

        NetMEOptions options = new NetMEOptions();

        File outputFilePrefix = new File(this.txtOutputPrefix.getText().replaceAll("(^\")|(\"$)", ""));

        options.setInputFile(new File(this.txtInput.getText().replaceAll("(^\")|(\"$)", "")));
        options.setOutputDir(outputFilePrefix.getParentFile());
        options.setPrefix(outputFilePrefix.getName());
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
                "Net ME Error",
                JOptionPane.ERROR_MESSAGE);
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
            log.info("Running in GUI mode");

            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    new NetMEGUI().setVisible(true);
                }
            });
            return;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}


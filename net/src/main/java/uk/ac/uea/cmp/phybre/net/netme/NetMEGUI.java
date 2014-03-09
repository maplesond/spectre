package uk.ac.uea.cmp.phybre.net.netme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.phybre.core.ui.gui.*;
import uk.ac.uea.cmp.phybre.net.netmake.NetMakeOptions;

import javax.swing.*;
import javax.swing.LookAndFeel;
import java.awt.*;
import java.io.File;

import static uk.ac.uea.cmp.phybre.core.ui.gui.LookAndFeel.*;

/**
 * Created by maplesod on 10/02/14.
 */
public class NetMEGUI extends JFrame implements ToolHost {

    private static Logger log = LoggerFactory.getLogger(NetMEGUI.class);

    private static final String TITLE = "NetMake";

    // ***** GUI components *****

    private JPanel pnlOptions;

    private JPanel pnlInput;
    private JPanel pnlSelectDistances;
    private JLabel lblInputDistances;
    private JTextField txtInputDistances;
    private JButton cmdInputDistances;
    private JPanel pnlSelectOrdering;
    private JLabel lblInputOrdering;
    private JTextField txtInputOrdering;
    private JButton cmdInputOrdering;

    private JPanel pnlOutput;
    private JPanel pnlSelectOutputDir;
    private JPanel pnlOutputPrefix;
    private JLabel lblOutputDir;
    private JTextField txtOutputDir;
    private JButton cmdOutputDir;
    private JLabel lblOutputPrefix;
    private JTextField txtOutputPrefix;

    private JPanel pnlStatus;
    private JPanel pnlControlButtons;
    private JButton cmdCancel;
    private JButton cmdRun;
    private JLabel lblStatus;
    private JProgressBar progStatus;



    private JDialog dialog = new JDialog(this, TITLE);
    private JFrame gui = new JFrame(TITLE);
    private JobController go_control;
    private NetMERunner netMERunner;

    public NetMEGUI() {
        initComponents();
        setTitle(TITLE);

        cmdRun.setEnabled(true);

        this.netMERunner = new NetMERunner(this);

        this.go_control = new JobController(this.cmdRun, this.cmdCancel);
        setRunningStatus(false);
    }

    /**
     * Input options
     */
    private void initInputComponents() {

        lblInputDistances = new JLabel();
        txtInputDistances = new JTextField();
        cmdInputDistances = new JButton();

        cmdInputDistances.setText("...");
        cmdInputDistances.setToolTipText(NetMEOptions.DESC_DISTANCES);
        cmdInputDistances.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdInputDistancesActionPerformed(evt);
            }
        });

        txtInputDistances.setPreferredSize(new Dimension(200, 25));
        txtInputDistances.setToolTipText(NetMEOptions.DESC_DISTANCES);

        lblInputDistances.setText("Input distance matrix file:");
        lblInputDistances.setToolTipText(NetMEOptions.DESC_DISTANCES);

        pnlSelectDistances = new JPanel();
        pnlSelectDistances.setLayout(new BoxLayout(pnlSelectDistances, BoxLayout.LINE_AXIS));
        pnlSelectDistances.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlSelectDistances.add(Box.createHorizontalGlue());
        pnlSelectDistances.add(lblInputDistances);
        pnlSelectDistances.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectDistances.add(txtInputDistances);
        pnlSelectDistances.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectDistances.add(cmdInputDistances);

        lblInputOrdering = new JLabel();
        txtInputOrdering = new JTextField();
        cmdInputOrdering = new JButton();

        cmdInputOrdering.setText("...");
        cmdInputOrdering.setToolTipText(NetMEOptions.DESC_CIRCULAR_ORDERING);
        cmdInputOrdering.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdInputOrderingActionPerformed(evt);
            }
        });

        txtInputOrdering.setPreferredSize(new Dimension(200, 25));
        txtInputOrdering.setToolTipText(NetMEOptions.DESC_CIRCULAR_ORDERING);

        lblInputOrdering.setText("Input circular ordering file:");
        lblInputOrdering.setToolTipText(NetMEOptions.DESC_CIRCULAR_ORDERING);

        pnlSelectOrdering = new JPanel();
        pnlSelectOrdering.setLayout(new BoxLayout(pnlSelectOrdering, BoxLayout.LINE_AXIS));
        pnlSelectOrdering.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        pnlSelectOrdering.add(Box.createHorizontalGlue());
        pnlSelectOrdering.add(lblInputOrdering);
        pnlSelectOrdering.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOrdering.add(txtInputOrdering);
        pnlSelectOrdering.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOrdering.add(cmdInputOrdering);

        pack();

        pnlInput = new JPanel();
        pnlInput.setLayout(new BoxLayout(pnlInput, BoxLayout.PAGE_AXIS));
        pnlInput.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Input:"));
        pnlInput.add(Box.createVerticalGlue());
        pnlInput.add(pnlSelectDistances);
        pnlInput.add(pnlSelectOrdering);

        pack();
    }

    /**
     * Output options
     */
    private void initOutputComponents() {


        pnlOutput = new JPanel(new BorderLayout());

        lblOutputDir = new JLabel();
        txtOutputDir = new JTextField();
        cmdOutputDir = new JButton();

        lblOutputPrefix = new JLabel();
        txtOutputPrefix = new JTextField();

        txtOutputDir.setPreferredSize(new Dimension(200, 25));
        txtOutputDir.setToolTipText(NetMakeOptions.DESC_OUTPUT_DIR);

        lblOutputDir.setText("Save to file:");
        lblOutputDir.setToolTipText(NetMakeOptions.DESC_OUTPUT_DIR);

        cmdOutputDir.setText("...");
        cmdOutputDir.setToolTipText(NetMakeOptions.DESC_OUTPUT_DIR);
        cmdOutputDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdOutputDirActionPerformed(evt);
            }
        });

        lblOutputPrefix.setText("Output prefix:");
        lblOutputPrefix.setToolTipText(NetMakeOptions.DESC_OUTPUT_PREFIX);

        txtOutputPrefix.setPreferredSize(new Dimension(200, 25));
        txtOutputPrefix.setToolTipText(NetMakeOptions.DESC_OUTPUT_PREFIX);

        pnlSelectOutputDir = new JPanel();
        pnlSelectOutputDir.setLayout(new BoxLayout(pnlSelectOutputDir, BoxLayout.LINE_AXIS));
        pnlSelectOutputDir.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlSelectOutputDir.add(Box.createHorizontalGlue());
        pnlSelectOutputDir.add(lblOutputDir);
        pnlSelectOutputDir.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOutputDir.add(txtOutputDir);
        pnlSelectOutputDir.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlSelectOutputDir.add(cmdOutputDir);

        pnlOutputPrefix = new JPanel();
        pnlOutputPrefix.setLayout(new BoxLayout(pnlOutputPrefix, BoxLayout.LINE_AXIS));
        pnlOutputPrefix.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        pnlOutputPrefix.add(Box.createHorizontalGlue());
        pnlOutputPrefix.add(lblOutputPrefix);
        pnlOutputPrefix.add(Box.createRigidArea(new Dimension(10, 0)));
        pnlOutputPrefix.add(txtOutputPrefix);

        pnlOutput = new JPanel();
        pnlOutput.setLayout(new BoxLayout(pnlOutput, BoxLayout.PAGE_AXIS));
        pnlOutput.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Output:"));
        pnlOutput.add(Box.createVerticalGlue());
        pnlOutput.add(pnlSelectOutputDir);
        pnlOutput.add(pnlOutputPrefix);

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
        cmdRun.setText("Run NetMake");
        cmdRun.setToolTipText("Run NetMake");
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

    /**
     * Choose file for output
     * @param evt
     */
    private void cmdOutputDirActionPerformed(java.awt.event.ActionEvent evt) {

        final JFileChooser fc = new JFileChooser();
        if (evt.getSource() == cmdOutputDir) {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showSaveDialog(NetMEGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();
                String z = file.getAbsolutePath();
                txtOutputDir.setText(z);
            } else {
                log.debug("Open output directory command cancelled by user.");
            }
        }
    }

    /**
     * Choose a file for input
     * @param evt
     */
    private void cmdInputDistancesActionPerformed(java.awt.event.ActionEvent evt) {

        final JFileChooser fc = new JFileChooser();
        if (evt.getSource() == cmdInputDistances) {
            int returnVal = fc.showOpenDialog(NetMEGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String z = "";
                z = file.getAbsolutePath();
                txtInputDistances.setText(z);
            } else {
                log.debug("Open distance matrix command cancelled by user.");
            }
        }
    }

    /**
     * Choose a file for input
     * @param evt
     */
    private void cmdInputOrderingActionPerformed(java.awt.event.ActionEvent evt) {

        final JFileChooser fc = new JFileChooser();
        if (evt.getSource() == cmdInputOrdering) {
            int returnVal = fc.showOpenDialog(NetMEGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String z = "";
                z = file.getAbsolutePath();
                txtInputOrdering.setText(z);
            } else {
                log.debug("Open circular ordering command cancelled by user.");
            }
        }
    }

    /**
     * Start
     * @param evt
     */
    private void cmdRunActionPerformed(java.awt.event.ActionEvent evt) {

        NetMEOptions options = buildNetMEOptions();

        if (options != null)
            this.netMERunner.runNetME(options, new StatusTracker(this.progStatus, this.lblStatus));

    }

    /**
     * Setup configuration using values specified in the GUI
     * @return configuration
     */
    private NetMEOptions buildNetMEOptions() {

        NetMEOptions options = new NetMEOptions();

        options.setDistancesFile(new File(this.txtInputDistances.getText().replaceAll("(^\")|(\"$)", "")));
        options.setCircularOrderingFile(new File(this.txtInputOrdering.getText().replaceAll("(^\")|(\"$)", "")));
        options.setOutputDir(new File(this.txtOutputDir.getText().replaceAll("(^\")|(\"$)", "")));
        options.setPrefix(this.txtOutputPrefix.getText());

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
     * @param args Program arguments... we expect nothing to be here.
     */
    public static void main(String args[]) {

        // Configure logging
        NetME.configureLogging();

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


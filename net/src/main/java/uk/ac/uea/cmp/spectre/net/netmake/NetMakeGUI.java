package uk.ac.uea.cmp.spectre.net.netmake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.uea.cmp.spectre.core.ui.gui.JobController;
import uk.ac.uea.cmp.spectre.core.ui.gui.StatusTracker;
import uk.ac.uea.cmp.spectre.core.ui.gui.ToolHost;
import uk.ac.uea.cmp.spectre.net.netmake.weighting.Weightings;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel.NIMBUS;
import static uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel.setLookAndFeel;

/**
 * Created by dan on 09/02/14.
 */
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

    private JPanel pnlWeightings;
    private JLabel lblWeighting1;
    private JComboBox<String> cboWeighting1;
    private JLabel lblWeighting2;
    private JComboBox<String> cboWeighting2;
    private JLabel lblWeight;
    private JTextField txtWeight;

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
    private NetMakeRunner netMakeRunner;

    public NetMakeGUI() {
        initComponents();
        setTitle(TITLE);

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

        cboWeighting1.setModel(new DefaultComboBoxModel(Weightings.values()));
        cboWeighting1.setToolTipText(NetMakeOptions.DESC_WEIGHTINGS_1);

        lblWeighting1.setText("Select weight type 1:");
        lblWeighting1.setToolTipText(NetMakeOptions.DESC_WEIGHTINGS_1);

        cboWeighting2.setModel(new DefaultComboBoxModel(Weightings.stringValuesWithNone()));
        cboWeighting2.setToolTipText(NetMakeOptions.DESC_WEIGHTINGS_2);

        lblWeighting2.setText("Select weight type 2:");
        lblWeighting2.setToolTipText(NetMakeOptions.DESC_WEIGHTINGS_2);


        lblWeight.setText("Set the tree weight:");
        lblWeight.setToolTipText(NetMakeOptions.DESC_TREE_PARAM);

        txtWeight.setText("");
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
        this.initWeightingComponents();
        this.initOutputComponents();
        this.initStatusComponents();
        this.initControlComponents();

        pnlOptions = new JPanel();
        pnlOptions.setLayout(new BoxLayout(pnlOptions, BoxLayout.PAGE_AXIS));
        pnlOptions.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlOptions.add(pnlInput);
        pnlOptions.add(Box.createRigidArea(new Dimension(0, 10)));
        pnlOptions.add(pnlWeightings);
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
            int returnVal = fc.showSaveDialog(NetMakeGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File file = fc.getSelectedFile();
                String z = file.getAbsolutePath();
                txtOutputDir.setText(z);
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
            int returnVal = fc.showOpenDialog(NetMakeGUI.this);
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
     * Start
     * @param evt
     */
    private void cmdRunActionPerformed(java.awt.event.ActionEvent evt) {

        NetMakeOptions options = buildNetMakeOptions();

        if (options != null)
            this.netMakeRunner.runNetMake(options, new StatusTracker(this.progStatus, this.lblStatus));

    }

    /**
     * Setup configuration using values specified in the GUI
     * @return configuration
     */
    private NetMakeOptions buildNetMakeOptions() {

        NetMakeOptions options = new NetMakeOptions();

        options.setInput(new File(this.txtInput.getText().replaceAll("(^\")|(\"$)", "")));
        options.setOutputDir(new File(this.txtOutputDir.getText().replaceAll("(^\")|(\"$)", "")));
        options.setOutputPrefix(this.txtOutputPrefix.getText());

        try {
            options.setTreeParam(Double.parseDouble(this.txtWeight.getText()));
        }
        catch(Exception e) {
            showErrorDialog("Tree param must be a real number.");
            return null;
        }

        // May need some more validation here.
        options.setWeighting1(this.cboWeighting1.getSelectedItem().toString());
        options.setWeighting2(this.cboWeighting2.getSelectedItem().toString());

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

    /**
     * Main entry point when running in GUI mode.
     * @param args Program arguments... we expect nothing to be here.
     */
    public static void main(String args[]) {

        // Configure logging
        NetMake.configureLogging();

        setLookAndFeel(NIMBUS);

        try {
            log.info("Running in GUI mode");

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

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

import uk.ac.uea.cmp.spectre.core.ui.gui.LookAndFeel;

/**
 * @author balvociute
 */
public class ColoringOptions extends javax.swing.JDialog {

    private javax.swing.ButtonGroup buttonGroupColoring;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JRadioButton jRadioButtonAutoColoring;
    private javax.swing.JRadioButton jRadioButtonKeepColoring;

    /**
     * Creates new form OpenOptions
     */
    public ColoringOptions(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        buttonGroupColoring.add(jRadioButtonAutoColoring);
        buttonGroupColoring.add(jRadioButtonKeepColoring);
    }

    private void initComponents() {

        buttonGroupColoring = new javax.swing.ButtonGroup();
        jRadioButtonKeepColoring = new javax.swing.JRadioButton();
        jRadioButtonAutoColoring = new javax.swing.JRadioButton();
        jButtonOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jRadioButtonKeepColoring.setSelected(true);
        jRadioButtonKeepColoring.setText("Keep coloring");

        jRadioButtonAutoColoring.setText("Automatic coloring");

        jButtonOK.setText("OK");
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jRadioButtonKeepColoring)
                                        .addComponent(jRadioButtonAutoColoring))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonOK)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jRadioButtonKeepColoring)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButtonAutoColoring)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonOK)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }
}

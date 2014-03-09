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

package uk.ac.uea.cmp.phybre.flatnj.netvi;

import uk.ac.uea.cmp.phybre.core.ui.gui.LookAndFeel;

/**
 *
 * @author balvociute
 */
public class FindDIalog extends javax.swing.JDialog
{
    public String lineToFind;
    
    
    /**
     * Creates new form FindDIalog
     */
    public FindDIalog(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jTextFieldText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButtonFind = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        jCheckBoxRegex = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Names to find:");

        jButtonFind.setText("Find & select");
        jButtonFind.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonFindActionPerformed(evt);
            }
        });

        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonCancelActionPerformed(evt);
            }
        });

        jCheckBoxRegex.setText("Regular expression");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldText)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jCheckBoxRegex))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 164, Short.MAX_VALUE)
                        .addComponent(jButtonCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonFind)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxRegex)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCancel)
                    .addComponent(jButtonFind))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonCancelActionPerformed
    {//GEN-HEADEREND:event_jButtonCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonFindActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonFindActionPerformed
    {//GEN-HEADEREND:event_jButtonFindActionPerformed
        setVisible(false);
        Window parent = ((NetView)getParent()).getDrawing();
        parent.find(jTextFieldText.getText(), jCheckBoxRegex.isSelected());
    }//GEN-LAST:event_jButtonFindActionPerformed

    /**
     * Checks if search query is a regular expression.
     * 
     * @return true if search query is a regular expression.
     */
    public boolean regEx()
    {
        return jCheckBoxRegex.isSelected();
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        LookAndFeel.setLookAndFeel(LookAndFeel.NIMBUS);

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            /**
             * Opens find dialog.
             */
            public void run()
            {
                FindDIalog dialog = new FindDIalog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter()
                {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e)
                    {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonFind;
    private javax.swing.JCheckBox jCheckBoxRegex;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextFieldText;
    // End of variables declaration//GEN-END:variables
}

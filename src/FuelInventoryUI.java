
import com.mysql.jdbc.*;
import com.widgets.ButtonColumn;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.DriverManager;
import javax.swing.*;

public class FuelInventoryUI extends javax.swing.JFrame {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/fuel_station";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // Model for JTable
    DefaultTableModel model;

    public FuelInventoryUI() {
        initComponents();
        initTable();
        fetchData();
    }

    private void initTable() {
        // Initialize JTable model
        model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Fuel Type");
        model.addColumn("Quantity(L)");
        model.addColumn("Price(LKR)");
        model.addColumn("UPDATE");
        model.addColumn("DELETE");
        jTable1.setModel(model);

        Action editAction = new AbstractAction("Edit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = Integer.parseInt(e.getActionCommand());
                int inventoryId = (int) model.getValueAt(row, 0);
                openEditModal(inventoryId);
            }
        };

        Action deleteAction = new AbstractAction("Delete") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = Integer.parseInt(e.getActionCommand());
                int inventoryId = (int) model.getValueAt(row, 0);
                showDeleteConfirmationDialog(inventoryId);
            }
        };

        ButtonColumn editButton = new ButtonColumn(jTable1, editAction, 4);
        ButtonColumn deleteButton = new ButtonColumn(jTable1, deleteAction, 5);
    }

    private void fetchData() {
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM FuelInventory";
            try (Statement statement = (Statement) connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    Object[] rowData = {
                        resultSet.getInt("inventory_id"),
                        resultSet.getString("fuel_type"),
                        resultSet.getDouble("quantity"),
                        resultSet.getDouble("price"),
                        "Edit",
                        "Delete"
                    };
                    model.addRow(rowData);
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addFuel(String fuelType, double quantity, double price) {
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO FuelInventory (fuel_type, quantity, price) VALUES (?, ?, ?)";
            try (PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql)) {
                statement.setString(1, fuelType);
                statement.setDouble(2, quantity);
                statement.setDouble(3, price);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Fuel added successfully!");
                model.setRowCount(0); // Clear the table
                fetchData(); // Reload data into the table
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 36)); // NOI18N
        jLabel1.setText("Fuel Inventory");

        jButton1.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton2.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jButton2.setText("Back");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(46, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 643, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(76, 76, 76)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 326, Short.MAX_VALUE)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(46, 46, 46))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        showAddFuelDialog();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        DashboardUI dashboardUi = new DashboardUI();
        dashboardUi.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed
    private void showAddFuelDialog() {
        // Create JDialog for Add Fuel
        JDialog addFuelDialog = new JDialog(this, "Add Fuel", true);
        addFuelDialog.setSize(400, 200);
        addFuelDialog.setLayout(new BorderLayout());

        // JPanel to hold input fields
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));

        // Input fields
        JTextField fuelTypeField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();

        // Labels for input fields
        JLabel fuelTypeLabel = new JLabel("Fuel Type:");
        JLabel quantityLabel = new JLabel("Quantity (L):");
        JLabel priceLabel = new JLabel("Price (LKR):");

        // Add components to the input panel
        inputPanel.add(fuelTypeLabel);
        inputPanel.add(fuelTypeField);
        inputPanel.add(quantityLabel);
        inputPanel.add(quantityField);
        inputPanel.add(priceLabel);
        inputPanel.add(priceField);

        // JPanel for buttons
        JPanel buttonPanel = new JPanel();

        // Add button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFuel(fuelTypeField.getText(), Double.parseDouble(quantityField.getText()), Double.parseDouble(priceField.getText()));
                addFuelDialog.dispose();  // Close the dialog
            }
        });

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addFuelDialog.dispose();  // Close the dialog
            }
        });

        // Add buttons to the button panel
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Add panels to the dialog
        addFuelDialog.add(inputPanel, BorderLayout.CENTER);
        addFuelDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog location relative to the main frame
        addFuelDialog.setLocationRelativeTo(this);

        // Set the dialog visible
        addFuelDialog.setVisible(true);
    }

    private void openEditModal(int inventoryId) {
        // Fetch data based on inventoryId and populate the modal fields
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM FuelInventory WHERE inventory_id = ?";
            try (PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql)) {
                statement.setInt(1, inventoryId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    // Retrieve data from the result set
                    String fuelType = resultSet.getString("fuel_type");
                    double quantity = resultSet.getDouble("quantity");
                    double price = resultSet.getDouble("price");

                    // Create a JDialog for editing
                    JDialog editFuelDialog = new JDialog(this, "Edit Fuel", true);
                    editFuelDialog.setSize(400, 200);
                    editFuelDialog.setLayout(new BorderLayout());

                    // JPanel to hold input fields
                    JPanel inputPanel = new JPanel(new GridLayout(3, 2));

                    // Input fields
                    JTextField fuelTypeField = new JTextField(fuelType);
                    JTextField quantityField = new JTextField(String.valueOf(quantity));
                    JTextField priceField = new JTextField(String.valueOf(price));

                    // Labels for input fields
                    JLabel fuelTypeLabel = new JLabel("Fuel Type:");
                    JLabel quantityLabel = new JLabel("Quantity:");
                    JLabel priceLabel = new JLabel("Price:");

                    // Add components to the input panel
                    inputPanel.add(fuelTypeLabel);
                    inputPanel.add(fuelTypeField);
                    inputPanel.add(quantityLabel);
                    inputPanel.add(quantityField);
                    inputPanel.add(priceLabel);
                    inputPanel.add(priceField);

                    // JPanel for buttons
                    JPanel buttonPanel = new JPanel();

                    // Update button
                    JButton updateButton = new JButton("Update");
                    updateButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            // TODO: Add logic to update data into the database
                            updateFuel(inventoryId, fuelTypeField.getText(), Double.parseDouble(quantityField.getText()), Double.parseDouble(priceField.getText()));
                            editFuelDialog.dispose();  // Close the dialog
                        }
                    });

                    // Cancel button
                    JButton cancelButton = new JButton("Cancel");
                    cancelButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            editFuelDialog.dispose();  // Close the dialog
                        }
                    });

                    // Add buttons to the button panel
                    buttonPanel.add(updateButton);
                    buttonPanel.add(cancelButton);

                    // Add panels to the dialog
                    editFuelDialog.add(inputPanel, BorderLayout.CENTER);
                    editFuelDialog.add(buttonPanel, BorderLayout.SOUTH);

                    // Set dialog location relative to the main frame
                    editFuelDialog.setLocationRelativeTo(this);

                    // Set the dialog visible
                    editFuelDialog.setVisible(true);
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteConfirmationDialog(int inventoryId) {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            deleteRecord(inventoryId);
        }
    }

    private void deleteRecord(int inventoryId) {
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "DELETE FROM FuelInventory WHERE inventory_id = ?";
            try (PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql)) {
                statement.setInt(1, inventoryId);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Record deleted successfully!");
                model.setRowCount(0); // Clear the table
                fetchData(); // Reload data into the table
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFuel(int inventoryId, String fuelType, double quantity, double price) {
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "UPDATE FuelInventory SET fuel_type = ?, quantity = ?, price = ? WHERE inventory_id = ?";
            try (PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql)) {
                statement.setString(1, fuelType);
                statement.setDouble(2, quantity);
                statement.setDouble(3, price);
                statement.setInt(4, inventoryId);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Fuel updated successfully!");
                model.setRowCount(0); // Clear the table
                fetchData(); // Reload data into the table
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FuelInventoryUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FuelInventoryUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FuelInventoryUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FuelInventoryUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FuelInventoryUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}

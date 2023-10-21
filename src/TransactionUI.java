
import com.models.ComboItem;
import com.mysql.jdbc.Statement;
import com.widgets.ButtonColumn;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import java.util.Properties;

public class TransactionUI extends javax.swing.JFrame {

    private DefaultTableModel model;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/fuel_station";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public TransactionUI() {
        initComponents();
        initTable();
        fetchTransactionData();
    }

    private void initTable() {
        model = new DefaultTableModel();
        model.addColumn("Transaction ID");
        model.addColumn("Customer Name");
        model.addColumn("Fuel Type");
        model.addColumn("Quantity");
        model.addColumn("Price");
        model.addColumn("Transaction Date");
        model.addColumn("Edit");
        model.addColumn("Delete");
        jTable1.setModel(model);

         Action editAction = new AbstractAction("Edit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = Integer.parseInt(e.getActionCommand());
                int transactionId = (int) model.getValueAt(row, 0);
                showEditTransactionDialog(transactionId);
            }
        };

        Action deleteAction = new AbstractAction("Delete") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = Integer.parseInt(e.getActionCommand());
                int transactionId = (int) model.getValueAt(row, 0);
                showDeleteConfirmationDialog(transactionId);
            }
        };

        ButtonColumn editButton = new ButtonColumn(jTable1, editAction, 6);
        ButtonColumn deleteButton = new ButtonColumn(jTable1, deleteAction, 7);
    }

    private void showAddTransactionDialog() {
        // Create a modal dialog
        JDialog addDialog = new JDialog(this, "Add Transaction", true);
        addDialog.setLayout(new BorderLayout());

        // Create components
        JLabel customerIdLabel = new JLabel("Customer ID:");
        JComboBox<String> customerIdDropdown = new JComboBox<>();

        // Fetch customer data and populate the dropdown
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "SELECT customer_id, customer_name FROM CustomerV2";
            try (Statement statement = (Statement) connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    int customerId = resultSet.getInt("customer_id");
                    String customerName = resultSet.getString("customer_name");
                    ComboItem com = new ComboItem();
                    com.setCustomerLable(customerName);
                    com.setValue(String.valueOf(customerId));
                    customerIdDropdown.addItem(com.toString());
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel fuelTypeLabel = new JLabel("Fuel Type:");
        JComboBox<String> fuelTypeField = new JComboBox<String>();

        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "SELECT inventory_id, fuel_type FROM FuelInventory";
            try (Statement statement = (Statement) connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    int customerId = resultSet.getInt("inventory_id");
                    String customerName = resultSet.getString("fuel_type");
                    ComboItem com = new ComboItem();
                    com.setCustomerLable(customerName);
                    com.setValue(String.valueOf(customerId));
                    fuelTypeField.addItem(com.toString());
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField();

        // Set the transaction date as the current time
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        JButton addButton = new JButton("Add");

        // Add action listener for the Add button
        addButton.addActionListener(e -> {
            try {
                // Get selected customer ID from the dropdown
                /*ComboItem selectedCustomer = (ComboItem) customerIdDropdown.getSelectedItem();*/
                int customerId = getCustomerIdByName(customerIdDropdown.getSelectedItem().toString());

                // Get other input values
                String fuelType = fuelTypeField.getSelectedItem().toString();
                BigDecimal quantity = new BigDecimal(quantityField.getText());
                BigDecimal price = new BigDecimal(priceField.getText());

                // Call the addTransaction method with the input values and current timestamp
                addTransaction(1, fuelType, quantity, price, currentTimestamp);

                // Close the dialog after adding the transaction
                addDialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding transaction. Please check input values.");
            }
        });

        // Create a panel to hold components
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(customerIdLabel);
        panel.add(customerIdDropdown);
        panel.add(fuelTypeLabel);
        panel.add(fuelTypeField);
        panel.add(quantityLabel);
        panel.add(quantityField);
        panel.add(priceLabel);
        panel.add(priceField);
        panel.add(new JLabel("Transaction Date: " + currentTimestamp)); // Display current timestamp
        panel.add(addButton);

        // Add the panel to the dialog
        addDialog.add(panel, BorderLayout.CENTER);

        // Set dialog properties
        addDialog.setSize(400, 250);
        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }

    private void addTransaction(int customerId, String fuelType, BigDecimal quantity, BigDecimal price, Timestamp transactionDate) {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO TransactionV2 (customer_id, fuel_type, quantity, price, transaction_date) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerId);
                statement.setString(2, fuelType);
                statement.setBigDecimal(3, quantity);
                statement.setBigDecimal(4, price);
                statement.setTimestamp(5, transactionDate);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Transaction added successfully!");
                model.setRowCount(0); // Clear the table
                fetchTransactionData(); // Reload data into the table
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchTransactionData() {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "SELECT t.transaction_id, c.customer_name, t.fuel_type, t.quantity, t.price, t.transaction_date "
                    + "FROM TransactionV2 t "
                    + "JOIN CustomerV2 c ON t.customer_id = c.customer_id";
            try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    

                    Object[] rowData = {
                        resultSet.getInt("transaction_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("fuel_type"),
                        resultSet.getBigDecimal("quantity"),
                        resultSet.getBigDecimal("price"),
                        resultSet.getTimestamp("transaction_date"),
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

    private JButton createEditButton(int transactionId) {
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> showEditTransactionDialog(transactionId));
        return editButton;
    }

    private JButton createDeleteButton(int transactionId) {
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteTransaction(transactionId));
        return deleteButton;
    }

    private void showEditTransactionDialog(int transactionId) {
        // Fetch transaction details based on transactionId
        // You'll need to implement a method to fetch details similar to fetchTransactionData

        // Create a modal dialog
        JDialog editDialog = new JDialog(this, "Edit Transaction", true);
        editDialog.setLayout(new BorderLayout());

        // Create components and populate with fetched data
        JLabel customerIdLabel = new JLabel("Customer ID:");
        JComboBox<String> customerIdDropdown = new JComboBox<>();

        // Fetch customer data and populate the dropdown
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String customerSql = "SELECT customer_id, customer_name FROM CustomerV2";
            try (Statement customerStatement = (Statement) connection.createStatement(); ResultSet customerResultSet = customerStatement.executeQuery(customerSql)) {
                while (customerResultSet.next()) {
                    int customerId = customerResultSet.getInt("customer_id");
                    String customerName = customerResultSet.getString("customer_name");
                    ComboItem com = new ComboItem();
                    com.setCustomerLable(customerName);
                    com.setValue(String.valueOf(customerId));
                    customerIdDropdown.addItem(com.toString());
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel fuelTypeLabel = new JLabel("Fuel Type:");
        JComboBox<String> fuelTypeField = new JComboBox<String>();

        // Fetch fuel type data and populate the dropdown
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String fuelTypeSql = "SELECT inventory_id, fuel_type FROM FuelInventory";
            try (Statement fuelTypeStatement = (Statement) connection.createStatement(); ResultSet fuelTypeResultSet = fuelTypeStatement.executeQuery(fuelTypeSql)) {
                while (fuelTypeResultSet.next()) {
                    int inventoryId = fuelTypeResultSet.getInt("inventory_id");
                    String fuelType = fuelTypeResultSet.getString("fuel_type");
                    ComboItem com = new ComboItem();
                    com.setCustomerLable(fuelType);
                    com.setValue(String.valueOf(inventoryId));
                    fuelTypeField.addItem(com.toString());
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField();

        // Set the transaction date as the current time
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        JButton updateButton = new JButton("Update");

        // Add action listener for the Update button
        updateButton.addActionListener(e -> {
            // Update transaction based on the input values
            try {
                // Get selected customer ID from the dropdown
                int customerId = getCustomerIdByName(customerIdDropdown.getSelectedItem().toString());

                // Get other input values
                String fuelType = fuelTypeField.getSelectedItem().toString();
                BigDecimal quantity = new BigDecimal(quantityField.getText());
                BigDecimal price = new BigDecimal(priceField.getText());

                // Call the updateTransaction method with the input values and current timestamp
                updateTransaction(transactionId, customerId, fuelType, quantity, price, currentTimestamp);

                // Close the dialog after updating the transaction
                editDialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating transaction. Please check input values.");
            }
        });

        // Create a panel to hold components
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(customerIdLabel);
        panel.add(customerIdDropdown);
        panel.add(fuelTypeLabel);
        panel.add(fuelTypeField);
        panel.add(quantityLabel);
        panel.add(quantityField);
        panel.add(priceLabel);
        panel.add(priceField);
        panel.add(new JLabel("Transaction Date: " + currentTimestamp)); // Display current timestamp
        panel.add(updateButton);

        // Add the panel to the dialog
        editDialog.add(panel, BorderLayout.CENTER);

        // Set dialog properties
        editDialog.setSize(400, 250);
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    // Add this method to your class
    private int getCustomerIdByName(String customerName) {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "SELECT customer_id FROM CustomerV2 WHERE customer_name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, customerName);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("customer_id");
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if customer is not found or an error occurs
    }

    private void updateTransaction(int transactionId, int customerId, String fuelType, BigDecimal quantity, BigDecimal price, Timestamp transactionDate) {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "UPDATE TransactionV2 SET customer_id = ?, fuel_type = ?, quantity = ?, price = ?, transaction_date = ? WHERE transaction_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerId);
                statement.setString(2, fuelType);
                statement.setBigDecimal(3, quantity);
                statement.setBigDecimal(4, price);
                statement.setTimestamp(5, transactionDate);
                statement.setInt(6, transactionId);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Transaction updated successfully!");
                model.setRowCount(0); // Clear the table
                fetchTransactionData(); // Reload data into the table
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteTransaction(int transactionId) {
        try {
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "DELETE FROM TransactionV2 WHERE transaction_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, transactionId);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Transaction deleted successfully!");
                model.setRowCount(0); // Clear the table
                fetchTransactionData(); // Reload data into the table
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goBack() {
        DashboardUI dashboardUi = new DashboardUI();
        dashboardUi.setVisible(true);
        this.dispose();
    }

    private void editTransaction(int transactionId) {
        try {
            // Fetch the transaction details based on the transactionId
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM TransactionV2 WHERE transaction_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, transactionId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    // Extract details from the result set
                    int customerId = resultSet.getInt("customer_id");
                    String fuelType = resultSet.getString("fuel_type");
                    BigDecimal quantity = resultSet.getBigDecimal("quantity");
                    BigDecimal price = resultSet.getBigDecimal("price");
                    Timestamp transactionDate = resultSet.getTimestamp("transaction_date");

                    // Create a modal dialog for editing
                    JDialog editDialog = new JDialog(this, "Edit Transaction", true);
                    editDialog.setLayout(new BorderLayout());

                    // Create components similar to the add dialog
                    // ...
                    // Populate components with the fetched data
                    // ...
                    // Create an "Update" button
                    JButton updateButton = new JButton("Update");

                    // Add action listener for the Update button
                    updateButton.addActionListener(e -> {
                        try {
                            // Get updated values from the components
                            // ...

                            // Update the transaction using the updateTransaction method
                            updateTransaction(transactionId, customerId, fuelType, quantity, price, transactionDate);

                            // Close the edit dialog after updating the transaction
                            editDialog.dispose();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(this, "Error updating transaction. Please check input values.");
                        }
                    });

                    // Create a panel to hold components
                    JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
                    // Populate the panel with components similar to the add dialog
                    // ...

                    // Add the "Update" button to the panel
                    panel.add(new JLabel()); // Empty space
                    panel.add(updateButton);

                    // Add the panel to the edit dialog
                    editDialog.add(panel, BorderLayout.CENTER);

                    // Set dialog properties
                    editDialog.setSize(400, 300);
                    editDialog.setLocationRelativeTo(this);
                    editDialog.setVisible(true);
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showDeleteConfirmationDialog(int transactionId) {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this transaction?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            deleteTransaction(transactionId);
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        backButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 36)); // NOI18N
        jLabel1.setText("Transaction Management");

        backButton.setText("Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(153, 153, 153))
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 708, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(617, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(22, 22, 22)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(94, 94, 94)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(453, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        showAddTransactionDialog();
    }//GEN-LAST:event_addButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
       DashboardUI dashboard = new DashboardUI();
       dashboard.setVisible(true);
       this.dispose();
    }//GEN-LAST:event_backButtonActionPerformed

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
            java.util.logging.Logger.getLogger(TransactionUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TransactionUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TransactionUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TransactionUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TransactionUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton backButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}

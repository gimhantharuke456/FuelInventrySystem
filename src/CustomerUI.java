import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import com.widgets.ButtonColumn;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;



public class CustomerUI extends javax.swing.JFrame {
    DefaultTableModel model;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/fuel_station";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    public CustomerUI() {
        initComponents();
 
        initTable();
        fetchData();
    }

    private void initTable() {
        model = new DefaultTableModel();
        model.addColumn("Customer ID");
        model.addColumn("Customer Name");
        model.addColumn("Contact Info");
        model.addColumn("Loyalty Membership");
        model.addColumn("Credit Limit");
        model.addColumn("Edit");
        model.addColumn("Delete");
        jTable1.setModel(model);

        Action editAction = new AbstractAction("Edit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = Integer.parseInt(e.getActionCommand());
                int customerId = (int) model.getValueAt(row, 0);
                openEditModal(customerId);
            }
        };

        Action deleteAction = new AbstractAction("Delete") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = Integer.parseInt(e.getActionCommand());
                int customerId = (int) model.getValueAt(row, 0);
                showDeleteConfirmationDialog(customerId);
            }
        };

        ButtonColumn editButton = new ButtonColumn(jTable1, editAction, 5);
        ButtonColumn deleteButton = new ButtonColumn(jTable1, deleteAction, 6);
    }

    private void fetchData() {
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM CustomerV2";
            try (Statement statement = (Statement) connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    Object[] rowData = {
                        resultSet.getInt("customer_id"),
                        resultSet.getString("customer_name"),
                        resultSet.getString("contact_info"),
                        resultSet.getBoolean("loyalty_membership"),
                        resultSet.getBigDecimal("credit_limit"),
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

    private void addCustomer(String customerName, String contactInfo, boolean loyaltyMembership, BigDecimal creditLimit) {
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO CustomerV2 (customer_name, contact_info, loyalty_membership, credit_limit) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql)) {
                statement.setString(1, customerName);
                statement.setString(2, contactInfo);
                statement.setBoolean(3, loyaltyMembership);
                statement.setBigDecimal(4, creditLimit);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Customer added successfully!");
                model.setRowCount(0); // Clear the table
                fetchData(); // Reload data into the table
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openEditModal(int customerId) {
        // Fetch data based on customerId and populate the modal fields
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM CustomerV2 WHERE customer_id = ?";
            try (PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql)) {
                statement.setInt(1, customerId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    // Retrieve data from the result set
                    String customerName = resultSet.getString("customer_name");
                    String contactInfo = resultSet.getString("contact_info");
                    boolean loyaltyMembership = resultSet.getBoolean("loyalty_membership");
                    BigDecimal creditLimit = resultSet.getBigDecimal("credit_limit");

                    // Create a JDialog for editing
                    JDialog editCustomerDialog = new JDialog(this, "Edit Customer", true);
                    editCustomerDialog.setSize(400, 200);
                    editCustomerDialog.setLayout(new BorderLayout());

                    // JPanel to hold input fields
                    JPanel inputPanel = new JPanel(new GridLayout(4, 2));

                    // Input fields
                    JTextField customerNameField = new JTextField(customerName);
                    JTextField contactInfoField = new JTextField(contactInfo);
                    JComboBox<String> loyaltyMembershipField = new JComboBox<>(new String[]{"Yes", "No"});
                    loyaltyMembershipField.setSelectedItem(loyaltyMembership ? "Yes" : "No");
                    JTextField creditLimitField = new JTextField(String.valueOf(creditLimit));

                    // Labels for input fields
                    JLabel customerNameLabel = new JLabel("Customer Name:");
                    JLabel contactInfoLabel = new JLabel("Contact Info:");
                    JLabel loyaltyMembershipLabel = new JLabel("Loyalty Membership:");
                    JLabel creditLimitLabel = new JLabel("Credit Limit:");

                    // Add components to the input panel
                    inputPanel.add(customerNameLabel);
                    inputPanel.add(customerNameField);
                    inputPanel.add(contactInfoLabel);
                    inputPanel.add(contactInfoField);
                    inputPanel.add(loyaltyMembershipLabel);
                    inputPanel.add(loyaltyMembershipField);
                    inputPanel.add(creditLimitLabel);
                    inputPanel.add(creditLimitField);

                    // JPanel for buttons
                    JPanel buttonPanel = new JPanel();

                    // Update button
                    JButton updateButton = new JButton("Update");
                    updateButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            // TODO: Add logic to update data into the database
                            updateCustomer(customerId, customerNameField.getText(), contactInfoField.getText(),
                                    loyaltyMembershipField.getSelectedItem() == "Yes",
                                    new BigDecimal(creditLimitField.getText()));
                            editCustomerDialog.dispose();  // Close the dialog
                        }
                    });

                    // Cancel button
                    JButton cancelButton = new JButton("Cancel");
                    cancelButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            editCustomerDialog.dispose();  // Close the dialog
                        }
                    });

                    // Add buttons to the button panel
                    buttonPanel.add(updateButton);
                    buttonPanel.add(cancelButton);

                    // Add panels to the dialog
                    editCustomerDialog.add(inputPanel, BorderLayout.CENTER);
                    editCustomerDialog.add(buttonPanel, BorderLayout.SOUTH);

                    // Set dialog location relative to the main frame
                    editCustomerDialog.setLocationRelativeTo(this);

                    // Set the dialog visible
                    editCustomerDialog.setVisible(true);
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteConfirmationDialog(int customerId) {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            deleteCustomer(customerId);
        }
    }

    private void deleteCustomer(int customerId) {
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "DELETE FROM CustomerV2 WHERE customer_id = ?";
            try (PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql)) {
                statement.setInt(1, customerId);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!");
                model.setRowCount(0); // Clear the table
                fetchData(); // Reload data into the table
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCustomer(int customerId, String customerName, String contactInfo, boolean loyaltyMembership, BigDecimal creditLimit) {
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "UPDATE CustomerV2 SET customer_name = ?, contact_info = ?, loyalty_membership = ?, credit_limit = ? WHERE customer_id = ?";
            try (PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql)) {
                statement.setString(1, customerName);
                statement.setString(2, contactInfo);
                statement.setBoolean(3, loyaltyMembership);
                statement.setBigDecimal(4, creditLimit);
                statement.setInt(5, customerId);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Customer updated successfully!");
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
        jSeparator1 = new javax.swing.JSeparator();
        BackButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 36)); // NOI18N
        jLabel1.setText("Customer Management");

        BackButton.setText("Back");
        BackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackButtonActionPerformed(evt);
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(160, 160, 160))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 653, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 677, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(36, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(587, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(35, 35, 35)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BackButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(101, 101, 101)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(453, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
          showAddCustomerDialog();
    }//GEN-LAST:event_addButtonActionPerformed
   private void showAddCustomerDialog() {
        // Create JDialog for Add Customer
        JDialog addCustomerDialog = new JDialog(this, "Add Customer", true);
        addCustomerDialog.setSize(400, 200);
        addCustomerDialog.setLayout(new BorderLayout());

        // JPanel to hold input fields
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));

        // Input fields
        JTextField customerNameField = new JTextField();
        JTextField contactInfoField = new JTextField();
        JComboBox<String> loyaltyMembershipField = new JComboBox<>(new String[]{"Yes", "No"});
        JTextField creditLimitField = new JTextField();

        // Labels for input fields
        JLabel customerNameLabel = new JLabel("Customer Name:");
        JLabel contactInfoLabel = new JLabel("Contact Info:");
        JLabel loyaltyMembershipLabel = new JLabel("Loyalty Membership:");
        JLabel creditLimitLabel = new JLabel("Credit Limit:");

        // Add components to the input panel
        inputPanel.add(customerNameLabel);
        inputPanel.add(customerNameField);
        inputPanel.add(contactInfoLabel);
        inputPanel.add(contactInfoField);
        inputPanel.add(loyaltyMembershipLabel);
        inputPanel.add(loyaltyMembershipField);
        inputPanel.add(creditLimitLabel);
        inputPanel.add(creditLimitField);

        // JPanel for buttons
        JPanel buttonPanel = new JPanel();

        // Add button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCustomer(customerNameField.getText(), contactInfoField.getText(),
                        loyaltyMembershipField.getSelectedItem() == "Yes",
                        new BigDecimal(creditLimitField.getText()));
                addCustomerDialog.dispose();  // Close the dialog
            }
        });

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCustomerDialog.dispose();  // Close the dialog
            }
        });

        // Add buttons to the button panel
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Add panels to the dialog
        addCustomerDialog.add(inputPanel, BorderLayout.CENTER);
        addCustomerDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog location relative to the main frame
        addCustomerDialog.setLocationRelativeTo(this);

        // Set the dialog visible
        addCustomerDialog.setVisible(true);
    }
    private void BackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackButtonActionPerformed
        DashboardUI dashboardUi = new DashboardUI();
        dashboardUi.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_BackButtonActionPerformed

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
            java.util.logging.Logger.getLogger(CustomerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CustomerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CustomerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CustomerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CustomerUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BackButton;
    private javax.swing.JButton addButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}

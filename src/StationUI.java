
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


public class StationUI extends javax.swing.JFrame {

      DefaultTableModel model;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/fuel_station";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    public StationUI() {
        initComponents();
         initTable();
        fetchData();
    }

     private void initTable() {
        model = new DefaultTableModel();
        model.addColumn("Station ID");
        model.addColumn("Station Name");
        model.addColumn("Location");
        model.addColumn("Contact Details");
        model.addColumn("Edit");
        model.addColumn("Delete");
        jTable1.setModel(model);

        Action editAction = new AbstractAction("Edit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = Integer.parseInt(e.getActionCommand());
                int stationId = (int) model.getValueAt(row, 0);
                openEditModal(stationId);
            }
        };

        Action deleteAction = new AbstractAction("Delete") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = Integer.parseInt(e.getActionCommand());
                int stationId = (int) model.getValueAt(row, 0);
                showDeleteConfirmationDialog(stationId);
            }
        };

        ButtonColumn editButton = new ButtonColumn(jTable1, editAction, 4);
        ButtonColumn deleteButton = new ButtonColumn(jTable1, deleteAction, 5);
    }

    private void fetchData() {
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM FuelStationV2";
            try (Statement statement = (Statement) connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    Object[] rowData = {
                        resultSet.getInt("station_id"),
                        resultSet.getString("station_name"),
                        resultSet.getString("location"),
                        resultSet.getString("contact_details"),
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

    private void openEditModal(int stationId) {
        // Fetch data based on stationId and populate the modal fields
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "SELECT * FROM FuelStationV2 WHERE station_id = ?";
            try (PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql)) {
                statement.setInt(1, stationId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    // Retrieve data from the result set
                    String stationName = resultSet.getString("station_name");
                    String location = resultSet.getString("location");
                    String contactDetails = resultSet.getString("contact_details");

                    // Create a JDialog for editing
                    JDialog editStationDialog = new JDialog(this, "Edit Station", true);
                    editStationDialog.setSize(400, 200);
                    editStationDialog.setLayout(new BorderLayout());

                    // JPanel to hold input fields
                    JPanel inputPanel = new JPanel(new GridLayout(3, 2));

                    // Input fields
                    JTextField stationNameField = new JTextField(stationName);
                    JTextField locationField = new JTextField(location);
                    JTextField contactDetailsField = new JTextField(contactDetails);

                    // Labels for input fields
                    JLabel stationNameLabel = new JLabel("Station Name:");
                    JLabel locationLabel = new JLabel("Location:");
                    JLabel contactDetailsLabel = new JLabel("Contact Details:");

                    // Add components to the input panel
                    inputPanel.add(stationNameLabel);
                    inputPanel.add(stationNameField);
                    inputPanel.add(locationLabel);
                    inputPanel.add(locationField);
                    inputPanel.add(contactDetailsLabel);
                    inputPanel.add(contactDetailsField);

                    // JPanel for buttons
                    JPanel buttonPanel = new JPanel();

                    // Update button
                    JButton updateButton = new JButton("Update");
                    updateButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            // TODO: Add logic to update data into the database
                            updateStation(stationId, stationNameField.getText(), locationField.getText(), contactDetailsField.getText());
                            editStationDialog.dispose();  // Close the dialog
                        }
                    });

                    // Cancel button
                    JButton cancelButton = new JButton("Cancel");
                    cancelButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            editStationDialog.dispose();  // Close the dialog
                        }
                    });

                    // Add buttons to the button panel
                    buttonPanel.add(updateButton);
                    buttonPanel.add(cancelButton);

                    // Add panels to the dialog
                    editStationDialog.add(inputPanel, BorderLayout.CENTER);
                    editStationDialog.add(buttonPanel, BorderLayout.SOUTH);

                    // Set dialog location relative to the main frame
                    editStationDialog.setLocationRelativeTo(this);

                    // Set the dialog visible
                    editStationDialog.setVisible(true);
                }
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteConfirmationDialog(int stationId) {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this record?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            deleteStation(stationId);
        }
    }

    private void deleteStation(int stationId) {
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "DELETE FROM FuelStationV2 WHERE station_id = ?";
            try (PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql)) {
                statement.setInt(1, stationId);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Station deleted successfully!");
                model.setRowCount(0); // Clear the table
                fetchData(); // Reload data into the table
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStation(int stationId, String stationName, String location, String contactDetails) {
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "UPDATE FuelStationV2 SET station_name = ?, location = ?, contact_details = ? WHERE station_id = ?";
            try (PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql)) {
                statement.setString(1, stationName);
                statement.setString(2, location);
                statement.setString(3, contactDetails);
                statement.setInt(4, stationId);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Station updated successfully!");
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
        backButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 36)); // NOI18N
        jLabel1.setText("Station Management");

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
                .addGap(187, 187, 187))
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 696, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(595, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(24, 24, 24)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(91, 91, 91)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(447, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
     DashboardUI dashboard= new DashboardUI();
     dashboard.setVisible(true);
     this.dispose();
    }//GEN-LAST:event_backButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        showAddStationDialog();
    }//GEN-LAST:event_addButtonActionPerformed
  private void showAddStationDialog() {
        // Create JDialog for Add Station
        JDialog addStationDialog = new JDialog(this, "Add Station", true);
        addStationDialog.setSize(400, 200);
        addStationDialog.setLayout(new BorderLayout());

        // JPanel to hold input fields
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));

        // Input fields
        JTextField stationNameField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField contactDetailsField = new JTextField();

        // Labels for input fields
        JLabel stationNameLabel = new JLabel("Station Name:");
        JLabel locationLabel = new JLabel("Location:");
        JLabel contactDetailsLabel = new JLabel("Contact Details:");

        // Add components to the input panel
        inputPanel.add(stationNameLabel);
        inputPanel.add(stationNameField);
        inputPanel.add(locationLabel);
        inputPanel.add(locationField);
        inputPanel.add(contactDetailsLabel);
        inputPanel.add(contactDetailsField);

        // JPanel for buttons
        JPanel buttonPanel = new JPanel();

        // Add button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStation(stationNameField.getText(), locationField.getText(), contactDetailsField.getText());
                addStationDialog.dispose();  // Close the dialog
            }
        });

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStationDialog.dispose();  // Close the dialog
            }
        });

        // Add buttons to the button panel
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // Add panels to the dialog
        addStationDialog.add(inputPanel, BorderLayout.CENTER);
        addStationDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Set dialog location relative to the main frame
        addStationDialog.setLocationRelativeTo(this);

        // Set the dialog visible
        addStationDialog.setVisible(true);
    }

    private void addStation(String stationName, String location, String contactDetails) {
        try {
            Connection connection = (Connection) DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            String sql = "INSERT INTO FuelStationV2 (station_name, location, contact_details) VALUES (?, ?, ?)";
            try (PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql)) {
                statement.setString(1, stationName);
                statement.setString(2, location);
                statement.setString(3, contactDetails);
                statement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Station added successfully!");
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
            java.util.logging.Logger.getLogger(StationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StationUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StationUI().setVisible(true);
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

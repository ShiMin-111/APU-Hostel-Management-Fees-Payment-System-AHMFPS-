package ahmfps;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ManagePaymentStaff {

    JFrame frame;
    JTable residentTable;
    JComboBox<String> durationBox;
    JButton processPaymentButton, backButton, viewRecordButton, searchButton;
    JTextField feeField, searchField;
    JLabel receiptLabel;
    JScrollPane receiptScrollPane; 

    public ManagePaymentStaff() {
        frame = new JFrame("Manage Payments - Staff");
        frame.setSize(800, 700);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Manage Payments", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 10, 800, 30);
        panel.add(titleLabel);
        
        JLabel feeLabel = new JLabel("Management Fee Rate (RM per month):");
        feeLabel.setBounds(20, 50, 250, 30);
        panel.add(feeLabel);

        feeField = new JTextField();
        feeField.setBounds(280, 50, 100, 30);
        panel.add(feeField);
        feeField.setEditable(false);

        loadCurrentFee(); // Load the latest fee on initialization

        // Payment duration dropdown
        JLabel durationLabel = new JLabel("Select Payment Duration:");
        durationLabel.setBounds(20, 100, 200, 30);
        panel.add(durationLabel);

        durationBox = new JComboBox<>(new String[]{"1 Month", "3 Months", "9 Months", "1 Year"});
        durationBox.setBounds(220, 100, 160, 30);
        panel.add(durationBox);

        // Search field and button
        JLabel searchLabel = new JLabel("Search by Username:");
        searchLabel.setBounds(20, 150, 200, 30);
        panel.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(220, 150, 160, 30);
        panel.add(searchField);

        searchButton = new JButton("Search");
        searchButton.setBounds(400, 150, 120, 30);
        searchButton.addActionListener(e -> loadResidents(searchField.getText().trim()));
        panel.add(searchButton);

        // Residents table
        residentTable = new JTable(new DefaultTableModel(new Object[]{"Username", "Last Payment", "Next Due", "Payment Status"}, 0));
        JScrollPane tableScroll = new JScrollPane(residentTable);
        tableScroll.setBounds(20, 200, 750, 300);
        panel.add(tableScroll);

        // Load residents into table
        loadResidents(null);

        // Process payment button
        processPaymentButton = new JButton("Process Payment");
        processPaymentButton.setBounds(20, 520, 200, 30);
        processPaymentButton.addActionListener(e -> processPayment());
        panel.add(processPaymentButton);
        
        // View Record button
        viewRecordButton = new JButton("View Customer Payment Record");
        viewRecordButton.setBounds(230, 520, 250, 30);
        viewRecordButton.addActionListener(e -> viewRecord());
        panel.add(viewRecordButton);

        // Receipt label
        receiptLabel = new JLabel("Receipt will appear here.");
        receiptLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        
        // Create a scroll pane for the receipt label
        receiptScrollPane = new JScrollPane(receiptLabel);
        receiptScrollPane.setBounds(20, 570, 200, 70); 
        panel.add(receiptScrollPane); // Add the scroll pane to the panel

        // Back button
        backButton = new JButton("Back");
        backButton.setBounds(650, 620, 100, 30);
        backButton.addActionListener(e -> {
            frame.dispose();
            new StaffPage();  
        });
        panel.add(backButton);

        frame.add(panel);
        frame.setVisible(true);
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                new StaffPage();  
            }
        });
    }
    
    private void viewRecord(){
        int selectedRow = residentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a resident to view his/her payment record.");
            return;
        }

        String username = residentTable.getValueAt(selectedRow, 0).toString();
        Users user = DataIO.checkUserName(username);

        if (user == null) {
            JOptionPane.showMessageDialog(frame, "User not found.");
            return;
        }
        
        new PaymentRecordsPage(username, frame);  
        
    }

    private void loadResidents(String searchQuery) {
        DefaultTableModel model = (DefaultTableModel) residentTable.getModel();
        model.setRowCount(0); // Clear existing rows

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        // First, add users with room assigned
        for (Users user: DataIO.allUsers) {
            if ("Resident".equals(user.getUserType()) && "Approved".equals(user.getStatus()) && hasRoomAssigned(user.getName())) {
                if (searchQuery!= null &&!searchQuery.isEmpty() &&!user.getName().contains(searchQuery)) {
                    continue;
                }
                String lastPaymentDate = "N/A";
                String nextDueDate = "N/A";
                if (user.getPaymentHistory()!= null &&!user.getPaymentHistory().isEmpty()) {
                    PaymentData lastPayment = user.getPaymentHistory().get(user.getPaymentHistory().size() - 1);
                    lastPaymentDate = sdf.format(lastPayment.getDate());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(lastPayment.getDate());
                    calendar.add(Calendar.MONTH, lastPayment.getDurationInMonths());
                    nextDueDate = sdf.format(calendar.getTime());
                    if (calendar.before(Calendar.getInstance())) {
                        model.addRow(new Object[]{user.getName(), lastPaymentDate, nextDueDate, "<html><font color='red'>OVERDUE</font></html>"});
                    } else {
                        model.addRow(new Object[]{user.getName(), lastPaymentDate, nextDueDate, user.getPaymentStatus()});
                    }
                } else {
                    model.addRow(new Object[]{user.getName(), lastPaymentDate, nextDueDate, user.getPaymentStatus()});
                }
            }
        }

        // Then, add the rest of the users (with no assigned rooms)
        for (Users user: DataIO.allUsers) {
            if ("Resident".equals(user.getUserType()) && "Approved".equals(user.getStatus()) &&!hasRoomAssigned(user.getName())) {
                if (searchQuery!= null &&!searchQuery.isEmpty() &&!user.getName().contains(searchQuery)) {
                    continue;
                }
                String lastPaymentDate = "N/A";
                String nextDueDate = "N/A";
                if (user.getPaymentHistory()!= null &&!user.getPaymentHistory().isEmpty()) {
                    PaymentData lastPayment = user.getPaymentHistory().get(user.getPaymentHistory().size() - 1);
                    lastPaymentDate = sdf.format(lastPayment.getDate());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(lastPayment.getDate());
                    calendar.add(Calendar.MONTH, lastPayment.getDurationInMonths());
                    nextDueDate = sdf.format(calendar.getTime());
                }
                model.addRow(new Object[]{user.getName(), lastPaymentDate, nextDueDate, "<html><font color='gray'>Unavailable</font></html>"});
            }
        }
        // Make the table non-editable
        residentTable.setDefaultEditor(Object.class, null);
    }
    
        // check if a user has a room assigned
        private boolean hasRoomAssigned(String username) {
            return DataIO.allRooms.stream()
                  .anyMatch(room -> room.getResident()!= null && room.getResident().equals(username));
        }
    
    private int getDurationInMonths() {
        String selectedDuration = (String) durationBox.getSelectedItem();
        switch (selectedDuration) {
            case "3 Months":
                return 3;
            case "9 Months":
                return 9;
            case "1 Year":
                return 12;
            default: // 1 Month
                return 1;
        }
    }

    private void processPayment() {
        int selectedRow = residentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a resident to process payment.");
            return;
        }

        String username = residentTable.getValueAt(selectedRow, 0).toString();
        Users user = DataIO.checkUserName(username);

        if (user == null) {
            JOptionPane.showMessageDialog(frame, "User not found.");
            return;
        }
        
        if(!hasRoomAssigned(username)){
            JOptionPane.showMessageDialog(frame, "Cannot process payment. User does not have a room assigned.");
            return;
        }
        
        // Calculate the total months due (including overdue months)
        int totalMonthsDue = getDurationInMonths(); // Start with the selected duration
        if (user.getPaymentHistory() != null && !user.getPaymentHistory().isEmpty()) {
            PaymentData lastPayment = user.getPaymentHistory().get(user.getPaymentHistory().size() - 1);

            Calendar expectedDueDate = Calendar.getInstance();
            expectedDueDate.setTime(lastPayment.getDate());
            expectedDueDate.add(Calendar.MONTH, lastPayment.getDurationInMonths());

            if (Calendar.getInstance().after(expectedDueDate)) {
                // Calculate overdue months
                long overdueMillis = Calendar.getInstance().getTimeInMillis() - expectedDueDate.getTimeInMillis();
                long overdueMonths = overdueMillis / (1000L * 60 * 60 * 24 * 30); // Approximate months
                totalMonthsDue += overdueMonths; // Add overdue months to the total
            }
            
            if (Calendar.getInstance().before(expectedDueDate)) {
                JOptionPane.showMessageDialog(frame, "Payment already processed for the selected duration.\nNext payment due on: " +
                        new SimpleDateFormat("dd-MM-yyyy").format(expectedDueDate.getTime()));
                return;
            }
        }

        // Retrieve the latest management fee
        double managementFee = 0.0;
        if (!DataIO.allFee.isEmpty()) {
            ManagementFee latestFee = DataIO.allFee.get(DataIO.allFee.size() - 1);
            managementFee = latestFee.getManagementFee();
        }

        // Ensure correct amount calculation based on selected duration and management fee
        double amountToPay = managementFee * totalMonthsDue;

        // Add new payment data
        PaymentData newPayment = new PaymentData(Calendar.getInstance().getTime(), amountToPay, getDurationInMonths());
        user.addPayment(newPayment);

        // Update the user payment status to "Paid"
        user.setPaymentStatus("Paid");


        DataIO.write();

        // Update receipt label with payment details
        receiptLabel.setText(String.format("<html><body>Payment Receipt<br>-------------------<br>Resident: %s<br>Duration: %s<br>Amount Paid: RM %.2f<br>Total Months Paid: %d<br>Thank you!</body></html>",
            username, durationBox.getSelectedItem(), amountToPay, totalMonthsDue));

        JOptionPane.showMessageDialog(frame, "Payment processed successfully!");

        loadResidents(null);
    }
    
    private void loadCurrentFee() {
        if (!DataIO.allFee.isEmpty()) {
            ManagementFee latestFee = DataIO.allFee.get(DataIO.allFee.size() - 1);
            feeField.setText(String.format("%.2f", latestFee.getManagementFee()));
        }
    }

}

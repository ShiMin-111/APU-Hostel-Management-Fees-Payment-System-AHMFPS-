package ahmfps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;

public class PaymentRecordsPage {

    JFrame frame;
    JTable paymentTable;
    JButton backButton;

    public PaymentRecordsPage(String username, JFrame previousPage) {
        // Frame setup
        frame = new JFrame("Resident Payment Records");
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        // Panel setup
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel(username + "'s Payment Records", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Payment records table
        paymentTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(paymentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        
        // Load payment records
        loadPaymentRecords(username, previousPage);

        // Back button
        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.dispose();
            previousPage.setVisible(true);
        });
        panel.add(backButton, BorderLayout.SOUTH);

        frame.add(panel);
        
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                previousPage.setVisible(true);
            }
        });
    }

    private void loadPaymentRecords(String username, JFrame previousPage) {
        Users user = DataIO.checkUserName(username); // Get the user object

        if (user == null) {
            JOptionPane.showMessageDialog(frame, "User not found.");
            return;
        }

        if (user.getPaymentHistory().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No payment records found for this user.");
            frame.setVisible(false);
            return;  
        }

        frame.setVisible(true);
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Payment Date", "Duration (months)", "Total Paid (RM)"}, 0){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        for (PaymentData payment: user.getPaymentHistory()) {
            tableModel.addRow(new Object[]{
                    sdf.format(payment.getDate()),
                    payment.getDurationInMonths(),
                    String.format("%.2f", payment.getAmount())
            });
        }

        paymentTable.setModel(tableModel);
    }
    
    
}

package ahmfps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;

public class StaffPage implements ActionListener {
    JFrame staffFrame;
    JButton managePaymentsButton, manageUserButton, logoutButton;

    public StaffPage() {
        // Initialize the frame
        staffFrame = new JFrame("Staff Page");
        staffFrame.setSize(400, 300);
        staffFrame.setLocationRelativeTo(null);
        
        // Create panel for layout
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title label
        JLabel titleLabel = new JLabel("Staff Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel);
        
        // Manage Payments Button
        managePaymentsButton = new JButton("Manage Payments");
        managePaymentsButton.setFont(new Font("Arial", Font.BOLD, 16));
        managePaymentsButton.addActionListener(this);
        panel.add(managePaymentsButton);
        
        // manageUser
        manageUserButton = new JButton("Manage User");
        manageUserButton.setFont(new Font("Arial", Font.BOLD, 16));
        manageUserButton.addActionListener(this);
        panel.add(manageUserButton);
        
        // Logout Button
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.addActionListener(this);
        panel.add(logoutButton);

        // Add panel to frame
        staffFrame.add(panel);
        staffFrame.setVisible(true);
        
        staffFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                AHMFPS.logoutTime = LocalDateTime.now(); // Record logout time
                DataIO.writelog(); // Save audit log
                JOptionPane.showMessageDialog(null, "You have been logged out. See you next time!");
                staffFrame.dispose();
                new MainPage();
                }
            });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == managePaymentsButton) {
            new ManagePaymentStaff();
            staffFrame.setVisible(false);
        } else if (e.getSource() == manageUserButton) {
            new ManageUserStaff();
            staffFrame.setVisible(false);
        }else if (e.getSource() == logoutButton) {
                AHMFPS.logoutTime = LocalDateTime.now(); // Record logout time
                DataIO.writelog(); // Save audit log
                JOptionPane.showMessageDialog(null, "You have been logged out. See you next time!");
                staffFrame.dispose();
                new MainPage();
            }
        }
    }
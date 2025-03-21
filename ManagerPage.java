package ahmfps;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import javax.swing.*;

public class ManagerPage implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
        try {
            if (ae.getSource() == approval) {
                new PendingUsersPage(); // Navigate to the Pending Users page
                x.setVisible(false);
            } else if (ae.getSource() == managePayment) {
                new ManagePaymentPage(); // Navigate to the Manage Payment page
                x.setVisible(false);
            } else if (ae.getSource() == auditLog) {
                new AuditLog(); // Navigate to the Audit Log page
                x.setVisible(false);
            } else if (ae.getSource() == manageRooms) {
                new ManageRoomsPage(); // Navigate to the Manage Rooms page
                x.setVisible(false);
            } else if (ae.getSource() == manageUser) {
                x.setVisible(false);
                new ManageUserPage();
            }else if (ae.getSource() == logout) {
                x.dispose(); // Close the Manager Dashboard
                AHMFPS.logoutTime = LocalDateTime.now(); // Record logout time
                DataIO.writelog(); // Save audit log
                JOptionPane.showMessageDialog(null, "You have been logged out. See you next time!");
                new MainPage();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(x, "Invalid input, try again");
        }
    }

    JFrame x;
    JButton approval, managePayment, auditLog, manageRooms, manageUser, logout;

    public ManagerPage() {
        x = new JFrame("Manager Page");
        x.setSize(400, 580); 
        x.setLocationRelativeTo(null);

        // Create a gradient panel for the background
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.BLUE, getWidth(), getHeight(), Color.MAGENTA);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(null);

        // Title Label
        JLabel titleLabel = new JLabel("Manager Page", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 29));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(100, 30, 200, 40);
        gradientPanel.add(titleLabel);

        // Approval Button
        approval = new JButton("Approval");
        approval.setBounds(50, 120, 300, 40);
        approval.setBackground(new Color(0, 150, 255));
        approval.setForeground(Color.WHITE);
        approval.setFont(new Font("Arial", Font.BOLD, 16));
        approval.addActionListener(this);
        gradientPanel.add(approval);

        // Manage Payment Button
        managePayment = new JButton("Manage Payment");
        managePayment.setBounds(50, 180, 300, 40);
        managePayment.setBackground(new Color(0, 200, 100));
        managePayment.setForeground(Color.WHITE);
        managePayment.setFont(new Font("Arial", Font.BOLD, 16));
        managePayment.addActionListener(this);
        gradientPanel.add(managePayment);

        // Audit Log Button
        auditLog = new JButton("Audit Log");
        auditLog.setBounds(50, 240, 300, 40);
        auditLog.setBackground(new Color(0, 255, 255));
        auditLog.setForeground(Color.WHITE);
        auditLog.setFont(new Font("Arial", Font.BOLD, 16));
        auditLog.addActionListener(this);
        gradientPanel.add(auditLog);

        // Manage Rooms Button
        manageRooms = new JButton("Manage Rooms");
        manageRooms.setBounds(50, 300, 300, 40);
        manageRooms.setBackground(new Color(255, 150, 0));
        manageRooms.setForeground(Color.WHITE);
        manageRooms.setFont(new Font("Arial", Font.BOLD, 16));
        manageRooms.addActionListener(this);
        gradientPanel.add(manageRooms);
        
        // Manage user Button
        manageUser = new JButton("Manage User");
        manageUser.setBounds(50, 360, 300, 40);
        manageUser.setBackground(new Color(224, 176, 255));
        manageUser.setForeground(Color.WHITE);
        manageUser.setFont(new Font("Arial", Font.BOLD, 16));
        manageUser.addActionListener(this);
        gradientPanel.add(manageUser);

        // Logout Button
        logout = new JButton("Log Out");
        logout.setBounds(50, 420, 300, 40);
        logout.setBackground(new Color(255, 50, 50));
        logout.setForeground(Color.WHITE);
        logout.setFont(new Font("Arial", Font.BOLD, 16));
        logout.addActionListener(this);
        gradientPanel.add(logout);

        // Add the gradient panel to the JFrame
        x.add(gradientPanel);
        
        x.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                AHMFPS.logoutTime = LocalDateTime.now(); // Record logout time
                DataIO.writelog();// Write the audit log
                JOptionPane.showMessageDialog(null, "You have been logged out. See you next time!");
                x.dispose();
                new MainPage();
            }
        });
    }
}

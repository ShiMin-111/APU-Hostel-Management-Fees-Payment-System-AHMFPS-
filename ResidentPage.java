package ahmfps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

public class ResidentPage implements ActionListener{
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewPaymentRecordsButton) {
            new PaymentRecordsPage(username, frame);  
        } else if (e.getSource() == editProfileButton) {
            frame.dispose();
            new EditProfilePage(username);  
        } else if (e.getSource() == logoutButton) {
            frame.dispose();
            AHMFPS.logoutTime = LocalDateTime.now(); // Record logout time
            DataIO.writelog(); // Save audit log
            JOptionPane.showMessageDialog(null, "You have been logged out. See you next time!");  
            new MainPage();
        }
    }

    JFrame frame;
    JButton viewPaymentRecordsButton, editProfileButton, logoutButton;
    String username; 

    public ResidentPage (String username)  {
        this.username = username; 
         
        // Frame setup
        frame = new JFrame("Resident Page");
        frame.setSize(400, 350);
        frame.setLocationRelativeTo(null);

        // Panel setup
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Resident Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Center panel with buttons
        JPanel centre = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        
        viewPaymentRecordsButton = new JButton("View Payment Records");
        editProfileButton = new JButton("Edit Profile");
        logoutButton = new JButton("Logout");

        viewPaymentRecordsButton.addActionListener(this);
        editProfileButton.addActionListener(this);
        logoutButton.addActionListener(this);

        // Add buttons to the center panel
        centre.add(viewPaymentRecordsButton);
        centre.add(editProfileButton);
        panel.add(centre, BorderLayout.CENTER);

        // Logout button at the bottom
        panel.add(logoutButton, BorderLayout.SOUTH);
        
        viewPaymentRecordsButton.setPreferredSize(new Dimension(300, 50)); // Width: 300, Height: 50
        editProfileButton.setPreferredSize(new Dimension(300, 50));

        frame.add(panel);
    }
    

}
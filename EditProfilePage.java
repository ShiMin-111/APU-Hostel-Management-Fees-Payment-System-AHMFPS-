package ahmfps;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class EditProfilePage implements ActionListener {
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            // Update the resident's password
            String newPassword = new String(passwordField.getPassword()).trim();

            if (newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(x, "Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Save changes
            user.setPassword(newPassword);

            try {
                DataIO.write(); 
                DataIO.read(); 
                JOptionPane.showMessageDialog(x, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error saving data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == cancelButton) {
            x.dispose(); // Close the Edit Profile page without saving
            AHMFPS.fourth.frame.setVisible(true);
        }
    }

    JFrame x;
    JTextField usernameField, emailField;
    JPasswordField passwordField;
    JButton saveButton, cancelButton;
    Users user;

    public EditProfilePage(String username) {
        user = DataIO.checkUserName(username);

        if (user!= null) {
            // Frame setup
            x = new JFrame("Edit Profile");
            x.setSize(400, 300);
            x.setLocationRelativeTo(null);

            // Panel for form
            JPanel formPanel = new JPanel(new GridLayout(3, 3, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Username field (non-editable)
            formPanel.add(new JLabel("Username:"));
            usernameField = new JTextField(user.getName());
            usernameField.setEditable(false);
            formPanel.add(usernameField);
            
            // Username field (non-editable)
            formPanel.add(new JLabel("Email:"));
            emailField = new JTextField(user.getEmail());
            emailField.setEditable(false);
            formPanel.add(emailField);

            // Password field
            formPanel.add(new JLabel("New Password:"));
            passwordField = new JPasswordField(user.getPassword()); // Set the user's current password here
            formPanel.add(passwordField);

            // Buttons
            saveButton = new JButton("Save");
            saveButton.addActionListener(this);

            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            // Add panels to frame
            x.add(formPanel, BorderLayout.CENTER);
            x.add(buttonPanel, BorderLayout.SOUTH);

            x.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        x.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                AHMFPS.fourth.frame.setVisible(true);
            }
        });
    }
}
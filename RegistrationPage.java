package ahmfps;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.*;
import java.util.List;

public class RegistrationPage implements ActionListener, Rooms.RoomUpdateListener {

    JFrame frame;
    JTextField usernameField, emailField;
    JPasswordField passwordField, confirmPasswordField;
    JButton submitButton, cancelButton;
    JComboBox<String> userTypeComboBox, roomComboBox;

    public RegistrationPage() {
        frame = new JFrame("Registration Page");
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);

        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.BLUE, 0, getHeight(), Color.MAGENTA);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Register", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gradientPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gradientPanel.add(createLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        gradientPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gradientPanel.add(createLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(15);
        gradientPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gradientPanel.add(createLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        gradientPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gradientPanel.add(createLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(15);
        gradientPanel.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gradientPanel.add(createLabel("User Type:"), gbc);

        gbc.gridx = 1;
        String[] userTypes = {"Resident", "Staff", "Manager"};
        userTypeComboBox = new JComboBox<>(userTypes);
        gradientPanel.add(userTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel roomLabel = createLabel("Select Room:");
        gradientPanel.add(roomLabel, gbc);
        
        gbc.gridx = 1;
        roomComboBox = new JComboBox<>();
        loadAvailableRooms();
        gradientPanel.add(roomComboBox, gbc);
        
        //if choose STAFF, hide the select room field
        userTypeComboBox.addActionListener(e -> {
            boolean isResident = "Resident".equals(userTypeComboBox.getSelectedItem());

            roomLabel.setVisible(isResident);
            roomComboBox.setVisible(isResident);
        });

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);
        gradientPanel.add(submitButton, gbc);

        gbc.gridx = 1;
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            // Clear the fields
            usernameField.setText("");
            emailField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");
            userTypeComboBox.setSelectedIndex(0); // Reset to the first item
            roomComboBox.setSelectedIndex(0); // Reset to the first item
        
            frame.setVisible(false);
            new MainPage();
        });
        gradientPanel.add(cancelButton, gbc);

        frame.add(gradientPanel);

        Rooms.addRoomUpdateListener(this);
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                new MainPage();
            }
        });
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    private void loadAvailableRooms() {
        roomComboBox.removeAllItems(); // Clear the combo box before adding items

        // Get available rooms from the Rooms list, filtering for vacant rooms
        List<Rooms> vacantRooms = Rooms.getVacantRooms();

        // Add available rooms to the combo box
        vacantRooms.forEach(room -> {
            roomComboBox.addItem(room.getName());
        });

        // Handle case when no rooms are available
        if (roomComboBox.getItemCount() == 0) {
            roomComboBox.addItem("No rooms available");
            roomComboBox.setEnabled(false); // Disable selection if no rooms are available
        } else {
            roomComboBox.setEnabled(true); // Enable selection if rooms are available
        }
    }



    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == submitButton) {
            registerUser();
        }
    }

   private void registerUser() {
    try {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String userType = (String) userTypeComboBox.getSelectedItem();
        String selectedRoom = (String) roomComboBox.getSelectedItem();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(frame, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (DataIO.checkUserName(username) != null) {
            JOptionPane.showMessageDialog(frame, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(frame, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (isEmailRepeated(email)) {
            JOptionPane.showMessageDialog(frame, "Email already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Users u = new Users();
        u.setName(username);
        u.setEmail(email);
        u.setPassword(password);  // Use setter method instead of direct access
        u.setStatus("Pending");
        u.setUserType(userType);
        u.setPaymentStatus("Not Paid");
 
        if ("Resident".equals(userType)) {
            u.setRoom(selectedRoom); // Use a setter method for room
        } else {
            u.setRoom(null);
        }
        DataIO.allUsers.add(u);

        if (u.getRoom() != null) {
            // Ensure the room is vacant
            Rooms room = Rooms.getAllRooms().stream()
                    .filter(r -> r.getName().equals(selectedRoom) && r.getResident() == null)  // Only allow assignment if the room is vacant
                    .findFirst()
                    .orElse(null);
            
            // Write room assignment to temp_rooms.txt (this file is used to track temporary assignments)
            updateTempRoomFile(u.getName(), selectedRoom);
        }

        DataIO.write();

        JOptionPane.showMessageDialog(frame, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        
        // Clear the fields
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        userTypeComboBox.setSelectedIndex(0); // Reset to the first item
        roomComboBox.setSelectedIndex(0); // Reset to the first item
        
        frame.setVisible(false);
        AHMFPS.first.x.setVisible(true);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(frame, "An error occurred, please try again!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
   
   private boolean isValidEmail(String email) {
    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.com$";
    return email.matches(emailRegex);
    }
   
   private boolean isEmailRepeated(String email) {
    return DataIO.allUsers.stream()
          .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    private void updateTempRoomFile(String username, String room) {
        // Logic to update `temp_rooms.txt` with the room assignment for the user.
        File tempFile = new File("temp_rooms.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile, true))) {
            writer.write(username + "," + room);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRoomUpdate() {
        loadAvailableRooms();
    }
}

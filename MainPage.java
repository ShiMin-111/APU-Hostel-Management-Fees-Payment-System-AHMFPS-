package ahmfps;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import javax.swing.*;

public class MainPage implements ActionListener {
    JFrame x;
    JButton register, login, stop;
    JTextField usernameField;
    JPasswordField passwordField;

    public MainPage() {
        x = new JFrame("Login Page");
        x.setSize(400, 500);
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
        JLabel titleLabel = new JLabel("Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(140, 30, 120, 40);
        gradientPanel.add(titleLabel);

        // Username Label and Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(50, 120, 100, 25);
        gradientPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 120, 200, 30);
        gradientPanel.add(usernameField);

        // Password Label and Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(50, 180, 100, 25);
        gradientPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 180, 200, 30);
        gradientPanel.add(passwordField);

        // Login Button
        login = new JButton("Login");
        login.setBounds(50, 250, 300, 40);
        login.setBackground(new Color(0, 150, 255));
        login.setForeground(Color.WHITE);
        login.setFont(new Font("Arial", Font.BOLD, 16));
        gradientPanel.add(login);

        // Register Button
        register = new JButton("Register");
        register.setBounds(50, 310, 140, 40);
        register.setBackground(new Color(0, 200, 100));
        register.setForeground(Color.WHITE);
        register.setFont(new Font("Arial", Font.BOLD, 16));
        gradientPanel.add(register);

        // Exit Button
        stop = new JButton("Exit");
        stop.setBounds(210, 310, 140, 40);
        stop.setBackground(new Color(255, 50, 50));
        stop.setForeground(Color.WHITE);
        stop.setFont(new Font("Arial", Font.BOLD, 16));
        gradientPanel.add(stop);

        // Add Action Listeners
        register.addActionListener(this);
        login.addActionListener(this);
        stop.addActionListener(this);

        x.add(gradientPanel);
        x.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            if (ae.getSource() == register) {
                AHMFPS.second.frame.setVisible(true);
                x.setVisible(false);
            } else if (ae.getSource() == login) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                Users found = DataIO.checkUserName(username);
                if (found == null || !found.getPassword().equals(password)) {
                    JOptionPane.showMessageDialog(x, "Invalid username or password!");
                    return;
                }

                if (!"Approved".equals(found.getStatus())) {
                    JOptionPane.showMessageDialog(x, "Your account is not approved yet. Please contact a staff.");
                    return;
                } else if (found.getUserType().equals("Staff")) {
                    new StaffPage();
                } else if (found.getUserType().equals("Manager")) {
                    new ManagerPage();
                    x.setVisible(false);
                    AHMFPS.third.x.setVisible(true);
                } else {
                    AHMFPS.fourth = new ResidentPage(username);  // Update AHMFPS.fourth
                    x.setVisible(false);
                    AHMFPS.fourth.frame.setVisible(true); 
                }

                AHMFPS.loginUser = found;
                AHMFPS.loginTime = LocalDateTime.now();
                x.setVisible(false);
            } else if (ae.getSource() == stop) {
                System.exit(0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(x, "An error occurred, please try again!");
        }
    }
}

import javax.swing.*;
import java.awt.*;

public class AuthPanel extends JPanel {
    private BulletBonanza mainFrame;
    private JTextField userField;
    private JPasswordField passField;

    public AuthPanel(BulletBonanza frame) {
        this.mainFrame = frame;
        setLayout(new GridBagLayout());
        setBackground(Color.DARK_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel title = new JLabel("BULLET BONANZA LOGIN");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.CYAN);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        // Username
        gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        add(userLabel, gbc);

        gbc.gridx = 1;
        userField = new JTextField(15);
        add(userField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        add(passLabel, gbc);

        gbc.gridx = 1;
        passField = new JPasswordField(15);
        add(passField, gbc);

        // Buttons
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> performLogin());
        JButton regBtn = new JButton("Register");
        regBtn.addActionListener(e -> performRegister());
        btnPanel.add(loginBtn);
        btnPanel.add(regBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(btnPanel, gbc);
    }

    private void performLogin() {
        String u = userField.getText();
        String p = new String(passField.getPassword());
        User user = mainFrame.getUserManager().login(u, p);
        if (user != null) mainFrame.startGame(user);
        else JOptionPane.showMessageDialog(this, "Invalid Credentials");
    }

    private void performRegister() {
        String u = userField.getText();
        String p = new String(passField.getPassword());
        if (!u.isEmpty() && !p.isEmpty()) {
            if (mainFrame.getUserManager().register(u, p)) 
                JOptionPane.showMessageDialog(this, "Registered! Please Login.");
            else 
                JOptionPane.showMessageDialog(this, "Username exists.");
        }
    }
    
    public void resetFields() { userField.setText(""); passField.setText(""); }
}
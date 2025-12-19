/*
Name: Vivaan Srivastav
Course: ICS4U
Teacher: Ms. Kim
*/

import javax.swing.*;
import java.awt.*;

public class BulletBonanza extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContainer;
    private GamePanel gamePanel;
    private AuthPanel authPanel;
    private UserManager userManager;
    private User currentUser;

    public BulletBonanza() {
        setTitle("Vivaan's Bullet Bonanza - Course Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        
        userManager = new UserManager("users.txt");

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        authPanel = new AuthPanel(this);
        mainContainer.add(authPanel, "AUTH");
        
        add(mainContainer);
        cardLayout.show(mainContainer, "AUTH");
        
        setVisible(true);
    }

    public void startGame(User user) {
        this.currentUser = user;
        setTitle("Bullet Bonanza - Player: " + user.username);
        
        if (gamePanel != null) mainContainer.remove(gamePanel);
        gamePanel = new GamePanel(this, user);
        mainContainer.add(gamePanel, "GAME");
        
        cardLayout.show(mainContainer, "GAME");
        gamePanel.requestFocusInWindow();
    }
    
    public void updateHighScore(int newScore) {
        if (newScore > currentUser.highScore) {
            currentUser.highScore = newScore;
            userManager.saveUserScore(currentUser);
            System.out.println("High Score Updated for " + currentUser.username + ": " + newScore);
        }
    }

    public void logout() {
        if (gamePanel != null) gamePanel.stopGame();
        cardLayout.show(mainContainer, "AUTH");
        authPanel.resetFields();
    }

    public UserManager getUserManager() { return userManager; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BulletBonanza::new);
    }
}
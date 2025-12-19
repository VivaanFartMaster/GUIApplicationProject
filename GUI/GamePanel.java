import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.Timer;

import java.awt.event.MouseEvent;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private BulletBonanza mainFrame;
    private User currentUser;

    // --- Constants ---
    private static final int PREF_WIDTH = 1920;
    private static final int PREF_HEIGHT = 1080;
    private static final int FPS = 60;
    private static final int DELAY = 1000 / FPS;

    enum State { START, PLAYING, PAUSED, DYING, GAME_OVER }
    private State currentState = State.START;

    private int lives = 5;
    private int score = 0;
    private int totalScoreAccumulated = 0;
    private double speedMultiplier = 1.0;

    private Player player;
    private List<Bullet> bullets = new CopyOnWriteArrayList<>();
    private List<GameEntity> enemies = new CopyOnWriteArrayList<>();

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private long lastTime = System.nanoTime();
    private double timeSinceRed = 0, timeSinceBlue = 0, timeSinceBouncer = 0;
    private double redSpawnInterval, blueSpawnInterval;
    private final double BOUNCER_INTERVAL = 8.0;

    private Color flashColor = null;
    private long flashEndTime = 0;
    private long deathSequenceStartTime = 0;
    private Random random = new Random();
    private Timer gameLoop;

    // UI Components
    private JButton startButton, pauseButton, restartButton, logoutButton, resumeButton, addLifeButton;
 
    public GamePanel(BulletBonanza frame, User user) {
        this.mainFrame = frame;
        this.currentUser = user;
        
        // --- FIX 1: Set the window size explicitly ---
        this.setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));
        
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.setLayout(null); // Absolute positioning

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentState == State.PLAYING) handleMouseClick(e.getX(), e.getY());
            }
        });

        initButtons();
        resetGame();

        gameLoop = new Timer(DELAY, this);
        gameLoop.start();
    }
    
    public void stopGame() { gameLoop.stop(); }

    private void handleMouseClick(int mx, int my) {
        for (GameEntity en : enemies) {
            if (en instanceof BouncingEnemy) {
                BouncingEnemy bouncer = (BouncingEnemy) en;
                if (bouncer.checkClick(mx, my)) {
                    bouncer.markedForRemoval = true;
                    increaseScore(3);
                    triggerFlash(Color.CYAN);
                }
            }
        }
    }

    private void initButtons() {
        startButton = createStyledButton("START GAME", Color.GREEN, Color.WHITE);
        startButton.addActionListener(e -> startGame());
        add(startButton);

        pauseButton = createStyledButton("||", Color.GRAY, Color.WHITE);
        pauseButton.setFont(new Font("Arial", Font.BOLD, 15));
        pauseButton.addActionListener(e -> togglePause());
        add(pauseButton);

        resumeButton = createStyledButton("RESUME", Color.WHITE, Color.WHITE);
        resumeButton.addActionListener(e -> togglePause());
        add(resumeButton);

        addLifeButton = createStyledButton("+1 Life (-5 Score)", Color.CYAN, Color.WHITE);
        addLifeButton.setFont(new Font("Arial", Font.PLAIN, 12));
        addLifeButton.addActionListener(e -> buyLife());
        add(addLifeButton);

        restartButton = createStyledButton("PLAY AGAIN", Color.WHITE, Color.WHITE);
        restartButton.addActionListener(e -> resetGame());
        add(restartButton);
        
        logoutButton = createStyledButton("LOGOUT", Color.RED, Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.addActionListener(e -> mainFrame.logout());
        add(logoutButton);
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusable(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        return btn;
    }

    private void resetGame() {
        lives = 5; score = 0; totalScoreAccumulated = 0; speedMultiplier = 1.0;
        currentState = State.START;
        bullets.clear();
        enemies.clear();
        
        // Ensure getWidth is not 0 to avoid crashes
        int w = getWidth() > 0 ? getWidth() : PREF_WIDTH;
        int h = getHeight() > 0 ? getHeight() : PREF_HEIGHT;
        
        double pw = w / 8.0;
        player = new Player((w - pw)/2, h - 40, pw, 30);
        
        setNextSpawnTimes();
        updateButtonVisibility();
    }

    private void startGame() {
        currentState = State.PLAYING;
        double pw = getWidth() / 8.0;
        player = new Player((getWidth()-pw)/2, getHeight()-40, pw, 30);
        updateButtonVisibility();
        requestFocusInWindow();
    }

    private void togglePause() {
        if (currentState == State.PLAYING) currentState = State.PAUSED;
        else if (currentState == State.PAUSED) {
            currentState = State.PLAYING;
            lastTime = System.nanoTime();
        }
        updateButtonVisibility();
    }

    private void buyLife() {
        if (lives < 5 && score >= 5) {
            score -= 5;
            lives++;
            triggerFlash(Color.GREEN);
        }
    }

    private void setNextSpawnTimes() {
        redSpawnInterval = 4.0 + (random.nextDouble() * 4.0);
        blueSpawnInterval = 4.0 + (random.nextDouble() * 4.0);
    }

    private void updateButtonVisibility() {
        startButton.setVisible(false);
        resumeButton.setVisible(false);
        restartButton.setVisible(false);
        pauseButton.setVisible(false);
        addLifeButton.setVisible(false);
        logoutButton.setVisible(false);

        if (currentState == State.START) {
            startButton.setVisible(true);
            logoutButton.setVisible(true);
        }
        else if (currentState == State.PLAYING) {
            pauseButton.setVisible(true);
            addLifeButton.setVisible(true);
        }
        else if (currentState == State.PAUSED) {
            resumeButton.setVisible(true);
            pauseButton.setVisible(true);
            logoutButton.setVisible(true);
        }
        else if (currentState == State.GAME_OVER) {
            restartButton.setVisible(true);
            logoutButton.setVisible(true);
        }
    }

    private void triggerFlash(Color c) {
        this.flashColor = c;
        this.flashEndTime = System.currentTimeMillis() + 500;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        double dt = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;
        
        // Prevent updates if window isn't ready
        if (getWidth() <= 0) return;

        if (currentState == State.PLAYING) updateGameLogic(dt);
        else if (currentState == State.DYING) {
            if (System.currentTimeMillis() - deathSequenceStartTime > 1000) {
                currentState = State.GAME_OVER;
                updateButtonVisibility();
            }
        }
        repaint();
    }

    private void updateGameLogic(double dt) {
        double pSpeed = (getWidth() / 3.5) * speedMultiplier;
        if (leftPressed) player.moveLeft(pSpeed, dt);
        if (rightPressed) player.moveRight(pSpeed, dt, getWidth());

        timeSinceRed += dt;
        timeSinceBlue += dt;
        timeSinceBouncer += dt;

        if (timeSinceRed >= redSpawnInterval) {
            spawnFallingEnemy(FallingEnemy.Type.RED);
            timeSinceRed = 0;
            redSpawnInterval = 4.0 + random.nextDouble() * 4.0;
        }
        if (timeSinceBlue >= blueSpawnInterval) {
            spawnFallingEnemy(FallingEnemy.Type.BLUE);
            timeSinceBlue = 0;
            blueSpawnInterval = 4.0 + random.nextDouble() * 4.0;
        }
        if (timeSinceBouncer >= BOUNCER_INTERVAL) {
            spawnBouncingEnemy();
            timeSinceBouncer = 0;
        }

        for (Bullet b : bullets) {
            b.move(dt, getWidth(), getHeight());
            if (b.markedForRemoval) bullets.remove(b);
        }

        for (GameEntity en : enemies) {
            en.move(dt, getWidth(), getHeight());

            Rectangle eRect = en.getBounds();
            Rectangle pRect = player.getBounds();
            
            if (!en.markedForRemoval && eRect.intersects(pRect)) {
                if (en instanceof BouncingEnemy || (en instanceof FallingEnemy && ((FallingEnemy)en).type == FallingEnemy.Type.RED)) {
                    lives--;
                    triggerFlash(Color.RED);
                    en.markedForRemoval = true;
                } else if (en instanceof FallingEnemy && ((FallingEnemy)en).type == FallingEnemy.Type.BLUE) {
                    increaseScore(1);
                    triggerFlash(Color.GREEN);
                    en.markedForRemoval = true;
                }
            }
            
            for (Bullet b : bullets) {
                if (b.getBounds().intersects(eRect)) {
                    b.markedForRemoval = true;
                    if (en instanceof FallingEnemy) {
                        FallingEnemy fe = (FallingEnemy)en;
                        if (fe.type == FallingEnemy.Type.RED) {
                            increaseScore(1);
                            triggerFlash(Color.MAGENTA);
                            en.markedForRemoval = true;
                        } else {
                            lives--;
                            triggerFlash(Color.RED);
                            en.markedForRemoval = true;
                        }
                    } else if (en instanceof BouncingEnemy) {
                        increaseScore(1);
                        triggerFlash(Color.CYAN);
                        en.markedForRemoval = true;
                    }
                    break;
                }
            }
            
            if (en instanceof FallingEnemy && !en.markedForRemoval && en.y > getHeight()) {
                FallingEnemy fe = (FallingEnemy)en;
                if (fe.type == FallingEnemy.Type.BLUE) score -= 2;
                else if (fe.type == FallingEnemy.Type.RED) { lives--; triggerFlash(Color.RED); }
            }

            if (en.markedForRemoval) enemies.remove(en);
        }

        if (lives <= 0 && currentState == State.PLAYING) {
            lives = 0;
            currentState = State.DYING;
            deathSequenceStartTime = System.currentTimeMillis();
            if (currentUser != null) { // --- FIX 2: Safety Check ---
                mainFrame.updateHighScore(score);
            }
            updateButtonVisibility();
        }
    }

    private void increaseScore(int amount) {
        int oldLevel = totalScoreAccumulated / 5; 

        score += amount;
        if (amount > 0) {
            totalScoreAccumulated += amount;
        }

        int newLevel = totalScoreAccumulated / 5;

        if (newLevel > oldLevel) {
            speedMultiplier += 0.1;
        }
    }

    private void spawnFallingEnemy(FallingEnemy.Type type) {
        double size = player.width / 2.0;
        double x = random.nextDouble() * (getWidth() - size);
        enemies.add(new FallingEnemy(x, -size, size, type));
    }
    
    private void spawnBouncingEnemy() {
        double size = player.width * 0.7;
        double x = random.nextDouble() * (getWidth() - size);
        enemies.add(new BouncingEnemy(x, size, speedMultiplier));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (currentState == State.START) {
            drawStartScreen(g2);
        } else {
            if (player != null) {
                if (currentState == State.DYING) player.color = Color.RED.darker();
                else if (System.currentTimeMillis() < flashEndTime && flashColor != null) player.color = flashColor;
                else player.color = Color.BLUE;
                player.draw(g2);
            }

            for (Bullet b : bullets) b.draw(g2);
            for (GameEntity e : enemies) e.draw(g2);

            drawHUD(g2);

            if (currentState == State.PAUSED) drawOverlay(g2, "PAUSED");
            else if (currentState == State.GAME_OVER) drawOverlay(g2, "GAME OVER");
        }
        
        repositionButtons();
    }

    private void repositionButtons() {
        int cx = getWidth()/2, cy = getHeight()/2;
        if(startButton.isVisible()) startButton.setBounds(cx - 100, cy + 150, 200, 60);
        if(restartButton.isVisible()) restartButton.setBounds(cx - 100, cy + 50, 200, 60);
        if(resumeButton.isVisible()) resumeButton.setBounds(cx - 100, cy, 200, 60);
        if(pauseButton.isVisible()) pauseButton.setBounds(getWidth() - 60, 10, 50, 50);
        if(addLifeButton.isVisible()) addLifeButton.setBounds(getWidth() - 220, getHeight() - 90, 150, 30);
        if(logoutButton.isVisible()) logoutButton.setBounds(20, 100, 100, 30);
    }

    private void drawHUD(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(Color.WHITE);
        g2.drawString("Score: " + score, 20, getHeight() - 20);
        
        // --- FIX 2: Safety Check for User ---
        int currentBest = score;
        if (currentUser != null) {
            currentBest = Math.max(score, currentUser.highScore);
        }
        g2.drawString("High Score: " + currentBest, 20, 40);
        
        g2.drawString("Lives: " + lives + "/5", getWidth() - 150, getHeight() - 20);
        
        String speedText = String.format("Speed: %.1fx", speedMultiplier);
        g2.drawString(speedText, getWidth() - 300, 40);
    }
    
    private void drawOverlay(Graphics2D g2, String text) {
        g2.setColor(new Color(0,0,0,0));
        g2.fillRect(0,0,getWidth(),getHeight());
        int yOffset = (text.equals("PAUSED")) ? -50 : 0;
        drawCenteredString(g2, text, getWidth()/2, getHeight()/2 + yOffset, new Font("Arial",Font.BOLD,60), Color.WHITE);
        if (text.equals("GAME OVER")) {
             drawCenteredString(g2, "Final Score: " + score, getWidth()/2, getHeight()/2 + 60, new Font("Arial",Font.BOLD,30), Color.WHITE);
        }
    }

    private void drawStartScreen(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        drawCenteredString(g2, "Bullet Bonanza", getWidth()/2, 100, new Font("Arial", Font.BOLD, 50), Color.CYAN);
        
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.YELLOW);
        
        // --- FIX 2: Safety Check for User ---
        if (currentUser != null) {
            g2.drawString("User: " + currentUser.username + " | Best: " + currentUser.highScore, 20, 70);
        } else {
            g2.drawString("Guest Mode", 20, 70);
        }
        
        String instr = "Arrows to Move, Space to Shoot\nRed=Shoot (+1), Blue=Catch (+1)\nNEW: Orange Bouncer = Click (+3) or Shoot (+1)";
        int y = 200;
        for (String line : instr.split("\n")) {
            drawCenteredString(g2, line, getWidth()/2, y, new Font("Arial", Font.PLAIN, 20), Color.WHITE);
            y += 30;
        }
    }

    private void drawCenteredString(Graphics g, String text, int x, int y, Font font, Color color) {
        g.setFont(font); g.setColor(color);
        int xPos = x - (g.getFontMetrics().stringWidth(text) / 2);
        g.drawString(text, xPos, y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (currentState == State.PLAYING) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                bullets.add(new Bullet(player.x + player.width/2 - 3, player.y));
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
    }
    @Override public void keyTyped(KeyEvent e) {}
}
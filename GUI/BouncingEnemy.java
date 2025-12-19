import java.awt.*;
import java.util.Random;

public class BouncingEnemy extends GameEntity {
    private int bounceCount = 0;
    private final int MAX_BOUNCES = 5;
    public Rectangle buttonBounds;

    public BouncingEnemy(double x, double size, double speedMultiplier) {
        super(x, -size, size, size, new Color(255, 140, 0));
        Random r = new Random();
        this.velX = (r.nextBoolean() ? 300 : -300) * speedMultiplier;
        this.velY = 250 * speedMultiplier;
        updateButtonBounds();
    }

    private void updateButtonBounds() {
        int bw = (int)(width * 0.8);
        int bh = (int)(height * 0.4);
        int bx = (int)(x + (width - bw) / 2);
        int by = (int)(y + (height - bh) / 2);
        buttonBounds = new Rectangle(bx, by, bw, bh);
    }

    @Override
    public void move(double dt, int pW, int pH) {
        x += velX * dt;
        y += velY * dt;

        if (x <= 0) { x
            = 0; velX *= -1; bounceCount++; 
        } 
        else if (x + width >= pW) { 
            x = pW - width; velX *= -1; bounceCount++; 
        }

        updateButtonBounds();

        if (bounceCount >= MAX_BOUNCES || y > pH) {
            markedForRemoval = true;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.fill(getBounds());
        g2.setColor(Color.WHITE);
        g2.fill(buttonBounds);
        g2.setColor(Color.BLACK);
        g2.draw(buttonBounds);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString("CLICK", buttonBounds.x + 2, buttonBounds.y + buttonBounds.height - 5);
    }
    
    public boolean checkClick(int mx, int my) {
        return buttonBounds.contains(mx, my);
    }
}
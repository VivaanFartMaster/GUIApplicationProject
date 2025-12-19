import java.awt.*;

public class Player extends GameEntity {
    public Player(double x, double y, double w, double h) {
        super(x, y, w, h, Color.BLUE);
    }

    @Override
    public void move(double dt, int pW, int pH) {
        
    }
    
    public void moveLeft(double speed, double dt) { x -= speed * dt; if(x < 0) x = 0; }
    public void moveRight(double speed, double dt, int pW) { x += speed * dt; if(x + width > pW) x = pW - width; }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.fill(getBounds());
        g2.setColor(Color.WHITE);
        g2.draw(getBounds());
    }
}
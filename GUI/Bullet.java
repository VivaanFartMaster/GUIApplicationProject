import java.awt.*;

public class Bullet extends GameEntity {
    public Bullet(double x, double y) {
        super(x, y, 6, 15, Color.YELLOW);
        this.velY = -1000;
    }

    @Override
    public void move(double dt, int pW, int pH) {
        y += velY * dt;
        if (y + height < 0) markedForRemoval = true;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.fill(getBounds());
    }
}
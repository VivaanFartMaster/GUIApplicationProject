import java.awt.*;

public abstract class GameEntity {
    double x, y, width, height;
    Color color;
    double velX, velY;
    boolean markedForRemoval = false;

    public GameEntity(double x, double y, double w, double h, Color c) {
        this.x = x; this.y = y; this.width = w; this.height = h; this.color = c;
    }

    public abstract void move(double dt, int panelWidth, int panelHeight);
    public abstract void draw(Graphics2D g2);

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, (int)width, (int)height);
    }
}
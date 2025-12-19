import java.awt.*;

public class FallingEnemy extends GameEntity {
    public enum Type { RED, BLUE }
    Type type;

    public FallingEnemy(double x, double y, double size, Type type) {
        super(x, y, size, size, type == Type.RED ? Color.RED : Color.BLUE);
        this.type = type;
        this.velY = 200;
    }

    @Override
    public void move(double dt, int pW, int pH) {
        y += velY * dt;
        if (y > pH) markedForRemoval = true;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.fill(getBounds());
        g2.setColor(Color.WHITE);
        g2.draw(getBounds());
    }
}
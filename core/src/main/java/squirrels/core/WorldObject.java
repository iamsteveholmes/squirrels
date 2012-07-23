package squirrels.core;


import playn.core.Image;

public class WorldObject {

    public Image img;
    public double oldx, oldy, oldz;
    public double x, y, z;
    public double vx, vy, vz;
    public double ax, ay, az;
    public double r;

    SquirrelWorld.Stack stack;
    int lastUpdated;
    boolean resting;

    public WorldObject( Image img ) {
        this.img = img;
    }

    public boolean isResting() {
        return resting;
    }

    public void setAcceleration( double ax, double ay, double az ) {
        this.ax = ax;
        this.ay = ay;
        this.az = az;
    }

    public void setPos( double x, double y, double z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setVelocity( double vx, double vy, double vz ) {
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
    }

    public void saveOldPos() {
        this.oldx = x;
        this.oldy = y;
        this.oldz = z;
    }

    public double x( double alpha ) {
        return x * alpha + oldx * ( 1.0f - alpha );
    }

    public double y( double alpha ) {
        return y * alpha + oldy * ( 1.0f - alpha );
    }

    public double z( double alpha ) {
        return z * alpha + oldz * ( 1.0f - alpha );
    }
}


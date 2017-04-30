package u1529313;

import robocode.*;
import robocode.util.Utils;
import robocode.Rules;
import java.awt.Color;
import java.util.HashMap;
import java.awt.geom.Point2D;

import u1529313.modes.RedMode;
import u1529313.modes.RedWallCrawler;
import u1529313.modes.RedOneToOne;

/**
 * Loop:
 * 1. repaint
 * 2. robot code executed then paused
 * 3. time = time + 1
 * 4. bullets move and check collisions
 * 5. robots move (gun, radar, heading, accel, vel, dist)
 * 6. robots perform scans
 * 7. robots are presumed to take new action
 * 8. each robot processes its event queue
 */

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

public class Red extends Robot {

    final public double power = 3.0; // small damage for both, less heat
    final public double bulletSpeed = Rules.getBulletSpeed(power);

    RedMode rmode; // robot mode

    public void selectMode() {
        // for now we dont look at sentrys
        if (getOthers() > 1) {
            rmode = new RedWallCrawler(this); // melee
        } else {
            rmode = new RedOneToOne(this); // one to one
        }
    }

    public void run() {
        setUI();
        selectMode();

        rmode.executeBeforeLoop();

        while (true) {
            System.out.println("x: " + getX() + " y: " + getY());
            rmode.executeMainLoop();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        System.out.println("distance: " + e.getDistance());
        rmode.executeScannedRobot(e);
    }

    public void onHitRobot(HitRobotEvent e) {
        rmode.executeHitRobot(e);
    }

    public void onBulletHit(BulletHitEvent e) {
        rmode.executeBulletHit(e);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        rmode.executeOnHitByBullet(e);
    }

    public void onRobotDeath(RobotDeathEvent e) {
        // update mode if now one to one
        if (getNumSentries() == 0 && getOthers() == 1) {
            selectMode();
            rmode.executeBeforeLoop();
        }
        rmode.executeRobotDeath(e);
    }

    public void setUI() {
        setBodyColor(Color.red);
        setGunColor(Color.black);
        setRadarColor(Color.magenta);
        setBulletColor(Color.green);
        setScanColor(Color.cyan);
    }

    // ======= Helpful AdvancedRobot methods =======

    // -- body --
    public void turnRightRadians(double theta) {
        turnRight(Math.toDegrees(theta));
    }

    public void turnLeftRadians(double theta) {
        turnLeft(Math.toDegrees(theta));
    }

    public double getHeadingRadians() {
        return Math.toRadians(getHeading());
    }

    // -- radar --
    public void turnRadarRightRadians(double theta) {
        turnRadarRight(Math.toDegrees(theta));
    }

    public void turnRadarLeftRadians(double theta) {
        turnRadarLeft(Math.toDegrees(theta));
    }

    public double getRadarHeadingRadians() {
        return Math.toRadians(getRadarHeading());
    }

    // -- gun --
    public void turnGunRightRadians(double theta) {
        turnGunRight(Math.toDegrees(theta));
    }

    public void turnGunLeftRadians(double theta) {
        turnGunLeft(Math.toDegrees(theta));
    }

    public double getGunHeadingRadians() {
        return Math.toRadians(getGunHeading());
    }


}

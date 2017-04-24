package u1529313;

import robocode.*;
import robocode.util.Utils;
import robocode.Rules;
import java.awt.Color;
import java.util.HashMap;
import java.awt.geom.Point2D;

import u1529313.modes.RedMode;
import u1529313.modes.RedWallCrawler;
import u1529313.modes.RedPatternMatch;

/**
 * Loop:
 * 1. repaint
 * 2. robot code executed then paused
 * 3. time = time + 1
 * 4. bullets move and check collisions
 * 5. robots move (gun, radar, heading, accel, vel, dist)
 * 6. robots perform scans
 * 7. robots are presumed to take new action
 * 8. reach robot processes its event queue
 */

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

public class Red extends Robot {

    final public double power = 1.0; // small damage but less 
    final public double bulletSpeed = Rules.getBulletSpeed(power);

    boolean peek; // dont turn if there's a robot there
    double moveAmount; // how much we move

    RedMode rmode;

    public void run() {
        setUI();

        if (getNumSentries() == 0 && getOthers() > 1) {
            rmode = new RedWallCrawler(this);
        } else if (getOthers() == 1) {
            rmode = new RedPatternMatch(this);
        }

        rmode.executeBeforeLoop();

        while (true) {
            rmode.executeMainLoop();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        rmode.executeScannedRobot(e);
    }

    public void onHitRobot(HitRobotEvent e) {
        rmode.executeHitRobot(e);
        // for now do nothing
        // if (e.getBearing() > -90 && e.getBearing() < 90)
        // {
        //     back(100);
        // }
        // else
        // {
        //     ahead(100);
        // }
    }

    public void setUI() {
        setBodyColor(Color.black);
        setGunColor(Color.black);
    }
}

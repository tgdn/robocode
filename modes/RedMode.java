package u1529313.modes;

import robocode.*;
import robocode.util.Utils;

import u1529313.Red;
import u1529313.guns.Gun;
import u1529313.dodging.Dodging;

public abstract class RedMode {
    protected Red r; // our robot
    
    protected Gun gun = null; // the gun we are using
    protected Dodging dodging = null; // the dodging mechanism

    public RedMode(Red robot) {
        r = robot;
    }

    public void executeBeforeLoop() {}

    public void executeMainLoop() {}

    public void executeScannedRobot(ScannedRobotEvent e) {
        r.fire(r.power);
    }

    /* on colision */
    public void executeHitRobot(HitRobotEvent e) {}

    /* when we hit a robot, he looses energy */
    public void executeBulletHit(BulletHitEvent e) {
        if (dodging != null) {
            dodging.onBulletHit(e);
        }
    }

    /* when we get hit: other robot gains energy */
    public void executeOnHitByBullet(HitByBulletEvent e) {
        if (dodging != null) {
            dodging.onHitByBullet(e);
        }
    }

    public void executeRobotDeath(RobotDeathEvent e) {
        if (dodging != null) {
            dodging.onRobotDeath(e);
        }
    }
}
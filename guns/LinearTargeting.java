package u1529313.guns;

import java.util.*;
import java.awt.geom.*;

import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import u1529313.Red;
import u1529313.guns.Gun;
import u1529313.utils.RedUtils;

/**
  * A very basic linear targeting gun
  *
  * Makes use of enemys current velocity, making no guesses
  * but assuming enemy will keep same heading and speed
  *
  *   e---->
  *   |   ^
  *   |  /
  *   | /
  *   |/
  *   +
  *
 */
public class LinearTargeting extends Gun {
    public LinearTargeting(Red robot) {
        super(robot);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // Assume enemy will continue current trajectory and speed (same velocity)
        // predict intersection of bullet and enemy and shoot using linear targeting

        double absBearing = RedUtils.getEnemyAbsBearing(r, e);
        double linearBearing =
            absBearing + Math.sin(e.getVelocity() / r.bulletSpeed * Math.sin(e.getHeadingRadians() - absBearing));

        r.turnGunRightRadians(Utils.normalRelativeAngle(
            linearBearing - r.getGunHeadingRadians()
        ));

        r.fire(r.power);
    }
}
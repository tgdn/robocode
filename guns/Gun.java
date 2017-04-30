package u1529313.guns;

import java.util.*;
import java.awt.geom.*;

import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import u1529313.Red;

/**
  * Abstract Gun class, base of every gun mechanism implementation.
  * Sublasses can access robot object using instance variable `r`
  *
  * subclasses should implement own version of onScannedRobot
  * which will be called when used.
 */
public abstract class Gun {

    protected Red r;

    public Gun(Red robot) {
        r = robot;
    }

    public void onScannedRobot(ScannedRobotEvent e) {}
}
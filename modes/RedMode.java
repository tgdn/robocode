package u1529313.modes;

import robocode.*;
import robocode.util.Utils;

import u1529313.Red;

public abstract class RedMode {
    protected Red r; // our robot

    public RedMode(Red robot) {
        r = robot;
    }

    public void executeBeforeLoop() {}

    public void executeMainLoop() {}

    public void executeScannedRobot(ScannedRobotEvent e) {
        r.fire(r.power);
    }

    public void executeHitRobot(HitRobotEvent e) {}
}
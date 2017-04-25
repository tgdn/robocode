package u1529313.modes;

import robocode.*;
import robocode.util.Utils;

import u1529313.Red;
import u1529313.modes.RedMode;
import u1529313.utils.ScannedLog;

public class RedWallCrawler extends RedMode {

    boolean peek;
    double moveAmount;

    public RedWallCrawler(Red robot) {
        super(robot);
    }

    public void executeBeforeLoop() {
        peek = false;
        moveAmount = Math.max(r.getBattleFieldWidth(), r.getBattleFieldHeight());

        r.turnLeft(r.getHeading() % 90);
        r.ahead(moveAmount);

        peek = true;
        r.turnGunRight(90);
        r.turnRight(90);
    }

    public void executeMainLoop() {
        peek = true;
        r.ahead(moveAmount);
        peek = false;
        r.turnRight(90);
    }

    public void executeScannedRobot(ScannedRobotEvent e) {
        if (peek) {
            r.scan();
        }

        // from http://robowiki.net/wiki/Linear_Targeting
        double headsOnBearing = Math.toRadians(r.getHeading() + e.getBearing());
        double linearBearing = headsOnBearing + Math.asin(e.getVelocity() / r.bulletSpeed * Math.sin(e.getHeadingRadians() - headsOnBearing));
        r.turnGunRight(Math.toDegrees(
            Utils.normalRelativeAngle(linearBearing - Math.toRadians(r.getGunHeading()))
        ));

        r.fire(r.power);

         // turn back to initial position
        r.turnGunRight(Utils.normalRelativeAngleDegrees(
            r.getHeading() - r.getGunHeading() + 90
        ));
    }
}
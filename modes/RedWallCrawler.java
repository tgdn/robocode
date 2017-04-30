package u1529313.modes;

import java.util.*;

import robocode.*;
import robocode.util.Utils;

import u1529313.Red;
import u1529313.modes.RedMode;
import u1529313.utils.ScannedLog;
import u1529313.guns.GuessFactorTargeting;
import u1529313.guns.LinearTargeting;

public class RedWallCrawler extends RedMode {

    public class RobotLock {
        String name;
        double energy;

        public String toString() {
            return name + ": " + energy;
        }
    }

    double HALF_PI = Math.PI/2;
    double TWO_PI = 2 * Math.PI;

    RobotLock currentLock;

    // ===============
    double moveAmount;

    byte dodgeDirection = 1;
    byte scanDirection = 1;

    ScannedLog log;

    public RedWallCrawler(Red robot) {
        super(robot);
        log = new ScannedLog();
        gun = new LinearTargeting(r);
    }

    public void executeBeforeLoop() {
        // r.setAdjustGunForRobotTurn(true);
        r.setAdjustRadarForRobotTurn(true); // radar independent from robots turn
        r.setAdjustRadarForGunTurn(true); // radar independent from gun turn

        // ===============
        moveAmount = Math.max(r.getBattleFieldWidth(), r.getBattleFieldHeight());

        r.turnLeft(r.getHeading() % 90);
        r.ahead(moveAmount);

        r.turnGunRight(90);
        r.turnRight(90);
    }

    public void executeMainLoop() {

        // ===============
        r.turnRadarRightRadians(TWO_PI);
        r.ahead(moveAmount * getNextDirection());
        r.turnRight(90);
        r.scan();
    }

    private int getNextDirection() {
        if (Math.random() > .5) {
            return -1;
        }
        return 1;
    }

    public void executeScannedRobot(ScannedRobotEvent e) {

        gun.onScannedRobot(e);

        // get radar back in correct position for best results
        r.turnRadarRight(Utils.normalRelativeAngleDegrees(
            r.getHeading() - r.getRadarHeading() + 90
        ));

        // last part get closer
        // int d = 300;
        // if (e.getDistance() < d) {
        //     r.turnRight(e.getBearing() + 180);
        //     r.ahead(d - e.getDistance());
        // } else {
        //     r.turnRight(e.getBearing());
        //     r.ahead(e.getDistance() - d);
        //     r.turnRight(180); // turn back to opponent
        // }


        // if (false) {
        //     double headsOnBearing = r.getHeadingRadians() + e.getBearingRadians();
        //     double linearBearing = headsOnBearing + Math.asin(e.getVelocity() / r.bulletSpeed * Math.sin(e.getHeadingRadians() - headsOnBearing));
        //     double gunTurnAngle = Utils.normalRelativeAngle(linearBearing - r.getGunHeadingRadians());

        //     // go perpendicular to the robot
        //     r.turnRight(e.getBearing()+90-30);

        //     // lock first
        //     RobotLock newlock = new RobotLock();
        //     newlock.name = e.getName();
        //     newlock.energy = e.getEnergy();

        //     // change in energy (he may be aiming at us)
        //     if (currentLock != null && currentLock.energy != newlock.energy) {
        //         r.turnGunRightRadians(gunTurnAngle);
        //         r.fire(r.power); // fire and go
        //         double rnd = Math.random();
        //         int distance = (int) Math.random() * (61) + 60;
        //         if (rnd > 0.5) {
        //             System.out.println("Forward of: " +distance);
        //             r.ahead(distance);
        //         } else {
        //             System.out.println("Back of: " +distance);
        //             r.ahead(-distance);
        //         }
        //         currentLock = newlock;
        //         return;
        //     }
        //     currentLock = newlock; // update lock

        //     // shoot after
        //     double radarTurn = headsOnBearing - r.getRadarHeadingRadians();
        //     r.turnRadarRightRadians(Utils.normalRelativeAngle(radarTurn));

        //     r.turnGunRightRadians(gunTurnAngle);
        //     r.fire(r.power);
        // }

        // ===============


        // ================== end Guess Factor

        // shootToKill(e); // shoot first

        //  // turn back to initial position
        // r.turnGunRight(Utils.normalRelativeAngleDegrees(
        //     r.getHeading() - r.getGunHeading() + 90
        // ));

        // // generate new scan
        // // scanDirection *= -1;
        // // r.turnRadarRightRadians(TWO_PI * scanDirection);

        // // get radar back in correct position
        // r.turnRadarRight(Utils.normalRelativeAngleDegrees(
        //     r.getHeading() - r.getRadarHeading() + 90
        // ));
    }

    public void executeHitRobot(HitRobotEvent e) {
        aimAndShoot(e.getBearingRadians());
        moveAmount *= -1;
        r.ahead(moveAmount);
        r.scan();
        r.turnRadarRight(360);
        // if (e.getBearing() > -10 && e.getBearing() < 10) {
        //     r.turnGunRight(r.getHeading() - r.getGunHeading() + e.getBearing());
        //     r.fire(r.power);
        // }
        // if (e.isMyFault()) {
        //     // r.back(100);
        // }
    }

    private void doLog(ScannedRobotEvent e) {
        log.push(e);
    }

    private void dodgeBullet(ScannedRobotEvent e) {
        // this method should dodge and log scanned robot
        // dodgeDirection *= -1;
        // r.ahead(((e.getDistance() / 4 + 25) * dodgeDirection) + (Math.random() * 18));
    }

    private void shootToKill(ScannedRobotEvent e) {
        // from http://robowiki.net/wiki/Linear_Targeting
        double headsOnBearing = r.getHeadingRadians() + e.getBearingRadians();
        double linearBearing = headsOnBearing + Math.asin(e.getVelocity() / r.bulletSpeed * Math.sin(e.getHeadingRadians() - headsOnBearing));
        r.turnGunRight(Math.toDegrees(
            Utils.normalRelativeAngle(linearBearing - r.getGunHeadingRadians())
        ));

        r.fire(r.power);
    }
    private void aimAndShoot(double ebearingRadians) {
        double absBearing = r.getHeadingRadians() + ebearingRadians;
        double gunAdjust = Utils.normalRelativeAngle(
            absBearing - r.getGunHeadingRadians()
        );
        r.turnGunRightRadians(gunAdjust);
        r.fire(r.power);
    }
}
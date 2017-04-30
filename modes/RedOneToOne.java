package u1529313.modes;

import java.util.*;
import java.awt.geom.*;

import robocode.*;
import robocode.util.Utils;

import u1529313.Red;
import u1529313.modes.RedMode;

import u1529313.guns.GuessFactorTargeting;
import u1529313.guns.LinearTargeting;
import u1529313.dodging.WaveSurfer;

public class RedOneToOne extends RedMode {
    byte dodgeDirection = 1;
    byte radarDirection = 1;

    /* constructor */
    public RedOneToOne(Red robot) {
        super(robot);
        gun = new LinearTargeting(r);
        dodging = new WaveSurfer(r);

        r.setAdjustGunForRobotTurn(true);
        r.setAdjustRadarForRobotTurn(true); // radar independent from robots turn
        // r.setAdjustRadarForGunTurn(true); // radar independent from gun turn
    }

    public void executeMainLoop() {
        r.turnRadarRight(10 * radarDirection); // scans automatically
    }

    public double getEnemyAbsBearing(ScannedRobotEvent e) {
        return r.getHeadingRadians() + e.getBearingRadians();
    }

    public void executeScannedRobot(ScannedRobotEvent e) {
        r.scan();

        // do gun
        gun.onScannedRobot(e);

        // dodge
        dodging.onScannedRobot(e);

        // LOCK
        radarDirection *= -1;
        double radarTurn = getEnemyAbsBearing(e) - r.getRadarHeadingRadians();
        r.turnRadarRightRadians(Utils.normalRelativeAngle(radarTurn) * radarDirection);

        // update energy
        dodging.updateEnergy(e.getName(), new Double(e.getEnergy()));

        // if (e.getDistance() <= 160) {
        //     double disp = 400 - e.getDistance();
        //     r.turnRight(e.getBearing());

        //     if (e.getBearing() > -90 && e.getBearing() <= 90) {
        //         r.back(disp);
        //     } else {
        //         r.ahead(disp);
        //     }
        //     r.scan();
        // }
    }

    public void executeHitRobot(HitRobotEvent e) {
        System.out.println("HIT ROBOT: " + e.getName());
        aimAndShoot(e.getBearingRadians());
        r.back(80 * (Math.random() + 1));
        r.scan();
        r.turnRadarRight(360);
    }

    private void aimAndShoot(double ebearingRadians) {
        // linear targeting
        double absBearing = r.getHeadingRadians() + ebearingRadians;
        double gunAdjust = Utils.normalRelativeAngle(
            absBearing - r.getGunHeadingRadians()
        );
        r.turnGunRightRadians(gunAdjust);
        r.fire(r.power);
    }

    
}
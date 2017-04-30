package u1529313.guns;

import java.util.*;
import java.awt.geom.*;

import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import u1529313.Red;
import u1529313.guns.Gun;
import u1529313.utils.RedUtils;
import u1529313.utils.WaveBullet;

public class GuessFactorTargeting extends Gun {

    List<WaveBullet> waves;

    static int[] stats = new int[31]; // num of GuessFactors -> needs to be odd in order to get middle value
    int direction = 1;

    public GuessFactorTargeting(Red robot) {
        super(robot);
        waves = new ArrayList<WaveBullet>();
    }

    public void onScannedRobot(ScannedRobotEvent e) {

        // enemy absolute bearing
        double absBearing = RedUtils.getEnemyAbsBearing(r, e);

        // enemy coordinates
        double ex = r.getX() + Math.sin(absBearing) * e.getDistance();
        double ey = r.getY() + Math.cos(absBearing) * e.getDistance();

        // process waves
        for (int i = 0; i < waves.size(); i++)
        {
            WaveBullet currentWave = (WaveBullet) waves.get(i);
            if (currentWave.checkHit(ex, ey, r.getTime()))
            {
                waves.remove(currentWave);
                i--;
            }

        }

        if (e.getVelocity() != 0)
        {
            if (Math.sin(e.getHeadingRadians() - absBearing) * e.getVelocity() < 0)
                direction = -1;
            else
                direction = 1;
        }

        int[] currentStats = stats;
        WaveBullet newWave = new WaveBullet(r.getX(), r.getY(), absBearing, r.power, direction, r.getTime(), currentStats);

        // fire
        int bestindex = 15; // middle of 31, guessfactor = 0
        for (int i = 0; i < 31; i++) {
            if (currentStats[bestindex] < currentStats[i])
                bestindex = i;
        }

        // opposite of the math in WaveBullet
        double guessfactor = (double) (bestindex - (stats.length - 1) / 2) / ((stats.length - 1) / 2);
        double angleOffset = direction * guessfactor * newWave.maxEscapeAngle();
        double gunAdjust = Utils.normalRelativeAngle(
            absBearing - r.getGunHeadingRadians() + angleOffset
        );
        r.turnGunRightRadians(gunAdjust);

        // try to fire and store wave if successful
        if (
            r.getGunHeat() == 0 &&
            gunAdjust < Math.atan2(9, e.getDistance()) &&
            r.fireBullet(r.power) != null)
        {
            waves.add(newWave);
        }

        // // get radar back in correct position
        r.turnRadarRight(Utils.normalRelativeAngleDegrees(
            r.getHeading() - r.getRadarHeading() + 90
        ));
    }
}
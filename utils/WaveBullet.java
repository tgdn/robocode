package u1529313.utils;

import java.awt.geom.*;
import robocode.util.Utils;

// http://robowiki.net/wiki/GuessFactor_Targeting_Tutorial

public class WaveBullet {
    double startX, startY, startBearing, power;
    long fireTime;
    int direction;
    int[] returnSegment;

    public WaveBullet(
        double x, double y, double bearing, double power,
        int direction, long time, int[] segment
    ) {
        startX = x;
        startY = y;
        startBearing = bearing;
        this.power = power;
        this.direction = direction;
        fireTime = time;
        returnSegment = segment;
    }

    public double getBulletSpeed() {
        return 20 - power * 3;
    }

    public double maxEscapeAngle() {
        return Math.asin(8 / getBulletSpeed());
    }

    public boolean checkHit(double enemyX, double enemyY, long currentTime)
    {
        // if the distance from the wave origin to our enemy has passed
        // the distance the bullet would have traveled...
        if (
            Point2D.distance(startX, startY, enemyX, enemyY) <=
                (currentTime - fireTime) * getBulletSpeed()
        ) {
            double desiredDirection = Math.atan2(enemyX - startX, enemyY - startY);
            double angleOffset = Utils.normalRelativeAngle(desiredDirection - startBearing);
            double guessFactor =
                Math.max(-1, Math.min(1, angleOffset / maxEscapeAngle())) * direction;
            int index = (int) Math.round((returnSegment.length - 1) /2 * (guessFactor + 1));
            returnSegment[index]++;
            return true;
        }
        return false;
    }
}
package u1529313.utils;

import java.util.*;
import java.awt.geom.*;

import robocode.util.Utils;
import robocode.ScannedRobotEvent;

import u1529313.Red;
import u1529313.dodging.WaveSurfer.EnemyWave;

public final class RedUtils {

    public static final double HALF_PI = Math.PI / 2;
    public static final double TWO_PI = 2 * Math.PI;

    // Get current enemy absolute bearing
    public static double getEnemyAbsBearing(Red r, ScannedRobotEvent e) {
        return r.getHeadingRadians() + e.getBearingRadians(); // see robocode docs
    }

    public static double bulletVelocity(double p) {
        return (20.0 - (3.0 * p)); // see robocode docs
    }
 
    public static double maxEscapeAngle(double velocity) {
        return Math.asin(8.0 / velocity); // see robocode docs
    }

    // ========= Wave surfing helper methods

    public static Point2D.Double project(Point2D.Double sourceLocation, double angle, double length)
    {
        return new Point2D.Double(
            sourceLocation.x + Math.sin(angle) * length,
            sourceLocation.y + Math.cos(angle) * length
        );
    }
 
    public static double absoluteBearing(Point2D.Double source, Point2D.Double target)
    {
        return Math.atan2(
            target.x - source.x,
            target.y - source.y
        );
    }
 
    public static double limit(double min, double value, double max)
    {
        return Math.max(min, Math.min(value, max));
    }
 
    public static void setBackAsFront(Red r, double goAngle)
    {
        double angle = Utils.normalRelativeAngle(goAngle - r.getHeadingRadians());

        if (Math.abs(angle) > (Math.PI/2))
        {
            if (angle < 0)
                r.turnRightRadians(Math.PI + angle);
            else
                r.turnLeftRadians(Math.PI - angle);

            r.back(100);
        } else
        {
            if (angle < 0)
                r.turnLeftRadians(-1*angle);
            else
                r.turnRightRadians(angle);

            r.ahead(100);
        }
    }

    public static int getFactorIndex(int BINS, EnemyWave ew, Point2D.Double targetLocation)
    {
        double offsetAngle = (absoluteBearing(ew.fireLocation, targetLocation) - ew.directAngle);
        double factor = Utils.normalRelativeAngle(offsetAngle) / maxEscapeAngle(ew.bulletVelocity) * ew.direction;

        return (int) limit(0,
            (factor * ((BINS - 1) / 2)) + ((BINS - 1) / 2),
            BINS - 1
        );
    }
}
package u1529313.dodging;

import java.util.*;
import java.awt.geom.*;

import robocode.util.Utils;
import robocode.ScannedRobotEvent;
import robocode.BulletHitEvent;

import u1529313.Red;
import u1529313.utils.RedUtils;
import u1529313.utils.WaveBullet;
import u1529313.dodging.Dodging;

public class WaveSurfer extends Dodging {

    public static int BINS = 47;
    public static double _surfStats[] = new double[BINS];

    public Point2D.Double _myLocation; // our location
    public Point2D.Double _enemyLocation; // enemy location

    private ArrayList<EnemyWave> _enemyWaves;
    public ArrayList<Integer> _surfDirections;
    public ArrayList<Double> _surfAbsBearings;

    public static Rectangle2D.Double _fieldRect = new Rectangle2D.Double(18, 18, 764, 564);

    public static double WALL_STICK = 160;

    public WaveSurfer(Red r) {
        super(r);

        _enemyWaves = new ArrayList<EnemyWave>();
        _surfDirections = new ArrayList<Integer>();
        _surfAbsBearings = new ArrayList<Double>();
    }

    @Override
    public void handleOnScannedRobot(ScannedRobotEvent e)
    {
        _myLocation = new Point2D.Double(r.getX(), r.getY());
        double lateralVelocity = r.getVelocity() * Math.sin(e.getBearingRadians());

        r.turnRadarRightRadians(Utils.normalRelativeAngle(
            RedUtils.getEnemyAbsBearing(r,e) - r.getRadarHeadingRadians()
        ) * 2);

        _surfDirections.add(0, new Integer((lateralVelocity >= 0) ? 1 : -1));

        _surfAbsBearings.add(0, new Double(RedUtils.getEnemyAbsBearing(r,e) + Math.PI));

        // TODO: update bullet power depending on enemy bot

        double bulletPower = getPreviousEnergy(e.getName()) - e.getEnergy();

        if (
            bulletPower < 3.01 && bulletPower > 0.09 &&
            _surfDirections.size() > 2
        ) {
            System.out.println("SURFING");

            EnemyWave ew = new EnemyWave();

            ew.fireTime = r.getTime() - 1;
            ew.bulletVelocity = RedUtils.bulletVelocity(bulletPower);
            ew.distanceTraveled = RedUtils.bulletVelocity(bulletPower);
            ew.direction = (_surfDirections.get(2)).intValue();
            ew.directAngle = (_surfAbsBearings.get(2)).doubleValue();
            ew.fireLocation = (Point2D.Double) _enemyLocation.clone(); // last tick

            _enemyWaves.add(ew);
        }

        _enemyLocation = RedUtils.project(_myLocation, RedUtils.getEnemyAbsBearing(r,e), e.getDistance());

        updateWaves();
        doSurfing();
    }

    @Override
    public void handleOnBulletHit(BulletHitEvent e) {
        // if empty we must have missed it somehow
        if (!_enemyWaves.isEmpty())
        {
            Point2D.Double hitBulletLocation = new Point2D.Double(
                e.getBullet().getX(),
                e.getBullet().getY()
            );

            EnemyWave hitWave = null;

            for (int i = 0; i < _enemyWaves.size(); i++)
            {
                EnemyWave ew = _enemyWaves.get(i);

                if (
                    Math.abs(ew.distanceTraveled - _myLocation.distance(ew.fireLocation)) < 50 &&
                    Math.abs(RedUtils.bulletVelocity(e.getBullet().getPower()) - ew.bulletVelocity) < 0.001
                ) {
                    hitWave = ew;
                    break;
                }
            }

            if (hitWave != null) {
                logHit(hitWave, hitBulletLocation);

                // we can remove wave now
                _enemyWaves.remove(_enemyWaves.lastIndexOf(hitWave));
            }
        }
    }

    private double wallSmoothing(Point2D.Double botLocation, double angle, int orientation) 
    {
        while (!_fieldRect.contains(RedUtils.project(botLocation, angle, WALL_STICK))) {
            angle += orientation * 0.05;
        }
        return angle;
    }

    private Point2D.Double predictPosition(EnemyWave surfWave, int direction)
    {
        Point2D.Double predictedPosition = (Point2D.Double) _myLocation.clone();
        double predictedVelocity = r.getVelocity();
        double predictedHeading = r.getHeadingRadians();
        double maxTurning, moveAngle, moveDir;

        int counter = 0; // number of ticks in the future
        boolean intercepted = false;

        do {
            moveAngle =
                wallSmoothing(
                    predictedPosition,

                    RedUtils.absoluteBearing(
                        surfWave.fireLocation,
                        predictedPosition
                    ) + (direction * (Math.PI/2)),

                    direction) - predictedHeading;

            moveDir = 1;

            if (Math.cos(moveAngle) < 0) {
                moveAngle += Math.PI;
                moveDir = -1;
            }

            moveAngle = Utils.normalRelativeAngle(moveAngle);

            // maxTurning is built in like this, you can't turn more then this in one tick
            maxTurning = Math.PI / 720d * (40d - 3d * Math.abs(predictedVelocity));

            predictedHeading = Utils.normalRelativeAngle(
                predictedHeading + RedUtils.limit(-maxTurning, moveAngle, maxTurning)
            );
 
            // this one is nice ;). if predictedVelocity and moveDir have
            // different signs you want to breack down
            // otherwise you want to accelerate (look at the factor "2")
            predictedVelocity += (predictedVelocity * moveDir < 0 ? 2*moveDir : moveDir);

            predictedVelocity = RedUtils.limit(-8, predictedVelocity, 8);
 
            // calculate the new predicted position
            predictedPosition = RedUtils.project(
                predictedPosition,
                predictedHeading,
                predictedVelocity
            );
 
            counter++;
 
            if (
                predictedPosition.distance(surfWave.fireLocation) <
                surfWave.distanceTraveled + (counter * surfWave.bulletVelocity) + surfWave.bulletVelocity)
            {
                intercepted = true;
            }
        } while (!intercepted && counter < 500);

        return predictedPosition;
    }

    private double checkDanger(EnemyWave surfWave, int direction)
    {
        int index = RedUtils.getFactorIndex(BINS, surfWave, predictPosition(surfWave, direction));
        return _surfStats[index];
    }

    private void doSurfing()
    {
        EnemyWave surfWave = getClosestSurfableWave();

        if (surfWave == null) { return; } // return early

        double dangerLeft = checkDanger(surfWave, -1);
        double dangerRight = checkDanger(surfWave, 1);

        double goAngle = RedUtils.absoluteBearing(surfWave.fireLocation, _myLocation);
        if (dangerLeft < dangerRight) {
            goAngle = wallSmoothing(_myLocation, goAngle - RedUtils.HALF_PI, -1);
        } else {
            goAngle = wallSmoothing(_myLocation, goAngle + RedUtils.HALF_PI, 1);
        }

        RedUtils.setBackAsFront(r, goAngle);
    }

    private void updateWaves()
    {
        for (int x = 0; x < _enemyWaves.size(); x++) {
            EnemyWave ew = _enemyWaves.get(x);

            ew.distanceTraveled = (r.getTime() - ew.fireTime) * ew.bulletVelocity;
            if (ew.distanceTraveled > _myLocation.distance(ew.fireLocation) + 50)
            {
                _enemyWaves.remove(x);
                x--;
            }
        }
    }

    private EnemyWave getClosestSurfableWave()
    {
        double closestDistance = Double.POSITIVE_INFINITY;
        EnemyWave surfWave = null;

        for (int x = 0; x < _enemyWaves.size(); x++) {
            EnemyWave ew = _enemyWaves.get(x);
            double distance = _myLocation.distance(ew.fireLocation) - ew.distanceTraveled;

            if (distance > ew.bulletVelocity && distance < closestDistance) {
                surfWave = ew;
                closestDistance = distance;
            }
        }

        return surfWave;
    }

    private void logHit(EnemyWave ew, Point2D.Double targetLocation)
    {
        int index = RedUtils.getFactorIndex(BINS, ew, targetLocation);

        for (int i = 0; i < BINS; i++) {
            _surfStats[i] += 1.0 / (Math.pow(index - i, 2) + 1);
        }
    }

    public static class EnemyWave {
        public Point2D.Double fireLocation;
        public long fireTime;
        public double bulletVelocity, directAngle, distanceTraveled;
        public int direction;

        public EnemyWave() {}
    }

}
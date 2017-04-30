package u1529313.dodging;

import java.util.*;

import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.RobotDeathEvent;

import u1529313.Red;

/**
  * Abstract Dodging class, base of every dodging mechanism implementation.
  * Sublasses can access robot object using instance variable `r`
  *
  * subclasses should implement own version of onScannedRobot
  * which will be called when used.
 */
public abstract class Dodging {

    protected Red r;

    protected HashMap<String, Double> energyMap;

    public Dodging(Red robot) {
        r = robot;
        energyMap = new HashMap<String, Double>();
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        handleOnScannedRobot(e);
    }

    public void onBulletHit(BulletHitEvent e) {
        // update previous energies
        double bulletPower = e.getBullet().getPower();
        double previousEnergy = getPreviousEnergy(e.getName());
        previousEnergy -= Rules.getBulletDamage(bulletPower);
        updateEnergy(e.getName(), new Double(previousEnergy));

        handleOnBulletHit(e);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        double previousEnergy = getPreviousEnergy(e.getName());
        previousEnergy += Rules.getBulletHitBonus(e.getPower());
        updateEnergy(e.getName(), new Double(previousEnergy));

        handleOnHitByBullet(e);
    }

    public void onRobotDeath(RobotDeathEvent e) {
        // remove energy mapping for robot
        energyMap.remove(e.getName());
    }

    public void updateEnergy(String name, Double level) {
        energyMap.put(name, level); // no checks needed
    }

    public double getPreviousEnergy(String name) {
        Double prevEnergy = energyMap.get(name);

        // return 100 if not in memory
        return (prevEnergy == null) ? 100 : prevEnergy.doubleValue();
    }

    public void handleOnScannedRobot(ScannedRobotEvent e) {} // override in subclass
    public void handleOnBulletHit(BulletHitEvent e) {} // override in subclass
    public void handleOnHitByBullet(HitByBulletEvent e) {} // override in subclass
}
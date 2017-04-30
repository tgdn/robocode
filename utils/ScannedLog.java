
package u1529313.utils;

import robocode.ScannedRobotEvent;

import java.util.*;

// a log that holds the logs of different robots
public class ScannedLog {

    // a log entry
    public class EnemyEntry {
        private String n; // name
        private double v; // velocity
        private double e; // energy
        private double h; // heading

        public EnemyEntry(String name, double vel, double nrg, double heading) {
            n = name;
            v = vel;
            e = nrg;
            h = heading;
        }

        public String getName() {
            return n;
        }

        public double getVelocity() {
            return v;
        }

        public double getEnergy() {
            return e;
        }

        public double getHeading() {
            return h;
        }

        public String toString() {
            return "vel: " + v + " heading: " + h;
        }
    }

    // ==============================================

    private HashMap<String,List<EnemyEntry>> log;

    public ScannedLog() {
        log = new HashMap<String,List<EnemyEntry>>();
    }

    public List<EnemyEntry> getEntries(String robotname) {
        return log.get(robotname);
    }

    public void push(ScannedRobotEvent e) {
        EnemyEntry entry = new EnemyEntry(
            e.getName(),
            e.getVelocity(),
            e.getEnergy(),
            e.getHeading()
        );

        // try and get from hashmap
        List<EnemyEntry> elog = log.get(e.getName());

        if (elog == null) {
            // create log
            elog = new ArrayList<EnemyEntry>();
        }
        elog.add(entry);

        log.put(e.getName(), elog);
    }
}
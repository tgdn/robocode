
package u1529313.modes;

import robocode.ScannedRobotEvent;

import java.util.*;

// a log that holds the logs of different robots
public class ScannedLog {

	// a log
	public class EnemyEntry {
		private String n; // name
		private double v; // velocity
		private double h; // heading

		public EnemyEntry(String name, double vel, double heading) {
			n = name;
			v = vel;
			h = heading;
		}

		public String getName() {
			return n;
		}

		public double getVelocity() {
			return v;
		}

		public double getHeading() {
			return h;
		}

		public String toString() {
			return "vel: " + v + " heading: " + h;
		}
	}

	// ==============================================

	// array holding the 7 latest logs of one robot
	public class EnemyLog {
		private int current = 0;
		private EnemyEntry[] entries;

		public EnemyLog(EnemyEntry first) {
			entries = new EnemyEntry[7]; // use 7 from the doc
			entries[current] = first;
		}

		public EnemyEntry[] getEntries() {
			return entries;
		}

		public void push(EnemyEntry entry) {
			if (current == 6) {
				shiftArray();
			}
			// increment current index and insert new
			current += 1;
			entries[current] = entry;
		}

		private void shiftArray() {
			EnemyEntry[] newentries = new EnemyEntry[7];
			System.arraycopy(entries, 1, newentries, 0, 6);
			current -= 1;
			entries = newentries;
		}

		public int getCurrent() {
			return current;
		}

		public String toString() {
			return "length: " + (int)((int)current+1);
		}
	}

	// ==============================================

	private HashMap<String,EnemyLog> log;

	public ScannedLog() {
		log = new HashMap<String,EnemyLog>();
	}

	public EnemyLog getEntries(String robotname) {
		return log.get(robotname);
	}

	public void push(ScannedRobotEvent e) {
		EnemyEntry entry = new EnemyEntry(
			e.getName(),
			e.getVelocity(),
			e.getHeading()
		);

		EnemyLog elog = log.get(e.getName());
		if (elog == null) {
			elog = new EnemyLog(entry);
		} else {
			elog.push(entry);
		}

		log.put(e.getName(), elog);
	}
}
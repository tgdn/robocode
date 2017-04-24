package u1529313.modes;

import robocode.*;
import robocode.util.Utils;

import u1529313.Red;
import u1529313.modes.RedMode;
import u1529313.modes.RedWallCrawler;
import u1529313.modes.ScannedLog;

public class RedPatternMatch extends RedWallCrawler {

	private ScannedLog log;

	public RedPatternMatch(Red robot) {
		// init(robot);
		super(robot);
		log = new ScannedLog();
	}

	public void executeBeforeLoop() {
		super.executeBeforeLoop();
	}

	public void executeMainLoop() {
		super.executeMainLoop();
	}

	public void executeScannedRobot(ScannedRobotEvent e) {
		log.push(e);
		super.executeScannedRobot(e);

		ScannedLog.EnemyLog elog = log.getEntries(e.getName());
		if (elog != null) {
			ScannedLog.EnemyEntry[] entries = elog.getEntries();

			for (int i = 0; i <= elog.getCurrent(); i++) {
				ScannedLog.EnemyEntry entry = entries[i];

				System.out.println(entry);
			}
			System.out.println("==============");
		}
	}
}
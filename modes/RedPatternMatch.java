package u1529313.modes;

import java.util.*;

import robocode.*;
import robocode.util.Utils;

import u1529313.Red;
import u1529313.modes.RedMode;
import u1529313.modes.RedWallCrawler;
import u1529313.utils.ScannedLog;

public class RedPatternMatch extends RedWallCrawler {

    private ScannedLog log;

    public RedPatternMatch(Red robot) {
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

        // get entries for given robot
        List<ScannedLog.EnemyEntry> elog = log.getEntries(e.getName());

        // use a trained robot
        if (elog != null && elog.size() >= 20)
        {
            for (ScannedLog.EnemyEntry entry : elog) {
                System.out.println(entry);
            }
        }
    }
}
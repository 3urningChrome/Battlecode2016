package team038;

import battlecode.common.Clock;
import battlecode.common.RobotController;

public class RobotPlayer {
	public static void run(RobotController rc){
		switch (rc.getType()){
		case ARCHON:
			RobotArchon.loop(rc);
		case SOLDIER:
			RobotSoldier.loop(rc);
		default:
			System.out.println("Unknown robot spawned");
			while(true){
				Clock.yield();
			}
		}
	}
}

package EpsilonAlt;

import battlecode.common.Clock;
import battlecode.common.RobotController;

public class RobotPlayer {
	public static void run(RobotController rc){
		switch (rc.getType()){
		case ARCHON:
			RobotArchon.loop(rc);
			break;
		case SOLDIER:
			RobotSoldier.loop(rc);
		case SCOUT:
			RobotScout.loop(rc);	
		case TURRET:
			RobotTurret.loop(rc);				
		default:
			System.out.println("Unknown robot spawned");
			while(true){
				Clock.yield();
			}
		}
	}
}

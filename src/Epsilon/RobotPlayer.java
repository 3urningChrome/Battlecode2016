package Epsilon;

import battlecode.common.RobotController;

public class RobotPlayer {
	public static void run(RobotController rc){
		switch (rc.getType()){
		case ARCHON:
			RobotArchon.loop(rc);
			break;
		case SOLDIER:
			RobotSoldier.loop(rc);
			break;
		case SCOUT:
			RobotScout.loop(rc);	
			break;
		case TURRET:
			RobotTurret.loop(rc);	
			break;
		case TTM:
			RobotTurret.loop(rc);
			break;
		default:
			System.out.println("Unknown robot spawned");
			RobotDefault.loop(rc);
		}
	}
}

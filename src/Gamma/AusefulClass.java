package Gamma;

import battlecode.common.*;

import java.util.Iterator;
import java.util.Random;

public class AusefulClass {
	
	static RobotController rc;
	
	static final Direction[] DIRECTIONS = {Direction.NORTH,Direction.SOUTH, Direction.EAST, Direction.WEST,Direction.NORTH_EAST,Direction.SOUTH_WEST, Direction.SOUTH_EAST, Direction.NORTH_WEST};
	
	static Team friendly;
	static Team enemy;
	static Team neutral;
	static Team zombie;
	
	static MapLocation previous_location;
	static MapLocation current_location;
	static MapLocation destination;
	static MapLocation location_of_archon;
	
	static RobotType my_type;
	static int byte_code_limiter;
	
	static Random rnd;
		
	public static void init(RobotController the_rc) {
		rc = the_rc;
		
		friendly = rc.getTeam();
		enemy = friendly.opponent();
		neutral = Team.NEUTRAL;
		zombie = Team.ZOMBIE;
		
		current_location = rc.getLocation();
		previous_location = current_location.add(Direction.NORTH);
		
		my_type = rc.getType();
		byte_code_limiter = my_type.bytecodeLimit;
		
		rnd = new Random(rc.getID());
		
		location_of_archon = current_location;

	}
	
	public static void yield(){
//		if(my_type == RobotType.ARCHON){
//			rc.setIndicatorString(0, "Gamma 1.3");
//			//rc.setIndicatorLine(current_location, destination, 0, 125, 125);
//		}else{
//			rc.setIndicatorString(0, destination.toString());
//			//rc.setIndicatorString(1, NavSimpleMove.life_insurance_policy.toString());
//			//rc.setIndicatorLine(current_location, destination, 0, 125, 125);
//		}
//		
//		if(my_type == RobotType.SCOUT)
//			if(!Communications.exclusion_zones.isEmpty())
//				for (Iterator<MapLocation> test = Communications.exclusion_zones.iterator(); test.hasNext();)
//					rc.setIndicatorDot(test.next(), 255, 0, 150);

		if(rc.getRoundNum()%25 == 0)
			Scanner.reset_health();
		
		Clock.yield();
		
//		rc.setIndicatorString(0,"");
//		rc.setIndicatorString(1,"");
//		rc.setIndicatorString(2,"");
	}
	
	public static Direction randomDirection(){
		return Direction.values()[(int)(rnd.nextDouble()*8)];
	}
}

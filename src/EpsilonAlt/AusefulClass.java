package EpsilonAlt;

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
	
	static MapLocation previous_location = null;
	static MapLocation current_location = null;
	static MapLocation destination = null;
	static MapLocation location_of_archon = null;
	
	static RobotType my_type;
	static int byte_code_limiter;
	
	static Random rnd;
		
	public static void init(RobotController the_rc) {
		rc = the_rc;
		
		friendly = rc.getTeam();
		enemy = friendly.opponent();
		neutral = Team.NEUTRAL;
		zombie = Team.ZOMBIE;
		
		my_type = rc.getType();
		byte_code_limiter = my_type.bytecodeLimit;
		
		rnd = new Random(rc.getID());
		
		current_location = rc.getLocation();
		destination = current_location;
		
		location_of_archon = current_location;

	}
	
	public static void yield(){	
		
		//rc.setIndicatorLine(current_location, destination, 120, 120, 120);
		
		if(my_type == RobotType.ARCHON)
			rc.setIndicatorString(0,"Version: Epsilon 1.0");
		
		Clock.yield();
		
		rc.setIndicatorString(0, "");
		rc.setIndicatorString(1, "");
		rc.setIndicatorString(2, "");
	}
	
	public static Direction randomDirection(){
		return Direction.values()[(int)(rnd.nextDouble()*8)];
	}
}

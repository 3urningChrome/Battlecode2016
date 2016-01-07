package Beta;

import battlecode.common.*;

public class AusefulClass {
	
	static RobotController rc;
	
	static final Direction[] DIRECTIONS = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	
	static Team friendly;
	static Team enemy;
	static Team neutral;
	static Team zombie;
	
	static MapLocation current_location;
	static MapLocation destination;
	static MapLocation location_of_archon;
	
	static RobotType my_type;
	static int byte_code_limiter;
		
	public static void init(RobotController the_rc) {
		rc = the_rc;
		
		friendly = rc.getTeam();
		enemy = friendly.opponent();
		neutral = Team.NEUTRAL;
		zombie = Team.ZOMBIE;
		
		current_location = rc.getLocation();
		
		my_type = rc.getType();
		byte_code_limiter = my_type.bytecodeLimit;
	}
	
	public static void yield(){
		if(my_type == RobotType.ARCHON)
			rc.setIndicatorString(0, "Beta 1.1");
		Clock.yield();
	}
}

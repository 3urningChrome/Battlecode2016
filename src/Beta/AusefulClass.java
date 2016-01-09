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
	
	static double max_X = Double.POSITIVE_INFINITY;
	static double min_X = Double.NEGATIVE_INFINITY;
	static double max_Y = Double.POSITIVE_INFINITY;
	static double min_Y = Double.NEGATIVE_INFINITY;
		
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
			rc.setIndicatorString(0, "Beta 1.5");
		
		if(rc.getRoundNum()%25 == 0)
			Scanner.reset_health();
		
		Clock.yield();
	}
	
	public static void clear_rubble() throws GameActionException{
		if(!rc.isCoreReady())
			return;
		if(Scanner.can_see_targets())
			return;
		if(Scanner.there_is_hidden_enemy())
			return;
		
		Direction direction_to_clear = current_location.directionTo(destination);
		
		if(direction_to_clear == Direction.NONE || direction_to_clear == Direction.OMNI)
			return;
		
		if (rc.senseRubble(current_location.add(direction_to_clear)) < 1)
			return;
		
		rc.clearRubble(direction_to_clear);
		rc.setIndicatorString(1, "Clearing: " + direction_to_clear.toString() );
	}
}

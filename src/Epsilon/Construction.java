package Epsilon;

import battlecode.common.*;

public class Construction extends AusefulClass{
	
    static int[] possible_build_directions = {0, 1, -1, 2, -2, 3, -3, 4};	
	
	protected static boolean i_cannot_build(Direction in_direction, RobotType the_type){
		
		if (!rc.isCoreReady())
			return true;
		
		if (in_direction == Direction.NONE)
			return true;
		
		if (in_direction == Direction.OMNI)
			return true;		
		
		if (!rc.canBuild(in_direction, the_type))
			return true;
		
		return false;		
	}
	
	protected static Direction get_a_decent_build_direction() throws GameActionException {
		return randomDirection();
	}
}
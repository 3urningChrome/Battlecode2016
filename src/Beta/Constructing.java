package Beta;

import battlecode.common.*;

public class Constructing extends AusefulClass{
	
    static int[] possible_build_directions = {0, 1, -1, 2, -2, 3, -3, 4};	
	
	protected static boolean i_cannot_build(Direction in_direction, RobotType the_type){
		
		if (!rc.isCoreReady())
			return true;
		
		if (in_direction == Direction.NONE)
			return true;
		
		if (!rc.canBuild(in_direction, the_type))
			return true;
		
		return false;		
	}
	
	protected static Direction get_a_decent_build_direction() throws GameActionException {
		//build in direction from north round
		Direction starting_direction = Direction.NORTH;
		
		//build in direction of rally point
		Direction to_rally_point = current_location.directionTo(Communications.get_rally_point());
		if (to_rally_point!=Direction.OMNI)
			starting_direction = to_rally_point;
		
		//build in direction of nearest enemy
		RobotInfo closest_enemy = Scanner.find_closest_enemy();
		if(closest_enemy != null)
			starting_direction = current_location.directionTo(closest_enemy.location);

		for (int build_direction_modifier:possible_build_directions){
			Direction test_direction = DIRECTIONS[(starting_direction.ordinal()+(build_direction_modifier)+8)%8];
			if(!rc.isLocationOccupied(current_location.add(test_direction)))
				if(rc.onTheMap(current_location.add(test_direction)))
					return test_direction;	
		}
		return Direction.NONE;
	}	
}
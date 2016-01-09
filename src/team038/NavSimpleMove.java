package team038;

import battlecode.common.*;

public class NavSimpleMove extends AusefulClass {

	static int[] try_directions = {0,1,-1,2,-2};
	static Safety life_insurance_policy = Safety.NONE;
	
	public static void go_towards_destination() throws GameActionException{
		if(!rc.isCoreReady())
			return;
		
		Direction direction_to_head = current_location.directionTo(destination);
		for(int offset:try_directions){
			Direction try_to_walk = Direction.values()[(direction_to_head.ordinal() + offset + 8)%8];
			if (rc.canMove(try_to_walk)){
				if(life_insurance_policy.says_this_move_will_shorten_your_life(current_location.add(try_to_walk)))
					continue;
				rc.move(try_to_walk);
				return;
			}
		}	
		if(Scanner.says_targets_are_in_range())
			return;
		
		if(rc.senseRubble(destination) < GameConstants.RUBBLE_OBSTRUCTION_THRESH)
			return;
		rc.clearRubble(direction_to_head);
	}
}

/**
Max Nav. 
Ripped straight from Max's lecture
*/
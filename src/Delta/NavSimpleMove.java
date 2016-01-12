package Delta;

import battlecode.common.*;

public class NavSimpleMove extends AusefulClass {

	static int[] try_directions = {0,1,-1,2,-2,3,-3};
	static int[] combat_directions = {0,1,-1};
	static int[] dig_directions = {0,1,-1,2,-2,3,-3,4};
	
	static boolean combat_mode = false;

	static Safety life_insurance_policy = Safety.NONE;
	
	public static void go_towards_destination() throws GameActionException{
		if(!rc.isCoreReady())
			return;
		
		if(destination==null)
			return;
		
		if(destination.equals(current_location))
			return;
//TODO alter try directions so we use non-diagonal if possible.	
		Direction direction_to_head = current_location.directionTo(destination);
		for(int offset:(combat_mode? combat_directions : try_directions)){
			Direction try_to_walk = Direction.values()[(direction_to_head.ordinal() + offset + 8)%8];
			if (rc.canMove(try_to_walk)){
				if(life_insurance_policy.says_this_move_will_shorten_your_life(current_location.add(try_to_walk)))
					continue;
				previous_location = current_location;
				if (rc.canMove(try_to_walk)){
					rc.move(try_to_walk);
				} else{
					System.out.println("Running over byte limit");
				}
				return;
			}
		}	
		
		if(Scanner.says_there_are_targets_in_range())
			return;
		
		for(int offset:dig_directions){
			Direction try_to_clear = Direction.values()[(direction_to_head.ordinal() + offset + 8)%8];
			if(rc.senseRubble(current_location.add(try_to_clear)) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH){
				rc.clearRubble(try_to_clear);
				return;
			}
		}
	}
}

/**
Max Nav. 
Ripped straight from Max's lecture
*/
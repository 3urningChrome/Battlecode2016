package Alpha;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class RobotArchon extends AusefulClass {
	
	private static BuildStrategy the_plan;
	static MapLocation temp_destination;
	
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
		temp_destination = current_location.add(Direction.NORTH, 50);
		
		the_plan = BuildStrategy.SOLDIER_RUSH;
		
		while(true){
			try{
				turn();
			} catch (Exception e){
				e.printStackTrace();
			}
			yield();
		}
	}

	private static void turn() throws GameActionException {
		current_location = rc.getLocation();
		the_plan.I_am_building();
		
		repair();
		
		if(Scanner.says_no_targets_are_in_range()){
			Navigation.go_to(temp_destination);
		} else{
			Navigation.move_away_from(Scanner.find_closest_enemy().location);
		}
		
		if(NavigationBase.current_navigation_state == NavBugState.UNREACHABLE && rc.isCoreReady())
			rc.clearRubble(current_location.directionTo(temp_destination));		
	}
	
	private static void repair() throws GameActionException{
		RobotInfo[] friends = Scanner.scan_for_friends_in_range();
		if(friends!=null)
			for(RobotInfo my_special_friend:friends)
				if(my_special_friend.health < my_special_friend.maxHealth && my_special_friend.type != RobotType.ARCHON){
					rc.repair(my_special_friend.location);
					return;
				}
	}
}

/**
update position
supply
shoot
Move {kite, press, stop, retreat}
processing
*/
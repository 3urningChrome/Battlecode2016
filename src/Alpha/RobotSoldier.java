package Alpha;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class RobotSoldier extends AusefulClass {
		
	static MapLocation temp_destination;
	
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
		NavigationBase.life_insurance_policy = Safety.AVOID_NON_FRIENDS;
		temp_destination = current_location.add(Direction.NORTH, 50);
		
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
		
		if(Scanner.says_no_targets_are_in_range()){
			Navigation.go_to(temp_destination);
			rc.setIndicatorString(1, NavigationBase.current_navigation_state.toString());
		}
		
		if(NavigationBase.current_navigation_state == NavBugState.UNREACHABLE && rc.isCoreReady())
			if(rc.onTheMap(current_location.add(current_location.directionTo(temp_destination))))
				rc.clearRubble(current_location.directionTo(temp_destination));
		
		FireControl.shoot_closest_enemy();
		FireControl.shoot_closest_zombie();
	}
}

/**
update position
supply
shoot
Move {kite, press, stop, retreat}
processing
*/
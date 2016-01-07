package Beta;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class RobotSoldier extends AusefulClass {
			
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
		NavigationBase.life_insurance_policy = Safety.DONT_ADVANCE_PAST_MAX_RANGE;
		NavigationBase.current_navigation_state = NavBugState.DIRECT;
		
		MapLocation location_of_archon = Communications.find_closest_Archon();
		destination = current_location;
				
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
		rc.setIndicatorString(2, "");
		
		Communications.log_enemies();
		
		FireControl.shoot_closest_zombie();
		FireControl.shoot_deadest_enemy();
				
		Communications.find_closest_Archon();
		if(location_of_archon == null)
			location_of_archon = current_location;
		
		rc.setIndicatorLine(current_location, location_of_archon, 0, 255, 0);
		
		RobotInfo closest_friend = Utilities.find_closest_RobotInfo(Scanner.scan_for_friend());
		if(closest_friend != null)
			destination = current_location.add(current_location.directionTo(closest_friend.location).opposite(),5);
		
		MapLocation closest_distress_call = Communications.find_closest_distress();
		if (closest_distress_call != null)
			destination = closest_distress_call;
		
		if(Scanner.can_see_targets())
			destination = Scanner.find_closest_enemy().location;
		
		if(rc.getHealth() < my_type.maxHealth*0.75)
			destination = location_of_archon;
		
		Navigation.go_to(destination);
		rc.setIndicatorLine(current_location, destination, 0, 125, 125);
		
		Direction away_from_archon = current_location.directionTo(location_of_archon).opposite();
		if(rc.isCoreReady() && !current_location.equals(location_of_archon))
			if(rc.onTheMap(current_location.add(away_from_archon)) && rc.senseRubble(current_location.add(away_from_archon)) > GameConstants.RUBBLE_SLOW_THRESH){
				rc.clearRubble(away_from_archon);
				rc.setIndicatorString(2, "Clearing: " + away_from_archon.toString() );
			}
//					
		rc.setIndicatorString(1, NavigationBase.current_navigation_state.toString());
	}
}

/**
repair
log enemies (log my position)
broadcast position (if see enemy/taking damage)
move(towards danger/enemy, towards goal, away from Archon (to create space)
*/
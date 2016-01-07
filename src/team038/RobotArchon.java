package team038;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class RobotArchon extends AusefulClass {
	
	private static BuildStrategy the_plan;
	
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
		destination = current_location.add(Direction.NORTH, 50);
		
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
		rc.setIndicatorString(1,"");
	
		repair();
		Communications.log_enemies();
		Communications.broadcast_my_position();
		
		destination = current_location.add(Direction.NORTH,10);  //need better plan!
		
		if(Scanner.cant_see_targets())
			the_plan.I_am_building();
		
		if (Scanner.sense_parts()){
			destination = Scanner.parts_location;
			rc.setIndicatorDot(destination, 0, 255, 0);
		}
		
		if(Scanner.cant_see_targets()){
			Navigation.go_to(destination);
		} else{
			Navigation.move_away_from(Scanner.find_closest_enemy().location);
		}
		
//		if(NavigationBase.current_navigation_state == NavBugState.UNREACHABLE && rc.isCoreReady() && !destination.equals(current_location) && rc.onTheMap(current_location.add(current_location.directionTo(destination)))){
//			rc.setIndicatorString(2, "Clearing: " + current_location.directionTo(destination).toString());
//			rc.clearRubble(current_location.directionTo(destination));		
//		}
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
buildingplan
repair
log enemies
broadcast position
move(toward parts, away from enemy, towards goal)
sense parts

*/
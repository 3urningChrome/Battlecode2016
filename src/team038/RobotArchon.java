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
		
		NavSimpleMove.life_insurance_policy = Safety.AVOID_ALL_NON_FRIENDS;
		
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
		
		repair();
		Communications.log_enemies();
		Communications.broadcast_my_position();
		Communications.broadcast_known_exclusion();
				
		MapLocation closest_Comms_data = Communications.find_closest_fight();
		if (closest_Comms_data != null){
			if(closest_Comms_data.distanceSquaredTo(current_location) < 64)
				destination = current_location.add(current_location.directionTo(closest_Comms_data).opposite(),2);
		}
			
		if(Scanner.cant_see_targets()){
			activate_neutral();
			the_plan.I_am_building();
		}
		
		if (Scanner.sense_parts()){
			destination = Scanner.parts_location;
			rc.setIndicatorDot(destination, 0, 255, 0);
		} else{
			MapLocation communicated_closest_parts = Communications.find_closest_parts();
			if(communicated_closest_parts != null)
				destination = communicated_closest_parts;
		}
		
		if(Scanner.find_closest_neutral() != null){
			destination = Scanner.find_closest_neutral().location;
			rc.setIndicatorDot(destination, 0, 0, 255);
		}
		
		if(Scanner.can_see_targets())
			destination = current_location.add(current_location.directionTo(Scanner.find_closest_hostile().location).opposite(),5);
		
		NavSimpleMove.go_towards_destination();	
	}
	
	private static void activate_neutral() throws GameActionException {
		RobotInfo closest_neutral_robot = Scanner.find_closest_neutral();

		if(!rc.isCoreReady())
			return;
				
		if(closest_neutral_robot == null)
			return;
		
		if(!closest_neutral_robot.location.isAdjacentTo(current_location))
			return;
		
		rc.activate(closest_neutral_robot.location);
		rc.setIndicatorString(1, "Activated: closest_neutral_robot.location");
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
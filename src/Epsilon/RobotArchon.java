package Epsilon;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class RobotArchon extends AusefulClass {
	
	private static BuildStrategy the_plan;
	
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
				
		the_plan = BuildStrategy.SOLDIER_TURRET;
		//the_plan = BuildStrategy.SOLDIER_RUSH;
		NavSimpleMove.life_insurance_policy = Safety.ARCHON;
		
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
		repair();
		
		if(Scanner.can_see_dangerous_hostiles()){
			byte_code_limiter = 12000;
			destination = current_location.add(current_location.directionTo(Scanner.find_closest_hostile().location).opposite(),5);
			Communications.log_distress_call();
		} else{
			byte_code_limiter = 20000;
			activate_neutral();
			the_plan.I_am_building();
			
			RobotInfo closest_neutral = Scanner.find_closest_neutral();
			if(closest_neutral != null)
				destination = closest_neutral.location;
			
			MapLocation closest_parts_location = Scanner.find_closest_parts();
			rc.setIndicatorString(1, "Parts: " + Scanner.parts_locations.size());
			if(closest_parts_location != null){
				destination = closest_parts_location;
				if(rc.canSenseLocation(destination))
					if(rc.senseParts(destination) < 1)
						Scanner.parts_locations.remove(destination);
			}			
		}
		Communications.update_communications();
		NavSimpleMove.go_towards_destination();
		
		//message	
		Communications.broadcast_known_exclusion();		
		Communications.broadcast_my_position();
		
		if(Scanner.cant_see_hostiles()){
			Scanner.sense_parts();
		}
		
		//debugging:
//		if(!Communications.exclusion_zones.isEmpty()){
//			System.out.println("");
//			System.out.println("*************************************");
//			System.out.println("");
//			for (Iterator<MapLocation> test = Communications.exclusion_zones.iterator(); test.hasNext();){
//				MapLocation exclusion_test = test.next();
//				System.out.println(exclusion_test.toString());
//			}
//			System.out.println("");
//			System.out.println("*************************************");
//			System.out.println("");
//		}
		
	}
	
	private static void activate_neutral() throws GameActionException {

		if(!rc.isCoreReady())
			return;
				
		RobotInfo closest_neutral_robots[] = Scanner.scan_for_neutrals_in_range();
		
		if(closest_neutral_robots.length < 1)
			return;

		//pick Archon first
		rc.activate(closest_neutral_robots[0].location);
		rc.setIndicatorString(1, "Activated: " + closest_neutral_robots[0].location);
	}

	private static void repair() throws GameActionException{
		RobotInfo friend_to_heal = Scanner.find_injured_friend_in_range();
		
		if(friend_to_heal == null)
			return;
		
		rc.repair(friend_to_heal.location);
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
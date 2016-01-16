package Turtle;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class RobotArchon extends AusefulClass {
	
	private static BuildStrategy the_plan;
	private static Direction[] archon_positions = {Direction.NORTH,Direction.SOUTH,Direction.EAST,Direction.WEST};
	
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
				
		the_plan = BuildStrategy.TURTLE_POWER;
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
		
		destination = my_Archon_centre;
		if(!current_location.equals(destination) && rc.canSense(destination)){
			if(rc.senseRobotAtLocation(destination) != null){
				for(Direction next_direction:archon_positions){
					destination = my_Archon_centre.add(next_direction);
					
					if(current_location.equals(destination))
						break; //i'm here all good
					
					
					if(rc.canSense(destination) && rc.senseRobotAtLocation(destination) == null)
						break; //we go here
				}
			}
		}
		
		
		if(Scanner.can_see_dangerous_hostiles()){
			if(current_location.distanceSquaredTo(my_Archon_centre)< 9){
				System.out.println("Close");
				byte_code_limiter = 12000;
				Communications.override_comms = true;
				Communications.broadcast_enemy_locations();
				Communications.override_comms = false;
			}
		} else{
			byte_code_limiter = 20000;
			activate_neutral();
			the_plan.I_am_building();
			
		}
		Communications.update_communications();
		NavSimpleMove.combat_mode = true; // limits dir choice to 0,1,-1
		NavSimpleMove.go_towards_destination();
		
		//message	
		Communications.broadcast_known_exclusion();		
		Communications.broadcast_my_position();
				
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
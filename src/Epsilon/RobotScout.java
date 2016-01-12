package Epsilon;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotScout extends AusefulClass {
	
	static final Direction[] scouting_directions = {Direction.NORTH, Direction.SOUTH,Direction.EAST,Direction.WEST,Direction.NORTH_EAST,Direction.SOUTH_WEST,Direction.NORTH_WEST,Direction.SOUTH_EAST};
	static int scouting_direction = 0;
	static boolean outbound = true;
	static boolean sensed_parts = false;
			
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
		
		NavSimpleMove.life_insurance_policy = Safety.GHOST;

		scouting_direction = randomDirection().ordinal();
		destination = current_location.add(scouting_directions[scouting_direction],2);
				
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
		//set up round
		byte_code_limiter = RobotType.SCOUT.bytecodeLimit;
		
		if(Scanner.can_see_hostiles()){
			byte_code_limiter = 12000;
			if(Scanner.hostiles_can_attack_me())
				outbound = false;
			
			Communications.log_distress_call();
		}

		if(outbound){
			destination = location_of_archon.add(scouting_directions[scouting_direction],100);
			if(!rc.onTheMap(current_location.add(scouting_directions[scouting_direction],5)))
				outbound = false;	
		} else{
			destination = location_of_archon;
			if(current_location.distanceSquaredTo(destination) < 4){
				outbound = true;
				//scouting_direction = (scouting_direction + 1)%8;
				scouting_direction = randomDirection().ordinal();
				burst_comms();
			}
		}
								
		if(rc.getHealth() < my_type.maxHealth*0.33 || NavSimpleMove.life_insurance_policy == Safety.RETREAT){
			destination = location_of_archon;
			NavSimpleMove.life_insurance_policy = Safety.RETREAT;
		}	
		
		NavSimpleMove.go_towards_destination();
		
		Communications.update_communications();
		
		if(rc.getHealth() > my_type.maxHealth*0.95)
			NavSimpleMove.life_insurance_policy = Safety.GHOST;	
		
		Scanner.log_turrets();
		
		Communications.broadcast_known_exclusion();
		
		
		if(Scanner.cant_see_hostiles())
			Scanner.sense_parts();
		
		rc.setIndicatorString(0, "Heading: " + scouting_directions[scouting_direction].toString());
	}
	private static void burst_comms() throws GameActionException {
		Communications.override_comms = true;
		Communications.burst_neutral();
		Communications.burst_parts();
		Communications.broadcast_known_exclusion();
		Communications.override_comms = false;
	}

	private static void Combat_micro() throws GameActionException {
		//Can see hostiles.
		//3 choices forward. still, back.
		//limited to 2k bytes (for max eff)
		if(Scanner.no_hostiles_can_attack_me())
			return;
		
		destination = current_location.add(current_location.directionTo(Scanner.find_closest_hostile().location).opposite(),2);
	}
}

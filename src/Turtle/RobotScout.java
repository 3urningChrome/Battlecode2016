package Turtle;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotScout extends AusefulClass {
	
	static MapLocation[] places = {new MapLocation(1,1),new MapLocation(-1,-1),new MapLocation(-1,1),new MapLocation(1,-1)};
			
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
		
		NavSimpleMove.life_insurance_policy = Safety.GHOST;
				
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
		
		destination = my_Archon_centre;
		if(rc.canSense(destination))
			for(MapLocation testLocation:places){
				MapLocation test_destination = my_Archon_centre.add(testLocation.x,testLocation.y);
				if(rc.canSense(test_destination) && rc.onTheMap(test_destination)){
					if(rc.senseRobotAtLocation(test_destination) == null){
						destination = test_destination;
					}
				}
			}
		
		if(current_location.equals(destination)){
			NavSimpleMove.dig(current_location);
		}
		
		if(Scanner.can_see_hostiles()){
			byte_code_limiter = 12000;
			if(current_location.distanceSquaredTo(my_Archon_centre)< 9){
				Communications.override_comms = true;
				Communications.broadcast_enemy_locations();
				Communications.override_comms = false;
			}			
		}
		
		if(current_location.distanceSquaredTo(destination) < 4){
			NavSimpleMove.head_directly_towards_destination();
		} else{
			NavSimpleMove.go_towards_destination();
		}
		
		Communications.update_communications();
		
		Communications.broadcast_known_exclusion();
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

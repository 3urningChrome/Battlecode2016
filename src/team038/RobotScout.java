package team038;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class RobotScout extends AusefulClass {
	
	static final Direction[] scouting_directions = {Direction.NORTH, Direction.SOUTH,Direction.EAST,Direction.WEST,Direction.NORTH_EAST,Direction.SOUTH_WEST,Direction.NORTH_WEST,Direction.SOUTH_EAST};
	static int turns_in_current_direction = 0;
	static int scouting_direction = 0;
	static final int max_scouting_direction_turns = 200;
	static boolean sensed_parts = false;
			
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
		
		NavSimpleMove.life_insurance_policy = Safety.AVOID_ALL_NON_FRIENDS;

		Communications.find_closest_Archon();
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
	//set up round
		current_location = rc.getLocation();
		
		if(rc.getHealth() > my_type.maxHealth*0.90)
			NavSimpleMove.life_insurance_policy = Safety.GHOST;		
		
	//Read Comms
		Communications.log_enemies();
		Communications.find_closest_Archon();
		
	//Navigation, Find next Destination.
		//head in direction n,s,e,w,ne,sw,nw,se depending on clock. 
		turns_in_current_direction +=1;
		if(turns_in_current_direction >= max_scouting_direction_turns){
			turns_in_current_direction = 0;
			scouting_direction = (scouting_direction+1)%8;
		}
		destination = location_of_archon.add(scouting_directions[scouting_direction],50);
		
		if(Scanner.can_see_targets()){
			destination = Scanner.find_closest_hostile().location;
			
			RobotInfo closest_hostile = Scanner.find_closest_hostile();
			if(closest_hostile.location.distanceSquaredTo(current_location) <= closest_hostile.type.sensorRadiusSquared && closest_hostile.type != RobotType.ZOMBIEDEN)
				destination = current_location.add(current_location.directionTo(closest_hostile.location).opposite(),5);
		}
		
				
		if(rc.getHealth() < my_type.maxHealth*0.66 || NavSimpleMove.life_insurance_policy == Safety.RETREAT){
			destination = location_of_archon;
			NavSimpleMove.life_insurance_policy = Safety.RETREAT;
		}
		
		if(Scanner.can_see_turrets())
			Communications.broadcast_turret_exclusion();
		
		System.out.println("Bytes left before nav: " + Clock.getBytecodesLeft());
		NavSimpleMove.go_towards_destination();
		
		System.out.println("Bytes left after nav: " + Clock.getBytecodesLeft());
		
		sensed_parts = Scanner.sense_parts();
		if(Scanner.cant_see_targets())
			Communications.broadcast_parts();
		
		System.out.println("Bytes left after parts comms: " + Clock.getBytecodesLeft());
	}
}

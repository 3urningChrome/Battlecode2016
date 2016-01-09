package team038;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class RobotSoldier extends AusefulClass {
			
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
		
		NavSimpleMove.life_insurance_policy = Safety.KITE;

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
		
		if(rc.getHealth() > my_type.maxHealth*0.70)
			NavSimpleMove.life_insurance_policy = Safety.KITE;		
		
	//Read Comms
		Communications.log_enemies();
		Communications.find_closest_Archon();
			
	//shoot
		FireControl.shoot_deadest_zombie();
		FireControl.shoot_deadest_enemy();
		
	//Navigation, Find next Destination.
		RobotInfo closest_friend = Utilities.find_closest_RobotInfo(Scanner.scan_for_friend());
		if(closest_friend != null)
			destination = current_location.add(current_location.directionTo(closest_friend.location).opposite(),5);
		
		MapLocation closest_Comms_data = Communications.find_closest_fight();
		if (closest_Comms_data != null){
			destination = closest_Comms_data;
			rc.setIndicatorDot(destination, 255, 0, 0);
		}
		
		if(Scanner.can_see_targets()){
			destination = Scanner.find_closest_hostile().location;
			
			RobotInfo closest_hostile = Scanner.find_closest_hostile();
			if(closest_hostile.location.distanceSquaredTo(current_location) <= closest_hostile.type.attackRadiusSquared && closest_hostile.type != RobotType.ZOMBIEDEN)
				destination = current_location.add(current_location.directionTo(closest_hostile.location).opposite(),1);
		}
		
		if(rc.getHealth() < my_type.maxHealth*0.33 || NavSimpleMove.life_insurance_policy == Safety.RETREAT){
			destination = location_of_archon;
			NavSimpleMove.life_insurance_policy = Safety.RETREAT;
		}
		
		NavSimpleMove.go_towards_destination();
	}
}

/**
*/
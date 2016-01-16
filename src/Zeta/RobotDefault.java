package Zeta;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class RobotDefault extends AusefulClass {
			
	static int my_previous_health;
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
		
		NavSimpleMove.life_insurance_policy = Safety.KITE;
		my_previous_health = (int) my_type.maxHealth;

		Communications.update_communications();
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
		byte_code_limiter = my_type.bytecodeLimit;
		
		if(Scanner.can_see_hostiles()){
			byte_code_limiter = 2000;
			NavSimpleMove.combat_mode = true;
			//move and or shoot only.
			Combat_micro(); //sets destination
			NavSimpleMove.go_towards_destination();
			FireControl.shoot_deadest_enemy();
			Scanner.log_turrets();
			Communications.log_distress_call();
			my_previous_health = (int) rc.getHealth();
		} else{
			NavSimpleMove.combat_mode = false;
			RobotInfo closest_friend = Scanner.find_closest_friend();
			if(closest_friend != null){
				//destination = current_location.add(current_location.directionTo(closest_friend.location).opposite(),2);
				destination = closest_friend.location.add(closest_friend.location.directionTo(current_location),6);
			}
				
			MapLocation closest_Comms_data = Utilities.find_closest_MapLocation(Communications.distress_zones);
			if(closest_Comms_data == null)
				closest_Comms_data = Utilities.find_closest_MapLocation(Communications.chatter_zones);
			
			if (closest_Comms_data != null){
				destination = closest_Comms_data;
				if(rc.canSense(destination))
					if(Communications.chatter_zones.contains(closest_Comms_data))
						Communications.chatter_zones.remove(closest_Comms_data);
						
				if(Communications.distress_zones.contains(closest_Comms_data))
					Communications.distress_zones.remove(closest_Comms_data);
			}	
						
			if(my_previous_health != (int) rc.getHealth()){
				 NavSimpleMove.life_insurance_policy = Safety.RETREAT;
				 my_previous_health = (int) rc.getHealth();
				 if(!Scanner.exclusion_squares.contains(current_location))
					 Scanner.exclusion_squares.add(current_location);
				 
				 my_previous_health = (int) rc.getHealth();
			}
				
			if(rc.getHealth() < my_type.maxHealth*0.33 || NavSimpleMove.life_insurance_policy == Safety.RETREAT){
				destination = location_of_archon;
				NavSimpleMove.life_insurance_policy = Safety.RETREAT;
				
				if(current_location.distanceSquaredTo(destination) < 4){
					destination = current_location;
					if(current_location.isAdjacentTo(location_of_archon))
						destination = current_location.add(location_of_archon.directionTo(current_location));
				}
			}	
			
			if(rc.getRoundNum() > 2800){
				if(!Communications.exclusion_zones.isEmpty())
					destination = Utilities.find_closest_MapLocation(Communications.exclusion_zones);
				if(rc.getRoundNum() > 2900){
					NavSimpleMove.life_insurance_policy = Safety.NONE;
				}
			}
			NavSimpleMove.go_towards_destination();
		}
		
		Communications.update_communications();
		
		if(rc.getHealth() > my_type.maxHealth*0.70)
			NavSimpleMove.life_insurance_policy = Safety.KITE;		
		
//		//debugging:
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

	private static void Combat_micro() throws GameActionException {
		//Can see hostiles.
		//3 choices forward. still, back.
		//limited to 2k bytes (for max eff)
		
		if(NavSimpleMove.life_insurance_policy == Safety.NONE){
			RobotInfo closest_hostile = Scanner.find_closest_hostile(); //last ditch effort to close range.
			if(closest_hostile != null){
				destination = closest_hostile.location;
				if(destination.isAdjacentTo(current_location))
					destination = current_location;
			}
			FireControl.attack_Archon();			
		}
		//if only 1 seen, and win 1v1 then advance
		if(Scanner.nearby_hostiles.length == 1){
			if(FireControl.i_will_win_1v1(Scanner.nearby_hostiles[0],Scanner.says_no_targets_are_in_range())){ //win if I advance
					
				destination = Scanner.nearby_hostiles[0].location;
				if(destination.isAdjacentTo(current_location) || Scanner.nearby_hostiles[0].type == RobotType.ZOMBIEDEN && current_location.distanceSquaredTo(destination) < 6){
					destination = current_location; //not advancing, just shooting.
					//if can't be shot back, log distress every 5 turns
					if(!Scanner.nearby_hostiles[0].type.canAttack())
						if(rc.getRoundNum()%5 ==0){
							Communications.override_comms = true;
							Communications.log_distress_call();
							Communications.override_comms = false;
						}	
				}
				return;
			}
			if(FireControl.i_will_win_1v1(Scanner.nearby_hostiles[0],false)){ //win if I don't advance
				destination = current_location;
				return;
			}
			//only one opponent, but going to lose.
			destination = current_location.add(current_location.directionTo(Scanner.nearby_hostiles[0].location).opposite(),1);
			FireControl.check_for_death();
			return;
		}
		if(Scanner.no_hostiles_can_attack_me() && rc.getHealth() > my_type.maxHealth*0.33 ){
			destination = current_location;
			return;
		}
		
//		// multiple enemies. hold your ground if there are more friends than enemies.
//		if(Scanner.nearby_hostiles.length < Scanner.friends_in_range.length){
//			destination = current_location;
//			return;
//		}
			
		destination = current_location.add(current_location.directionTo(Scanner.nearby_hostiles[0].location).opposite(),1);
		FireControl.check_for_death();
	}
}

/**
*/
package Zeta;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class RobotGuard extends AusefulClass {
			
	static int my_previous_health;
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
		
		NavSimpleMove.life_insurance_policy = Safety.EXPLORING;
		my_previous_health = (int) my_type.maxHealth;

		Communications.update_communications();
		destination = current_location;
		
				
		while(true){
			try{
				//turn();
				switch(NavSimpleMove.life_insurance_policy){
				case KITE:
					kite_turn();
					break;
				case EXPLORING:
					standard_turn();
				case RETREAT:
					get_healing_turn();
					break;
				case CHARGE:
					charge_turn();
					break;
				case MASS_FOR_CHARGE:
					mass_for_charge_turn();
					break;
				default:
					System.out.println("Unknown Soldier turn");
					NavSimpleMove.life_insurance_policy = Safety.EXPLORING;
					break;
				}
				
				NavSimpleMove.go_towards_destination();
				FireControl.shoot_deadest_enemy();
				
				if(Scanner.can_see_hostiles())
					Communications.log_distress_call();
				
				Communications.update_communications();	
				rc.setIndicatorString(0, NavSimpleMove.life_insurance_policy.toString());
			} catch (Exception e){
				e.printStackTrace();
			}
			yield();
		}
	}

	private static void get_healing_turn() throws GameActionException {
		//goto Archon for healing. but don't crowd.
		NavSimpleMove.life_insurance_policy = Safety.RETREAT;
		
		if(Scanner.can_see_hostiles()){ //I can see hostiles, Kite them.
			kite_turn();
			return;
		}
		
		if(rc.getHealth() > my_type.maxHealth*0.70){
			standard_turn();
			return;
		}
			
		destination = location_of_archon;
		if(current_location.distanceSquaredTo(destination) < 4){
			destination = current_location;
			if(current_location.isAdjacentTo(location_of_archon))
				destination = current_location.add(location_of_archon.directionTo(current_location));
		}
	}

	private static void kite_turn() throws GameActionException{
		NavSimpleMove.life_insurance_policy = Safety.KITE;
		byte_code_limiter = 2000;
		my_previous_health = (int) rc.getHealth();
		
		if(Scanner.cant_see_hostiles()){
			standard_turn();
			return;
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
	
		destination = current_location.add(current_location.directionTo(Scanner.nearby_hostiles[0].location).opposite(),1);
		FireControl.check_for_death();
	}

	private static void standard_turn() throws GameActionException {
		//move a distance away from closest friend.
		//move towards distress call
		//move towards enemy chatter (If no distress calls)
		//check for unseen turrets.
		NavSimpleMove.life_insurance_policy = Safety.EXPLORING;
		
		
		if(Scanner.can_see_hostiles()){ //I can see hostiles, Kite them.
			kite_turn();
			return;
		}
		
		if(rc.getHealth() < my_type.maxHealth*0.33){ //  injured, return for heal
			get_healing_turn();
			return;
		}		
		
		if(rc.getRoundNum() > 2100) //try to take down that turtle.
			mass_for_charge_turn();
		
		if(check_for_hidden_enemy()){ //I've been shot without seeing enemy; logged exclusion square
			get_healing_turn(); //get away from here, and don't return
		}

		
		if(check_for_hidden_enemy()){ //I've been shot without seeing enemy; logged exclusion square
			get_healing_turn(); //get away from here, and don't return
		}
		
		//standard turn
		byte_code_limiter = my_type.bytecodeLimit;
			
		//head away from close friends
		RobotInfo closest_friend = Scanner.find_closest_friend();
		if(closest_friend != null){
			//destination = current_location.add(current_location.directionTo(closest_friend.location).opposite(),2);
			destination = closest_friend.location.add(closest_friend.location.directionTo(current_location),6);
		}
			
		//head towards distress calls
		MapLocation closest_Comms_data = Utilities.find_closest_MapLocation(Communications.distress_zones);
		if(closest_Comms_data == null)
			closest_Comms_data = Utilities.find_closest_MapLocation(Communications.chatter_zones);
			
		//head towards chatter if no distress calls.
		if (closest_Comms_data != null){
			destination = closest_Comms_data;
			if(rc.canSense(destination))
				if(Communications.chatter_zones.contains(closest_Comms_data))
					Communications.chatter_zones.remove(closest_Comms_data);
						
			if(Communications.distress_zones.contains(closest_Comms_data))
				Communications.distress_zones.remove(closest_Comms_data);
		}			
	}
	
	private static boolean check_for_hidden_enemy(){
		if(my_previous_health > (int) rc.getHealth()){
			 if(!Scanner.exclusion_squares.contains(current_location))
				 Scanner.exclusion_squares.add(current_location);
			 
			 my_previous_health = (int) rc.getHealth();
			 return true;
		}
		return false;
	}

	private static void mass_for_charge_turn() throws GameActionException {
		if(Scanner.can_see_hostiles()){ //I can see hostiles, Kite them.
			kite_turn();
			return;
		}
		
		NavSimpleMove.life_insurance_policy = Safety.MASS_FOR_CHARGE;
		if(!Communications.exclusion_zones.isEmpty())
			destination = Utilities.find_closest_MapLocation(Communications.exclusion_zones);
		if(rc.getRoundNum() > 2220){
			charge_turn();
		}		
	}
	
	private static void charge_turn() throws GameActionException {
		NavSimpleMove.life_insurance_policy = Safety.CHARGE;
		
		if(Scanner.can_see_hostiles()){ //I can see hostiles, Kite them.
			destination = Scanner.find_closest_hostile().location;
			FireControl.shoot_deadest_enemy();
			return;
		}
		
		if(!Communications.exclusion_zones.isEmpty())
			destination = Utilities.find_closest_MapLocation(Communications.exclusion_zones);	
	}
}

/**
*/
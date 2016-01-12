package Epsilon;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class RobotTurret extends AusefulClass {
	
	static boolean i_am_turret = true;
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
		
		NavSimpleMove.life_insurance_policy = Safety.KITE;
		
		if(my_type == RobotType.TTM){
			//neutral i guess.
			my_type = RobotType.TURRET;
			i_am_turret = false;
		}
						
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
		byte_code_limiter = RobotType.TURRET.bytecodeLimit;
		
		Communications.update_communications();
		
		MapLocation nearest_enemy = null;
		if(Scanner.can_see_hostiles()){
			nearest_enemy = Utilities.find_closest_RobotInfo(Scanner.nearby_hostiles).location;
		}else if(!Communications.distress_zones.isEmpty()){
			nearest_enemy = Utilities.find_closest_MapLocation(Communications.distress_zones);
			nearest_enemy = nearest_enemy.add(current_location.directionTo(nearest_enemy),4);
		} else if(!Communications.chatter_zones.isEmpty()){
			nearest_enemy = Utilities.find_closest_MapLocation(Communications.chatter_zones);
		}
		
		if(rc.getHealth() > my_type.maxHealth*0.70)
			NavSimpleMove.life_insurance_policy = Safety.GHOST;		
				
		if(i_am_turret){
			if(Scanner.can_see_hostiles()){
				for(RobotInfo hostile:Scanner.nearby_hostiles){
					if(FireControl.attack_location(hostile.location))
							break;
				}	
				Communications.log_distress_call();
			} else{
				FireControl.attack_locations(Communications.location_zones);
				FireControl.attack_locations(Communications.chatter_zones);
			}
			
			if((nearest_enemy == null || current_location.distanceSquaredTo(nearest_enemy) > RobotType.TURRET.attackRadiusSquared)){
				//pack up and head for maplocation
				MapLocation nearest_distress = Utilities.find_closest_MapLocation(Communications.distress_zones);
				boolean adjacent_to_distress = true;
				if(nearest_distress == null || !nearest_distress.isAdjacentTo(current_location))
					adjacent_to_distress = false;
				
				if(!adjacent_to_distress){
					Communications.distress_zones.clear();
					Communications.chatter_zones.clear();
					Communications.location_zones.clear();
					if(rc.getCoreDelay() < 1){
						rc.pack();
						i_am_turret = false;
					}
				}
			}				
			
		}else{ //am mobile
			if(Scanner.can_see_hostiles()){
				//run away
				destination = location_of_archon;
			} else{
				//head for nearest distress
				destination = nearest_enemy;
			}
			
			if(rc.getHealth() < my_type.maxHealth*0.33 || NavSimpleMove.life_insurance_policy == Safety.RETREAT){
				destination = location_of_archon;
				NavSimpleMove.life_insurance_policy = Safety.RETREAT;
			}
			
			if(nearest_enemy == null ||(nearest_enemy.distanceSquaredTo(current_location) > GameConstants.TURRET_MINIMUM_RANGE && (nearest_enemy.distanceSquaredTo(current_location) <= RobotType.TURRET.attackRadiusSquared)) ){
				if(rc.getCoreDelay() < 1){
					rc.unpack();
					i_am_turret = true;
				}
			} else{				
				NavSimpleMove.go_towards_destination();
			}
		}
		
		
		if(rc.getHealth() > my_type.maxHealth*0.95)
			NavSimpleMove.life_insurance_policy = Safety.GHOST;	
	}
}

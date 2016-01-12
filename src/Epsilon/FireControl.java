package Epsilon;

import java.util.ArrayList;
import java.util.Iterator;

import battlecode.common.*;

public class FireControl extends AusefulClass{
	
	public static boolean shoot_closest_hostile() throws GameActionException{
		if (i_cannot_fire())
			return false;
		
		if(Scanner.says_no_targets_are_in_range())
			return false;
		
		RobotInfo target = Scanner.find_closest_hostile();
		
		rc.attackLocation(target.location);
		return true;
	}
	
	public static void attack_Archon() throws GameActionException {
		if (i_cannot_fire())
			return;
		
		if(Scanner.says_no_targets_are_in_range())
			return;
		
		for(RobotInfo possible_target:Scanner.hostiles_in_range){
			if(possible_target.type == RobotType.ARCHON){
				if (attack_location(possible_target.location))
					return;
			}
		}
	}	
	
	public static boolean attack_location(MapLocation location) throws GameActionException{
		if (i_cannot_fire())
			return false;
		
		if(location == null)
			return false;
		
		if(!rc.canAttackLocation(location))
			return false;
		
		if(my_type==RobotType.TURRET && current_location.distanceSquaredTo(location) < GameConstants.TURRET_MINIMUM_RANGE)
			return false;
		
		rc.attackLocation(location);
		return true;
	}
	
	public static boolean shoot_deadest_enemy() throws GameActionException{
		if(i_cannot_fire())
			return false;
		
		if(Scanner.says_no_targets_are_in_range())
			return false;
		
//todo any robot that can be one shot can be considered 'deadest' so return early.		
		RobotInfo[] hostiles_in_range = Scanner.hostiles_in_range;
		RobotInfo target = null;
		double health = Double.POSITIVE_INFINITY; 
		
		for(RobotInfo current_hostile:hostiles_in_range)
			if(current_hostile.health < health){
				target = current_hostile;
				health = current_hostile.health;
			}
		
		if(target == null)
			return false;
		
		rc.attackLocation(target.location);
		return true;
	}
	
	public static boolean i_cannot_fire(){
		if (rc.getWeaponDelay() -1 >= 0)
			return true;
		return false;
	}
	
	public static int turns_until_robot_can_attack(RobotInfo subject){
		return (int) Math.max(0, (subject.weaponDelay - 1));	
	}
	
	public static int effective_attack_delay(RobotInfo subject){
			return (int) subject.type.attackDelay;
	}	
	
	public static int how_many_shots_for_subject_to_kill_target(RobotInfo subject, RobotInfo target){
		return (int) (((target.health - 0.1) / subject.type.attackPower) + 1);
	}
	
	public static boolean i_will_win_1v1(RobotInfo adversary,boolean need_to_advance) throws GameActionException{
		
		if(adversary.type == RobotType.ZOMBIEDEN)
			return true;
		
		RobotInfo my_info = rc.senseRobot(rc.getID());
		int shots_for_enemy_to_kill_me = how_many_shots_for_subject_to_kill_target(adversary,my_info);
		int shots_for_me_to_kill_enemy = how_many_shots_for_subject_to_kill_target(my_info,adversary);

		int rounds_for_enemy_to_kill_me = turns_until_robot_can_attack(adversary) + (shots_for_enemy_to_kill_me * effective_attack_delay(adversary));
		int rounds_for_me_to_kill_enemy = turns_until_robot_can_attack(my_info) + (shots_for_me_to_kill_enemy * effective_attack_delay(my_info));
		
		if(need_to_advance)
			rounds_for_me_to_kill_enemy += (int) my_type.cooldownDelay;
		
		if (rounds_for_me_to_kill_enemy < rounds_for_enemy_to_kill_me)
			return true;
		
		return false;
	}

	public static void check_for_death() throws GameActionException {
		int total_damage = 0;
		if(rc.isCoreReady())
			return;
		
		for(RobotInfo nearby_friend:Scanner.nearby_friends){
			if(nearby_friend.health < rc.getHealth())
				return; //let them shout instead.
		}
		
		for(RobotInfo nearby_hostile:Scanner.nearby_hostiles){
			if(current_location.distanceSquaredTo(nearby_hostile.location) <= nearby_hostile.type.attackRadiusSquared)
				if(nearby_hostile.weaponDelay < 2)
					total_damage += nearby_hostile.attackPower;
			
			if(total_damage > rc.getHealth()){
				shoot_deadest_enemy();
				Communications.death_shout();
				return;
			}
		}
		return;
	}

	public static void attack_locations(ArrayList<MapLocation> location_zones) throws GameActionException {
		MapLocation target = null;
		
		if(location_zones.size() == 0)
			return;
		
		for (Iterator<MapLocation> test = location_zones.iterator(); test.hasNext();){
			MapLocation test_location = test.next();
			if(attack_location(test_location))
					return;
		}
		return;
	}	
}
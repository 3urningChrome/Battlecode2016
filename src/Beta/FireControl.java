package Beta;

import battlecode.common.*;

public class FireControl extends AusefulClass{
	
	public static boolean shoot_closest_enemy() throws GameActionException{
		if (i_cannot_fire())
			return false;
		
		RobotInfo target = Utilities.find_closest_RobotInfo(Scanner.scan_for_enemies_in_range());
		
		if(target == null)
			return false;
		
		rc.attackLocation(target.location);
		return true;
	}
	
	public static boolean shoot_closest_zombie() throws GameActionException{
		if (i_cannot_fire())
			return false;
		
		RobotInfo target = Utilities.find_closest_RobotInfo(Scanner.scan_for_zombies_in_range());
		
		if(target == null)
			return false;
		
		rc.attackLocation(target.location);
		return true;
	}	
	
	public static boolean shoot_deadest_enemy() throws GameActionException{
		if(i_cannot_fire())
			return false;
//todo any robot that can be one shot can be considered 'deadest' so return early.		
		RobotInfo[] enemies_in_range = Scanner.scan_for_enemies_in_range();
		RobotInfo target = null;
		double health = Double.POSITIVE_INFINITY; 
		
		for(RobotInfo current_enemy:enemies_in_range)
			if(current_enemy.health < health){
				target = current_enemy;
				health = current_enemy.health;
			}
		
		if(target == null)
			return false;
		
		rc.attackLocation(target.location);
		return true;
	}
	
	public static boolean shoot_deadest_zombie() throws GameActionException{
		if(i_cannot_fire())
			return false;
//todo any robot that can be one shot can be considered 'deadest' so return early.		
		RobotInfo[] zombies_in_range = Scanner.scan_for_zombies_in_range();
		RobotInfo target = null;
		double health = Double.POSITIVE_INFINITY; 
		
		for(RobotInfo current_zombie:zombies_in_range)
			if(current_zombie.health < health){
				target = current_zombie;
				health = current_zombie.health;
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
	
	public static boolean i_will_win_1v1(RobotInfo adversary) throws GameActionException{
		RobotInfo my_info = rc.senseRobot(rc.getID());
		int shots_for_enemy_to_kill_me = how_many_shots_for_subject_to_kill_target(adversary,my_info);
		int shots_for_me_to_kill_enemy = how_many_shots_for_subject_to_kill_target(my_info,adversary);
		
		int rounds_for_enemy_to_kill_me = turns_until_robot_can_attack(adversary) + (shots_for_enemy_to_kill_me * effective_attack_delay(adversary));
		int rounds_for_me_to_kill_enemy = turns_until_robot_can_attack(my_info) + (shots_for_me_to_kill_enemy * effective_attack_delay(my_info));
		
		if (rounds_for_me_to_kill_enemy < rounds_for_enemy_to_kill_me)
			return true;
		
		return false;
	}
}
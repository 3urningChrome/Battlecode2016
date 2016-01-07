package Alpha;

import battlecode.common.*;

public class Scanner extends AusefulClass{
	
	static RobotInfo[] nearby_enemies;
	static int turn_nearby_enemy_last_updated =-1;
	
	static RobotInfo[] enemies_in_range;
	static int turn_enemies_in_range_last_updated =-1;
	
	static RobotInfo[] nearby_friends;
	static int turn_nearby_friend_last_updated =-1;
	
	static RobotInfo[] friends_in_range;
	static int turn_friends_in_range_last_updated =-1;	
	
	static RobotInfo[] nearby_zombies;
	static int turn_nearby_zombie_last_updated =-1;
	
	static RobotInfo[] zombies_in_range;
	static int turn_zombies_in_range_last_updated =-1;
	
	
	static final int MAX_FIREING_RANGE = 53;
		
	public static RobotInfo[] scan_for_enemy(){
		if(turn_nearby_enemy_last_updated < rc.getRoundNum()){
			nearby_enemies = rc.senseNearbyRobots(MAX_FIREING_RANGE, enemy);
			turn_nearby_enemy_last_updated = rc.getRoundNum();
		}
		return nearby_enemies;
	}
	
	public static RobotInfo[] scan_for_enemies_in_range(){
		if(turn_enemies_in_range_last_updated < rc.getRoundNum()){
			enemies_in_range = rc.senseNearbyRobots(my_type.attackRadiusSquared, enemy);
			turn_enemies_in_range_last_updated = rc.getRoundNum();
		}
		return enemies_in_range;
	}
	
	public static RobotInfo[] scan_for_friend(){
		if(turn_nearby_friend_last_updated < rc.getRoundNum()){
			nearby_friends = rc.senseNearbyRobots(MAX_FIREING_RANGE, friendly);
			turn_nearby_friend_last_updated = rc.getRoundNum();
		}
		return nearby_friends;
	}
	
	public static RobotInfo[] scan_for_friends_in_range(){
		if(turn_friends_in_range_last_updated < rc.getRoundNum()){
			rc.setIndicatorString(0, "Rad:" + my_type.attackRadiusSquared);
			friends_in_range = rc.senseNearbyRobots(my_type.attackRadiusSquared, friendly);
			turn_friends_in_range_last_updated = rc.getRoundNum();
		}
		return friends_in_range;
	}	
	
	public static RobotInfo[] scan_for_zombie(){
		if(turn_nearby_zombie_last_updated < rc.getRoundNum()){
			nearby_zombies = rc.senseNearbyRobots(MAX_FIREING_RANGE, zombie);
			turn_nearby_zombie_last_updated = rc.getRoundNum();
		}
		return nearby_zombies;
	}
	
	public static RobotInfo[] scan_for_zombies_in_range(){
		if(turn_zombies_in_range_last_updated < rc.getRoundNum()){
			zombies_in_range = rc.senseNearbyRobots(my_type.attackRadiusSquared, zombie);
			turn_zombies_in_range_last_updated = rc.getRoundNum();
		}
		return zombies_in_range;
	}
	
	public static RobotInfo find_closest_enemy() {
		RobotInfo closestEnemy = Utilities.find_closest_RobotInfo(scan_for_enemy());
		RobotInfo closestZombie = Utilities.find_closest_RobotInfo(scan_for_zombie());
		
		if(closestEnemy == null)
			return closestZombie;
		
		if(closestZombie == null)
			return closestEnemy;
		
		if(current_location.distanceSquaredTo(closestEnemy.location) > current_location.distanceSquaredTo(closestZombie.location))
			return closestZombie;
		
		return closestEnemy;
	}

	public static boolean says_no_targets_are_in_range() {
		if(scan_for_enemies_in_range().length > 0) return false;
		if(scan_for_zombies_in_range().length > 0) return false;
		return true;
	}
}
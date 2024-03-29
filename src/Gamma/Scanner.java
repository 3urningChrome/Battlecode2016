package Gamma;

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
	
	static RobotInfo[] nearby_neutrals;
	static int turn_nearby_neutral_last_updated =-1;
	
	static RobotInfo[] neutrals_in_range;
	static int turn_neutrals_in_range_last_updated =-1;	
	
	public static MapLocation parts_location = null;
	public static MapLocation[] sensed_locations = null;
	public static int parts_scan_counter = 0;
	
	static final int MAX_FIREING_RANGE = 53;
	
	static double previous_health = my_type.maxHealth;
		
	public static RobotInfo[] scan_for_enemy(){
		if(turn_nearby_enemy_last_updated < rc.getRoundNum()){
			nearby_enemies = rc.senseNearbyRobots(my_type.sensorRadiusSquared, enemy);
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
			nearby_friends = rc.senseNearbyRobots(my_type.sensorRadiusSquared, friendly);
			turn_nearby_friend_last_updated = rc.getRoundNum();
		}
		return nearby_friends;
	}
	
	public static RobotInfo[] scan_for_friends_in_range(){
		if(turn_friends_in_range_last_updated < rc.getRoundNum()){
			friends_in_range = rc.senseNearbyRobots(my_type.attackRadiusSquared, friendly);
			turn_friends_in_range_last_updated = rc.getRoundNum();
		}
		return friends_in_range;
	}	
	
	public static RobotInfo[] scan_for_zombie(){
		if(turn_nearby_zombie_last_updated < rc.getRoundNum()){
			nearby_zombies = rc.senseNearbyRobots(my_type.sensorRadiusSquared, zombie);
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
	
	public static RobotInfo[] scan_for_neutral() {
		if(turn_nearby_neutral_last_updated < rc.getRoundNum()){
			nearby_neutrals = rc.senseNearbyRobots(my_type.sensorRadiusSquared, neutral);
			turn_nearby_neutral_last_updated = rc.getRoundNum();
		}
		return nearby_neutrals;
	}
	
	public static boolean can_see_turrets(){
		scan_for_enemy();
		for(RobotInfo near_enemy:nearby_enemies){
			if(near_enemy.type == RobotType.TURRET){
				rc.setIndicatorString(2,"Seen Turret: " + near_enemy.location);
				return true;
			}
		}
		return false;
	}
	
	public static RobotInfo find_closest_hostile() {
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
	
	public static RobotInfo find_closest_neutral() {
		return Utilities.find_closest_RobotInfo(scan_for_neutral());
	}	
	

	public static RobotInfo find_closest_friendly_Archon(){
		return Utilities.find_closest_RobotInfo_of_type(scan_for_friends_in_range(),RobotType.ARCHON);
	}

	public static boolean says_no_targets_are_in_range() {
		if(scan_for_enemies_in_range().length > 0) return false;
		if(scan_for_zombies_in_range().length > 0) return false;
		return true;
	}
	
	public static boolean says_targets_are_in_range(){
		return !says_no_targets_are_in_range();
	}

	public static boolean can_see_targets() {
		if(scan_for_enemy().length > 0) return true;
		if(scan_for_zombie().length > 0) return true;
		return false;
	}
	
	public static boolean cant_see_targets(){
		return !can_see_targets();
	}

	public static boolean sense_parts() {				
		if(parts_location != null){
			if(!rc.canSense(parts_location))
					return true;
			if(rc.senseParts(parts_location) > 0)
				return true;
			
			if(parts_scan_counter >= sensed_locations.length){
				sensed_locations = null;
				parts_location = null;
				parts_scan_counter = 0;
			}
		}
			
		if(sensed_locations == null || parts_scan_counter >= sensed_locations.length){
			sensed_locations = MapLocation.getAllMapLocationsWithinRadiusSq(current_location, my_type.sensorRadiusSquared);
		}
		
		while(Clock.getBytecodesLeft() > 200 && parts_scan_counter < sensed_locations.length){
			if(rc.senseParts(sensed_locations[parts_scan_counter]) > 0 && rc.senseRubble(sensed_locations[parts_scan_counter]) < GameConstants.RUBBLE_OBSTRUCTION_THRESH){
				parts_location = sensed_locations[parts_scan_counter];
				return true;
			}
			parts_scan_counter +=1;
		}
		sensed_locations = null;
		parts_location = null;
		parts_scan_counter = 0;
		return false;
	}

	public static boolean there_is_no_hidden_enemy() {
		if(rc.getHealth() >= previous_health || can_see_targets())
			return true;
		return false;
	}

	public static boolean there_is_hidden_enemy() {
		return !there_is_no_hidden_enemy();
	}

	public static void reset_health() {
		previous_health = rc.getHealth();
	}
}
package Delta;

import java.util.ArrayList;
import java.util.Iterator;

import battlecode.common.*;

public class Scanner extends AusefulClass{
	
	static RobotInfo[] nearby_hostiles;	
	static RobotInfo[] hostiles_in_range;
	static RobotInfo[] nearby_friends;
	static RobotInfo[] friends_in_range;
	static RobotInfo[] nearby_neutrals;
	static RobotInfo[] neutrals_in_range;	
	
	static int round_last_scanned = -1;
	
	public static ArrayList<MapLocation> parts_locations = new ArrayList<MapLocation>();	
	static int last_scanned_for_parts = 1;
	
	
/**
scan_for_hostiles()
scan_for_hostiles_in_range()
find_closest_hostile()
can_see_hostiles()
cant_see_hostiles()

says_no_targets_are_in_range()
says_there_are_targets_in_range()

scan_for_friends()
scan_for_friends_in_range
find_closest_friend()
find_injured_friend_in_range

scan_for_neutrals()
find_closest_neutral()
scan_for_neutrals_in_range()

scan_for_parts();
*/
	
	private static void update_scan_information() {
		if(!rc.getLocation().equals(current_location) || round_last_scanned < rc.getRoundNum()){
			current_location = rc.getLocation();
			switch(my_type){
			case ARCHON:
				nearby_hostiles = rc.senseHostileRobots(current_location, my_type.sensorRadiusSquared);
				nearby_friends = rc.senseNearbyRobots(my_type.sensorRadiusSquared, friendly);
				friends_in_range = rc.senseNearbyRobots(my_type.attackRadiusSquared, friendly);
				nearby_neutrals = rc.senseNearbyRobots(my_type.sensorRadiusSquared, neutral);
				neutrals_in_range = rc.senseNearbyRobots(2, neutral);
				if(parts_locations.contains(current_location))
					parts_locations.remove(current_location);
			case SCOUT:
				nearby_hostiles = rc.senseHostileRobots(current_location, my_type.sensorRadiusSquared);
				nearby_neutrals = rc.senseNearbyRobots(my_type.sensorRadiusSquared, neutral);
				neutrals_in_range = rc.senseNearbyRobots(2, neutral);
				
				for(RobotInfo neutral_info:neutrals_in_range)
					if(!Communications.neutral_zones.contains(neutral_info.location))
						Communications.neutral_zones.add(neutral_info.location);
				
			default:
				nearby_hostiles = rc.senseHostileRobots(current_location, my_type.sensorRadiusSquared);
				hostiles_in_range = rc.senseHostileRobots(current_location, my_type.attackRadiusSquared);
				nearby_friends = rc.senseNearbyRobots(my_type.sensorRadiusSquared, friendly);
				friends_in_range = rc.senseNearbyRobots(my_type.attackRadiusSquared, friendly);
			}
		}	
	}
	
	//scan_for_hostiles - returns seen hostiles
	public static RobotInfo[] scan_for_hostiles(){
		update_scan_information();
		return nearby_hostiles;
	}
	
	//scan_for_hostiles_in range hostiles I can shoot
	public static RobotInfo[] scan_for_hostiles_in_range(){
		update_scan_information();
		return hostiles_in_range;
	}
	
	//find_closest_hostile
	public static RobotInfo find_closest_hostile() {
		return Utilities.find_closest_RobotInfo(scan_for_hostiles());
	}	
	
	public static boolean can_see_hostiles() {
		if(scan_for_hostiles().length > 0) return true;
		return false;
	}
	
	public static boolean can_see_dangerous_hostiles(){
		if(scan_for_hostiles().length == 0) return false;
		
		for(RobotInfo hostile:nearby_hostiles){
			if(hostile.attackPower > 0)
				return true;
		}
		
		return false;
	}
	
	public static boolean cant_see_hostiles(){
		return !can_see_hostiles();
	}
	
	public static boolean says_no_targets_are_in_range() {
		if(scan_for_hostiles_in_range().length > 0) return false;
		return true;
	}
	
	public static boolean says_there_are_targets_in_range(){
		return !says_no_targets_are_in_range();
	}	
	
	//scan_for_friends - returns seens friends
	public static RobotInfo[] scan_for_friends(){
		update_scan_information();
		return nearby_friends;
	}
	
	public static RobotInfo[] scan_for_friends_in_range(){
		update_scan_information();
		return friends_in_range;
	}	
		
	//find_closest_friend
	public static RobotInfo find_closest_friend() {
		return Utilities.find_closest_RobotInfo(scan_for_friends());
	}	
	
	public static RobotInfo find_injured_friend_in_range(){
		RobotInfo[] friends = scan_for_friends_in_range();
		if(friends!=null)
			for(RobotInfo my_special_friend:friends)
				if(my_special_friend.health < my_special_friend.maxHealth && my_special_friend.type != RobotType.ARCHON){
					return my_special_friend;
				}
		return null;
	}
	
	public static RobotInfo[] scan_for_neutrals(){
		update_scan_information();
		return nearby_neutrals;
	}
	
	public static RobotInfo[] scan_for_neutrals_in_range(){
		update_scan_information();
		return neutrals_in_range;
	}
	
	public static RobotInfo find_closest_neutral() {
		return Utilities.find_closest_RobotInfo(scan_for_neutrals());
	}	
	
	public static void sense_parts() {				
//TODO should be clever here, and only scan new tiles... 
		if(last_scanned_for_parts > rc.getRoundNum() - 10)
			return;
		
		last_scanned_for_parts = rc.getRoundNum();
		
		MapLocation[] sensed_locations = MapLocation.getAllMapLocationsWithinRadiusSq(current_location, my_type.sensorRadiusSquared);
		for(MapLocation test_for_parts:sensed_locations){
			if(Clock.getBytecodeNum() > byte_code_limiter - 2000)
				return;
			
			if(rc.senseParts(test_for_parts) > 0){
				if(!parts_locations.contains(test_for_parts) && rc.senseRubble(test_for_parts) < GameConstants.RUBBLE_OBSTRUCTION_THRESH){
					parts_locations.add(test_for_parts);
					rc.setIndicatorString(1, "Added Parts:" + test_for_parts.toString() + " " + parts_locations.size());
				}
			}
		}
	}
	
	public static MapLocation find_closest_parts(){	
		return Utilities.find_closest_MapLocation(parts_locations);
	}

	public static boolean no_hostiles_can_attack_me() {
		return hostiles_cant_shoot(current_location);
	}
	
	public static boolean hostiles_can_attack_me(){
		return !no_hostiles_can_attack_me();
	}

	public static boolean hostiles_cant_shoot(MapLocation location) {
		for(RobotInfo nearby_hostile:nearby_hostiles){
			if(location.distanceSquaredTo(nearby_hostile.location) <= nearby_hostile.type.attackRadiusSquared)
				return false;
		}
		return true;
	}
	
	public static boolean hostiles_can_Shoot(MapLocation location){
		return !hostiles_cant_shoot(location);
	}

	public static boolean hostiles_cant_see_location(MapLocation location) {
		for(RobotInfo nearby_hostile:nearby_hostiles){
			if(location.distanceSquaredTo(nearby_hostile.location) <= nearby_hostile.type.sensorRadiusSquared)
				return false;
		}
		return true;
	}
	
	public static boolean hostiles_can_see_location(MapLocation location){
		return !hostiles_cant_see_location(location);
	}
}
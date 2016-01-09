package team038;

import java.util.Iterator;

import battlecode.common.*;

//cheers Duck,I like this idea.
public enum Safety {
	NONE{

	},
	KITE{
		public boolean says_this_move_will_shorten_your_life(MapLocation location) throws GameActionException{
			if(stepping_into_exclusion_zone(location))
				return true;
			
			if(Scanner.cant_see_targets()){
				AusefulClass.rc.setIndicatorString(2, "no Enemies in sight");
				return false; // no enemies, go where ya like
			}
		
			RobotInfo closest_hostile = Scanner.find_closest_hostile();
			
			//if no enemies in range, but can see them. if moving forward means you can't be shot... should we?
			if(Scanner.says_no_targets_are_in_range()){
				AusefulClass.rc.setIndicatorString(2, "No Enemies in range");
				if(!enemy_can_fire_at(location)){
					AusefulClass.rc.setIndicatorString(2, "no enemy can fire at dest");
					return false;
				}
			}
			
			if(Scanner.enemies_in_range.length < 2){ //if 1v1
				AusefulClass.rc.setIndicatorString(2, "1 enemy");
				if(FireControl.i_will_win_1v1(closest_hostile)){ //and I'll win
					AusefulClass.rc.setIndicatorString(2, "win 1v1");
					return true; //we got this. don't move now.
				}
			}
//Step away does not work. 209-210			
			//otherwise, move away. forcing them to move to you....
			if(AusefulClass.current_location.distanceSquaredTo(closest_hostile.location) >= location.distanceSquaredTo(closest_hostile.location)){
				AusefulClass.rc.setIndicatorString(2, "moving Closer");
				return true;
			}
			return false;
		}
	},
	RETREAT{
		public boolean says_this_move_will_shorten_your_life(MapLocation location){
			if(stepping_into_exclusion_zone(location))
				return true;
			
			if(Scanner.cant_see_targets())
				return false; // no enemies, go where ya like
			
			//if enemy can fire at square, it's a no, unless they can shoot me now too.
			if(enemy_can_fire_at(location)){
				if(enemy_can_fire_at(AusefulClass.current_location))
					return false;
				return true;
			}
			return false;
		}
	},
	GHOST{
		public boolean says_this_move_will_shorten_your_life(MapLocation location){
			if(stepping_into_exclusion_zone(location))
				return true;
			
			if(Scanner.cant_see_targets())
				return false; // no enemies, go where ya like
			
			if(zombies_cant_see(location) && enemy_cant_see(location))
				return false;
			
			//if enemy can fire at square, it's a no, unless they can shoot me now too.
			if(enemy_can_fire_at(location)){
				if(enemy_can_fire_at(AusefulClass.current_location))
					return false;
				return true;
			}
			
			//don't step into sensor range.
			if(enemy_can_see(location))
				return true;
			
			if(zombies_can_see(location))
				return true;
			
			return false;
		}
	},	
	AVOID_ALL_ENEMY{
		public boolean says_this_move_will_shorten_your_life(MapLocation location){
			if(stepping_into_exclusion_zone(location))
				return true;
			
			if(enemy_can_fire_at(location))
				return true;
			
			return false;
		}	
	},
	AVOID_ALL_ZOMBIE{
		public boolean says_this_move_will_shorten_your_life(MapLocation location){
			if(zombies_can_fire_at(location))
				return true;
			
			return false;
		}	
	},
	AVOID_ALL_NON_FRIENDS{
		public boolean says_this_move_will_shorten_your_life(MapLocation location){
			if(stepping_into_exclusion_zone(location))
				return true;
			
			if(enemy_can_fire_at(location))
				return true;
				
			if(zombies_can_fire_at(location))
				return true;
			
			return false;
		}	
	};
	
	public boolean stepping_into_exclusion_zone(MapLocation location){
		if(!Communications.exclusion_zones.isEmpty())
			for (Iterator<MapLocation> test = Communications.exclusion_zones.iterator(); test.hasNext();){
				if(location.distanceSquaredTo(test.next()) <= RobotType.TURRET.attackRadiusSquared){
					AusefulClass.rc.setIndicatorString(2, "Stopped by Exclusion");
					return true;
				}
			}
		
		return false;
	}
	public boolean says_this_move_will_shorten_your_life(MapLocation location) throws GameActionException{
		return false;
	}	
	
	private static boolean enemy_can_fire_at(MapLocation location) {
		for(RobotInfo enemy_robot:Scanner.scan_for_enemy()){
			if(enemy_robot.location.distanceSquaredTo(location) <= enemy_robot.type.attackRadiusSquared)
				return true;
		}
		return false;
	}	
	
	private static boolean zombies_can_fire_at(MapLocation location) {
		for(RobotInfo zombie_robot:Scanner.scan_for_zombie()){
			if(zombie_robot.location.distanceSquaredTo(location) <= zombie_robot.type.attackRadiusSquared)
				return true;
		}
		return false;
	}	
	
	private static boolean enemy_can_see(MapLocation location) {
		for(RobotInfo enemy_robot:Scanner.scan_for_enemy()){
			if(enemy_robot.location.distanceSquaredTo(location) <= enemy_robot.type.sensorRadiusSquared)
				return true;
		}
		return false;
	}	
	
	private static boolean zombies_can_see(MapLocation location) {
		for(RobotInfo zombie_robot:Scanner.scan_for_zombie()){
			if(zombie_robot.location.distanceSquaredTo(location) <= zombie_robot.type.sensorRadiusSquared)
				return true;
		}
		return false;
	}		
	
	private static boolean enemy_cant_see(MapLocation location) {
		return !enemy_can_see(location);
	}
	
	private static boolean zombies_cant_see(MapLocation location) {
		return !zombies_can_see(location);
	}	
}

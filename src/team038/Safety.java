package team038;

import battlecode.common.*;

//cheers Duck,I like this idea.
public enum Safety {
	NONE{

	},
	DONT_ADVANCE_PAST_MAX_RANGE{
		public boolean says_this_move_will_shorten_your_life(MapLocation location){
			if(Scanner.says_targets_are_in_range()){
				MapLocation closest_target = Scanner.find_closest_enemy().location;
				if(AusefulClass.current_location.distanceSquaredTo(closest_target) > location.distanceSquaredTo(closest_target))
					return true;
			}
			return false;
		}
	},
	RETREAT{
		public boolean says_this_move_will_shorten_your_life(MapLocation location){
			if(Scanner.can_see_targets()){
				MapLocation closest_target = Scanner.find_closest_enemy().location;
				if(AusefulClass.current_location.distanceSquaredTo(closest_target) >= location.distanceSquaredTo(closest_target))
					return true;
			}
			return false;
		}
	},
	AVOID_ALL_ENEMY{
		public boolean says_this_move_will_shorten_your_life(MapLocation location){
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
			if(enemy_can_fire_at(location))
				return true;
				
			if(zombies_can_fire_at(location))
				return true;
			
			return false;
		}	
	};
	
	public boolean says_this_move_will_shorten_your_life(MapLocation location){
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
}

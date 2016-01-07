package Alpha;

import battlecode.common.*;

//cheers Duck,I like this idea.
public enum Safety {
	NONE{

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
	AVOID_NON_FRIENDS{
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

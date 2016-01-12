package Epsilon;

import java.util.Iterator;
import battlecode.common.*;

//cheers Duck,I like this idea.
public enum Safety {
	NONE{

	},
	KITE{
//stepping into out of fire zones if can win.
		public boolean says_this_move_will_shorten_your_life(MapLocation location) throws GameActionException{
			if(stepping_into_exclusion_zone(location))
				return true;
			return false;
		}
	},
	RETREAT{
//heading back to Archone
	},
	GHOST{
//Avoiding sensor ranges and attack zones.
		public boolean says_this_move_will_shorten_your_life(MapLocation location) throws GameActionException{
			if(stepping_into_exclusion_zone(location))
				return true;
			
			if(Scanner.cant_see_hostiles())
				return false; // no enemies, go where ya like
			
			if(Scanner.hostiles_cant_see_location(location))
				return false;

			if(Scanner.hostiles_can_Shoot(location)){
				if(Scanner.hostiles_can_attack_me())
					return false;
				return true;
			}
			
			//don't step into sensor range, unless already in it.
			if(Scanner.hostiles_can_see_location(location))
				if(Scanner.hostiles_cant_see_location(AusefulClass.current_location))
					return true;
			
			return false;
		}	
	},
	ARCHON{
		//Avoiding sensor ranges and attack zones.
				public boolean says_this_move_will_shorten_your_life(MapLocation location) throws GameActionException{
					if(stepping_into_exclusion_zone(location))
						return true;
					
					if(Scanner.cant_see_hostiles())
						return false; // no enemies, go where ya like
					
					MapLocation closest_hostile = Scanner.find_closest_hostile().location;
					if(AusefulClass.current_location.distanceSquaredTo(closest_hostile) >= location.distanceSquaredTo(closest_hostile))
						return false;

					if(Scanner.hostiles_can_Shoot(location)){
						if(Scanner.hostiles_can_attack_me())
							return false;
						return true;
					}
					
					//don't step into sensor range, unless already in it.
					if(Scanner.hostiles_can_see_location(location))
						if(Scanner.hostiles_cant_see_location(AusefulClass.current_location))
							return true;
					
					return false;
				}	
			};
	
	public boolean stepping_into_exclusion_zone(MapLocation location){
		if(Scanner.can_see_turrets())
			return false; //too late may as well see if can get to safety
		
		if(!Communications.exclusion_zones.isEmpty())
			for (Iterator<MapLocation> test = Communications.exclusion_zones.iterator(); test.hasNext();){
				MapLocation exclusion_test = test.next();
				if(location.distanceSquaredTo(exclusion_test) <= RobotType.TURRET.attackRadiusSquared){
					if(exclusion_test.distanceSquaredTo(AusefulClass.current_location) >= exclusion_test.distanceSquaredTo(location))
					AusefulClass.rc.setIndicatorString(2, "Exclusion: " + Clock.getBytecodesLeft());
					return true;
				}
			}
		if(!Scanner.exclusion_squares.isEmpty())
			for (Iterator<MapLocation> test = Scanner.exclusion_squares.iterator(); test.hasNext();){
				MapLocation exclusion_test = test.next();
				if(location.equals(exclusion_test)){
					return true;
				}
			}	
		
		return false;
	}
	public boolean says_this_move_will_shorten_your_life(MapLocation location) throws GameActionException{
		return false;
	}	
}

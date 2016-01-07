package Beta;

import battlecode.common.*;

public enum NavType {
	ONEBUG{
		public Direction get_direction_of_next_move_towards(MapLocation destination){
			return NavOneBug.get_direction_of_next_move_towards(destination);
		}
		
		public MapLocation navigate_to_destination(MapLocation destination) {
			return NavOneBug.navigate_to_destination(destination);
		}	
	},
	
	ALG2{
		public Direction get_direction_of_next_move_towards(MapLocation destination){
			return NavAlg2Bug.get_direction_of_next_move_towards(destination);
		}		
	},
	DUCKBUG{
		public Direction get_direction_of_next_move_towards(MapLocation destination){
			return NavDuckBug.get_direction_of_next_move_towards(destination);
		}
		
		public MapLocation navigate_to_destination(MapLocation destination) {
			return NavDuckBug.navigate_to_destination(destination);
		}	
	};
	
	public Direction get_direction_of_next_move_towards(MapLocation destination){
		//must override this.
		return Direction.NONE;
	}

	public MapLocation navigate_to_destination(MapLocation a) {
		// TODO Auto-generated method stub
		return null;
	}	
}

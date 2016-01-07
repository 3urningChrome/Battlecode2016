package Alpha;

import battlecode.common.*;

public enum BuildStrategy {
	SOLDIER_RUSH{
		public boolean I_am_building() throws GameActionException{
			return SoldierRushBuildStrategy.attempt_to_build();
		}
	},
	
	GUARD_RUSH{
		public boolean I_am_building() throws GameActionException{
			return false;
		}		
	};
	
	public boolean I_am_building() throws GameActionException{
		//must override this.
		return false;
	}	
}

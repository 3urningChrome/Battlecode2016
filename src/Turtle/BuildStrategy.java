package Turtle;

import battlecode.common.*;

public enum BuildStrategy {
	SOLDIER_RUSH{
		public boolean I_am_building() throws GameActionException{
			return SoldierRushBuildStrategy.attempt_to_build();
		}
	},
	SOLDIER_TURRET{
		public boolean I_am_building() throws GameActionException{
			return SoldierRushWithSomeTurret.attempt_to_build();
		}
	},
	GUARD_RUSH{
		public boolean I_am_building() throws GameActionException{
			return false;
		}		
	},
	TURTLE_POWER{
		public boolean I_am_building() throws GameActionException{
			return Turtle_Turrets.attempt_to_build();
		}
	};
	
	
	public boolean I_am_building() throws GameActionException{
		//must override this.
		return false;
	}	
}

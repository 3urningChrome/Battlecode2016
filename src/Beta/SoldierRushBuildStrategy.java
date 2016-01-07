package Beta;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;

public class SoldierRushBuildStrategy extends Constructing  {

	public static boolean attempt_to_build() throws GameActionException {

		Direction build_direction = get_a_decent_build_direction();
		
		if(i_cannot_build(build_direction, RobotType.SOLDIER))
			return false;
		
		rc.build(build_direction, RobotType.SOLDIER);
		return true;
	}
}

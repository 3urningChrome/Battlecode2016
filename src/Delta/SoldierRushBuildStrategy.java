package Delta;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;


public class SoldierRushBuildStrategy extends Construction  {
	
	static int scouts_built = 0;
	
	public static boolean attempt_to_build() throws GameActionException {

		Direction build_direction = get_a_decent_build_direction();
		
		RobotType build_type = RobotType.SCOUT;
		if(scouts_built > 0)
			build_type = RobotType.SOLDIER;
		
		if(i_cannot_build(build_direction, build_type))
			return false;
		
		rc.build(build_direction, build_type);
		rc.setIndicatorString(1, "Building: " + build_type.toString());
		if(build_type == RobotType.SCOUT)
			scouts_built +=1;
		return true;
	}
}

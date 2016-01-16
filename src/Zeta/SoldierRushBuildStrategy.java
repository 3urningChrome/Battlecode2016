package Zeta;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotType;


public class SoldierRushBuildStrategy extends Construction  {
	
	static int scouts_built = 0;
	static int soldiers_built = 0;
	static int turrets_built = 0;
	
	public static boolean attempt_to_build() throws GameActionException {

		Direction build_direction = get_a_decent_build_direction();
		
		RobotType build_type;
		build_type = RobotType.SOLDIER;
		if(soldiers_built > 5)
			build_type = RobotType.SCOUT;
		
		if(scouts_built > 1)
			build_type = RobotType.SOLDIER;
		
		if(soldiers_built > 40){
			RobotType[] buildList = new RobotType[]{RobotType.SOLDIER, RobotType.SOLDIER, RobotType.GUARD};
			build_type = buildList[rnd.nextInt(buildList.length)];
		}
		
		if(i_cannot_build(build_direction, build_type))
			return false;
		
		rc.build(build_direction, build_type);
		rc.setIndicatorString(1, "Building: " + build_type.toString());
		if(build_type == RobotType.SCOUT)
			scouts_built +=1;
		if(build_type == RobotType.SOLDIER)
			soldiers_built +=1;
		if(build_type == RobotType.TURRET)
			turrets_built +=1;
		return true;
	}
}


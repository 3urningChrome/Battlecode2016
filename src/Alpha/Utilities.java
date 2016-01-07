package Alpha;

import battlecode.common.*;

public class Utilities extends AusefulClass{
	
	public static RobotInfo find_closest_RobotInfo(RobotInfo[] enemies_in_range){
		double range = Double.POSITIVE_INFINITY; 
		RobotInfo target = null;
		
		if(enemies_in_range == null)
			return null;
		
		for(RobotInfo current_enemy:enemies_in_range){
			rc.setIndicatorDot(current_enemy.location, 255, 255, 255);
			if(current_location.distanceSquaredTo(current_enemy.location) < range){
				target = current_enemy;
				range = current_location.distanceSquaredTo(current_enemy.location);
			}
		}
		return target;
	}
}
package Gamma;

import battlecode.common.*;

public class Utilities extends AusefulClass{
	
	public static RobotInfo find_closest_RobotInfo(RobotInfo[] enemies_in_range){
		double range = Double.POSITIVE_INFINITY; 
		RobotInfo target = null;
		
		if(enemies_in_range == null)
			return null;
		
		for(RobotInfo current_enemy:enemies_in_range){
			if(current_location.distanceSquaredTo(current_enemy.location) < range){
				target = current_enemy;
				range = current_location.distanceSquaredTo(current_enemy.location);
			}
		}
		return target;
	}
	
	public static RobotInfo find_closest_RobotInfo_of_type(RobotInfo[] robots_in_range, RobotType type){
		double range = Double.POSITIVE_INFINITY; 
		RobotInfo target = null;
		
		if(robots_in_range == null)
			return null;
		
		for(RobotInfo current_robot:robots_in_range){
			if(current_robot.type != type)
				continue;
			
			rc.setIndicatorDot(current_robot.location, 255, 255, 255);
			if(current_location.distanceSquaredTo(current_robot.location) < range){
				target = current_robot;
				range = current_location.distanceSquaredTo(current_robot.location);
			}
		}
		return target;
	}	
}
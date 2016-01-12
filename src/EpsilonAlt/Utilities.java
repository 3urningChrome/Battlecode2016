package EpsilonAlt;

import java.util.ArrayList;
import java.util.Iterator;

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
	
	public static MapLocation find_closest_MapLocation(ArrayList<MapLocation> the_list){
		double range = Double.POSITIVE_INFINITY; 
		MapLocation target = null;
		
		if(the_list.size() == 0)
			return null;
		
		for (Iterator<MapLocation> test = the_list.iterator(); test.hasNext();){
			MapLocation test_location = test.next();
			if(rc.canSense(test_location))
				if(rc.senseParts(test_location) == 0)
					test.remove();
				
			if(current_location.distanceSquaredTo(test_location) < range){
				target = test_location;
				range = current_location.distanceSquaredTo(test_location);
			}			
		}
		return target;
	}
}
package Turtle;

import java.util.Iterator;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class RobotTurret extends AusefulClass {
	
	static boolean i_am_turret = true;
	static MapLocation[] places = {new MapLocation(0,2),new MapLocation(0,-2),new MapLocation(1,2),new MapLocation(-1,-2),new MapLocation(2,2),new MapLocation(-2,-2),
		new MapLocation(2,1),new MapLocation(-2,-1),new MapLocation(2,0),new MapLocation(-2,0), new MapLocation(2,-1), new MapLocation(-2,1),
		new MapLocation(2,-2),new MapLocation(-2,2),new MapLocation(1,-2),new MapLocation(-1,2)};
	
	public static void loop(RobotController robot_controller){
		AusefulClass.init(robot_controller);
		
		NavSimpleMove.life_insurance_policy = Safety.KITE;
		
		if(my_type == RobotType.TTM){
			//neutral i guess.
			my_type = RobotType.TURRET;
			i_am_turret = false;
		}
						
		while(true){
			try{
				turn();
			} catch (Exception e){
				e.printStackTrace();
			}
			yield();
		}
	}

	private static void turn() throws GameActionException {
		//set up round
		byte_code_limiter = RobotType.TURRET.bytecodeLimit;
		
		Communications.update_communications();
		Scanner.can_see_hostiles(); //force update of info.
		
		if(i_am_turret){
			//shoooooot
			for (Iterator<MapLocation> test = Communications.location_zones.iterator(); test.hasNext();){
				MapLocation test_location = test.next();
				if(rc.canSense(test_location) && (rc.senseRobotAtLocation(test_location) == null || rc.senseRobotAtLocation(test_location).team == friendly))
						continue;

				if(FireControl.attack_location(test_location))
					break;	
			}
			
			if(Scanner.can_see_hostiles()){
				for(RobotInfo hostile:Scanner.hostiles_in_range){
					if(FireControl.attack_location(hostile.location))
						break;						
				}
			}
		}
		
		
		boolean i_need_to_move = true;
		for(MapLocation testLocation:places){
			if(current_location.equals(my_Archon_centre.add(testLocation.x,testLocation.y))){
				i_need_to_move = false;
				if(i_am_turret==false){
					rc.unpack();
					i_am_turret = true;
				}
				break;
			}
		}
		
		
		if(i_need_to_move){
			destination = my_Archon_centre;
			for(MapLocation testLocation:places){
					if(rc.canSense(my_Archon_centre.add(testLocation.x,testLocation.y)) && rc.onTheMap(my_Archon_centre.add(testLocation.x,testLocation.y))){
						if(rc.senseRobotAtLocation(my_Archon_centre.add(testLocation.x,testLocation.y)) == null){
							destination = my_Archon_centre.add(testLocation.x,testLocation.y);
						}
					}
			}
			if(i_am_turret){
				i_am_turret = false;
				rc.pack();
			}
			rc.setIndicatorLine(current_location, destination, 120, 120, 0);
			NavSimpleMove.go_towards_destination();
		}
	}
}

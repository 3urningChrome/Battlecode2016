package Alpha;

import battlecode.common.*;

public class Communications extends AusefulClass{
	
	public static MapLocation get_rally_point() throws GameActionException {
		//return rally point. or current location
		return current_location;
	}

	public static MapLocation get_current_BFS_destination() {
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean can_i_process_BFS() {
		// TODO Auto-generated method stub
		return false;
	}

	public static boolean has_no_BFS_route(MapLocation from_location) {
		// TODO Auto-generated method stub
		return false;
	}

	public static void set_BFS_route(MapLocation from_location, Direction move_in_direction) {
		// TODO Auto-generated method stub
		
	}

	public static void add_additional_BFS_route(MapLocation from_location) {
		// TODO Auto-generated method stub
		
	}

	public static Direction next_BFS_move_direction() {
		// TODO Auto-generated method stub
		return null;
	}

	public static MapLocation pop_next_BFS() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void set_new_BFS_destination(MapLocation destination) {
		// TODO Auto-generated method stub
		
	}
}
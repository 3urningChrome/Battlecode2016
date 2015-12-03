package Pre_competition_dev;

import battlecode.common.*;

public class NavigationBase extends AusefulClass{
	static MapLocation destination;
	static MapLocation starting_point;
	
	static MapLocation[] hit_points;
	static MapLocation[] leave_points;
	static int i = -1;
	
	static Direction direction_to_move;
	static MapLocation location_to_move_to;
	
	static MapLocation location_of_wall;
	
	static final int MEMORY_SIZE = 10;
	static final boolean RIGHT_HAND = true;
	static final boolean LEFT_HAND = false;
	static final int FOLLOW_WALL_LOOK_ORDER[] = new int[]{0,-1,-2,-3,-4,-5,-6,-7};
	static final Direction[] DIRECTIONS = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	 
	static NavBugState current_navigation_state;
	
	static boolean collision_logged_this_turn = false;
	
	
	public NavigationBase(){
	}
	
	public static boolean we_have_changed(MapLocation the_destination){
		return the_destination.equals(destination);
	}
	
	public static void initialise_ready_for(MapLocation passed_destination){
		destination = passed_destination;
		starting_point = current_location;
		
		i = -1;
		hit_points   = new MapLocation[MEMORY_SIZE];
		leave_points = new MapLocation[MEMORY_SIZE];
		
		current_navigation_state = NavBugState.DIRECT;
		direction_to_move = current_location.directionTo(destination);
		location_to_move_to = current_location;
		collision_logged_this_turn = false;
		
		location_of_wall = destination;
		return;
	}
	
	public static MapLocation navigate_to_destination(MapLocation A, MapLocation B){
		//override this in specific Navigation Algorithm.
		return current_location;
	}
	
	public static int the_distance_from(MapLocation A, MapLocation B){
		return A.distanceSquaredTo(B);
	}
	
	public static boolean i_can_move_directly_towards_destination(){
		if(current_location.equals(destination)) return true;
		
		Direction towards_destination = current_location.directionTo(destination);
		return can_move(towards_destination);
	}
	
	public static boolean can_move(Direction towards_destination){
		//TODO implement *safty*
		if (rc.canMove(towards_destination)){
			direction_to_move = towards_destination;
			location_to_move_to = current_location.add(direction_to_move);
			return true;
		}
		return false;
	}
	
	public static void log_new_collision(){
		i+=1;
		hit_points[i] = current_location;
		collision_logged_this_turn = true;
	}
	
	public static MapLocation position_of_last_collision(){
		return hit_points[i];
	}
	
	public static MapLocation follow_wall(boolean right_handed){
// Movable objects, other robots for instance, cause this issues. as wall could disappear
// hence the escape - Crap Walls gone
		
		int hand_multiplier;
		hand_multiplier = right_handed ? 1 : -1;
		
		Direction direction_of_wall = current_location.directionTo(location_of_wall);
		location_of_wall = current_location.add(direction_of_wall);
		
		for(int directional_offset:FOLLOW_WALL_LOOK_ORDER){
			if(can_move(DIRECTIONS[(direction_of_wall.ordinal()+(directional_offset*hand_multiplier)+8)%8])){
				location_of_wall = current_location.add(DIRECTIONS[(direction_of_wall.ordinal()+((directional_offset+1)*hand_multiplier)+8)%8]);
				return location_to_move_to;
			}
		}	
		return current_location;
	}	
}

package Turtle;

import battlecode.common.*;

import java.util.Random;

public class AusefulClass {
	
	static RobotController rc;
	
	static final Direction[] DIRECTIONS = {Direction.NORTH,Direction.SOUTH, Direction.EAST, Direction.WEST,Direction.NORTH_EAST,Direction.SOUTH_WEST, Direction.SOUTH_EAST, Direction.NORTH_WEST};
	
	static Team friendly;
	static Team enemy;
	static Team neutral;
	static Team zombie;
	
	static MapLocation previous_location = null;
	static MapLocation current_location = null;
	static MapLocation destination = null;
	static MapLocation location_of_archon = null;
	
	static MapLocation[] my_Archon_starting_positions;
	static MapLocation my_Archon_centre;
	
	static MapLocation[] enemy_Archon_starting_positions;
	static MapLocation enemy_Archon_centre;
	
	
	static RobotType my_type;
	static int byte_code_limiter;
	
	static Random rnd;
		
	public static void init(RobotController the_rc) {
		rc = the_rc;
		
		friendly = rc.getTeam();
		enemy = friendly.opponent();
		neutral = Team.NEUTRAL;
		zombie = Team.ZOMBIE;
		
		my_type = rc.getType();
		byte_code_limiter = my_type.bytecodeLimit;
		
		rnd = new Random(rc.getID());
		
		current_location = rc.getLocation();
		destination = current_location;
		
		location_of_archon = current_location;
		
		my_Archon_starting_positions = rc.getInitialArchonLocations(friendly);
		enemy_Archon_starting_positions = rc.getInitialArchonLocations(enemy);
		
		if(my_Archon_starting_positions.length > 1){
			int x = 0;
			int y = 0;
			
			for(MapLocation next_location:my_Archon_starting_positions){
				x += next_location.x;
				y += next_location.y;
			}
			
			x = x/my_Archon_starting_positions.length;
			y = y/my_Archon_starting_positions.length;
			
			my_Archon_centre = new MapLocation(x,y);	
			
		} else{
			my_Archon_centre = my_Archon_starting_positions[0];
		}
		
		if(enemy_Archon_starting_positions.length > 1){
			int x = 0;
			int y = 0;
			
			for(MapLocation next_location:enemy_Archon_starting_positions){
				x += next_location.x;
				y += next_location.y;
			}
			
			x = x/enemy_Archon_starting_positions.length;
			y = y/enemy_Archon_starting_positions.length;
			
			enemy_Archon_centre = new MapLocation(x,y);			
			
		} else{
			enemy_Archon_centre = enemy_Archon_starting_positions[0];
		}		
	}
	
	public static void yield(){	
		
		//rc.setIndicatorLine(current_location, destination, 120, 120, 120);
		
		if(my_type == RobotType.ARCHON)
			rc.setIndicatorString(0,"Version: Turtle 1.0");
		
		Clock.yield();
		
		rc.setIndicatorString(0, "");
		rc.setIndicatorString(1, "");
		rc.setIndicatorString(2, "");
	}
	
	public static Direction randomDirection(){
		return Direction.values()[(int)(rnd.nextDouble()*8)];
	}
}

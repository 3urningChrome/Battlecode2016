package Beta;

import battlecode.common.*;

public class Communications extends AusefulClass{
	
	public static final int Location_Data = 1;
	public static final int Distress_data = 2;
	public static final int Enemy_Location_Data = 3;
	public static final int Map_Boundary_Data = 4;
	public static Signal[] message_queue;
	public static int round_message_queue_updated = -1;
	public static final int Standard_Comms_Distance = 250;
	
	public static void update_communications(){
		if(rc.getRoundNum() <= round_message_queue_updated)
			return;
		
		message_queue = rc.emptySignalQueue();
		round_message_queue_updated = rc.getRoundNum();
	}
	
	public static MapLocation get_rally_point() throws GameActionException {
		//return rally point. or current location
		return current_location;
	}
	
	public static void broadcast_my_position() throws GameActionException{
		if(rc.getType() == RobotType.ARCHON){
			rc.broadcastMessageSignal(Location_Data, 0, Standard_Comms_Distance);
		} else{
			rc.broadcastSignal(100);
		}
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

	public static void log_enemies() throws GameActionException {
		RobotInfo[] enemy_contacts = Scanner.scan_for_enemy();
		RobotInfo[] zombie_contacts = Scanner.scan_for_zombie();
		
		if(enemy_contacts.length + zombie_contacts.length == 0)
			return;
		
		switch(my_type){
		case ARCHON:
		case SCOUT:
			rc.broadcastMessageSignal(Enemy_Location_Data, enemy_contacts.length + zombie_contacts.length, Standard_Comms_Distance);
			return;
		default:
			rc.broadcastSignal(100);
		}
	}

	public static MapLocation find_closest_distress() {
		return find_closest(Distress_data);
	}
	
	public static MapLocation find_closest_enemy_location() {
		return find_closest(Enemy_Location_Data);
	}
	
	public static MapLocation find_closest_fight(){
		MapLocation closest_distress = find_closest_distress();
		MapLocation closest_enemy = find_closest_enemy_location();
		MapLocation closest_chatter = find_closest_chatter_location();
		
		if(closest_enemy == null)
			if(closest_distress!=null)
			return current_location.add(current_location.directionTo(closest_distress),5);
		
		if(closest_chatter == null)
			return closest_enemy;
		
		if (closest_enemy == null) 
			return closest_chatter;
	
		if(current_location.distanceSquaredTo(closest_chatter) < current_location.distanceSquaredTo(closest_enemy))
				return closest_chatter;
		
		return closest_enemy;
	}
	
	public static MapLocation find_closest_Archon(){
		MapLocation temp = find_closest(Location_Data);
		if(temp != null)
			location_of_archon = temp;
		
		return location_of_archon;
	}
	
	public static MapLocation find_closest(int message_type){
		update_communications();
	
		MapLocation closest_point_of_interest = null;
		double closest_distance = Double.POSITIVE_INFINITY;
	
		for(Signal current_message:message_queue){
			if(Clock.getBytecodesLeft() < my_type.bytecodeLimit/5)
				return closest_point_of_interest;			
			if(current_message.getTeam() == friendly)
				if((current_message.getMessage() == null && message_type == Distress_data) || (current_message.getMessage() != null && current_message.getMessage()[0] == message_type))
					if(current_message.getLocation().distanceSquaredTo(current_location) < closest_distance){
						closest_distance = current_message.getLocation().distanceSquaredTo(current_location);
						closest_point_of_interest = current_message.getLocation();
					}
		}
		return closest_point_of_interest;
	}
	
	public static MapLocation find_closest_chatter_location(){
		update_communications();
	
		MapLocation closest_point_of_interest = null;
		double closest_distance = Double.POSITIVE_INFINITY;
	
		for(Signal current_message:message_queue){
			if(Clock.getBytecodesLeft() < my_type.bytecodeLimit/5)
				return closest_point_of_interest;			
			if(current_message.getTeam() == enemy)
				if(current_message.getLocation().distanceSquaredTo(current_location) < closest_distance){
					closest_distance = current_message.getLocation().distanceSquaredTo(current_location);
					closest_point_of_interest = current_message.getLocation();
				}
		}
		return closest_point_of_interest;
	}
	
	private static MapLocation convert_message_into_MapLocation(int broadcast_message) {
        int x = broadcast_message % GameConstants.MAP_MAX_WIDTH;
        int y = broadcast_message / GameConstants.MAP_MAX_WIDTH;
        return new MapLocation(x, y);	
	}
	
	private static int convert_MapLocation_to_integer(MapLocation location){
		return GameConstants.MAP_MAX_WIDTH * (location.y) + (location.x);
	}	
}
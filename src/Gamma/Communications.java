package Gamma;

import java.util.ArrayList;
import java.util.Iterator;

import battlecode.common.*;

public class Communications extends AusefulClass{
	
	public static final int Location_Data = 1;
	public static final int Distress_data = 2;
	public static final int Enemy_Location_Data = 3;
	public static final int Map_Boundary_Data = 4;
	public static final int Part_Location_Data = 5;
	public static final int Exclustion_Zone_Location_Data = 6;
	public static final int Exclustion_Zone_Remove_Data = 7;
	
	public static Signal[] message_queue;
	public static int round_message_queue_updated = -1;
	public static final int Standard_Comms_Distance = 250;
	public static final int Full_Comms_Distance = 500;
	public static final int Minimal_Comms_Distance = 0;
	
	public static ArrayList<MapLocation> exclusion_zones = new ArrayList<MapLocation>();
	
	public static void update_communications(){
		if(rc.getRoundNum() <= round_message_queue_updated)
			return;
		
		message_queue = rc.emptySignalQueue();
		round_message_queue_updated = rc.getRoundNum();
	}
	
	public static MapLocation get_rally_point() throws GameActionException {
		update_communications();
		//return rally point. or current location
		return current_location;
	}
	
	public static void broadcastMessageSignal(int message_type,int message_data,int distance) throws GameActionException{
		if(rc.getMessageSignalCount() >= GameConstants.MESSAGE_SIGNALS_PER_TURN)
			return;
		rc.broadcastMessageSignal(message_type, message_data, distance);
	}
	
	public static void broadcast_my_position() throws GameActionException{
		if(my_type == RobotType.ARCHON){
			broadcastMessageSignal(Location_Data, 1, Standard_Comms_Distance);
		}else if(my_type == RobotType.SCOUT){
			broadcastMessageSignal(Location_Data, 2, Standard_Comms_Distance);
		} else{
			rc.broadcastSignal(100);
		}
	}

	public static void log_enemies() throws GameActionException {
		RobotInfo[] enemy_contacts = Scanner.scan_for_enemy();
		RobotInfo[] zombie_contacts = Scanner.scan_for_zombie();
		
		if(enemy_contacts.length + zombie_contacts.length == 0)
			return;
		
		switch(my_type){
		case ARCHON:
		case SCOUT:
			int coded_map_location = convert_MapLocation_to_integer(Scanner.find_closest_hostile().location);
			broadcastMessageSignal(Enemy_Location_Data, coded_map_location, Standard_Comms_Distance);
			rc.setIndicatorDot(Scanner.find_closest_hostile().location, 255, 0, 0);
			return;
		default:
			rc.broadcastSignal(100);
		}
	}

	public static MapLocation find_closest_distress() {
		return find_closest(Distress_data,0);
	}
	
	public static MapLocation find_closest_enemy_location() {
		return find_closest(Enemy_Location_Data,0);
	}
	
	public static MapLocation find_closest_fight(){	
		//ordered from most likely to continue to least in terms of signal.
		MapLocation closest_distress = find_closest_distress();
		if(closest_distress!=null){
			rc.setIndicatorDot(closest_distress, 255, 255, 255);
			return current_location.add(current_location.directionTo(closest_distress),5);
		}
	
		MapLocation closest_enemy = find_closest_enemy_location();
		if(closest_enemy != null){
			rc.setIndicatorDot(closest_enemy, 255, 0, 255);
			return closest_enemy;
		}
		
		return find_closest_chatter_location();
	}
	
	public static MapLocation find_closest_Archon(){
		MapLocation temp = find_closest(Location_Data,1);
		if(temp != null)
			location_of_archon = temp;
		
		return location_of_archon;
	}
	
	public static MapLocation find_closest_messenger(){
		return find_closest(Location_Data,0);
	}
	
	public static MapLocation find_closest_parts(){
		return find_closest(Part_Location_Data,0);
	}
	
	public static MapLocation find_closest(int message_type, int message_value){
		update_communications();
	
		MapLocation closest_point_of_interest = null;
		double closest_distance = Double.POSITIVE_INFINITY;
	
		for(Signal current_message:message_queue){
			if(Clock.getBytecodesLeft() < my_type.bytecodeLimit/5)
				return closest_point_of_interest;			
			if(current_message.getTeam() == friendly){
				if(current_message.getMessage() == null){
					if(message_type == Distress_data){
						if(current_message.getLocation().distanceSquaredTo(current_location) < closest_distance){
							closest_distance = current_message.getLocation().distanceSquaredTo(current_location);
							closest_point_of_interest = current_message.getLocation();
						}
					}
				} else{
					if(message_type == current_message.getMessage()[0]){
						MapLocation message_location = current_message.getLocation();
						if(message_type == Enemy_Location_Data)
							message_location = convert_message_into_MapLocation(current_message.getMessage()[1]);
						
						if(message_value == 0 || message_value == current_message.getMessage()[1])	
							if(message_location.distanceSquaredTo(current_location) < closest_distance){
								closest_distance = message_location.distanceSquaredTo(current_location);
								closest_point_of_interest = message_location;
							}
					} else{
						//bonus, check for exclusion zones.
						if(current_message.getMessage()[0] == Exclustion_Zone_Location_Data) {
							MapLocation test_exclusion_location = convert_message_into_MapLocation(current_message.getMessage()[1]);
							if(!exclusion_zones.contains(test_exclusion_location))
								exclusion_zones.add(test_exclusion_location);
						}
						if(current_message.getMessage()[0] == Exclustion_Zone_Remove_Data) {
							MapLocation test_exclusion_location = convert_message_into_MapLocation(current_message.getMessage()[1]);
							if(exclusion_zones.contains(test_exclusion_location))
								exclusion_zones.remove(test_exclusion_location);
						}						
					}
				}
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
        int x = (broadcast_message % 32000) - 16000;
        int y = (broadcast_message / 32000) - 16000;
        //System.out.println("Message->Maplocation : " + broadcast_message + "->" + new MapLocation(x, y).toString());
        return new MapLocation(x, y);	
	}
	
	private static int convert_MapLocation_to_integer(MapLocation location){
		int message_int = 32000 * (location.y + 16000) + (location.x + 16000);
		//System.out.println("Maplocation->int : " + location.toString() + "->" + message_int);
		return message_int;
	}

	public static void broadcast_parts() throws GameActionException {
	//	System.out.println("bytes left before parts comms" + Clock.getBytecodesLeft());
		if(rc.getCoreDelay() > 2)
			return;
		if(!Scanner.sense_parts())
			return; 
		if(Clock.getBytecodesLeft() < 300)
			return;
		
		for(MapLocation part:Scanner.sensed_locations){
			if(rc.senseParts(part) > 0 && rc.senseRubble(part) < GameConstants.RUBBLE_OBSTRUCTION_THRESH){
				broadcastMessageSignal(Part_Location_Data,convert_MapLocation_to_integer(part),Full_Comms_Distance);
				rc.setIndicatorDot(part, 100, 100, 100);
			}
		}
	}	
	
	public static void log_turret_exclusion() throws GameActionException{		
		RobotInfo[] near_enemies = Scanner.scan_for_enemy();
		if (near_enemies.length < 1)
			return;
		
		for(RobotInfo near_enemy:near_enemies){
			if(Clock.getBytecodesLeft() < 2000)
				return;
			
			if(near_enemy.type == RobotType.TURRET){
			//	Communications.broadcastMessageSignal(Exclustion_Zone_Location_Data, convert_MapLocation_to_integer(near_enemy.location), Full_Comms_Distance);
			//	rc.setIndicatorString(2, "Broadcasting exclusion");
				if(!exclusion_zones.contains(near_enemy.location))
					exclusion_zones.add(near_enemy.location);
			}
		}
	}
	
	public static void broadcast_known_exclusion() throws GameActionException{		
		if(!Communications.exclusion_zones.isEmpty()){
			int signal_strength = Full_Comms_Distance;
			if(my_type==RobotType.ARCHON)
				signal_strength = Minimal_Comms_Distance;
			
		//	System.out.println("number_of_exclusions: " + exclusion_zones.size());
			for (Iterator<MapLocation> test = Communications.exclusion_zones.iterator(); test.hasNext();){
				MapLocation exclusion_location = test.next();
				if(rc.canSenseLocation(exclusion_location)){
					RobotInfo check_exclusion = rc.senseRobotAtLocation(exclusion_location);
					if(check_exclusion==null || check_exclusion.type!=RobotType.TURRET){
						Communications.broadcastMessageSignal(Exclustion_Zone_Remove_Data, convert_MapLocation_to_integer(exclusion_location),Full_Comms_Distance);
						test.remove();
						continue;
					}
				}
				Communications.broadcastMessageSignal(Exclustion_Zone_Location_Data, convert_MapLocation_to_integer(exclusion_location), Full_Comms_Distance);
			}
		}
	}	
}
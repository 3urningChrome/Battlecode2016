package EpsilonAlt;

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
	public static final int Neutral_Location_Data = 8;
	
	public static Signal[] message_queue;
	public static int round_message_queue_updated = -1;
	public static final int Standard_Comms_Distance = 250;
	public static final int Full_Comms_Distance = 500;
	public static final int Minimal_Comms_Distance = 2;
	
	public static ArrayList<MapLocation> exclusion_zones = new ArrayList<MapLocation>();
	public static ArrayList<MapLocation> chatter_zones = new ArrayList<MapLocation>();
	public static ArrayList<MapLocation> location_zones = new ArrayList<MapLocation>();
	public static ArrayList<MapLocation> distress_zones = new ArrayList<MapLocation>();
	public static ArrayList<MapLocation> archon_zones = new ArrayList<MapLocation>();
	public static ArrayList<MapLocation> neutral_zones = new ArrayList<MapLocation>();
	
	public static boolean override_comms = false; 
		
	
	public static void update_communications(){
		if(Clock.getBytecodeNum() > byte_code_limiter - 2000)
			return;		
		if(rc.getRoundNum() <= round_message_queue_updated)
			return;
		
		if(rc.getRoundNum() % 50 == 0){
			distress_zones.clear();
			chatter_zones.clear();
		}
		
		if(!archon_zones.isEmpty())
			location_of_archon = Utilities.find_closest_MapLocation(archon_zones);
		
		archon_zones.clear();
		location_zones.clear();
		
		message_queue = rc.emptySignalQueue();
		round_message_queue_updated = rc.getRoundNum();
				
		for(Signal current_message:message_queue){
			MapLocation message_location = current_message.getLocation();
			if(Clock.getBytecodeNum() > byte_code_limiter - 4000)
				return;
			if(current_message.getTeam() == enemy){
				if(my_type == RobotType.TURRET){
				if(!chatter_zones.contains(message_location))
					chatter_zones.add(message_location);
				}
				continue;
			}
			
			if(current_message.getMessage() == null){ //basic can only be distress
				if(!distress_zones.contains(message_location))
					distress_zones.add(message_location);
				continue;
			}
			int message_data = current_message.getMessage()[1];
			MapLocation data_location;
					
			switch(current_message.getMessage()[0]){
			case Location_Data:
				if(message_data == 1){
					if(!archon_zones.contains(message_location))
							archon_zones.add(message_location);
				} else{
					if(my_type == RobotType.TURRET){
						data_location = convert_message_into_MapLocation(current_message.getMessage()[1]);
						if(!location_zones.contains(data_location))
							location_zones.add(data_location);
					}
				}
				break;
			case Part_Location_Data:
				if(my_type==RobotType.ARCHON){
					data_location = convert_message_into_MapLocation(current_message.getMessage()[1]);
					if(!Scanner.parts_locations.contains(data_location))
						Scanner.parts_locations.add(data_location);
				}
				break;
			case Neutral_Location_Data:
				if(my_type==RobotType.ARCHON){
					data_location = convert_message_into_MapLocation(current_message.getMessage()[1]);
					if(!neutral_zones.contains(data_location))
						neutral_zones.add(data_location);
				}
				break;				
			case Exclustion_Zone_Location_Data:
				data_location = convert_message_into_MapLocation(current_message.getMessage()[1]);
				if(!exclusion_zones.contains(data_location))
					exclusion_zones.add(data_location);
				break;				
			case Exclustion_Zone_Remove_Data:
				data_location = convert_message_into_MapLocation(current_message.getMessage()[1]);
				if(exclusion_zones.contains(data_location))
					exclusion_zones.remove(data_location);				
				break;
			default:
				System.out.println("Unknown messageType");
			}
		}
	}
	
	public static void log_distress_call() throws GameActionException{
		broadcast_basic();
	}
	
	public static void death_shout() throws GameActionException{
		rc.broadcastSignal(70);
		rc.setIndicatorString(2,"Death Shout");
	}
	
	public static void broadcast_my_position() throws GameActionException{
		rc.setIndicatorString(2, "Broadcast_message start");
		if(Clock.getBytecodeNum() > byte_code_limiter - 300)
			return;
		
		if(my_type == RobotType.ARCHON){
			rc.setIndicatorString(2, "Broadcast_message Archon");
			broadcast_message(Location_Data, 1);
		}else if(my_type == RobotType.SCOUT){
			broadcast_message(Location_Data, 2);
		}
	}
	
	public static void broadcast_known_exclusion() throws GameActionException{		
		if(!exclusion_zones.isEmpty()){
			for (Iterator<MapLocation> test = Communications.exclusion_zones.iterator(); test.hasNext();){
				if(Clock.getBytecodeNum() > byte_code_limiter - 500)
					return;
				MapLocation exclusion_location = test.next();
				if(rc.canSenseLocation(exclusion_location)){
					RobotInfo check_exclusion = rc.senseRobotAtLocation(exclusion_location);
					if(check_exclusion==null || check_exclusion.type!=RobotType.TURRET){
						broadcast_message(Exclustion_Zone_Remove_Data, convert_MapLocation_to_integer(exclusion_location),Minimal_Comms_Distance);
						test.remove();
						continue;
					}
				}
				broadcast_message(Exclustion_Zone_Location_Data, convert_MapLocation_to_integer(exclusion_location), Minimal_Comms_Distance);
			}
		}
	}	

	private static void broadcast_basic() throws GameActionException {

		int signal_strength = get_signal_strength();
		
		if(override_comms){
			rc.broadcastSignal(my_type.sensorRadiusSquared * 20);
			rc.setIndicatorString(2, "Broadcast Distress at: " + my_type.sensorRadiusSquared * 20);
			return;
		}
		if(signal_strength < 0)
			return;
		

		rc.broadcastSignal(Math.min(signal_strength,my_type.sensorRadiusSquared * 10));

		rc.setIndicatorString(2, "Broadcast Distress at: " + signal_strength);
	
	}

	private static void broadcast_message(int message_type, int data) throws GameActionException {
		rc.setIndicatorString(2, "Broadcast start");
		if(rc.getMessageSignalCount() >= GameConstants.MESSAGE_SIGNALS_PER_TURN)
			return;
				
		if(Clock.getBytecodeNum() > byte_code_limiter - 300)
			return;		

		int signal_strength = get_signal_strength();
		if(signal_strength < 0)
			return;	
				
		rc.broadcastMessageSignal(message_type, data, signal_strength);

		rc.setIndicatorString(2, "Broadcast_message:" + message_type + " at: " + signal_strength);
	}
	
	public static void burst_neutral() throws GameActionException {
		for (Iterator<MapLocation> test = neutral_zones.iterator(); test.hasNext();){
			MapLocation test_location = test.next();
			if(broadcast_message(Neutral_Location_Data,convert_MapLocation_to_integer(test_location), current_location.distanceSquaredTo(location_of_archon))){
				test.remove();
				rc.setIndicatorString(2,"Burst Neutral" +  + current_location.distanceSquaredTo(location_of_archon));
			}
		}
	}
	
	public static void burst_parts() throws GameActionException {
		rc.setIndicatorString(2,"Starting Burst Parts");
		for (Iterator<MapLocation> test = Scanner.parts_locations.iterator(); test.hasNext();){
			MapLocation test_location = test.next();
			if(broadcast_message(Part_Location_Data,convert_MapLocation_to_integer(test_location), current_location.distanceSquaredTo(location_of_archon))){
				test.remove();
				rc.setIndicatorString(2,"Burst Parts: " + current_location.distanceSquaredTo(location_of_archon));
			}
		}
	}	
	
	private static boolean broadcast_message(int message_type, int data, int comms_Distance) throws GameActionException {

		if(rc.getMessageSignalCount() >= GameConstants.MESSAGE_SIGNALS_PER_TURN)
			return false;
		
		if(Clock.getBytecodeNum() > byte_code_limiter - 300)
			return false;		
		
		int signal_strength = get_signal_strength();
		if(signal_strength < 0 && override_comms == false)
			return false;	
		
		if(signal_strength < comms_Distance && override_comms == false)
			return false;

		rc.broadcastMessageSignal(message_type, data, comms_Distance);

		rc.setIndicatorString(2, "Broadcast_message:" + message_type + " at: " +comms_Distance );
		return true;
	}	
	
	private static int get_signal_strength(){
		
		// 1/3 * sensorRadius * (100 * x + 1) = Broadcast Radius
		
		double my_core_delay = 1 - (rc.getCoreDelay() - (int) rc.getCoreDelay());
		double my_weapon_delay = 1 - (rc.getWeaponDelay() - (int) rc.getWeaponDelay());
		
		if(my_type.canAttack() == false){
			my_weapon_delay = 0.99999; // hack to stop archons and scouts from not sending messages to conserve weapon.
		}
		
		double left_over = Math.min(my_core_delay,my_weapon_delay);
		
		if(left_over == 0 && Math.max(rc.getCoreDelay(), rc.getWeaponDelay()) > 0) 
			return -1; //delay is whole number, but not zero.
		
		if(left_over == 0)
			left_over = 1;
		
		left_over -= 0.005;
		
		//    - so ((0.95 * 100)+ 1) * 35 * 0.333333  should === 1119.9999 NOT 0!!
		
		if(left_over < 0.05)
			return -1;
		
		double max_distance = (int) (0.33333333 * my_type.sensorRadiusSquared * (100 * (left_over) + 1));
		
		
		if(max_distance < my_type.sensorRadiusSquared)
			max_distance = -1;
		
		if(max_distance > 20000)
			max_distance = 20000;
		
		return (int) (max_distance);		
	}
	
	private static MapLocation convert_message_into_MapLocation(int broadcast_message) {
        int x = (broadcast_message % 32000) - 16000;
        int y = (broadcast_message / 32000) - 16000;
        return new MapLocation(x, y);	
	}
	
	private static int convert_MapLocation_to_integer(MapLocation location){
		int message_int = 32000 * (location.y + 16000) + (location.x + 16000);
		return message_int;
	}
	
}
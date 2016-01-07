package team038;

import battlecode.common.*;

public class NavDuckBug extends NavigationBase {
	static double closest_distance;

    public enum WallSide {
        LEFT, RIGHT
    }
    
	
    private static int bugStartDistSq;
    private static Direction bugLastMoveDir;
    private static Direction bugLookStartDir;
    private static int bugRotationCount;
    private static int bugMovesSinceSeenObstacle = 0;

    public static int minBfsInitRound = 0;
    
    public static WallSide bugWallSide = WallSide.LEFT;
    
    public static MapLocation return_value;
    
	public static MapLocation navigate_to_destination(MapLocation the_destination){	
		return_value = current_location;
		try {
			goTo(the_destination);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return return_value;
	}

	private static boolean tryMoveDirect() throws GameActionException {
	    if (rc.getType() == RobotType.SCOUT) return tryMoveDirectDrone();
	    Direction toDest = current_location.directionTo(destination);

	    if (can_move(toDest)) {
	    	return_value = current_location.add(toDest,1);
	    	return true;
	    }

	    Direction[] dirs = new Direction[2];
	    Direction dirLeft = toDest.rotateLeft();
	    Direction dirRight = toDest.rotateRight();
	    if (current_location.add(dirLeft).distanceSquaredTo(destination) < current_location.add(dirRight).distanceSquaredTo(destination)) {
	        dirs[0] = dirLeft;
	        dirs[1] = dirRight;
	    } else {
	        dirs[0] = dirRight;
	        dirs[1] = dirLeft;
	    }
	    for (Direction dir : dirs) {
	        if (can_move(dir)) {
	        	return_value = current_location.add(dir,1);
	            return true;
	        }
	    }
	    return false;
	}

	private static boolean tryMoveDirectDrone() throws GameActionException {
	    Direction dirAhead = current_location.directionTo(destination);
	    MapLocation locAhead = current_location.add(dirAhead);
	    if(can_move(dirAhead) && rc.onTheMap(locAhead)) {
	    	return_value = current_location.add(dirAhead,1);
	        return true;
	    }
	       
	    Direction dirLeft = dirAhead.rotateLeft();
	    Direction dirRight = dirAhead.rotateRight();
	        
	    Direction[] dirs = new Direction[3];
	    dirs[0] = dirAhead;
	    if (current_location.add(dirLeft).distanceSquaredTo(destination) < current_location.add(dirRight).distanceSquaredTo(destination)) {
	        dirs[1] = dirLeft;
	        dirs[2] = dirRight;
	    } else {
	        dirs[1] = dirRight;
	        dirs[2] = dirLeft;
	    }

	    Direction voidMove = null;
	    for(Direction dir : dirs) {
	        if(can_move(dir)) {
	            if(rc.onTheMap(current_location.add(dir))) {
	            	return_value = current_location.add(dir,1);
	                return true;
	            } else {
	                if(voidMove != null) {
	                    voidMove = dir;
	                }
	            }
	        }
	    }
	        
	    if(voidMove != null) {
	    	return_value = current_location.add(voidMove,1);
	        return true;
	    }
	        
	    return false;
	}

	private static void startBug() throws GameActionException {
	    bugStartDistSq = current_location.distanceSquaredTo(destination);
	    bugLastMoveDir = current_location.directionTo(destination);
	    bugLookStartDir = current_location.directionTo(destination);
	    bugRotationCount = 0;
	    bugMovesSinceSeenObstacle = 0;

	    // try to intelligently choose on which side we will keep the wall
	    Direction leftTryDir = bugLastMoveDir.rotateLeft();
	    for (int i = 0; i < 3; i++) {
	        if (!can_move(leftTryDir)) leftTryDir = leftTryDir.rotateLeft();
	        else break;
	    }
	    Direction rightTryDir = bugLastMoveDir.rotateRight();
	    for (int i = 0; i < 3; i++) {
	    	if (!can_move(rightTryDir)) rightTryDir = rightTryDir.rotateRight();
	    	else break;
	    }
	    if (destination.distanceSquaredTo(current_location.add(leftTryDir)) < destination.distanceSquaredTo(current_location.add(rightTryDir))) {
	        bugWallSide = WallSide.RIGHT;
	    } else {
	        bugWallSide = WallSide.LEFT;
	    }
	}

	private static Direction findBugMoveDir() throws GameActionException {
	    bugMovesSinceSeenObstacle++;
	    Direction dir = bugLookStartDir;
	    for (int i = 8; i-- > 0;) {
	        if (can_move(dir)) return dir;
	        dir = (bugWallSide == WallSide.LEFT ? dir.rotateRight() : dir.rotateLeft());
	        bugMovesSinceSeenObstacle = 0;
	    }
	    return null;
	}
	
	private static int numRightRotations(Direction start, Direction end) {
	    return (end.ordinal() - start.ordinal() + 8) % 8;
	}

	private static int numLeftRotations(Direction start, Direction end) {
	    return (-end.ordinal() + start.ordinal() + 8) % 8;
	}

	private static int calculateBugRotation(Direction moveDir) {
	    if (bugWallSide == WallSide.LEFT) {
	        return numRightRotations(bugLookStartDir, moveDir) - numRightRotations(bugLookStartDir, bugLastMoveDir);
	    } else {
	        return numLeftRotations(bugLookStartDir, moveDir) - numLeftRotations(bugLookStartDir, bugLastMoveDir);
	    }
	}

	private static void bugMove(Direction dir) throws GameActionException {
	    if (Navigation.move_towards(dir)) {
	    	return_value = current_location.add(dir,1);
	        bugRotationCount += calculateBugRotation(dir);
	        bugLastMoveDir = dir;
	        if (bugWallSide == WallSide.LEFT) bugLookStartDir = dir.rotateLeft().rotateLeft();
	        else bugLookStartDir = dir.rotateRight().rotateRight();
	    }
	}

	private static boolean detectBugIntoEdge() throws GameActionException {
	    if (bugWallSide == WallSide.LEFT) {
	        return rc.onTheMap(current_location.add(bugLastMoveDir.rotateLeft()));
	    } else {
	        return rc.onTheMap(current_location.add(bugLastMoveDir.rotateRight()));
	    }
	}

	private static void reverseBugWallFollowDir() throws GameActionException {
	    bugWallSide = (bugWallSide == WallSide.LEFT ? WallSide.RIGHT : WallSide.LEFT);
	    startBug();
	}

	private static void bugTurn() throws GameActionException {
	    if (detectBugIntoEdge()) {
	        reverseBugWallFollowDir();
	    }
	    Direction dir = findBugMoveDir();
	    if (dir != null) {
	       bugMove(dir);
	    }
	}

	private static boolean canEndBug() {
	    if (bugMovesSinceSeenObstacle >= 4) return true;
	    return (bugRotationCount <= 0 || bugRotationCount >= 8) && current_location.distanceSquaredTo(destination) <= bugStartDistSq;
	}

	private static void bugMove() throws GameActionException {
	    // Check if we can stop bugging at the *beginning* of the turn
	    if (current_navigation_state == NavBugState.BUGGING) {
	        if (canEndBug()) {
	            current_navigation_state = NavBugState.DIRECT;
	        }
	    }

	    // If DIRECT mode, try to go directly to target
	    if (current_navigation_state == NavBugState.DIRECT) {
	        if (!tryMoveDirect()) {
	            // Debug.indicateAppend("nav", 1, "starting to bug; ");
	            current_navigation_state = NavBugState.BUGGING;
	            startBug();
	        } else {
	                // Debug.indicateAppend("nav", 1, "successful direct move; ");
	        }
	    }

	    // If that failed, or if bugging, bug
	    if (current_navigation_state == NavBugState.BUGGING) {
	        // Debug.indicateAppend("nav", 1, "bugging; ");
	        bugTurn();
	    }
	}

	public static void goTo(MapLocation theDest) throws GameActionException {
	    if (!theDest.equals(destination)) {
	        destination = theDest;
	        current_navigation_state = NavBugState.DIRECT;
	    }

	    if (current_location.equals(destination)) return;

	    bugMove();
	}
}

/**
Duck Nav. 
Ripped straight from github for last year.
What does this do differently than I have.
How does it handle bots in the way.
*/
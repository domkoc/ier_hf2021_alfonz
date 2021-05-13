import java.util.Random;
import jason.environment.grid.GridWorldModel;
import java.util.ArrayList;
import jason.environment.grid.Location;


class FloorModel extends GridWorldModel {
	
	public static final int GridSize = 7; 			// grid size
	public static final int Base  = 8; 		// station code in grid model
    public static final int Dirt  = 16; 		// garbage code in grid model
    ArrayList<Location> dirt_stains = new ArrayList<Location>();

    Random random = new Random(System.currentTimeMillis());
    
    Vacuum vacuum_B = new Vacuum("vacuum_B");
    Vacuum vacuum_A = new Vacuum("vacuum_A");
    Vacuum vacuum_C = new Vacuum("vacuum_C");
    
    FloorModel() {
        super(GridSize, GridSize, 3);
        
        // initial location of agents
        try {
            setAgPos(0, 0, 0);
            setAgPos(1, 1, 0);
            setAgPos(2, 2, 0);
        
        // location of walls    
            addWall(0, 1, 0, 6);
            addWall(6, 1, 6, 6);
            addWall(2, 1, 4, 1);
            addWall(3, 1, 3, 6);
            addWall(0, 6, 6, 6);
            
       // location of station 
            add(Base, 6, 0);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        Location garbage = new Location(2,4);
        dirt_stains.add(garbage);
        
        // initial location of a garbage
        add(Dirt, 2, 4);

    }

    public void addDirt(int x, int y){
    	add(Dirt, x, y);
    }
    
    public void empty(String ag) {
    	switch(ag) {
		case "vacuum_B":
			vacuum_B.empty();
			break;
		case "vacuum_A":
			vacuum_A.empty();
			break;
		case "vacuum_C":
			vacuum_C.empty();
			break;		
		}	
	}

	public void recharge(String ag) {
		switch(ag) {
		case "vacuum_B":
			vacuum_B.recharge();
			break;
		case "vacuum_A":
			vacuum_A.recharge();
			break;
		case "vacuum_C":
			vacuum_C.recharge();
			break;		
		}	
	}

	public boolean adjustResourceLimits(String ag, int x, int y, int fuel) {
		Location loc = new Location(0,0);
    	if(ag.equals("vacuum_B")){
    		loc = getAgPos(0);
    	}
    	else if (ag.equals("vacuum_A")){
    		loc = getAgPos(1);
    	}
    	else if (ag.equals("vacuum_C")){
    		loc = getAgPos(2);
    	}
    	if(findPathAndDistanceTo(loc.x, loc.y, x, y).get(2) + findPathAndDistanceTo(x, y, 6, 0).get(2) >= fuel - 5)
    		return false;
    	return true;
		
	}

	public void clean(String ag, int x, int y) throws Exception {
		
		switch(ag) {
		case "vacuum_B":
			vacuum_B.reducePower(Vacuum.CleaningChargeCost);
			vacuum_B.reduceSpace(Vacuum.CleaningSpaceCost);
			remove(Dirt, x, y);
			break;
		case "vacuum_A":
			vacuum_A.reducePower(Vacuum.CleaningChargeCost);
			vacuum_A.reduceSpace(Vacuum.CleaningSpaceCost);
			remove(Dirt, x, y);
			break;
		case "vacuum_C":
			vacuum_C.reducePower(Vacuum.CleaningChargeCost);
			vacuum_C.reduceSpace(Vacuum.CleaningSpaceCost);
			remove(Dirt, x, y);
			break;		
		}
    }
    
    public void moveTowards(String ag, int x, int y) {
    	Location loc = new Location(0,0);
    	int agent = 0;
    	if(ag.equals("vacuum_B")){
    		loc = getAgPos(0);
    	}
    	else if (ag.equals("vacuum_A")){
    		loc = getAgPos(1);
    		agent = 1;
    	}
    	else if (ag.equals("vacuum_C")){
    		loc = getAgPos(2);
    		agent = 2;
    	}
    	ArrayList<Integer> path = findPathAndDistanceTo(loc.x, loc.y, x, y);
    	loc.x += path.get(0);
    	loc.y += path.get(1);
    	setAgPos(agent, loc);
    }

    ArrayList<Integer> findPathAndDistanceTo(int x1, int y1, int x2, int y2){
    	int first_inside = inside(x1, y1);
    	int second_inside = inside(x2, y2);
    	ArrayList<Integer> returnList = new ArrayList<Integer>();
    	if(first_inside == -1 && second_inside == -1){
    		returnList.add(x2 - x1);
    		returnList.add(y2 - y1);
    		returnList.add(Math.abs(x1 - x2) + Math.abs(y1 - y2));
    	} 
    	else if(first_inside == 1 && second_inside == 1){
    		if(x1 <= 2 && x2 <= 2){
    			returnList.add(x2 - x1);
	    		returnList.add(y2 - y1);
	    		returnList.add(Math.abs(x1 - x2) + Math.abs(y1 - y2));
    		}
    		else if(x1 > 2 && x2 > 2){
    			returnList.add(x2 - x1);
	    		returnList.add(y2 - y1);
	    		returnList.add(Math.abs(x1 - x2) + Math.abs(y1 - y2));
    		}
    		else if(x1 <= 2){
    			returnList.add(1 - x1);
		    	returnList.add(1 - y1);
		    	returnList.add(Math.abs(x1 - 1) + Math.abs(y1 - 1));
    		}
    		else{
    			returnList.add(5 - x1);
		    	returnList.add(1 - y1);
		    	returnList.add(Math.abs(x1 - 5) + Math.abs(y1 - 1));
    		}
    	}
    	else if(first_inside == 1){
    		if(second_inside == 0){
    			if(x1 <= 2 && x2 == 1){
    				returnList.add(x2 - x1);
		    		returnList.add(y2 - y1);
		    		returnList.add(Math.abs(x1 - x2) + Math.abs(y1 - y2));
    			}
    			if(x1 <= 2){
    				returnList.add(1 - x1);
		    		returnList.add(1 - y1);
		    		returnList.add(Math.abs(x1 - 1) + Math.abs(y1 - 1) + 6);
    			}
    			if(x2 == 5){
    				returnList.add(x2 - x1);
		    		returnList.add(y2 - y1);
		    		returnList.add(Math.abs(x1 - x2) + Math.abs(y1 - y2));
    			}
    			returnList.add(5 - x1);
		    	returnList.add(1 - y1);
		    	returnList.add(Math.abs(x1 - 5) + Math.abs(y1 - 1) + 6);
    		}
    		else{
    			if(x1 <= 2){
    				returnList.add(1 - x1);
    				returnList.add(1 - y1);
    				returnList.add(Math.abs(1 - y1) + Math.abs(x1 - 1));
    			}
    			else {
    				returnList.add(5 - x1);
    				returnList.add(1 - y1);
    				returnList.add(Math.abs(1 - y1) + Math.abs(x1 - 5));
    			}
    		}
    	}
    	else if(first_inside == -1){
    		if(second_inside == 0){
    			if(x2 <= 2){
    				returnList.add(1 - x1);
		    		returnList.add(1);
		    		returnList.add(Math.abs(x1 - x2) + 1);
    			} 
    			else{
	    			returnList.add(5 - x1);
			    	returnList.add(1);
			    	returnList.add(Math.abs(x1 - 5) + 1);
		   		}
    		}
    		else{
    			if(x2 <= 2){
    				returnList.add(1 - x1);
		    		returnList.add(1);
		    		returnList.add(Math.abs(x1 - x2) + 1);
    			}
    			else {
    				returnList.add(5 - x1);
			    	returnList.add(1);
			    	returnList.add(Math.abs(x1 - 5) + 1);
    			}
    		}
    	}
    	else if(first_inside == 0){
    		if(second_inside == 1){
    			if(x1 <= 2 && x2 <= 2){
    				returnList.add(0);
			    	returnList.add(1);
			    	returnList.add(6);
    			} 
    			else if(x1 <= 2 && x2 > 2){
		    		returnList.add(0);
			    	returnList.add(-1);
			    	returnList.add(6);
    			}
    			else if(x1 > 2 && x2 <= 2){
    				returnList.add(0);
			    	returnList.add(-1);
			    	returnList.add(6);
    			}
    			else{
    				returnList.add(0);
			    	returnList.add(1);
			    	returnList.add(6);
    			}
    		}
    		else{
	    		returnList.add(0);
		    	returnList.add(-1);
		    	returnList.add(6);
    		}
    	}
    	else{
	    	returnList.add(0);
	    	returnList.add(-1);
	    	returnList.add(6);
    	}
    	return returnList;

    }

    int inside(int x, int y){
    	if(y == 0)
    		return -1;
    	if(y == 1)
    		return 0;
    	return 1;
    }
}
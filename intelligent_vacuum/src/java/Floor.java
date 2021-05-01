import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.Location;
import java.util.logging.Logger;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class Floor extends Environment{

    public static final Term	clean = Literal.parseLiteral("clean(x1, y1)");
    public static final Term	recharge = Literal.parseLiteral("recharge(vacuum)");
    public static final Term	empty = Literal.parseLiteral("empty(vacuum)");
    
    static Logger logger = Logger.getLogger(Floor.class.getName());

    FloorModel model;
    FloorView  view;
    private Location vacuum_BPrevious;
    private Location vacuum_APrevious;
    private Location vacuum_CPrevious;
    ArrayList<Location> dirt_stains = new ArrayList<Location>();
    ArrayList<Boolean> garbageValidity = new ArrayList<Boolean>();

    int vacuum_BCharge = 50;
    int vacuum_ACharge = 50;
    int vacuum_CCharge = 50;
    int vacuum_BSupplies = 30;
    int vacuum_ASupplies = 30;
    int vacuum_CSupplies = 30;

    int vacuum_BOldCharge;
    int vacuum_AOldCharge;
    int vacuum_COldCharge;
    int vacuum_BOldSupplies;
    int vacuum_AOldSupplies;
    int vacuum_COldSupplies;
   

    @Override
    public void init(String[] args) {
        model = new FloorModel();

        Location vacuum_BLoc = model.getAgPos(0);
        Location vacuum_ALoc = model.getAgPos(1);
        Location vacuum_CLoc = model.getAgPos(2);
        vacuum_BPrevious = vacuum_BLoc;
        vacuum_APrevious = vacuum_ALoc;
        vacuum_CPrevious = vacuum_CLoc;    

        Literal self1 = Literal.parseLiteral("isSelf(vacuum_B)");
        Literal self2 = Literal.parseLiteral("isSelf(vacuum_A)");
        Literal self3 = Literal.parseLiteral("isSelf(vacuum_C)");

        addPercept("vacuum_B", self1);
        addPercept("vacuum_A", self2);
        addPercept("vacuum_C", self3);

        Literal limit1 = Literal.parseLiteral("needs_charge(25, vacuum_B)");
        Literal limit2 = Literal.parseLiteral("needs_charge(25, vacuum_A)");
        Literal limit3 = Literal.parseLiteral("needs_charge(25, vacuum_C)");
        addPercept("vacuum_B", limit1);
        addPercept("vacuum_A", limit2);
        addPercept("vacuum_C", limit3);

        Literal limit4 = Literal.parseLiteral("needs_space(15, vacuum_B)");
        Literal limit5 = Literal.parseLiteral("needs_space(15, vacuum_A)");
        Literal limit6 = Literal.parseLiteral("needs_space(15, vacuum_C)");
        addPercept("vacuum_B", limit4);
        addPercept("vacuum_A", limit5);
        addPercept("vacuum_C", limit6);

        Location garbage = new Location(2,4);
        dirt_stains.add(garbage);
        garbageValidity.add(true);

        Literal free1 = Literal.parseLiteral("free(vacuum_B)");
        Literal free2 = Literal.parseLiteral("free(vacuum_A)");
        Literal free3 = Literal.parseLiteral("free(vacuum_C)");

        addPercept("dirtsensor", free1);
        addPercept("dirtsensor", free2);
        addPercept("dirtsensor", free3);

        view  = new FloorView(model);
		view.addClickListener(this);
        
        model.setView(view);
        Literal dirt_generated = Literal.parseLiteral("dirt_generated(garbage1)");
        Literal garbage_place = Literal.parseLiteral("pos(garbage1, 2, 4)");
        addPercept("dirtsensor", dirt_generated);
        addPercept(garbage_place);
        updatePercepts();
    }
    
    @Override
    public boolean executeAction(String ag, Structure action) {
        logger.info(ag+" doing: "+ action);
        try {
        	if (action.getFunctor().equals("moveTowards")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                model.moveTowards(ag,x,y);
                int agent = 0;
                if(ag.equals("vacuum_A"))
                    agent = 1;
                if(ag.equals("vacuum_C"))
                    agent = 2;
                Location loc = model.getAgPos(agent);

                int fuelCost = model.findPathAndDistanceTo(loc.x, loc.y, x, y).get(2);
                if(ag.equals("vacuum_B"))
                    vacuum_BCharge -= fuelCost;
                if(ag.equals("vacuum_A"))
                    vacuum_ACharge -= fuelCost;
                if(ag.equals("vacuum_C"))
                    vacuum_CCharge -= fuelCost;
                
            } else if (action.getFunctor().equals("clean")) {
            	int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                model.clean(ag,x,y);
                garbageValidity.set(dirt_stains.indexOf(new Location(x, y)), false);
                if(ag.equals("vacuum_B"))
                    vacuum_BSupplies -= 5;
                if(ag.equals("vacuum_A"))
                    vacuum_ASupplies -= 5;
                if(ag.equals("vacuum_C"))
                    vacuum_CSupplies -= 5;
                
            } else if (action.getFunctor().equals("set_resource_limits")) {
            	int x = (int)((NumberTerm)action.getTerm(1)).solve();
                int y = (int)((NumberTerm)action.getTerm(2)).solve();
                int fuel;
                if(ag.equals("vacuum_B")){
                    fuel = vacuum_BCharge;
                    Literal allowance = Literal.parseLiteral("is_allowed_to_clean(vacuum_B)");
                    if(model.adjustResourceLimits(ag,x,y, fuel)){
                        addPercept("vacuum_A", allowance);
                    }
                    else{
                        removePercept("vacuum_B", allowance);
                    }
                }
                if(ag.equals("vacuum_A")){
                    fuel = vacuum_ACharge;
                    Literal allowance = Literal.parseLiteral("is_allowed_to_clean(vacuum_A)");
                    if(model.adjustResourceLimits(ag,x,y, fuel)){
                        addPercept("vacuum_A", allowance);
                    }
                    else{
                        removePercept("vacuum_A", allowance);
                    }
                }
                if(ag.equals("vacuum_C")){
                    fuel = vacuum_CCharge;
                    Literal allowance = Literal.parseLiteral("is_allowed_to_clean(vacuum_C)");
                    if(model.adjustResourceLimits(ag,x,y, fuel)){
                        addPercept("vacuum_C", allowance);
                    }
                    else{
                        removePercept("vacuum_C", allowance);
                    }
                }
                
            } else if (action.getFunctor().equals("recharge")) {
                model.recharge(ag);
                System.out.println(vacuum_BCharge);
                if(ag.equals("vacuum_B"))
                    vacuum_BCharge = 50;
                if(ag.equals("vacuum_A"))
                    vacuum_ACharge = 50;
                if(ag.equals("vacuum_C"))
                    vacuum_CCharge = 50;
                
            } else if (action.getFunctor().equals("empty")) {
                model.empty(ag);
                System.out.println("vacuum_BSupplies " + vacuum_BSupplies);
                if(ag.equals("vacuum_B"))
                    vacuum_BSupplies = 25;
                if(ag.equals("vacuum_A"))
                    vacuum_ASupplies = 25;
                if(ag.equals("vacuum_C"))
                    vacuum_CSupplies = 25;
                
            } else if(action.getFunctor().equals("task_cleaning")){
                String vacuum = action.getTerm(0).toString();
                String position = action.getTerm(1).toString();
                Literal command = Literal.parseLiteral("told_to_vacuum(" + vacuum + ", " + position + ")");
                Literal busy = Literal.parseLiteral("free(" + vacuum + ")");
                Literal cleaned = Literal.parseLiteral("dirt_generated(" + position + ")");
                removePercept("dirtsensor", cleaned);
                removePercept("dirtsensor", busy);
                addPercept(vacuum, command);
            }
            else if(action.getFunctor().equals("done")){
                Literal free = Literal.parseLiteral("free(" + action.getTerm(0).toString() + ")");
                addPercept("dirtsensor", free);
            }
            else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        updatePercepts();

        try {
            Thread.sleep(200);
        } catch (Exception e) {}
        informAgsEnvironmentChanged();
        return true;
    }
    
    /** creates the agents' perception based on the FloorModel */
    void updatePercepts() {
        
        Literal oldPos1 = Literal.parseLiteral("pos(vacuum_B," + vacuum_BPrevious.x + ", " + vacuum_BPrevious.y + ")");
        Literal oldPos2 = Literal.parseLiteral("pos(vacuum_A," + vacuum_APrevious.x + ", " + vacuum_APrevious.y + ")");
        Literal oldPos3 = Literal.parseLiteral("pos(vacuum_C," + vacuum_CPrevious.x + ", " + vacuum_CPrevious.y + ")");
        removePercept("vacuum_B", oldPos1);
        removePercept("vacuum_A", oldPos2);
        removePercept("vacuum_C", oldPos3);

        Literal oldCharge1 = Literal.parseLiteral("has_charge(" + vacuum_BOldCharge + ", vacuum_B)");
        Literal oldCharge2 = Literal.parseLiteral("has_charge(" + vacuum_AOldCharge + ", vacuum_A)");
        Literal oldCharge3 = Literal.parseLiteral("has_charge(" + vacuum_COldCharge + ", vacuum_C)");
        removePercept("vacuum_B", oldCharge1);
        removePercept("vacuum_A", oldCharge2);
        removePercept("vacuum_C", oldCharge3);

        vacuum_BOldCharge = vacuum_BCharge;
        vacuum_AOldCharge = vacuum_ACharge;
        vacuum_COldCharge = vacuum_CCharge;

        Literal newCharge1 = Literal.parseLiteral("has_charge(" + vacuum_BCharge + ", vacuum_B)");
        Literal newCharge2 = Literal.parseLiteral("has_charge(" + vacuum_ACharge + ", vacuum_A)");
        Literal newCharge3 = Literal.parseLiteral("has_charge(" + vacuum_COldCharge + ", vacuum_C)");
        addPercept("vacuum_B", newCharge1);
        addPercept("vacuum_A", newCharge2);
        addPercept("vacuum_C", newCharge3);



        Literal oldSpace1 = Literal.parseLiteral("has_space(" + vacuum_BOldSupplies + ", vacuum_B)");
        Literal oldSpace2 = Literal.parseLiteral("has_space(" + vacuum_AOldSupplies + ", vacuum_A)");
        Literal oldSpace3 = Literal.parseLiteral("has_space(" + vacuum_COldSupplies + ", vacuum_C)");
        removePercept("vacuum_B", oldSpace1);
        removePercept("vacuum_A", oldSpace2);
        removePercept("vacuum_C", oldSpace3);

        vacuum_BOldSupplies = vacuum_BSupplies;
        vacuum_AOldSupplies = vacuum_ASupplies;
        vacuum_COldSupplies = vacuum_CSupplies;

        Literal newSupplies1 = Literal.parseLiteral("has_space(" + vacuum_BOldSupplies + ", vacuum_B)");
        Literal newSupplies2 = Literal.parseLiteral("has_space(" + vacuum_AOldSupplies + ", vacuum_A)");
        Literal newSupplies3 = Literal.parseLiteral("has_space(" + vacuum_COldSupplies + ", vacuum_C)");
        addPercept("vacuum_B", newSupplies1);
        addPercept("vacuum_A", newSupplies2);
        addPercept("vacuum_C", newSupplies3);




        Literal garbage1 = Literal.parseLiteral("garbage(vacuum_B)");
        Literal garbage2 = Literal.parseLiteral("garbage(vacuum_A)");
        Literal garbage3 = Literal.parseLiteral("garbage(vacuum_C)");

        Location vacuum_BLoc = model.getAgPos(0);
        Location vacuum_ALoc = model.getAgPos(1);
        Location vacuum_CLoc = model.getAgPos(2);

        if(dirt_stains.contains(vacuum_BLoc) && !garbageValidity.get(dirt_stains.indexOf(vacuum_BLoc))){
            removePercept("vacuum_B", garbage1);
        }
        if(dirt_stains.contains(vacuum_ALoc) && !garbageValidity.get(dirt_stains.indexOf(vacuum_ALoc))){
            removePercept("vacuum_A", garbage2);
        }
        if(dirt_stains.contains(vacuum_CLoc) && !garbageValidity.get(dirt_stains.indexOf(vacuum_CLoc))){
            removePercept("vacuum_C", garbage3);
        }

        if(dirt_stains.contains(vacuum_BLoc) && garbageValidity.get(dirt_stains.indexOf(vacuum_BLoc))){
            Literal warning = Literal.parseLiteral("garbage(vacuum_B)");
            addPercept("vacuum_B", warning);

            Literal cleaning = Literal.parseLiteral("is_allowed_to_clean(vacuum_B)");
            addPercept("vacuum_B", cleaning);
        }
        if(dirt_stains.contains(vacuum_ALoc) && garbageValidity.get(dirt_stains.indexOf(vacuum_ALoc))){
            Literal warning = Literal.parseLiteral("garbage(vacuum_A)");
            addPercept("vacuum_A", warning);

            Literal cleaning = Literal.parseLiteral("is_allowed_to_clean(vacuum_A)");
            addPercept("vacuum_A", cleaning);
        }
        if(dirt_stains.contains(vacuum_CLoc) && garbageValidity.get(dirt_stains.indexOf(vacuum_CLoc))){
            Literal warning = Literal.parseLiteral("garbage(vacuum_C)");
            addPercept("vacuum_C", warning);

            Literal cleaning = Literal.parseLiteral("is_allowed_to_clean(vacuum_C)");
            addPercept("vacuum_C", cleaning);
        }


        vacuum_BPrevious = vacuum_BLoc;
        vacuum_APrevious = vacuum_ALoc;
        vacuum_CPrevious = vacuum_CLoc;    
        
        Literal pos1 = Literal.parseLiteral("pos(vacuum_B," + vacuum_BLoc.x + "," + vacuum_BLoc.y + ")");
        Literal pos2 = Literal.parseLiteral("pos(vacuum_A," + vacuum_ALoc.x + "," + vacuum_ALoc.y + ")");
        Literal pos3 = Literal.parseLiteral("pos(vacuum_C," + vacuum_CLoc.x + "," + vacuum_CLoc.y + ")");

        addPercept("vacuum_B",pos1);
        addPercept("vacuum_A",pos2);
        addPercept("vacuum_C",pos3);

        model.setAgPos(0, vacuum_BLoc);
        model.setAgPos(1, vacuum_ALoc);
        model.setAgPos(2, vacuum_CLoc);

        informAgsEnvironmentChanged();

    }

    void addGarbage(int x, int y){
        dirt_stains.add(new Location(x, y));
        garbageValidity.add(true);
        model.addGarbage(x, y);

        Literal dirt_generated = Literal.parseLiteral("dirt_generated(garbage" + dirt_stains.size() +")");
        Literal garbage_place = Literal.parseLiteral("pos(garbage" + dirt_stains.size() + ", " + x + ", " + y + ")");
        addPercept("dirtsensor", dirt_generated);
        addPercept(garbage_place);
    }
}
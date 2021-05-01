public class Vacuum {
	
	public static final int ChargeInit = 50;				// initial values
	public static final int SpaceInit = 30;			// initial values
	
	public static final int ChargeLimit = 25; 			// fuel limit
	public static final int SpaceLimit = 15; 			// liquid limit
	
	public static final int CleaningChargeCost = 5;		// cost of cleaning a dirt
	public static final int MovingChargeCost = 1;			// cost of moving one step
	
	public static final int CleaningSpaceCost = 5;		// cost of cleaning a dirt
	
	String name;
	int power;
	int space;
	public boolean inside;
	
	Vacuum(String name) {
		this.name = name;
		power = ChargeInit;
		space = SpaceInit;
		inside = false;	
	}
	
	void reducePower(int cost) throws Exception {
		if (power - cost < 0)
			throw new Exception( name + " is dead");
		power -= cost;
	}
	
	void reduceSpace(int cost) throws Exception {
		if (space - cost < 0)
			throw new Exception( name +" has no space");
		space -= cost;
	}
	
	void recharge() {
		power = ChargeInit;
	}
	
	void empty() {
		space = SpaceInit;
	}
}
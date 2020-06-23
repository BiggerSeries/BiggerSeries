package net.roguelogix.biggerreactors.classic.helpers;

import net.minecraft.nbt.CompoundNBT;

import net.roguelogix.biggerreactors.classic.reactor.ReactorFuelRegistry;

import static net.roguelogix.biggerreactors.classic.reactor.ReactorFuelRegistry.cyanite;

/**
 * Class to help with fuel/waste tracking in reactors.
 * For now, 
 * @author ErogenousBeef
 *
 */
public class FuelContainer extends ReactantContainer {
	private static final String[] tankNames = { "fuel", "waste" };
	private static final int FUEL = 0;
	private static final int WASTE = 1;
	
	private float radiationFuelUsage;
	
	public FuelContainer() {
		super(tankNames, 0);
		radiationFuelUsage = 0f;
	}

	public int getFuelAmount() {
		return getReactantAmount(FUEL);
	}
	
	public int getWasteAmount() {
		return getReactantAmount(WASTE);
	}
	
	@Override
	public boolean isReactantValidForStack(int idx, String name) {
		switch(idx) {
		case FUEL:
			return ReactorFuelRegistry.isFuel(name);
		case WASTE:
			// Allow anything into our output slot, in case someone wants to do breeders or something
			return true;
		default:
			return false;
		}
	}

	/**
	 * Add some fuel to the current pile, if possible.
	 * @param name The name of the reactant to add.
	 * @param amount The quantity of reactant to add.
	 * @param doAdd If true, this will only simulate a fill and will not alter the fuel amount.
	 * @return The amount of reactant actually added
	 */
	public int addFuel(String name, int amount, boolean doAdd) {
		if(name == null || amount <= 0) { return 0; }
		else {
			return fill(FUEL, name, amount, doAdd);
		}
	}
	
	/**
	 * Add some waste to the current pile, if possible.
//	 * @param incoming A FluidStack representing the fluid to fill, and the maximum amount to add to the tank.
	 * @return The amount of waste actually added
	 */
	public int addWaste(String name, int amount) {
		int wasteAdded = fill(WASTE, name, amount, true);
		return wasteAdded;
	}
	
	private int addWaste(int wasteAmt) {
		if(this.getWasteType() == null) {
//			BRLog.warning("System is using addWaste(int) when there's no waste present, defaulting to cyanite");
			return fill(WASTE, cyanite, wasteAmt, true);
		}
		else {
			return addToStack(WASTE, wasteAmt);
		}
	}
	
	public int dumpFuel() {
		return dump(FUEL);
	}
	
	public int dumpFuel(int amount) {
		return dump(FUEL, amount);
	}
	
	public int dumpWaste() {
		return dump(WASTE);
	}
	
	public int dumpWaste(int amount) {
		return dump(WASTE, amount);
	}
	
	public String getFuelType() {
		return getReactantType(FUEL);
	}
	
	public String getWasteType() {
		return getReactantType(WASTE);
	}
	
	public CompoundNBT writeToNBT(CompoundNBT destination) {
		super.writeToNBT(destination);
		
		destination.putFloat("fuelUsage", radiationFuelUsage);
		return destination;
	}
	
	public void readFromNBT(CompoundNBT data) {
		super.readFromNBT(data);
		
		if(data.hasUniqueId("fuelUsage")) {
			radiationFuelUsage = data.getFloat("fuelUsage");
		}
	}
	
	public void emptyFuel() {
		setReactant(FUEL, null);
	}
	
	public void emptyWaste() {
		setReactant(WASTE, null);
	}
	
	public void setFuel(ReactantStack newFuel) {
		setReactant(FUEL, newFuel);
	}
	
	public void setWaste(ReactantStack newWaste) {
		setReactant(WASTE, newWaste);
	}
	
	public void merge(FuelContainer other) {
		radiationFuelUsage = Math.max(radiationFuelUsage, other.radiationFuelUsage);
		
		super.merge(other);
	}
	
	public void onRadiationUsesFuel(float fuelUsed) {
		if(Float.isInfinite(fuelUsed) || Float.isNaN(fuelUsed)) { return; }
		
		radiationFuelUsage += fuelUsed;
		
		if(radiationFuelUsage < 1f) {
			return;
		}

		int fuelToConvert = Math.min(getFuelAmount(), (int)radiationFuelUsage);

		if(fuelToConvert <= 0) { return; }
		
		radiationFuelUsage = Math.max(0f, radiationFuelUsage - fuelToConvert);

		String fuelType = getFuelType();
		if(fuelType != null) {
			this.dumpFuel(fuelToConvert);
			
			if(getWasteType() != null) {
				// If there's already waste, just keep on producing the same type.
				this.addWaste(fuelToConvert);
			}
			else {
				// Create waste type from registry
				ReactorReaction reaction = ReactorConversions.get(fuelType);
				String wasteType = reaction == null ? null : reaction.getProduct();

				if(wasteType == null) {
					// TODO: 6/22/20 LOG
//					BRLog.warning("Could not locate waste for reaction of fuel type " + fuelType + "; using cyanite");
					wasteType = cyanite;
				}
				
				this.addWaste(wasteType, fuelToConvert);
			}
		}
		else {
			// TODO: 6/22/20 LOG
//			BRLog.warning("Attempting to use %d fuel and there's no fuel in the tank", fuelToConvert);
		}
	}

	public float getFuelReactivity() {
		String reactant = getFuelType();
		ReactorReaction reaction = ReactorConversions.get(reactant);
		if(reaction == null) {
			// TODO: 6/22/20 LOG
//			BRLog.warning("Could not locate reaction data for reactant type " + reactant + "; using default value for reactivity");
			return ReactorReaction.standardReactivity;
		}
		else {
			return reaction.getReactivity();
		}
	}
}

package net.roguelogix.biggerreactors.classic.helpers;

import net.minecraft.nbt.CompoundNBT;

public class ReactantStack {

	private String reactant;
	public int amount;
	
	public ReactantStack(String name, int amount) {
		assert(name != null);
		reactant = name;
		this.amount = amount;
	}
	
	public ReactantStack(String name) {
		assert(name != null);
		reactant = name;
		this.amount = 0;
	}

	public static ReactantStack createFromNBT(CompoundNBT tag) {
		String name = null;
		int amount = 0;
		
		if(tag.hasUniqueId("name")) {
			name = tag.getString("name");
		}
		else {
			return null;
		}
		
		if(tag.hasUniqueId("amount")) {
			amount = tag.getInt("amount");
		}
		
		return new ReactantStack(name, amount);
	}
	
	public void readFromNBT(CompoundNBT tag) {
		if(tag.hasUniqueId("name")) {
			reactant = tag.getString("name");
		}
		else {
			// TODO: Logging
//			BRLog.warning("ReactantStack::readFromNBT - Received a tag with no name!");
		}
		
		if(tag.hasUniqueId("amount")) {
			amount = tag.getInt("amount");
		}
	}
	
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag.putString("name", reactant);
		tag.putInt("amount", amount);
		
		return tag;
	}
	
	public boolean isReactantEqual(String name) {
		return reactant.equals(name);
	}
	
	public boolean isReactantEqual(ReactantStack other) {
		return reactant.equals(other.reactant);
	}
	
	public String getName() { return reactant; }

	/**
	 * Split a reactant stack into two stacks. If the amount desired is greater than
	 * the current amount, returns the current item. Otherwise, returns a new stack
	 * with the desired amount and removes the desired amount from the current stack's
	 * amount.
	 * @param desiredAmount The amount of reactant desired.
	 * @return The 
	 */
	public ReactantStack split(int desiredAmount) {
		if(desiredAmount <= 0) { 
			throw new IllegalArgumentException("Cannot split a reactant into a stack of size zero");
		}

		if(desiredAmount >= amount) { return this; }
		else {
			ReactantStack newStack = new ReactantStack(reactant, desiredAmount);
			amount -= desiredAmount;
			return newStack;
		}
	}
	
	public String toString() {
		if(reactant == null) {
			return "UNKNOWN";
		}
		return String.format("%s, %d mB", reactant, amount);
	}
}

package net.roguelogix.biggerreactors.classic.helpers;

import net.minecraft.util.Direction;

public class RotorInfo {
	// Location of bearing
	public int x, y, z;
	
	// Rotor direction
	public Direction rotorDirection = null;
	
	// Rotor length
	public int rotorLength = 0;
	
	// Array of arrays, containing rotor lengths
	public int[][] bladeLengths = null;
}

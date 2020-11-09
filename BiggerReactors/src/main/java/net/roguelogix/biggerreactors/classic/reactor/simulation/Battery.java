package net.roguelogix.biggerreactors.classic.reactor.simulation;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class Battery implements INBTSerializable<CompoundNBT> {
    private double partialStored = 0;
    private long storedPower = 0;
    private long maxStoredPower = 0;
    
    void setMaxStoredPower(long maxStoredPower) {
        this.maxStoredPower = maxStoredPower;
    }
    
    void addPower(double powerProduced) {
        if (Double.isInfinite(powerProduced) || Double.isNaN(powerProduced)) {
            return;
        }
        
        partialStored += powerProduced;
        
        if (partialStored < 1f) {
            return;
        }
        
        long toAdd = (long) partialStored;
        partialStored -= toAdd;
        
        storedPower += toAdd;
        
        if (storedPower > maxStoredPower) {
            storedPower = maxStoredPower;
        }
    }
    
    public void extractPower(long toExtract) {
        storedPower -= toExtract;
    }
    
    public long storedPower() {
        return storedPower;
    }
    
    public long size() {
        return maxStoredPower;
    }
    
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("storedPower", storedPower);
        nbt.putLong("maxStoredPower", maxStoredPower);
        return nbt;
    }
    
    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        storedPower = nbt.getLong("storedPower");
        maxStoredPower = nbt.getLong("maxStoredPower");
    }
}

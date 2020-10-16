package net.roguelogix.biggerreactors.classic.reactor.simulation;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public class FuelTank implements INBTSerializable<CompoundNBT> {
    private long capacity;
    
    private long fuel = 0;
    private long waste = 0;
    
    private double partialUsed = 0;
    
    void burn(double amount) {
        if (Double.isInfinite(amount) || Double.isNaN(amount)) {
            return;
        }
        
        partialUsed += amount;
        
        if (partialUsed < 1f) {
            return;
        }
        
        long toBurn = Math.min(fuel, (long) partialUsed);
        partialUsed -= toBurn;
        
        if (toBurn <= 0) {
            return;
        }
        
        fuel -= toBurn;
        waste += toBurn;
    }
    
    void setCapacity(long capacity) {
        this.capacity = capacity;
    }
    
    public long getCapacity() {
        return capacity;
    }
    
    public long insertFuel(long amount, boolean simulated) {
        if (getTotalAmount() >= capacity) {
            // if we are overfilled, then we need to *not* insert more
            return 0;
        }
        
        amount = Math.min(amount, capacity - getTotalAmount());
        
        if (!simulated) {
            fuel += amount;
        }
        
        return amount;
    }
    
    public long insertWaste(long amount, boolean simulated) {
        if (getTotalAmount() >= capacity) {
            // if we are overfilled, then we need to *not* insert more
            return 0;
        }
        
        amount = Math.min(amount, capacity - getTotalAmount());
        
        if (!simulated) {
            waste += amount;
        }
        
        return amount;
    }
    
    public long spaceAvailable() {
        return getCapacity() - getTotalAmount();
    }
    
    public long extractFuel(long toExtract, boolean simulated) {
        toExtract = Math.min(fuel, toExtract);
        if (!simulated) {
            fuel -= toExtract;
        }
        return toExtract;
    }
    
    public long extractWaste(long toExtract, boolean simulated) {
        toExtract = Math.min(waste, toExtract);
        if (!simulated) {
            waste -= toExtract;
        }
        return toExtract;
    }
    
    public long getTotalAmount() {
        return fuel + waste;
    }
    
    public long getFuelAmount() {
        return fuel;
    }
    
    public long getWasteAmount() {
        return waste;
    }
    
    @Override
    @Nonnull
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("capacity", capacity);
        nbt.putLong("fuel", fuel);
        nbt.putLong("waste", waste);
        nbt.putDouble("partialUsed", partialUsed);
        return nbt;
    }
    
    @Override
    public void deserializeNBT(@Nonnull CompoundNBT nbt) {
        if (nbt.contains("capacity")) {
            capacity = nbt.getLong("capacity");
        }
        if (nbt.contains("fuel")) {
            fuel = nbt.getLong("fuel");
        }
        if (nbt.contains("waste")) {
            waste = nbt.getLong("waste");
        }
        if (nbt.contains("partialUsed")) {
            partialUsed = nbt.getDouble("partialUsed");
        }
    }
}

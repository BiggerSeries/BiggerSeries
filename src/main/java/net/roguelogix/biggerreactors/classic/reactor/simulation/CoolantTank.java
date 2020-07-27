package net.roguelogix.biggerreactors.classic.reactor.simulation;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.roguelogix.biggerreactors.Config;

public class CoolantTank implements INBTSerializable<CompoundNBT> {
    
    private long perSideCapacity = 0;
    private long waterAmount = 0;
    private long steamAmount = 0;
    
    private long vaporizedLastTick = 0;
    
    public long getFluidVaporizedLastTick() {
        return vaporizedLastTick;
    }
    
    public long getSteamAmount() {
        return steamAmount;
    }
    
    public long getWaterAmount() {
        return waterAmount;
    }
    
    public long getPerSideCapacity() {
        return perSideCapacity;
    }
    
    public void setPerSideCapacity(long capacity) {
        perSideCapacity = Math.min(capacity, Config.Reactor.MaxActiveTankSize);
    }
    
    public double absorbHeat(double rfTransferred) {
        vaporizedLastTick = 0;
        if (waterAmount <= 0 || rfTransferred <= 0) {
            return rfTransferred;
        }
        
        long amountVaporized = Math.min(waterAmount, (long) (rfTransferred / Config.Reactor.CoolantVaporizationEnergy));
        
        amountVaporized = Math.min(amountVaporized, perSideCapacity - steamAmount);
        
        if (amountVaporized < 1) {
            return rfTransferred;
        }
        
        vaporizedLastTick = amountVaporized;
        waterAmount -= amountVaporized;
        steamAmount += amountVaporized;
        
        double energyUsed = amountVaporized * Config.Reactor.CoolantVaporizationEnergy;
        
        return Math.max(0, rfTransferred - energyUsed);
    }
    
    public double getCoolantTemperature(double reactorHeat) {
        if (waterAmount <= 0) {
            return reactorHeat;
        }
        return Math.min(reactorHeat, Config.Reactor.CoolantBoilingPoint);
    }
    
    public long insertWater(long amount, boolean simulated) {
        amount = Math.min(perSideCapacity - waterAmount, amount);
        if (!simulated) {
            waterAmount += amount;
        }
        return amount;
    }
    
    public long extractSteam(long amount, boolean simulated) {
        amount = Math.min(steamAmount, amount);
        if (!simulated) {
            steamAmount -= amount;
        }
        return amount;
    }
    
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("perSideCapacity", perSideCapacity);
        nbt.putLong("waterAmount", waterAmount);
        nbt.putLong("steamAmount", steamAmount);
        return nbt;
    }
    
    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("perSideCapacity")) {
            perSideCapacity = nbt.getLong("perSideCapacity");
        }
        if (nbt.contains("waterAmount")) {
            waterAmount = nbt.getLong("waterAmount");
        }
        if (nbt.contains("steamAmount")) {
            steamAmount = nbt.getLong("steamAmount");
        }
    }
}

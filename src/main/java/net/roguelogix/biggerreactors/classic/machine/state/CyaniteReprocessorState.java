package net.roguelogix.biggerreactors.classic.machine.state;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.IIntArray;
import net.minecraftforge.common.util.INBTSerializable;

public class CyaniteReprocessorState implements IIntArray, INBTSerializable {
    
    /**
     * The number of ticks the current item has processed for.
     */
    public int workTime;
    private final int WORK_TIME_INDEX = 0;
    /**
     * The total amount of time required to process the item.
     */
    public int workTimeTotal;
    private final int WORK_TIME_TOTAL_INDEX = 1;
    
    /**
     * The amount of energy stored in the machine.
     */
    public int energy;
    private final int ENERGY_INDEX = 2;
    /**
     * The max energy capacity of the machine.
     */
    public int energyCapacity;
    private final int ENERGY_CAPACITY_INDEX = 3;
    
    /**
     * The amount of water stored in the machine.
     */
    public int water;
    private final int WATER_INDEX = 4;
    /**
     * The max water capacity of the machine.
     */
    public int waterCapacity;
    private final int WATER_CAPACITY_INDEX = 5;
    
    private final int STATE_SIZE_INDEX = 6;
    
    @Override
    public INBT serializeNBT() {
        CompoundNBT data = new CompoundNBT();
        data.putInt("workTime", workTime);
        data.putInt("workTimeTotal", workTimeTotal);
        data.putInt("energy", energy);
        data.putInt("energyCapacity", energyCapacity);
        data.putInt("water", water);
        data.putInt("waterCapacity", water);
        return data;
    }
    
    @Override
    public void deserializeNBT(INBT nbt) {
        CompoundNBT data = (CompoundNBT) nbt;
        workTime = data.getInt("workTime");
        workTimeTotal = data.getInt("workTimeTotal");
        energy = data.getInt("energy");
        energyCapacity = data.getInt("energyCapacity");
        water = data.getInt("water");
        waterCapacity = data.getInt("waterCapacity");
    }
    
    @Override
    public int get(int index) {
        switch (index) {
            case WORK_TIME_INDEX:
                return workTime;
            case WORK_TIME_TOTAL_INDEX:
                return workTimeTotal;
            case ENERGY_INDEX:
                return energy;
            case ENERGY_CAPACITY_INDEX:
                return energyCapacity;
            case WATER_INDEX:
                return water;
            case WATER_CAPACITY_INDEX:
                return waterCapacity;
            default:
                throw new IndexOutOfBoundsException(String.format("unexpected value %s", index));
        }
    }
    
    @Override
    public void set(int index, int value) {
        switch (index) {
            case WORK_TIME_INDEX:
                workTime = value;
                return;
            case WORK_TIME_TOTAL_INDEX:
                workTimeTotal = value;
                return;
            case ENERGY_INDEX:
                energy = value;
                return;
            case ENERGY_CAPACITY_INDEX:
                energyCapacity = value;
                return;
            case WATER_INDEX:
                water = value;
                return;
            case WATER_CAPACITY_INDEX:
                waterCapacity = value;
                return;
            default:
                throw new IndexOutOfBoundsException(String.format("unexpected value %s", index));
        }
    }
    
    @Override
    public int size() {
        return STATE_SIZE_INDEX;
    }
}

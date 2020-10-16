package net.roguelogix.biggerreactors.classic.turbine.deps;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalTankBuilder;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.roguelogix.biggerreactors.classic.turbine.TurbineMultiblockController;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class TurbineGasHandler implements IGasHandler{
    
    private final Supplier<TurbineMultiblockController> controllerSupplier;
    public static final Gas steam;
    public static final GasStack steamStack;
    public static final IGasHandler EMPTY_TANK;
    
    static {
        steam = Gas.getFromRegistry(new ResourceLocation("mekanism:steam"));
        steamStack = new GasStack(steam, 0);
        EMPTY_TANK = (IGasHandler) ChemicalTankBuilder.GAS.createDummy(0);
    }
    
    public static LazyOptional<Object> create(@Nonnull Supplier<TurbineMultiblockController> controllerSupplier) {
        return LazyOptional.of(() -> new TurbineGasHandler(controllerSupplier));
    }
    
    public TurbineGasHandler(Supplier<TurbineMultiblockController> controllerSupplier) {
        this.controllerSupplier = controllerSupplier;
    }
    
    @Override
    public int getTanks() {
        return 1;
    }
    
    @Nonnull
    @Override
    public GasStack getChemicalInTank(int tank) {
        if(tank == 0){
            steamStack.setAmount(controllerSupplier.get().CCgetInputAmount());
            return steamStack;
        }
        return GasStack.EMPTY;
    }
    
    @Override
    public void setChemicalInTank(int tank, @Nonnull GasStack stack) {
    }
    
    @Override
    public long getTankCapacity(int tank) {
        return controllerSupplier.get().getSteamCapacity();
    }
    
    @Override
    public boolean isValid(int tank, @Nonnull GasStack stack) {
        if(tank == 0){
            return stack.getRaw() == steam;
        }
        return false;
    }
    
    @Nonnull
    @Override
    public GasStack insertChemical(int tank, @Nonnull GasStack stack, @Nonnull Action action) {
        if(tank != 0 || stack.getRaw() != steam){
            return stack;
        }
        boolean simulated = action.simulate();
        long toInsert = stack.getAmount();
        long inserted = controllerSupplier.get().addSteam(toInsert, simulated);
        if(inserted > 0){
            stack = stack.copy();
            stack.setAmount(stack.getAmount() - inserted);
        }
        return stack;
    }
    
    @Nonnull
    @Override
    public GasStack extractChemical(int tank, long amount, @Nonnull Action action) {
        return GasStack.EMPTY;
    }
}

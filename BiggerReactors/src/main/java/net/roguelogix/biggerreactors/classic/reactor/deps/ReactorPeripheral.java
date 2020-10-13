package net.roguelogix.biggerreactors.classic.reactor.deps;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.util.LazyOptional;
import net.roguelogix.biggerreactors.classic.reactor.ReactorMultiblockController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public class ReactorPeripheral implements IDynamicPeripheral {
    
    private final ReactorMultiblockController controller;
    
    public static LazyOptional<Object> create(@Nonnull ReactorMultiblockController controller) {
        return LazyOptional.of(() -> new ReactorPeripheral(controller));
    }
    
    public ReactorPeripheral(@Nonnull ReactorMultiblockController controller) {
        this.controller = controller;
    }
    
    private interface ReactorLuaFunc {
        MethodResult func(@Nonnull ReactorMultiblockController reactor, @Nonnull IArguments args) throws LuaException;
    }
    
    private static HashMap<String, ReactorLuaFunc> methodHandlers;
    private static String[] methods;
    
    static {
        methodHandlers = new HashMap<>();
        {
            methodHandlers.put("getConnected", (reactor, args) -> MethodResult.of(reactor.CCgetConnected()));
            methodHandlers.put("getActive", (reactor, args) -> MethodResult.of(reactor.CCgetActive()));
            methodHandlers.put("getNumberOfControlRods", (reactor, args) -> MethodResult.of(reactor.CCgetNumberOfControlRods()));
            methodHandlers.put("getEnergyStored", (reactor, args) -> MethodResult.of(reactor.CCgetEnergyStored()));
            methodHandlers.put("getFuelTemperature", (reactor, args) -> MethodResult.of(reactor.CCgetFuelTemperature()));
            methodHandlers.put("getCasingTemperature", (reactor, args) -> MethodResult.of(reactor.CCgetCasingTemperature()));
            methodHandlers.put("getFuelAmount", (reactor, args) -> MethodResult.of(reactor.CCgetFuelAmount()));
            methodHandlers.put("getWasteAmount", (reactor, args) -> MethodResult.of(reactor.CCgetWasteAmount()));
            methodHandlers.put("getReactantAmount", (reactor, args) -> MethodResult.of(reactor.CCgetReactantAmount()));
            methodHandlers.put("getFuelAmountMax", (reactor, args) -> MethodResult.of(reactor.CCgetFuelAmountMax()));
            methodHandlers.put("getControlRodName", (reactor, args) -> MethodResult.of(reactor.CCgetControlRodName(args.getInt(0))));
            methodHandlers.put("getControlRodLevel", (reactor, args) -> MethodResult.of(reactor.CCgetControlRodLevel(args.getInt(0))));
            methodHandlers.put("getEnergyProducedLastTick", (reactor, args) -> MethodResult.of(reactor.CCgetEnergyProducedLastTick()));
            methodHandlers.put("getHotFluidProducedLastTick", (reactor, args) -> MethodResult.of(reactor.CCgetHotFluidProducedLastTick()));
            methodHandlers.put("getCoolantType", (reactor, args) -> MethodResult.of(reactor.CCgetCoolantType()));
            methodHandlers.put("getCoolantAmount", (reactor, args) -> MethodResult.of(reactor.CCgetCoolantAmount()));
            methodHandlers.put("getHotFluidType", (reactor, args) -> MethodResult.of(reactor.CCgetHotFluidType()));
            methodHandlers.put("getHotFluidAmount", (reactor, args) -> MethodResult.of(reactor.CCgetHotFluidAmount()));
            methodHandlers.put("getFuelReactivity", (reactor, args) -> MethodResult.of(reactor.CCgetFuelReactivity()));
            methodHandlers.put("getFuelConsumedLastTick", (reactor, args) -> MethodResult.of(reactor.CCgetFuelConsumedLastTick()));
            methodHandlers.put("isActivelyCooled", (reactor, args) -> MethodResult.of(reactor.CCisActivelyCooled()));
            methodHandlers.put("setActive", (reactor, args) -> {
                reactor.CCsetActive(args.getBoolean(0));
                return MethodResult.of();
            });
            methodHandlers.put("setAllControlRodLevels", (reactor, args) -> {
                reactor.CCsetAllControlRodLevels(args.getDouble(0));
                return MethodResult.of();
            });
            methodHandlers.put("setControlRodLevel", (reactor, args) -> {
                reactor.CCsetControlRodLevel(args.getDouble(0), args.getInt(1));
                return MethodResult.of();
            });
            methodHandlers.put("doEjectWaste", (reactor, args) -> {
                reactor.CCdoEjectWaste();
                return MethodResult.of();
            });
        }
        methods = methodHandlers.keySet().toArray(new String[0]);
    }
    
    @Nonnull
    @Override
    public String[] getMethodNames() {
        return methods;
    }
    
    @Nonnull
    @Override
    public MethodResult callMethod(@Nonnull IComputerAccess computer, @Nonnull ILuaContext context, int method, @Nonnull IArguments arguments) throws LuaException {
        ReactorLuaFunc func = methodHandlers.get(methods[method]);
        try {
            return func.func(controller, arguments);
        }catch (RuntimeException e){
            throw new LuaException(e.getMessage());
        }
    }
    
    @Nonnull
    @Override
    public String getType() {
        return "bigger-reactor";
    }
    
    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (other instanceof ReactorPeripheral) {
            return ((ReactorPeripheral) other).controller == controller;
        }
        return false;
    }
}
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
import java.util.function.Supplier;

public class ReactorPeripheral implements IDynamicPeripheral {
    
    private final Supplier<ReactorMultiblockController> controllerSupplier;
    
    public static LazyOptional<Object> create(@Nonnull Supplier<ReactorMultiblockController> controllerSupplier) {
        return LazyOptional.of(() -> new ReactorPeripheral(controllerSupplier));
    }
    
    public ReactorPeripheral(@Nonnull Supplier<ReactorMultiblockController> controllerSupplier) {
        this.controllerSupplier = controllerSupplier;
    }
    
    private interface ReactorLuaFunc {
        MethodResult func(@Nullable ReactorMultiblockController reactor, @Nonnull IArguments args) throws LuaException;
    }
    
    private static HashMap<String, ReactorLuaFunc> methodHandlers;
    private static String[] methods;
    
    static {
        methodHandlers = new HashMap<>();
        {
            methodHandlers.put("getConnected", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetConnected());
                }
                return MethodResult.of(false);
            });
            methodHandlers.put("getActive", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetActive());
                }
                return MethodResult.of(false);
            });
            methodHandlers.put("getNumberOfControlRods", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetNumberOfControlRods());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getEnergyStored", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetEnergyStored());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getFuelTemperature", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetFuelTemperature());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getCasingTemperature", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetCasingTemperature());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getFuelAmount", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetFuelAmount());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getWasteAmount", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetWasteAmount());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getReactantAmount", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetReactantAmount());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getFuelAmountMax", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetFuelAmountMax());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getControlRodName", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetControlRodName(args.getInt(0)));
                }
                return MethodResult.of((String) null);
            });
            methodHandlers.put("getControlRodLevel", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetControlRodLevel(args.getInt(0)));
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getEnergyProducedLastTick", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetEnergyProducedLastTick());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getHotFluidProducedLastTick", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetHotFluidProducedLastTick());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getCoolantType", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetCoolantType());
                }
                return MethodResult.of((String) null);
            });
            methodHandlers.put("getCoolantAmount", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetCoolantAmount());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getHotFluidType", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetHotFluidType());
                }
                return MethodResult.of((String) null);
            });
            methodHandlers.put("getHotFluidAmount", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetHotFluidAmount());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getFuelReactivity", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetFuelReactivity());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getFuelConsumedLastTick", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCgetFuelConsumedLastTick());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("isActivelyCooled", (reactor, args) -> {
                if (reactor != null) {
                    return MethodResult.of(reactor.CCisActivelyCooled());
                }
                return MethodResult.of(false);
            });
            methodHandlers.put("setActive", (reactor, args) -> {
                if (reactor != null) {
                    reactor.CCsetActive(args.getBoolean(0));
                }
                return MethodResult.of();
            });
            methodHandlers.put("setAllControlRodLevels", (reactor, args) -> {
                if (reactor != null) {
                    reactor.CCsetAllControlRodLevels(args.getDouble(0));
                }
                return MethodResult.of();
            });
            methodHandlers.put("setControlRodLevel", (reactor, args) -> {
                if (reactor != null) {
                    reactor.CCsetControlRodLevel(args.getDouble(0), args.getInt(1));
                }
                return MethodResult.of();
            });
            methodHandlers.put("doEjectWaste", (reactor, args) -> {
                if (reactor != null) {
                    reactor.CCdoEjectWaste();
                }
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
            return func.func(controllerSupplier.get(), arguments);
        } catch (RuntimeException e) {
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
            if (controllerSupplier.get() == null) {
                return false;
            }
            return ((ReactorPeripheral) other).controllerSupplier.get() == controllerSupplier.get();
        }
        return false;
    }
}
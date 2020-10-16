package net.roguelogix.biggerreactors.classic.turbine.deps;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.util.LazyOptional;
import net.roguelogix.biggerreactors.classic.turbine.TurbineMultiblockController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Supplier;

public class TurbinePeripheral implements IDynamicPeripheral {
    
    private final Supplier<TurbineMultiblockController> controllerSupplier;
    
    public static LazyOptional<Object> create(@Nonnull Supplier<TurbineMultiblockController> controllerSupplier) {
        return LazyOptional.of(() -> new TurbinePeripheral(controllerSupplier));
    }
    
    public TurbinePeripheral(@Nonnull Supplier<TurbineMultiblockController> controllerSupplier) {
        this.controllerSupplier = controllerSupplier;
    }
    
    private interface TurbineLuaFunc {
        MethodResult func(@Nullable TurbineMultiblockController Turbine, @Nonnull IArguments args) throws LuaException;
    }
    
    private static final HashMap<String, TurbinePeripheral.TurbineLuaFunc> methodHandlers;
    private static final String[] methods;
    
    static {
        methodHandlers = new HashMap<>();
        {
            methodHandlers.put("getConnected", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetConnected());
                }
                return MethodResult.of(false);
            });
            methodHandlers.put("getActive", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetActive());
                }
                return MethodResult.of(false);
            });
            methodHandlers.put("getEnergyStored", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetEnergyStored());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getRotorSpeed", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetRotorSpeed());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getInputAmount", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetInputAmount());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getInputType", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetInputType());
                }
                return MethodResult.of((String) null);
            });
            methodHandlers.put("getOutputAmount", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetOutputAmount());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getOutputType", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetOutputType());
                }
                return MethodResult.of((String) null);
            });
            methodHandlers.put("getFluidAmountMax", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetFluidAmountMax());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getFluidFlowRate", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetFluidFlowRate());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getFluidFlowRateMax", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetFluidFlowRateMax());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getFluidFlowRateMaxMax", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetFluidFlowRateMaxMax());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getEnergyProducedLastTick", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetEnergyProducedLastTick());
                }
                return MethodResult.of(0);
            });
            methodHandlers.put("getInductorEngaged", (turbine, args) -> {
                if (turbine != null) {
                    return MethodResult.of(turbine.CCgetInductorEngaged());
                }
                return MethodResult.of(false);
            });
            methodHandlers.put("setActive", (turbine, args) -> {
                if (turbine != null) {
                    turbine.CCsetActive(args.getBoolean(0));
                }
                return MethodResult.of();
            });
            methodHandlers.put("setFluidFlowRateMax", (turbine, args) -> {
                if (turbine != null) {
                    turbine.CCsetFluidFlowRateMax(args.getLong(0));
                }
                return MethodResult.of();
            });
            methodHandlers.put("setVentNone", (turbine, args) -> {
                if (turbine != null) {
                    turbine.CCsetVentNone();
                }
                return MethodResult.of();
            });
            methodHandlers.put("setVentOverflow", (turbine, args) -> {
                if (turbine != null) {
                    turbine.CCsetVentOverflow();
                }
                return MethodResult.of();
            });
            methodHandlers.put("setVentAll", (turbine, args) -> {
                if (turbine != null) {
                    turbine.CCsetVentAll();
                }
                return MethodResult.of();
            });
            methodHandlers.put("setInductorEngaged", (turbine, args) -> {
                if (turbine != null) {
                    turbine.CCsetInductorEngaged(args.getBoolean(0));
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
        TurbineLuaFunc func = methodHandlers.get(methods[method]);
        try {
            return func.func(controllerSupplier.get(), arguments);
        } catch (RuntimeException e) {
            throw new LuaException(e.getMessage());
        }
    }
    
    @Nonnull
    @Override
    public String getType() {
        return "bigger-turbine";
    }
    
    @Override
    public boolean equals(@Nullable IPeripheral other) {
        if (other instanceof TurbinePeripheral) {
            if (controllerSupplier.get() == null) {
                return false;
            }
            return ((TurbinePeripheral) other).controllerSupplier.get() == controllerSupplier.get();
        }
        return false;
    }
}

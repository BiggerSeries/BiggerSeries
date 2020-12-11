package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.roguelogix.biggerreactors.classic.reactor.ReactorMultiblockController;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorRedstonePort;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorRedstonePortContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorActivity;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorRedstonePortSelection;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorRedstonePortState;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorRedstonePortTriggers;
import net.roguelogix.phosphophyllite.gui.old.client.api.IHasUpdatableState;
import net.roguelogix.phosphophyllite.multiblock.generic.ITickableMultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockBlock;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RegisterTileEntity(name = "reactor_redstone_port")
public class ReactorRedstonePortTile extends ReactorBaseTile implements INamedContainerProvider, IHasUpdatableState<ReactorRedstonePortState>, ITickableMultiblockTile {

    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;

    public final ReactorRedstonePortState reactorRedstonePortState = new ReactorRedstonePortState(this);
    public final ReactorRedstonePortState uncommittedReactorRedstonePortState = new ReactorRedstonePortState(this);

    public ReactorRedstonePortTile() {
        super(TYPE);
    }

    private boolean isEmitting;
    double mainVal = 0;
    double secondaryVal = 0;
    Direction powerOutputDirection = null;

    public boolean isEmitting(Direction side) {
        if (side.getOpposite() != powerOutputDirection) {
            return false;
        }
        return isEmitting;
    }

    private boolean isPowered = false;
    private boolean wasPowered = false;

    public void updatePowered() {
        if (powerOutputDirection == null) {
            return;
        }
        assert world != null;
        isPowered = world.getRedstonePower(pos.offset(powerOutputDirection), powerOutputDirection) > 0;
    }

    private boolean isLit = false;

    @Override
    public void tick() {
        boolean shouldBeEmitting = false;
        boolean shouldLight = false;
        ReactorMultiblockController reactor = reactor();
        assert reactor != null;
        switch (reactorRedstonePortState.selectedTab) {
            case INPUT_ACTIVITY:
                shouldLight = isPowered;
                if (reactorRedstonePortState.triggerPS.toBool()) {
                    // signal
                    if (wasPowered != isPowered) {
                        reactor.setActive(isPowered ? ReactorActivity.ACTIVE : ReactorActivity.INACTIVE);
                    }
                } else if (!wasPowered && isPowered) {
                    // not signal, so, pulse
                    reactor.toggleActive();
                }
                break;
            case INPUT_CONTROL_ROD_INSERTION: {
                shouldLight = isPowered;
                if (reactorRedstonePortState.triggerPS.toBool()) {
                    if (wasPowered == isPowered) {
                        break;
                    }
                    if (isPowered) {
                        reactor.CCsetAllControlRodLevels(mainVal);
                    } else {
                        reactor.CCsetAllControlRodLevels(secondaryVal);
                    }
                } else {
                    if (!wasPowered && isPowered) {
                        switch (reactorRedstonePortState.triggerMode) {
                            case 0: {
                                reactor.CCsetAllControlRodLevels(reactor.CCgetControlRodLevel(0) + mainVal);
                                break;
                            }
                            case 1: {
                                reactor.CCsetAllControlRodLevels(reactor.CCgetControlRodLevel(0) - mainVal);
                                break;
                            }
                            case 2: {
                                reactor.CCsetAllControlRodLevels(mainVal);
                                break;
                            }
                            default:
                                break;
                        }
                    }
                }
            }
            break;
            case INPUT_EJECT_WASTE: {
                shouldLight = isPowered;
                if (!wasPowered && isPowered) {
                    reactor.CCdoEjectWaste();
                }
                break;
            }
            case OUTPUT_FUEL_TEMP: {
                double fuelTemp = reactor.CCgetFuelTemperature();
                if ((fuelTemp < mainVal) == reactorRedstonePortState.triggerAB.toBool()) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_CASING_TEMP: {
                double casingTemperature = reactor.CCgetCasingTemperature();
                if ((casingTemperature < mainVal) == reactorRedstonePortState.triggerAB.toBool()) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_FUEL_ENRICHMENT: {
                double fuelPercent = reactor.CCgetFuelAmount();
                fuelPercent /= reactor.CCgetReactantAmount();
                fuelPercent *= 100;
                if ((fuelPercent < mainVal) == reactorRedstonePortState.triggerAB.toBool()) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_FUEL_AMOUNT: {
                double fuelAmount = reactor.CCgetFuelAmount();
                if ((fuelAmount < mainVal) == reactorRedstonePortState.triggerAB.toBool()) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_WASTE_AMOUNT: {
                double wasteAmount = reactor.CCgetWasteAmount();
                if ((wasteAmount < mainVal) == reactorRedstonePortState.triggerAB.toBool()) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_ENERGY_AMOUNT: {
                double energyAmount = reactor.CCgetEnergyStoredUnscaled();
                energyAmount /= (double) reactor.CCgetMaxEnergyStored();
                energyAmount *= 100;
                if ((energyAmount < mainVal) == reactorRedstonePortState.triggerAB.toBool()) {
                    shouldBeEmitting = true;
                }
            }
            break;
        }
        shouldLight |= shouldBeEmitting;
        if (shouldBeEmitting != isEmitting || wasPowered != isPowered) {
            isEmitting = shouldBeEmitting;
            wasPowered = isPowered;
            assert world != null;
            world.notifyNeighborsOfStateChange(this.getPos(), this.getBlockState().getBlock());
        }
        if (isLit != shouldLight) {
            isLit = shouldLight;
            world.setBlockState(pos, getBlockState().with(ReactorRedstonePort.IS_LIT_BOOLEAN_PROPERTY, isLit));
        }
        this.markDirty();
    }

    public void updateOutputDirection() {
        if (controller.assemblyState() == MultiblockController.AssemblyState.DISASSEMBLED) {
            powerOutputDirection = null;
        } else if (pos.getX() == controller.minCoord().x()) {
            powerOutputDirection = Direction.WEST;
        } else if (pos.getX() == controller.maxCoord().x()) {
            powerOutputDirection = Direction.EAST;
        } else if (pos.getY() == controller.minCoord().y()) {
            powerOutputDirection = Direction.DOWN;
        } else if (pos.getY() == controller.maxCoord().y()) {
            powerOutputDirection = Direction.UP;
        } else if (pos.getZ() == controller.minCoord().z()) {
            powerOutputDirection = Direction.NORTH;
        } else if (pos.getZ() == controller.maxCoord().z()) {
            powerOutputDirection = Direction.SOUTH;
        }
        updatePowered();
    }

    @Override
    @Nonnull
    public ActionResultType onBlockActivated(@Nonnull PlayerEntity player, @Nonnull Hand handIn) {
        assert world != null;
        if (world.getBlockState(pos).get(MultiblockBlock.ASSEMBLED)) {
            if (!world.isRemote) {
                NetworkHooks.openGui((ServerPlayerEntity) player, this, this.getPos());
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ReactorRedstonePort.INSTANCE.getTranslationKey());
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
        return new ReactorRedstonePortContainer(windowId, this.pos, player);
    }

    // I could of just used States to contain these values, but that was causing an issue. This didn't solve it, but CTRL-Z doesn't have the old version in it anymore.
    // Active variables are what are currently being used by the port.
    //private int activeSettingId = 0;
    //private boolean activeTriggerPulseOrSignal = false;
    //private boolean activeTriggerAboveOrBelow = false;
    //private int activeMode = 0;
    //private String activeMainBuffer = "";
    //private String activeSecondBuffer = "";

    // Uncommitted variables are what are pending to be stored, but not currently in use.
    //private int uncommittedSettingId = 0;
    //private boolean uncommittedTriggerPulseOrSignal = false;
    //private boolean uncommittedTriggerAboveOrBelow = false;
    //private int uncommittedMode = 0;
    //private String uncommittedMainBuffer = "";
    //private String uncommittedSecondBuffer = "";

    @Nullable
    @Override
    public ReactorRedstonePortState getState() {
        this.updateState();
        return this.reactorRedstonePortState;
    }

    public ReactorRedstonePortState getUncommittedState() {
        this.updateState();
        return this.uncommittedReactorRedstonePortState;
    }

    @Override
    public void updateState() {
        // Update committed/active values.
        //reactorRedstonePortState.selectedTab = activeRedstonePortSettings.selectedTab;
        //reactorRedstonePortState.triggerPS = activeRedstonePortSettings.triggerPS;
        //reactorRedstonePortState.triggerAB = activeRedstonePortSettings.triggerAB;
        //reactorRedstonePortState.triggerMode = activeRedstonePortSettings.triggerMode;
        //reactorRedstonePortState.textBufferA = activeRedstonePortSettings.textBufferA;
        //reactorRedstonePortState.textBufferB = activeRedstonePortSettings.textBufferB;
        // Update uncommitted values.
        //uncommittedPortState.selectedTab = uncommittedSettingId;
        //uncommittedPortState.triggerPS = uncommittedTriggerPulseOrSignal;
        //uncommittedPortState.triggerAB = uncommittedTriggerAboveOrBelow;
        //uncommittedPortState.triggerMode = uncommittedMode;
        //uncommittedPortState.textBufferA = uncommittedMainBuffer;
        //uncommittedPortState.textBufferB = uncommittedSecondBuffer;
    }

    public void commitChanges() {
        reactorRedstonePortState.selectedTab = uncommittedReactorRedstonePortState.selectedTab;
        reactorRedstonePortState.triggerPS = uncommittedReactorRedstonePortState.triggerPS;
        reactorRedstonePortState.triggerAB = uncommittedReactorRedstonePortState.triggerAB;
        reactorRedstonePortState.triggerMode = uncommittedReactorRedstonePortState.triggerMode;
        reactorRedstonePortState.textBufferA = uncommittedReactorRedstonePortState.textBufferA;
        reactorRedstonePortState.textBufferB = uncommittedReactorRedstonePortState.textBufferB;

        mainVal = (!reactorRedstonePortState.textBufferA.isEmpty()) ? Double.parseDouble(reactorRedstonePortState.textBufferA) : 0D;
        secondaryVal = (!reactorRedstonePortState.textBufferB.isEmpty()) ? Double.parseDouble(reactorRedstonePortState.textBufferB) : 0D;

        //activeSettingId = uncommittedSettingId;
        //activeTriggerPulseOrSignal = uncommittedTriggerPulseOrSignal;
        //activeTriggerAboveOrBelow = uncommittedTriggerAboveOrBelow;
        //activeMode = uncommittedMode;
        //activeMainBuffer = uncommittedMainBuffer;
        //activeSecondBuffer = uncommittedSecondBuffer;
        //if (!activeMainBuffer.isEmpty()) {
        //    mainVal = Double.parseDouble(activeMainBuffer);
        //} else {
        //    mainVal = 0;
        //}
        //if (!activeSecondBuffer.isEmpty()) {
        //    secondaryVal = Double.parseDouble(activeSecondBuffer);
        //} else {
        //    secondaryVal = 0;
        //}
    }

    public void revertChanges() {
        uncommittedReactorRedstonePortState.selectedTab = reactorRedstonePortState.selectedTab;
        uncommittedReactorRedstonePortState.triggerPS = reactorRedstonePortState.triggerPS;
        uncommittedReactorRedstonePortState.triggerAB = reactorRedstonePortState.triggerAB;
        uncommittedReactorRedstonePortState.triggerMode = reactorRedstonePortState.triggerMode;
        uncommittedReactorRedstonePortState.textBufferA = reactorRedstonePortState.textBufferA;
        uncommittedReactorRedstonePortState.textBufferB = reactorRedstonePortState.textBufferB;

        //uncommittedSettingId = activeSettingId;
        //uncommittedTriggerPulseOrSignal = activeTriggerPulseOrSignal;
        //uncommittedTriggerAboveOrBelow = activeTriggerAboveOrBelow;
        //uncommittedMode = activeMode;
        //uncommittedMainBuffer = activeMainBuffer;
        //uncommittedSecondBuffer = activeSecondBuffer;
    }

    @Override
    public void runRequest(String requestName, Object requestData) {
        ReactorMultiblockController reactor = reactor();
        if (reactor == null) {
            return;
        }

        // We save changes to an uncommitted changes temp state. When commit is pressed, then we send the run requests forward.
        switch (requestName) {
            case "setSelectedTab":
                System.out.println("SET TAB");
                uncommittedReactorRedstonePortState.selectedTab = ReactorRedstonePortSelection.fromInt((Integer) requestData);
                break;
            case "setTriggerPS":
                uncommittedReactorRedstonePortState.triggerPS = ReactorRedstonePortTriggers.fromBool((Boolean) requestData);
                break;
            case "setTriggerAB":
                uncommittedReactorRedstonePortState.triggerAB = ReactorRedstonePortTriggers.fromBool((Boolean) requestData);
                break;
            case "setTriggerMode":
                //uncommittedMode = (Integer) requestData;
                int triggerMode = (Integer) requestData;
                if(triggerMode >= 0 && triggerMode <= 2) {
                    uncommittedReactorRedstonePortState.triggerMode = triggerMode;
                } else {
                    uncommittedReactorRedstonePortState.triggerMode = 2;
                }
                //uncommittedReactorRedstonePortState.triggerMode++;
                //if (uncommittedReactorRedstonePortState.triggerMode > 2) {
                //    uncommittedReactorRedstonePortState.triggerMode = 0;
                //}
                break;
            case "setTextBufferA":
                uncommittedReactorRedstonePortState.textBufferA = (String) requestData;
                break;
            case "setTextBufferB":
                uncommittedReactorRedstonePortState.textBufferB = (String) requestData;
                break;
            case "revertChanges":
                revertChanges();
                break;
            case "commitChanges":
                commitChanges();
                break;
            default:
                super.runRequest(requestName, requestData);
                break;
        }
    }

    @Override
    @Nonnull
    protected CompoundNBT writeNBT() {
        CompoundNBT compound = super.writeNBT();
        compound.putInt("settingId", reactorRedstonePortState.selectedTab.toInt());
        compound.putBoolean("triggerPulseOrSignal", reactorRedstonePortState.triggerPS.toBool());
        compound.putBoolean("triggerAboveOrBelow", reactorRedstonePortState.triggerAB.toBool());
        compound.putInt("mode", reactorRedstonePortState.triggerMode);
        compound.putString("mainBuffer", reactorRedstonePortState.textBufferA);
        compound.putString("secondBuffer", reactorRedstonePortState.textBufferB);
        compound.putBoolean("isPowered", isPowered);
        compound.putBoolean("isEmitting", isEmitting);
        return compound;
    }

    @Override
    protected void readNBT(@Nonnull CompoundNBT compound) {
        super.readNBT(compound);
        if (compound.contains("settingId")) {
            reactorRedstonePortState.selectedTab = ReactorRedstonePortSelection.fromInt(compound.getInt("settingId"));
        }
        if (compound.contains("triggerPulseOrSignal")) {
            reactorRedstonePortState.triggerPS = ReactorRedstonePortTriggers.fromBool(compound.getBoolean("triggerPulseOrSignal"));
        }
        if (compound.contains("triggerAboveOrBelow")) {
            reactorRedstonePortState.triggerAB = ReactorRedstonePortTriggers.fromBool(compound.getBoolean("triggerAboveOrBelow"));
        }
        if (compound.contains("mode")) {
            reactorRedstonePortState.triggerMode = compound.getInt("mode");
        }
        if (compound.contains("mainBuffer")) {
            reactorRedstonePortState.textBufferA = compound.getString("mainBuffer");
        }
        if (compound.contains("secondBuffer")) {
            reactorRedstonePortState.textBufferB = compound.getString("secondBuffer");
        }
        if (compound.contains("isPowered")) {
            wasPowered = isPowered = compound.getBoolean("isPowered");
        }
        // Call reverted changes to align uncommitted settings to the active ones.
        revertChanges();
        commitChanges();
    }
}

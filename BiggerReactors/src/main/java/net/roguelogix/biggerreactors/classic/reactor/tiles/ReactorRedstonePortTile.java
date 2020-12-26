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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorRedstonePort;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorRedstonePortContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorActivity;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorRedstonePortSelection;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorRedstonePortState;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorRedstonePortTriggers;
import net.roguelogix.phosphophyllite.gui.client.api.IHasUpdatableState;
import net.roguelogix.phosphophyllite.multiblock.generic.ITickableMultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockBlock;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RegisterTileEntity(name = "reactor_redstone_port")
public class ReactorRedstonePortTile extends ReactorBaseTile implements INamedContainerProvider, ITickableMultiblockTile, IHasUpdatableState<ReactorRedstonePortState> {

    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;

    public final ReactorRedstonePortState reactorRedstonePortState = new ReactorRedstonePortState(this);

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
        switch (reactorRedstonePortState.selectedTab) {
            case INPUT_ACTIVITY:
                shouldLight = isPowered;
                if (reactorRedstonePortState.triggerPS.toBool()) {
                    // signal
                    if (wasPowered != isPowered) {
                        controller.setActive(isPowered ? ReactorActivity.ACTIVE : ReactorActivity.INACTIVE);
                    }
                } else if (!wasPowered && isPowered) {
                    // not signal, so, pulse
                    controller.toggleActive();
                }
                break;
            case INPUT_CONTROL_ROD_INSERTION: {
                shouldLight = isPowered;
                if (reactorRedstonePortState.triggerPS.toBool()) {
                    if (wasPowered == isPowered) {
                        break;
                    }
                    if (isPowered) {
                        controller.CCsetAllControlRodLevels(mainVal);
                    } else {
                        controller.CCsetAllControlRodLevels(secondaryVal);
                    }
                } else {
                    if (!wasPowered && isPowered) {
                        switch (reactorRedstonePortState.triggerMode) {
                            case 0: {
                                controller.CCsetAllControlRodLevels(controller.CCgetControlRodLevel(0) + mainVal);
                                break;
                            }
                            case 1: {
                                controller.CCsetAllControlRodLevels(controller.CCgetControlRodLevel(0) - mainVal);
                                break;
                            }
                            case 2: {
                                controller.CCsetAllControlRodLevels(mainVal);
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
                    controller.CCdoEjectWaste();
                }
                break;
            }
            case OUTPUT_FUEL_TEMP: {
                double fuelTemp = controller.CCgetFuelTemperature();
                if ((fuelTemp < mainVal) == reactorRedstonePortState.triggerAB.toBool()) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_CASING_TEMP: {
                double casingTemperature = controller.CCgetCasingTemperature();
                if ((casingTemperature < mainVal) == reactorRedstonePortState.triggerAB.toBool()) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_FUEL_ENRICHMENT: {
                double fuelPercent = controller.CCgetFuelAmount();
                fuelPercent /= controller.CCgetReactantAmount();
                fuelPercent *= 100;
                if ((fuelPercent < mainVal) == reactorRedstonePortState.triggerAB.toBool()) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_FUEL_AMOUNT: {
                double fuelAmount = controller.CCgetFuelAmount();
                if ((fuelAmount < mainVal) == reactorRedstonePortState.triggerAB.toBool()) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_WASTE_AMOUNT: {
                double wasteAmount = controller.CCgetWasteAmount();
                if ((wasteAmount < mainVal) == reactorRedstonePortState.triggerAB.toBool()) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_ENERGY_AMOUNT: {
                double energyAmount = controller.CCgetEnergyStoredUnscaled();
                energyAmount /= (double) controller.CCgetMaxEnergyStored();
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
            BlockPos updatePos = pos.offset(powerOutputDirection);
            world.notifyNeighborsOfStateChange(this.getPos(), this.getBlockState().getBlock());
            world.notifyNeighborsOfStateChange(updatePos, world.getBlockState(updatePos).getBlock());
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

    // Current changes/non-active. See reactorRedstonePortState to see what's actually being used for operations.
    private final ReactorRedstonePortState currentChanges = new ReactorRedstonePortState(this);

    public ReactorRedstonePortState getCurrentChanges() {
        return this.currentChanges;
    }

    @Nullable
    @Override
    public ReactorRedstonePortState getState() {
        this.updateState();
        return this.reactorRedstonePortState;
    }

    @Override
    public void updateState() {
        // Update committed/active values.
    }

    public void applyChanges() {
        this.reactorRedstonePortState.selectedTab = this.currentChanges.selectedTab;
        this.reactorRedstonePortState.triggerPS = this.currentChanges.triggerPS;
        this.reactorRedstonePortState.triggerAB = this.currentChanges.triggerAB;
        this.reactorRedstonePortState.triggerMode = this.currentChanges.triggerMode;
        this.reactorRedstonePortState.textBufferA = this.currentChanges.textBufferA;
        this.reactorRedstonePortState.textBufferB = this.currentChanges.textBufferB;

        this.mainVal = (!this.reactorRedstonePortState.textBufferA.isEmpty()) ? Double.parseDouble(this.reactorRedstonePortState.textBufferA) : 0D;
        this.secondaryVal = (!this.reactorRedstonePortState.textBufferB.isEmpty()) ? Double.parseDouble(this.reactorRedstonePortState.textBufferB) : 0D;

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
        this.currentChanges.selectedTab = this.reactorRedstonePortState.selectedTab;
        this.currentChanges.triggerPS = this.reactorRedstonePortState.triggerPS;
        this.currentChanges.triggerAB = this.reactorRedstonePortState.triggerAB;
        this.currentChanges.triggerMode = this.reactorRedstonePortState.triggerMode;
        this.currentChanges.textBufferA = this.reactorRedstonePortState.textBufferA;
        this.currentChanges.textBufferB = this.reactorRedstonePortState.textBufferB;
    }

    @Override
    public void runRequest(String requestName, Object requestData) {
        // We save changes to an uncommitted changes temp state. When apply is pressed, then we send the run requests forward.
        switch (requestName) {
            case "setSelectedTab":
                this.currentChanges.selectedTab = ReactorRedstonePortSelection.fromInt((Integer) requestData);
                break;
            case "setTriggerPS":
                this.currentChanges.triggerPS = ReactorRedstonePortTriggers.fromBool((Boolean) requestData);
                break;
            case "setTriggerAB":
                this.currentChanges.triggerAB = ReactorRedstonePortTriggers.fromBool((Boolean) requestData);
                break;
            case "setTriggerMode":
                int triggerMode = (Integer) requestData;
                if (triggerMode >= 0 && triggerMode <= 2) {
                    this.currentChanges.triggerMode = triggerMode;
                } else {
                    this.currentChanges.triggerMode = 2;
                }
                break;
            case "setTextBufferA":
                this.currentChanges.textBufferA = (String) requestData;
                break;
            case "setTextBufferB":
                this.currentChanges.textBufferB = (String) requestData;
                break;
            case "revertChanges":
                System.out.println("No longer implemented!");
                //revertChanges();
                break;
            case "applyChanges":
                this.applyChanges();
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
        applyChanges();
    }
}

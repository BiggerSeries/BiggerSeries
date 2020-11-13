package net.roguelogix.biggerreactors.classic.reactor.tiles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.roguelogix.biggerreactors.classic.reactor.ReactorMultiblockController;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorRedstonePort;
import net.roguelogix.biggerreactors.classic.reactor.containers.RedstonePortContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorActivity;
import net.roguelogix.biggerreactors.classic.reactor.state.RedstonePortSelector;
import net.roguelogix.biggerreactors.classic.reactor.state.RedstonePortState;
import net.roguelogix.phosphophyllite.gui.client.api.IHasUpdatableState;
import net.roguelogix.phosphophyllite.multiblock.generic.ITickableMultiblockTile;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockPositions;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RegisterTileEntity(name = "reactor_redstone_port")
public class ReactorRedstonePortTile extends ReactorBaseTile implements INamedContainerProvider, IHasUpdatableState<RedstonePortState>, ITickableMultiblockTile {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public final RedstonePortState redstonePortState = new RedstonePortState(this);
    public final RedstonePortState uncommittedPortState = new RedstonePortState(this);
    
    public ReactorRedstonePortTile() {
        super(TYPE);
    }
    
    private boolean isEmitting;
    
    public boolean isEmitting() {
        return isEmitting;
    }
    
    private boolean isPowered = false;
    private boolean wasPowered = false;
    
    public void setPowered(boolean powered) {
        isPowered = powered;
    }
    
    private boolean isLit = false;
    
    @Override
    public void tick() {
        boolean shouldBeEmitting = false;
        boolean shouldLight = false;
        ReactorMultiblockController reactor = reactor();
        assert reactor != null;
        switch (RedstonePortSelector.valueOf(activeSettingId)) {
            case INPUT_ACTIVITY:
                shouldLight = isPowered;
                if (activeTriggerPulseOrSignal) {
                    // signal
                    if(wasPowered != isPowered){
                        reactor.setActive(isPowered ? ReactorActivity.ACTIVE : ReactorActivity.INACTIVE);
                    }
                } else if (!wasPowered && isPowered) {
                    // not signal, so, pulse
                    reactor.toggleActive();
                }
                break;
            case INPUT_CONTROL_ROD_INSERTION: {
                shouldLight = isPowered;
                if (activeTriggerPulseOrSignal) {
                    if(wasPowered == isPowered){
                        break;
                    }
                    if (isPowered) {
                        reactor.CCsetAllControlRodLevels(mainVal);
                    } else {
                        reactor.CCsetAllControlRodLevels(secondaryVal);
                    }
                } else {
                    if (!wasPowered && isPowered) {
                        switch (activeMode) {
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
                if ((fuelTemp < mainVal) == activeTriggerAboveOrBelow) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_CASING_TEMP: {
                double casingTemperature = reactor.CCgetCasingTemperature();
                if ((casingTemperature < mainVal) == activeTriggerAboveOrBelow) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_FUEL_ENRICHMENT: {
                double fuelPercent = reactor.CCgetFuelAmount();
                fuelPercent /= reactor.CCgetReactantAmount();
                fuelPercent *= 100;
                if ((fuelPercent < mainVal) == activeTriggerAboveOrBelow) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_FUEL_AMOUNT: {
                double fuelAmount = reactor.CCgetFuelAmount();
                if ((fuelAmount < mainVal) == activeTriggerAboveOrBelow) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_WASTE_AMOUNT: {
                double wasteAmount = reactor.CCgetWasteAmount();
                if ((wasteAmount < mainVal) == activeTriggerAboveOrBelow) {
                    shouldBeEmitting = true;
                }
            }
            break;
            case OUTPUT_ENERGY_AMOUNT: {
                double energyAmount = reactor.CCgetEnergyStoredUnscaled();
                energyAmount /= (double) reactor.CCgetMaxEnergyStored();
                energyAmount *= 100;
                if ((energyAmount < mainVal) == activeTriggerAboveOrBelow) {
                    shouldBeEmitting = true;
                }
            }
            break;
        }
        shouldLight |= shouldBeEmitting;
        wasPowered = isPowered;
        if (shouldBeEmitting != isEmitting) {
            isEmitting = shouldBeEmitting;
            assert world != null;
            world.notifyNeighborsOfStateChange(this.getPos(), this.getBlockState().getBlock());
        }
        if(isLit != shouldLight){
            isLit = shouldLight;
            world.setBlockState(pos, getBlockState().with(ReactorRedstonePort.IS_LIT_BOOLEAN_PROPERTY, isLit));
        }
        this.markDirty();
    }
    
    @Override
    @Nonnull
    public ActionResultType onBlockActivated(@Nonnull PlayerEntity player, @Nonnull Hand handIn) {
        assert world != null;
        if (world.getBlockState(pos).get(RectangularMultiblockPositions.POSITIONS_ENUM_PROPERTY) != RectangularMultiblockPositions.DISASSEMBLED) {
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
        return new RedstonePortContainer(windowId, this.pos, player);
    }
    
    // I could of just used States to contain these values, but that was causing an issue. This didn't solve it, but CTRL-Z doesn't have the old version in it anymore.
    // Active variables are what are currently being used by the port.
    private int activeSettingId = 0;
    private boolean activeTriggerPulseOrSignal = false;
    private boolean activeTriggerAboveOrBelow = false;
    private int activeMode = 0;
    private String activeMainBuffer = "";
    private String activeSecondBuffer = "";
    double mainVal = 0;
    double secondaryVal = 0;
    
    // Uncommitted variables are what are pending to be stored, but not currently in use.
    private int uncommittedSettingId = 0;
    private boolean uncommittedTriggerPulseOrSignal = false;
    private boolean uncommittedTriggerAboveOrBelow = false;
    private int uncommittedMode = 0;
    private String uncommittedMainBuffer = "";
    private String uncommittedSecondBuffer = "";
    
    @Nullable
    @Override
    public RedstonePortState getState() {
        this.updateState();
        return this.redstonePortState;
    }
    
    @Override
    public void updateState() {
        // Update committed/active values.
        redstonePortState.settingId = activeSettingId;
        redstonePortState.triggerPulseOrSignal = activeTriggerPulseOrSignal;
        redstonePortState.triggerAboveOrBelow = activeTriggerAboveOrBelow;
        redstonePortState.mode = activeMode;
        redstonePortState.mainBuffer = activeMainBuffer;
        redstonePortState.secondBuffer = activeSecondBuffer;
        // Update uncommitted values.
        uncommittedPortState.settingId = uncommittedSettingId;
        uncommittedPortState.triggerPulseOrSignal = uncommittedTriggerPulseOrSignal;
        uncommittedPortState.triggerAboveOrBelow = uncommittedTriggerAboveOrBelow;
        uncommittedPortState.mode = uncommittedMode;
        uncommittedPortState.mainBuffer = uncommittedMainBuffer;
        uncommittedPortState.secondBuffer = uncommittedSecondBuffer;
    }
    
    public void commitChanges() {
        activeSettingId = uncommittedSettingId;
        activeTriggerPulseOrSignal = uncommittedTriggerPulseOrSignal;
        activeTriggerAboveOrBelow = uncommittedTriggerAboveOrBelow;
        activeMode = uncommittedMode;
        activeMainBuffer = uncommittedMainBuffer;
        activeSecondBuffer = uncommittedSecondBuffer;
        if (!activeMainBuffer.isEmpty()) {
            mainVal = Double.parseDouble(activeMainBuffer);
        } else {
            mainVal = 0;
        }
        if (!activeSecondBuffer.isEmpty()) {
            secondaryVal = Double.parseDouble(activeSecondBuffer);
        } else {
            secondaryVal = 0;
        }
    }
    
    public void revertChanges() {
        uncommittedSettingId = activeSettingId;
        uncommittedTriggerPulseOrSignal = activeTriggerPulseOrSignal;
        uncommittedTriggerAboveOrBelow = activeTriggerAboveOrBelow;
        uncommittedMode = activeMode;
        uncommittedMainBuffer = activeMainBuffer;
        uncommittedSecondBuffer = activeSecondBuffer;
    }
    
    @Override
    public void runRequest(String requestName, Object requestData) {
        ReactorMultiblockController reactor = reactor();
        if (reactor == null) {
            return;
        }
        
        // We save changes to an uncommitted changes temp state. When commit is pressed, then we send the run requests forward.
        switch (requestName) {
            case "setSelectedButton":
                uncommittedSettingId = (Integer) requestData;
                break;
            case "setPulseOrSignal":
                uncommittedTriggerPulseOrSignal = (Boolean) requestData;
                break;
            case "setAboveOrBelow":
                uncommittedTriggerAboveOrBelow = (Boolean) requestData;
                break;
            case "setMode":
                uncommittedMode = (Integer) requestData;
                break;
            case "setMainBuffer":
                uncommittedMainBuffer = (String) requestData;
                break;
            case "setSecondBuffer":
                uncommittedSecondBuffer = (String) requestData;
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
        compound.putInt("settingId", activeSettingId);
        compound.putBoolean("triggerPulseOrSignal", activeTriggerPulseOrSignal);
        compound.putBoolean("triggerAboveOrBelow", activeTriggerAboveOrBelow);
        compound.putInt("mode", activeMode);
        compound.putString("mainBuffer", activeMainBuffer);
        compound.putString("secondBuffer", activeSecondBuffer);
        compound.putBoolean("isPowered", isPowered);
        compound.putBoolean("isEmitting", isEmitting);
        return compound;
    }
    
    @Override
    protected void readNBT(@Nonnull CompoundNBT compound) {
        super.readNBT(compound);
        if (compound.contains("settingId")) {
            activeSettingId = compound.getInt("settingId");
        }
        if (compound.contains("triggerPulseOrSignal")) {
            activeTriggerPulseOrSignal = compound.getBoolean("triggerPulseOrSignal");
        }
        if (compound.contains("triggerAboveOrBelow")) {
            activeTriggerAboveOrBelow = compound.getBoolean("triggerAboveOrBelow");
        }
        if (compound.contains("mode")) {
            activeMode = compound.getInt("mode");
        }
        if (compound.contains("mainBuffer")) {
            activeMainBuffer = compound.getString("mainBuffer");
        }
        if (compound.contains("secondBuffer")) {
            activeSecondBuffer = compound.getString("secondBuffer");
        }
        if (compound.contains("isPowered")) {
            isPowered = compound.getBoolean("isPowered");
        }
        if (compound.contains("isEmitting")) {
            isEmitting = compound.getBoolean("isEmitting");
        }
        // Call reverted changes to align uncommitted settings to the active ones.
        revertChanges();
        commitChanges();
    }
}

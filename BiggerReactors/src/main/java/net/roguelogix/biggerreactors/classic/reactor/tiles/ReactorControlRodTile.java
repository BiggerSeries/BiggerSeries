package net.roguelogix.biggerreactors.classic.reactor.tiles;

import com.mojang.datafixers.util.Pair;
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
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorControlRod;
import net.roguelogix.biggerreactors.classic.reactor.containers.ControlRodContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ControlRodState;
import net.roguelogix.phosphophyllite.gui.client.api.IHasUpdatableState;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockPositions;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RegisterTileEntity(name = "reactor_control_rod")
public class ReactorControlRodTile extends ReactorBaseTile implements INamedContainerProvider, IHasUpdatableState<ControlRodState> {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public ReactorControlRodTile() {
        super(TYPE);
    }
    
    public final ControlRodState controlRodState = new ControlRodState(this);
    
    @Override
    @Nonnull
    public ControlRodState getState() {
        this.updateState();
        return this.controlRodState;
    }
    
    @Override
    public void updateState() {
        controlRodState.name = name;
        controlRodState.insertionLevel = insertion;
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
    @Nonnull
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ReactorControlRod.INSTANCE.getTranslationKey());
    }
    
    @Nullable
    @Override
    public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
        return new ControlRodContainer(windowId, this.pos, player);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public void runRequest(String requestName, Object requestData) {
        ReactorMultiblockController reactor = reactor();
        if(reactor == null){
            return;
        }
        
        if (requestName.equals("changeInsertionLevel")) {
            Pair<Double, Boolean> dataPair = (Pair<Double, Boolean>) requestData;
            double newLevel = this.insertion + dataPair.getFirst();
            newLevel = Math.max(0, Math.min(100, newLevel));
            if (dataPair.getSecond()) {
                reactor.setAllControlRodLevels(newLevel);
            } else {
                this.insertion = newLevel;
                reactor.updateControlRodLevels();
            }
        }
        super.runRequest(requestName, requestData);
    }
    
    private double insertion = 0;
    
    public void setInsertion(double newLevel) {
        if(newLevel < 0){
            newLevel = 0;
        }
        if(newLevel > 100){
            newLevel = 100;
        }
        insertion = newLevel;
    }
    
    public double getInsertion() {
        return insertion;
    }
    
    private String name = "Chris Richardson";
    
    public void setName(@Nonnull String name) {
        this.name = name;
    }
    
    @Nonnull
    public String getName() {
        return name;
    }
    
    @Override
    @Nonnull
    protected CompoundNBT writeNBT() {
        CompoundNBT compound = super.writeNBT();
        compound.putDouble("insertion", insertion);
        compound.putString("name", name);
        return compound;
    }
    
    @Override
    protected void readNBT(@Nonnull CompoundNBT compound) {
        super.readNBT(compound);
        if (compound.contains("insertion")) {
            insertion = compound.getDouble("insertion");
        }
        if (compound.contains("name")) {
            name = compound.getString("name");
        }
    }
}

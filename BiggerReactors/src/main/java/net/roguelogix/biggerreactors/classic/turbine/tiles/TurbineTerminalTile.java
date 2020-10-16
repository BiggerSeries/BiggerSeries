package net.roguelogix.biggerreactors.classic.turbine.tiles;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorTerminal;
import net.roguelogix.biggerreactors.classic.turbine.TurbineMultiblockController;
import net.roguelogix.biggerreactors.classic.turbine.containers.TurbineContainer;
import net.roguelogix.biggerreactors.classic.turbine.state.TurbineState;
import net.roguelogix.phosphophyllite.gui.client.api.IHasUpdatableState;
import net.roguelogix.phosphophyllite.items.DebugTool;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockPositions;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@RegisterTileEntity(name = "turbine_terminal")
public class TurbineTerminalTile extends TurbineBaseTile implements INamedContainerProvider, IHasUpdatableState<TurbineState> {
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public TurbineTerminalTile() {
        super(TYPE);
    }
    
    public final TurbineState turbineState = new TurbineState(this);
    
    @Override
    @Nonnull
    public TurbineState getState() {
        this.updateState();
        return new TurbineState(this);
    }
    
    @Override
    public void updateState() {
        if (controller != null && controller instanceof TurbineMultiblockController) {
            ((TurbineMultiblockController) controller).updateDataPacket(turbineState);
        }
    }
    
    @SuppressWarnings("DuplicatedCode")
    @Override
    @Nonnull
    public ActionResultType onBlockActivated(@Nonnull PlayerEntity player, @Nonnull Hand handIn) {
        if (player.isCrouching() && handIn == Hand.MAIN_HAND && player.getHeldItemMainhand().getItem() == DebugTool.INSTANCE) {
            if (controller != null) {
                turbine().toggleActive();
            }
            return ActionResultType.SUCCESS;
        }
        
        if (handIn == Hand.MAIN_HAND) {
            assert world != null;
            if (world.getBlockState(pos).get(RectangularMultiblockPositions.POSITIONS_ENUM_PROPERTY) != RectangularMultiblockPositions.DISASSEMBLED) {
                if (!world.isRemote) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, this, this.getPos());
                }
                return ActionResultType.SUCCESS;
            }
        }
        
        return super.onBlockActivated(player, handIn);
    }
    
    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ReactorTerminal.INSTANCE.getTranslationKey());
    }
    
    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
        return new TurbineContainer(windowId, this.pos, playerInventory.player);
    }
}

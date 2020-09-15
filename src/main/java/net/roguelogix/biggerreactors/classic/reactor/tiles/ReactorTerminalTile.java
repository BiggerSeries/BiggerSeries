package net.roguelogix.biggerreactors.classic.reactor.tiles;

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
import net.roguelogix.biggerreactors.classic.reactor.ReactorMultiblockController;
import net.roguelogix.biggerreactors.classic.reactor.blocks.ReactorTerminal;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorContainer;
import net.roguelogix.biggerreactors.classic.reactor.state.ReactorState;
import net.roguelogix.phosphophyllite.gui.client.api.IHasUpdatableState;
import net.roguelogix.phosphophyllite.items.DebugTool;
import net.roguelogix.phosphophyllite.multiblock.rectangular.RectangularMultiblockPositions;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import javax.annotation.Nullable;

@RegisterTileEntity(name = "reactor_terminal")
public class ReactorTerminalTile extends ReactorBaseTile implements INamedContainerProvider, IHasUpdatableState<ReactorState> {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public ReactorTerminalTile() {
        super(TYPE);
    }
    
    public final ReactorState reactorState = new ReactorState(this);
    
    @Override
    public ReactorState getState() {
        this.updateState();
        return new ReactorState(this);
    }
    
    @Override
    public void updateState() {
        if (controller != null && controller instanceof ReactorMultiblockController) {
            ((ReactorMultiblockController) controller).updateDataPacket(reactorState);
        }
    }
    
    @Override
    public ActionResultType onBlockActivated(PlayerEntity player, Hand handIn) {
        if (player.isCrouching() && handIn == Hand.MAIN_HAND && player.getHeldItemMainhand().getItem() == DebugTool.INSTANCE) {
            if (controller != null) {
                reactor().toggleActive();
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
        return ActionResultType.PASS;
    }
    
    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ReactorTerminal.INSTANCE.getTranslationKey());
    }
    
    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ReactorContainer(windowId, this.pos, playerInventory.player);
    }
}

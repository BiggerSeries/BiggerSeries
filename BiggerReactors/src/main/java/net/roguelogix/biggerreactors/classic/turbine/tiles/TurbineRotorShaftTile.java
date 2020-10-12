package net.roguelogix.biggerreactors.classic.turbine.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineRotorBlade;
import net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineRotorShaft;
import net.roguelogix.biggerreactors.classic.turbine.state.TurbineShaftRotationState;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;

import static net.roguelogix.biggerreactors.classic.turbine.state.TurbineShaftRotationState.*;
import static net.roguelogix.biggerreactors.classic.turbine.state.TurbineShaftRotationState.TURBINE_SHAFT_ROTATION_STATE_ENUM_PROPERTY;

@RegisterTileEntity(name = "turbine_rotor_shaft")
public class TurbineRotorShaftTile extends TurbineBaseTile {
    
    @RegisterTileEntity.Type
    public static TileEntityType<?> TYPE;
    
    public TurbineRotorShaftTile() {
        super(TYPE);
    }
    
    @Override
    protected void onAssemblyAttempted() {
        super.onAssemblyAttempted();
        TurbineShaftRotationState newRotation = getBlockState().get(TurbineShaftRotationState.TURBINE_SHAFT_ROTATION_STATE_ENUM_PROPERTY);
        for (Direction value : Direction.values()) {
            BlockPos neighbor = getPos().offset(value);
            assert world != null;
            BlockState state = world.getBlockState(neighbor);
            if (state.getBlock() != TurbineRotorShaft.INSTANCE) {
                continue;
            }
            if (neighbor.getX() != pos.getX() && neighbor.getY() == pos.getY() && neighbor.getZ() == pos.getZ()) {
                newRotation = X;
            } else if (neighbor.getX() == pos.getX() && neighbor.getY() != pos.getY() && neighbor.getZ() == pos.getZ()) {
                newRotation = Y;
            } else if (neighbor.getX() == pos.getX() && neighbor.getY() == pos.getY() && neighbor.getZ() != pos.getZ()) {
                newRotation = Z;
            }
            world.setBlockState(pos, getBlockState().with(TURBINE_SHAFT_ROTATION_STATE_ENUM_PROPERTY, newRotation));
        }
        
        // propagate it out to the blades
        for (Direction value : Direction.values()) {
            BlockPos pos = getPos();
            while (true) {
                pos = pos.offset(value);
                BlockState state = world.getBlockState(pos);
                if(state.getBlock() != TurbineRotorBlade.INSTANCE){
                    break;
                }
                world.setBlockState(pos, state.with(TURBINE_SHAFT_ROTATION_STATE_ENUM_PROPERTY, newRotation));
            }
        }
    }
}

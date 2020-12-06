package net.roguelogix.biggerreactors.classic.turbine.tiles;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.biggerreactors.classic.turbine.TurbineMultiblockController;
import net.roguelogix.phosphophyllite.registry.RegisterTileEntity;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector4i;

import javax.annotation.Nullable;
import java.util.ArrayList;

@RegisterTileEntity(name = "turbine_rotor_bearing")
public class TurbineRotorBearingTile extends TurbineBaseTile{
    
    @RegisterTileEntity.Type
    public static TileEntityType<TurbineRotorBearingTile> TYPE;
    
    public TurbineRotorBearingTile() {
        super(TYPE);
    }
    
    public double angle = 0;
    
    public boolean isRenderBearing = false;
    public double speed = 0;
    public Vector3f rotationAxis = null;
    public ArrayList<Vector4i> rotorConfiguration = null;
    public AxisAlignedBB AABB = null;
    private boolean sendFullUpdate = false;
    
    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        TurbineMultiblockController turbine = turbine();
        if(turbine != null) {
            nbt.putDouble("speed", turbine.CCgetRotorSpeed());
            if (sendFullUpdate) {
                sendFullUpdate = false;
                nbt.put("config", getUpdateTag());
            }
        }
        return new SUpdateTileEntityPacket(this.getPos(), 0, nbt);
    }
    
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getNbtCompound();
        if(nbt.contains("speed")) {
            speed = nbt.getDouble("speed");
            if (nbt.contains("config")) {
                handleUpdateTag(getBlockState(), nbt.getCompound("config"));
            }
        }
    }
    
    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        if(nbt.contains("rotx")) {
            isRenderBearing = true;
            if(rotationAxis == null){
                rotationAxis = new Vector3f();
            }
            rotationAxis.set(nbt.getFloat("rotx"), nbt.getFloat("roty"), nbt.getFloat("rotz"));
            if(rotorConfiguration == null){
                rotorConfiguration = new ArrayList<>();
            }
            rotorConfiguration.clear();
            int rotorShafts = nbt.getInt("shafts");
            for (int i = 0; i < rotorShafts; i++) {
                Vector4i vec = new Vector4i();
                vec.x = nbt.getInt("shaft" + i + "0");
                vec.y = nbt.getInt("shaft" + i + "1");
                vec.z = nbt.getInt("shaft" + i + "2");
                vec.w = nbt.getInt("shaft" + i + "3");
                rotorConfiguration.add(vec);
            }
            AABB = new AxisAlignedBB(nbt.getInt("minx"), nbt.getInt("miny"), nbt.getInt("minz"), nbt.getInt("maxx"), nbt.getInt("maxy"), nbt.getInt("maxz"));
        }else{
            isRenderBearing = false;
        }
    }
    
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        if(isRenderBearing && turbine() != null) {
            nbt.putFloat("rotx", turbine().rotationAxis.getX());
            nbt.putFloat("roty", turbine().rotationAxis.getY());
            nbt.putFloat("rotz", turbine().rotationAxis.getZ());
            nbt.putInt("minx", turbine().minCoord().x());
            nbt.putInt("miny", turbine().minCoord().y());
            nbt.putInt("minz", turbine().minCoord().z());
            nbt.putInt("maxx", turbine().maxCoord().x());
            nbt.putInt("maxy", turbine().maxCoord().y());
            nbt.putInt("maxz", turbine().maxCoord().z());
            nbt.putInt("shafts", turbine().rotorConfiguration.size());
            ArrayList<Vector4i> config = turbine().rotorConfiguration;
            for (int i = 0; i < config.size(); i++) {
                Vector4i vec = config.get(i);
                nbt.putInt("shaft" + i + "0", vec.x);
                nbt.putInt("shaft" + i + "1", vec.y);
                nbt.putInt("shaft" + i + "2", vec.z);
                nbt.putInt("shaft" + i + "3", vec.w);
            }
        }
        return nbt;
    }
    
    @Override
    protected void onAssemblyAttempted() {
        assert world != null;
        sendFullUpdate = true;
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if(AABB == null){
            return INFINITE_EXTENT_AABB;
        }
        return AABB;
    }
}

package net.roguelogix.phosphophyllite.util;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3i;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector3ic;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileMap<TileType extends TileEntity> {
    private Vector3i scratchVector = new Vector3i();
    
    private final LinkedHashMap<Vector3ic, TileEntity[][][]> internalMap = new LinkedHashMap<>();
    private int size = 0;
    
    public boolean addTile(TileType tile) {
        BlockPos tilePos = tile.getPos();
        scratchVector.set(tilePos.getX() >> 4, tilePos.getY() >> 4, tilePos.getZ() >> 4);
        TileEntity[][][] sectionArray = internalMap.computeIfAbsent(scratchVector, k -> {
            // the previous scratch vector is now the key, no longer allowed to edit it, so, new one plx
            scratchVector = new Vector3i(k);
            return new TileEntity[16][][];
        });
        TileEntity[][] XsubSection = sectionArray[tilePos.getX() & 15];
        if (XsubSection == null) {
            XsubSection = new TileEntity[16][];
            sectionArray[tilePos.getX() & 15] = XsubSection;
        }
        TileEntity[] XYsubSection = XsubSection[tilePos.getY() & 15];
        if (XYsubSection == null) {
            XYsubSection = new TileEntity[16];
            XsubSection[tilePos.getY() & 15] = XYsubSection;
        }
        
        TileEntity prevVal = XYsubSection[tilePos.getZ() & 15];
        XYsubSection[tilePos.getZ() & 15] = tile;
        if (prevVal == null) {
            size++;
            return true;
        }
        return false;
    }
    
    public void addAll(TileMap<TileType> otherMap) {
        otherMap.forEachTile(this::addTile);
    }
    
    public boolean removeTile(TileType tile) {
        BlockPos tilePos = tile.getPos();
        scratchVector.set(tilePos.getX() >> 4, tilePos.getY() >> 4, tilePos.getZ() >> 4);
        TileEntity[][][] sectionArray = internalMap.get(scratchVector);
        if (sectionArray == null) {
            return false;
        }
        TileEntity[][] XsubSection = sectionArray[tilePos.getX() & 15];
        if (XsubSection == null) {
            return false;
        }
        TileEntity[] XYsubSection = XsubSection[tilePos.getY() & 15];
        if (XYsubSection == null) {
            return false;
        }
        
        TileEntity prevVal = XYsubSection[tilePos.getZ() & 15];
        XYsubSection[tilePos.getZ() & 15] = null;
        if (prevVal != null) {
            size--;
        }
        
        for (TileEntity tileEntity : XYsubSection) {
            if (tileEntity != null) {
                return prevVal != null;
            }
        }
        XsubSection[tilePos.getY() & 15] = null;
        
        for (TileEntity[] tileEntities : XsubSection) {
            if (tileEntities != null) {
                return prevVal != null;
            }
        }
        sectionArray[tilePos.getX() & 15] = null;
        
        for (TileEntity[][] tileEntities : sectionArray) {
            if (tileEntities != null) {
                return prevVal != null;
            }
        }
        internalMap.remove(scratchVector);
        
        return prevVal != null;
    }
    
    public boolean containsTile(TileType tile) {
        return containsPos(tile.getPos());
    }
    
    public boolean containsPos(BlockPos pos) {
        return getTile(pos) != null;
    }
    
    @Nullable
    public TileType getTile(BlockPos pos) {
        scratchVector.set(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
        TileEntity[][][] sectionArray = internalMap.get(scratchVector);
        if (sectionArray == null) {
            return null;
        }
        TileEntity[][] XsubSection = sectionArray[pos.getX() & 15];
        if (XsubSection == null) {
            return null;
        }
        TileEntity[] XYsubSection = XsubSection[pos.getY() & 15];
        if (XYsubSection == null) {
            return null;
        }
        
        //noinspection unchecked
        return (TileType) XYsubSection[pos.getZ() & 15];
    }
    
    public void forEach(BiConsumer<BlockPos, TileType> consumer) {
        forEachTile((t) -> consumer.accept(t.getPos(), t));
    }
    
    public void forEachTile(Consumer<TileType> consumer) {
        internalMap.forEach((vec, sectionMap) -> {
            for (int i = 0, sectionMapLength = sectionMap.length; i < sectionMapLength; i++) {
                TileEntity[][] tileEntities = sectionMap[i];
                if (tileEntities != null) {
                    for (int j = 0, tileEntitiesLength = tileEntities.length; j < tileEntitiesLength; j++) {
                        TileEntity[] tileEntity = tileEntities[j];
                        if (tileEntity != null) {
                            for (int k = 0, tileEntityLength = tileEntity.length; k < tileEntityLength; k++) {
                                TileEntity entity = tileEntity[k];
                                if (entity != null) {
                                    //noinspection unchecked
                                    consumer.accept((TileType) entity);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
    
    public void forEachPos(Consumer<BlockPos> consumer) {
        forEachTile((t) -> consumer.accept(t.getPos()));
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public int size() {
        return size;
    }
}

package net.roguelogix.phosphophyllite.quartz_old.internal.blocks;

import org.joml.Vector3i;


/**
 * Info to be directly placed into buffers
 * all values here have packed data
 * DO NOT ATTEMPT TO MANUALLY CREATE
 */

public class BlockRenderInfo implements Cloneable {
    public Vector3i location = null;

    public int x = -1, y = -1, z = -1;

    // west
    public int textureOffsetRotation0 = 0;
    // east
    public int textureOffsetRotation1 = 0;
    // bottom
    public int textureOffsetRotation2 = 0;
    // top
    public int textureOffsetRotation3 = 0;
    // south
    public int textureOffsetRotation4 = 0;
    // north
    public int textureOffsetRotation5 = 0;

    // west
    // low y, low z
    public short lightmap00 = 0xFFF;
    // high y, low z
    public short lightmap01 = 0xFFF;
    // low y, high z
    public short lightmap02 = 0xFFF;
    // high y, high z
    public short lightmap03 = 0xFFF;

    // east
    // low y, low z
    public short lightmap10 = 0xFFF;
    // high y, low z
    public short lightmap11 = 0xFFF;
    // low y, high z
    public short lightmap12 = 0xFFF;
    // high y, high z
    public short lightmap13 = 0xFFF;


    public short lightmap20 = 0xFFF;
    public short lightmap21 = 0xFFF;
    public short lightmap22 = 0xFFF;
    public short lightmap23 = 0xFFF;
    public short lightmap30 = 0xFFF;
    public short lightmap31 = 0xFFF;
    public short lightmap32 = 0xFFF;
    public short lightmap33 = 0xFFF;
    public short lightmap40 = 0xFFF;
    public short lightmap41 = 0xFFF;
    public short lightmap42 = 0xFFF;
    public short lightmap43 = 0xFFF;
    public short lightmap50 = 0xFFF;
    public short lightmap51 = 0xFFF;
    public short lightmap52 = 0xFFF;
    public short lightmap53 = 0xFFF;

    public BlockRenderInfo deepCopy() {
        try {
            BlockRenderInfo newInfo = (BlockRenderInfo) this.clone();
            newInfo.location = new Vector3i(location);
            return newInfo;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public BlockRenderInfo shallowCopy() {
        try {
            return (BlockRenderInfo) this.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

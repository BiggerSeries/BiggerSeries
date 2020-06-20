package net.roguelogix.phosphophyllite.multiblock.generic;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.roguelogix.phosphophyllite.registry.Registry;

public class MultiblockBlock extends Block {
    public MultiblockBlock(Properties properties) {
        super(properties);
    }

    protected MultiblockBakedModel model = null;

    public MultiblockBakedModel setupBakedModel(ResourceLocation defaultTexture) {
        model = new MultiblockBakedModel(defaultTexture);
        Registry.registerBakedModel(this, model);
        return model;
    }

    public boolean usesBlockState(){
        return true;
    }
}

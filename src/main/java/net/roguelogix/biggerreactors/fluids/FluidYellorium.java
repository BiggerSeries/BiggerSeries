package net.roguelogix.biggerreactors.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.registry.PhosphophylliteFluid;
import net.roguelogix.phosphophyllite.registry.RegisterFluid;

@RegisterFluid(name = "fluid_yellorium", registerBucket = true)
public class FluidYellorium extends PhosphophylliteFluid {
    
    @RegisterFluid.Instance
    public static FluidYellorium INSTANCE;
    
    private final ResourceLocation TEXTURE_STILL = new ResourceLocation(BiggerReactors.modid, "fluid/yellorium_still");
    private final ResourceLocation TEXTURE_FLOWING = new ResourceLocation(BiggerReactors.modid, "fluid/yellorium_flowing");
    private final ResourceLocation TEXTURE_OVERLAY = new ResourceLocation(BiggerReactors.modid, "fluid/yellorium_overlay");
    
    public FluidYellorium(Properties properties) {
        super(properties);
    }
    
    @Override
    public FluidAttributes createAttributes() {
        return FluidAttributes.builder(TEXTURE_STILL, TEXTURE_FLOWING)
                .color(0xBCBA50)
                .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY)
                .overlay(TEXTURE_OVERLAY)
                .density(1)
                .build(this);
    }
}

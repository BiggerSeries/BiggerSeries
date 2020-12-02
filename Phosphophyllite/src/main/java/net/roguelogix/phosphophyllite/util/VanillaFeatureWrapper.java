package net.roguelogix.phosphophyllite.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.BooleanSupplier;

public class VanillaFeatureWrapper<Config extends IFeatureConfig, FeatureC extends Feature<Config>> extends ConfiguredFeature<Config, FeatureC> {
    
    final private BooleanSupplier test;
    
    public VanillaFeatureWrapper(ConfiguredFeature<Config, FeatureC> wrap, BooleanSupplier cannotRun) {
        
        super(wrap.feature, wrap.config);
        test = cannotRun;
    }
    
    @Nonnull
    public ConfiguredFeature<?, ?> withPlacement(@Nonnull ConfiguredPlacement<?> placement) {
        
        return new VanillaFeatureWrapper<>(super.withPlacement(placement), test);
    }
    
    public boolean generate(@Nonnull ISeedReader reader, @Nonnull ChunkGenerator generator, @Nonnull Random rand, @Nonnull BlockPos pos) {
        if (!test.getAsBoolean())
            return false;
        return super.generate(reader, generator, rand, pos);
    }
}

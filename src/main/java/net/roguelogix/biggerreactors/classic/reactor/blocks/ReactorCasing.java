package net.roguelogix.biggerreactors.classic.reactor.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.roguelogix.biggerreactors.classic.reactor.tiles.ReactorCasingTile;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;

import javax.annotation.Nullable;

@RegisterBlock(name = "reactor_casing", tileEntityClass = ReactorCasingTile.class)
public class ReactorCasing extends ReactorBaseBlock {

    @RegisterBlock.Instance
    public static ReactorCasing INSTANCE;

    public ReactorCasing(){
        super();

//        ResourceLocation corner = new ResourceLocation(BiggerReactors.modid, "block/reactor_casing_corner");
//        ResourceLocation disassembled = new ResourceLocation(BiggerReactors.modid, "block/reactor_casing_disassembled");
//        ResourceLocation face = new ResourceLocation(BiggerReactors.modid, "block/reactor_casing_face");
//        ResourceLocation frame = new ResourceLocation(BiggerReactors.modid, "block/reactor_casing_frame");
//
//        setupBakedModel(disassembled);
//        model.map.property = RectangularMultiblockPositions.POSITIONS_MODEL_PROPERTY;
//
//        MultiblockBakedModel.TextureMap cornerTextureMap = new MultiblockBakedModel.TextureMap();
//        cornerTextureMap.spriteLocation = corner;
//        model.map.map.put(RectangularMultiblockPositions.CORNER, cornerTextureMap);
//
//        MultiblockBakedModel.TextureMap faceTextureMap = new MultiblockBakedModel.TextureMap();
//        faceTextureMap.spriteLocation = face;
//        model.map.map.put(RectangularMultiblockPositions.FACE, faceTextureMap);
//
//        MultiblockBakedModel.TextureMap frame_yTextureMap = new MultiblockBakedModel.TextureMap();
//        frame_yTextureMap.spriteLocation = frame;
//        model.map.map.put(RectangularMultiblockPositions.FRAME_Y, frame_yTextureMap);
//
//        MultiblockBakedModel.TextureMap frame_xTextureMap = new MultiblockBakedModel.TextureMap();
//        frame_xTextureMap.spriteLocation = frame;
//        model.map.map.put(RectangularMultiblockPositions.FRAME_X, frame_xTextureMap);
//
//        MultiblockBakedModel.TextureMap frame_zTextureMap = new MultiblockBakedModel.TextureMap();
//        frame_zTextureMap.spriteLocation = frame;
//        model.map.map.put(RectangularMultiblockPositions.FRAME_Z, frame_zTextureMap);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReactorCasingTile();
    }

    @Override
    public boolean usesBlockState() {
        return false;
    }
}

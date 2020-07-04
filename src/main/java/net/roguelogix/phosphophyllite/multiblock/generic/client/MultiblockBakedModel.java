package net.roguelogix.phosphophyllite.multiblock.generic.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MultiblockBakedModel implements IDynamicBakedModel {
    
    public static class TextureMap {
        public ResourceLocation spriteLocation;
        public ModelProperty<?> property;
        public final HashMap<Object, TextureMap> map = new HashMap<>();
        public final HashMap<Direction, Boolean> sideRotations = new HashMap<>();
    }
    
    public TextureMap map = new TextureMap();
    
    public MultiblockBakedModel(ResourceLocation defaultTexture) {
        map.spriteLocation = defaultTexture;
    }
    
    private void putVertex(BakedQuadBuilder builder, TextureAtlasSprite sprite, float x, float y, float z, float normalx, float normaly, float normalz, float u, float v) {
        ImmutableList<VertexFormatElement> elements = builder.getVertexFormat().getElements();
        for (int i = 0; i < elements.size(); i++) {
            VertexFormatElement e = elements.get(i);
            switch (e.getUsage()) {
                case POSITION:
                    builder.put(i, x, y, z, 1f);
                    break;
                case NORMAL:
                    builder.put(i, normalx, normaly, normalz);
                    break;
                case COLOR:
                    builder.put(i, 1f, 1f, 1f, 1f);
                    break;
                case UV:
                    switch (e.getIndex()) {
                        case 0: {
                            builder.put(i, sprite.getInterpolatedU(u), sprite.getInterpolatedV(v));
                            break;
                        }
                        case 2: {
                            builder.put(i, 0.0f / 2048f, 0.0f / 2048.0f);
                            break;
                        }
                        default: {
                            builder.put(i);
                            break;
                        }
                    }
                    break;
                default:
                    builder.put(i);
            }
        }
    }
    
    static HashMap<Direction, float[][][][]> vertexMap = new HashMap<>();
    
    static {
        vertexMap.put(Direction.UP, new float[][][][]{
                {
                        {{0, 1, 0}, {0, 1, 0}, {0, 0},},
                        {{0, 1, 1}, {0, 1, 0}, {0, 16},},
                        {{1, 1, 1}, {0, 1, 0}, {16, 16},},
                        {{1, 1, 0}, {0, 1, 0}, {16, 0},},
                },
                {
                        {{0, 1, 0,}, {0, 1, 0,}, {16, 0,},},
                        {{0, 1, 1,}, {0, 1, 0,}, {0, 0,},},
                        {{1, 1, 1,}, {0, 1, 0,}, {0, 16,},},
                        {{1, 1, 0,}, {0, 1, 0,}, {16, 16,},},
                },
        });
        vertexMap.put(Direction.DOWN, new float[][][][]{
                {
                        //rotation
                        {
                                {0, 0, 0}, // position
                                {0, -1, 0}, //normal
                                {0, 0}, // texture position
                        },
                        {{1, 0, 0}, {0, -1, 0}, {16, 0},},
                        {{1, 0, 1}, {0, -1, 0}, {16, 16},},
                        {{0, 0, 1}, {0, -1, 0}, {0, 16},},
                },
                {
                        {{0, 0, 0}, {0, -1, 0}, {16, 0},},
                        {{1, 0, 0}, {0, -1, 0}, {16, 16},},
                        {{1, 0, 1}, {0, -1, 0}, {0, 16},},
                        {{0, 0, 1}, {0, -1, 0}, {0, 0},},
                },
        });
        vertexMap.put(Direction.EAST, new float[][][][]{
                {
                        {{1, 0, 0}, {1, 0, 0}, {0, 0},},
                        {{1, 1, 0}, {1, 0, 0}, {16, 0},},
                        {{1, 1, 1}, {1, 0, 0}, {16, 16},},
                        {{1, 0, 1}, {1, 0, 0}, {0, 16},},
                },
                {
                        {{1, 0, 0}, {1, 0, 0}, {16, 0},},
                        {{1, 1, 0}, {1, 0, 0}, {16, 16},},
                        {{1, 1, 1}, {1, 0, 0}, {0, 16},},
                        {{1, 0, 1}, {1, 0, 0}, {0, 0},},
                },
        });
        vertexMap.put(Direction.WEST, new float[][][][]{
                {
                        {{0, 0, 0}, {-1, 0, 0}, {0, 0}},
                        {{0, 0, 1}, {-1, 0, 0}, {0, 16}},
                        {{0, 1, 1}, {-1, 0, 0}, {16, 16}},
                        {{0, 1, 0}, {-1, 0, 0}, {16, 0}},
                },
                {
                        {{0, 0, 0}, {-1, 0, 0}, {16, 0}},
                        {{0, 0, 1}, {-1, 0, 0}, {0, 0}},
                        {{0, 1, 1}, {-1, 0, 0}, {0, 16}},
                        {{0, 1, 0}, {-1, 0, 0}, {16, 16}},
                },
        });
        vertexMap.put(Direction.SOUTH, new float[][][][]{
                {
                        {{0, 0, 1}, {0, 0, 1}, {0, 0}},
                        {{1, 0, 1}, {0, 0, 1}, {16, 0}},
                        {{1, 1, 1}, {0, 0, 1}, {16, 16}},
                        {{0, 1, 1}, {0, 0, 1}, {0, 16}},
                },
                {
                        {{0, 0, 1}, {0, 0, 1}, {16, 0}},
                        {{1, 0, 1}, {0, 0, 1}, {16, 16}},
                        {{1, 1, 1}, {0, 0, 1}, {0, 16}},
                        {{0, 1, 1}, {0, 0, 1}, {0, 0}},
                },
        });
        vertexMap.put(Direction.NORTH, new float[][][][]{
                {
                        {{0, 0, 0}, {0, 0, -1}, {0, 0}},
                        {{0, 1, 0}, {0, 0, -1}, {0, 16}},
                        {{1, 1, 0}, {0, 0, -1}, {16, 16}},
                        {{1, 0, 0}, {0, 0, -1}, {16, 0}},
                },
                {
                        {{0, 0, 0}, {0, 0, -1}, {16, 0}},
                        {{0, 1, 0}, {0, 0, -1}, {0, 0}},
                        {{1, 1, 0}, {0, 0, -1}, {0, 16}},
                        {{1, 0, 0}, {0, 0, -1}, {16, 16}},
                },
        });
    }
    
    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (side == null) {
//            side = Direction.UP;
            return Collections.emptyList();
        }

//        long start = System.nanoTime();
        TextureMap map = this.map;
        while (true) {
            if (map.property == null) {
                break;
            }
            TextureMap nextMap = map.map.get(extraData.getData(map.property));
            if (nextMap == null) {
                break;
            }
            map = nextMap;
        }
        
        
        boolean rotate = false;
        if (map.sideRotations.containsKey(side)) {
            rotate = map.sideRotations.get(side);
        }
        float[][][] vertexData = vertexMap.get(side)[rotate ? 1 : 0];
        List<BakedQuad> quads = new ArrayList<>();
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(map.spriteLocation);
        BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
        builder.setQuadOrientation(Direction.getFacingFromVector(vertexData[0][1][0], vertexData[0][1][1], vertexData[0][1][2]));
        
        putVertex(builder, sprite, vertexData[0][0][0], vertexData[0][0][1], vertexData[0][0][2], vertexData[0][1][0], vertexData[0][1][1], vertexData[0][1][2], vertexData[0][2][0], vertexData[0][2][1]);
        putVertex(builder, sprite, vertexData[1][0][0], vertexData[1][0][1], vertexData[1][0][2], vertexData[1][1][0], vertexData[1][1][1], vertexData[1][1][2], vertexData[1][2][0], vertexData[1][2][1]);
        putVertex(builder, sprite, vertexData[2][0][0], vertexData[2][0][1], vertexData[2][0][2], vertexData[2][1][0], vertexData[2][1][1], vertexData[2][1][2], vertexData[2][2][0], vertexData[2][2][1]);
        putVertex(builder, sprite, vertexData[3][0][0], vertexData[3][0][1], vertexData[3][0][2], vertexData[3][1][0], vertexData[3][1][1], vertexData[3][1][2], vertexData[3][2][0], vertexData[3][2][1]);
        quads.add(builder.build());
//        long end = System.nanoTime();
//        float ms = (float) (end - start) / 1_000f;
//        System.out.println(ms);
        return quads;
    }
    
    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }
    
    @Override
    public boolean isGui3d() {
        return false;
    }
    
    @Override
    public boolean func_230044_c_() {
        return false;
    }
    
    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }
    
    @Nonnull
    @Override
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(map.spriteLocation);
    }
    
    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return null;
    }
}

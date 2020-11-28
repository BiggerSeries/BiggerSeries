package net.roguelogix.biggerreactors.classic.turbine.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineRotorBlade;
import net.roguelogix.biggerreactors.classic.turbine.blocks.TurbineRotorShaft;
import net.roguelogix.biggerreactors.classic.turbine.tiles.TurbineRotorBearingTile;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockBlock;
import net.roguelogix.phosphophyllite.repack.org.joml.Vector4i;

import javax.annotation.Nonnull;

public class BladeRenderer extends TileEntityRenderer<TurbineRotorBearingTile> {
    
    public BladeRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }
    
    @Override
    public void render(TurbineRotorBearingTile bearing, float partialTicks, @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        // fuck using MC's render engine, its a slow pile of garbage
        // if they cant do it properly, ill just have to do it myself
        // later, im lazy right now, still, fuck this shit
        BlockState state = bearing.getBlockState();
        if (state.get(MultiblockBlock.ASSEMBLED)) {
            // it is notable that this is on the client, and as a result i do not have direct access to the multiblock controller
            // so, tile entity, do your thing and just magically be updated k thx
            
            // each turbine has two, one of them is chosen
            if (!bearing.isRenderBearing) {
                return;
            }
            
            // signal to not render
            // ie: no glass
            if (bearing.rotationAxis == null || bearing.rotorConfiguration == null || (bearing.rotationAxis.getX() == 0 && bearing.rotationAxis.getY() == 0 && bearing.rotationAxis.getZ() == 0)) {
                return;
            }
            
            double angle = bearing.angle;
            long elapsedTimeNano = System.nanoTime() - BiggerReactors.lastRenderTime;
            
            double speed = bearing.speed / 10f;
            if (speed > 0.001f) {
                double elapsedTimeMilis = ((double) elapsedTimeNano) / 1_000_000;
                angle += speed * ((float) elapsedTimeMilis / 60000f) * 360f; // RPM * time in minutes * 360 degrees per rotation
                angle = angle % 360f;
                bearing.angle = angle;
            }
            
            matrixStackIn.push();
            
            matrixStackIn.translate(0.5, 0, 0.5);
            
            matrixStackIn.rotate(new Quaternion(bearing.rotationAxis, (float) angle, true));
            
            matrixStackIn.translate(-0.5, 0, -0.5);
            
            for (Vector4i vector4i : bearing.rotorConfiguration) {
                matrixStackIn.translate(bearing.rotationAxis.getX(), bearing.rotationAxis.getY(), bearing.rotationAxis.getZ());
                
                Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(TurbineRotorShaft.INSTANCE.getDefaultState(), matrixStackIn, bufferIn, 0x007F007F, combinedOverlayIn, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
                
                int i = 0;
                for (Direction direction : Direction.values()) {
                    switch (direction) {
                        case UP:
                        case DOWN: {
                            if (bearing.rotationAxis.getY() != 0) {
                                continue;
                            }
                            break;
                        }
                        case NORTH:
                        case SOUTH: {
                            if (bearing.rotationAxis.getZ() != 0) {
                                continue;
                            }
                            break;
                        }
                        case WEST:
                        case EAST: {
                            if (bearing.rotationAxis.getX() != 0) {
                                continue;
                            }
                            break;
                        }
                    }
                    matrixStackIn.push();
                    for (int j = 0; j < vector4i.get(i); j++) {
                        matrixStackIn.translate(direction.getXOffset(), direction.getYOffset(), direction.getZOffset());
                        matrixStackIn.push();
                        matrixStackIn.translate(0.5, 0, 0.5);
                        matrixStackIn.rotate(new Quaternion(bearing.rotationAxis, (float) (180 * (i & 1) + 135 * (i & 2)), true));
                        matrixStackIn.translate(-0.5, 0, -0.5);
                        Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(TurbineRotorBlade.INSTANCE.getDefaultState(), matrixStackIn, bufferIn, 0x007F007F, combinedOverlayIn, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
                        matrixStackIn.pop();
                    }
                    matrixStackIn.pop();
                    i++;
                }
            }
            
            
            matrixStackIn.pop();
        }
    }
    
    @Override
    public boolean isGlobalRenderer(@Nonnull TurbineRotorBearingTile te) {
        return true;
    }
}

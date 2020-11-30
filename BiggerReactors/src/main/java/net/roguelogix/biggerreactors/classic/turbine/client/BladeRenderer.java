package net.roguelogix.biggerreactors.classic.turbine.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
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
            
            int blade180RotationMultiplier = ((int) -bearing.rotationAxis.getX()) | ((int) -bearing.rotationAxis.getY()) | ((int) bearing.rotationAxis.getZ());
            if (blade180RotationMultiplier > 0) {
                angle += 180;
            }
            //            blade180RotationMultiplier *= -1;
            
            matrixStackIn.push();
            
            matrixStackIn.translate(0.5, 0.5, 0.5);
            
            Quaternion axisRotation = null;
            
            if (bearing.rotationAxis.getX() != 0) {
                axisRotation = new Quaternion(Vector3f.ZP, -90 * bearing.rotationAxis.getX(), true);
                angle -= 90;
            } else if (bearing.rotationAxis.getZ() != 0) {
                axisRotation = new Quaternion(Vector3f.XP, 90 * bearing.rotationAxis.getZ(), true);
            } else if (bearing.rotationAxis.getY() != 1) {
                axisRotation = new Quaternion(Vector3f.XP, 180, true);
            }
            if (axisRotation != null) {
                matrixStackIn.rotate(axisRotation);
            }
            matrixStackIn.rotate(new Quaternion(Vector3f.YP, (float) angle, true));
            
            matrixStackIn.translate(-0.5, -0.5, -0.5);
            
            
            for (Vector4i vector4i : bearing.rotorConfiguration) {
                matrixStackIn.translate(0, 1, 0);
                
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
                    for (int j = 0; j < vector4i.get(i); j++) {
                        matrixStackIn.push();
                        matrixStackIn.translate(0.5, 0.5, 0.5);
                        matrixStackIn.rotate(new Quaternion(Vector3f.YP, (float) (180 * (i & 1) + blade180RotationMultiplier * 135 * (i & 2)), true));
                        matrixStackIn.translate(-0.5, -0.5, -0.5);
                        matrixStackIn.translate(0, 0, -(j + 1));
                        Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(TurbineRotorBlade.INSTANCE.getDefaultState(), matrixStackIn, bufferIn, 0x007F007F, combinedOverlayIn, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
                        matrixStackIn.pop();
                    }
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

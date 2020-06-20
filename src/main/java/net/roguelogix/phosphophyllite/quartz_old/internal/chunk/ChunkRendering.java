package net.roguelogix.phosphophyllite.quartz_old.internal.chunk;

import net.minecraft.client.Minecraft;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.ResourceLocation;
import net.roguelogix.phosphophyllite.quartz_old.internal.OperationMode;
import net.roguelogix.phosphophyllite.quartz_old.internal.blocks.BlockRenderInfo;
import net.roguelogix.phosphophyllite.quartz_old.internal.shaders.Program;
import net.roguelogix.phosphophyllite.quartz_old.internal.shaders.ShaderRegistry;
import net.roguelogix.phosphophyllite.quartz_old.internal.textures.TextureRegistry;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static net.roguelogix.phosphophyllite.Phosphophyllite.modid;
import static net.roguelogix.phosphophyllite.quartz_old.internal.Renderer.*;
import static org.lwjgl.opengl.GL45.*;

public class ChunkRendering {

    static Program cullQueryProgram;
    static Program blockProgram;
    static final ArrayList<ArrayList<RenderChunk>> chunks = new ArrayList<>(16);

    static {
        for (int i = 0; i < 16; i++) {
            chunks.add(new ArrayList<>());
        }
    }

    static int layerCullVAO;
    static int layerCullBuffer;

    static int chunkCullVAO;
    static int chunkCullQueryCubeBuffer;
    static int chunkCullQueryCubeElementBuffer;

    static int cubeVAO;
    static int cubeBuffer;
    static int cubeElementBuffer;

    private static final Vector4f layerMaxYVector = new Vector4f();
    private static final Vector4f layerMinYVector = new Vector4f();
    private static float minLayerY = 0;
    private static float maxLayerY = 0;

    public static final FloatBuffer fogColorBuffer = BufferUtils.createFloatBuffer(4);

    public static void startup() {
        cullQueryProgram = ShaderRegistry.getOrLoadProgram(new ResourceLocation(modid, "shaders/450/render_chunk_query"));
        blockProgram = ShaderRegistry.getOrLoadProgram(new ResourceLocation(modid, "shaders/450/block"));
        // TODO remove
        TextureRegistry.getOrRegister(new ResourceLocation("biggerreactors", "textures/block/reactor_terminal_off.png"));
        TextureRegistry.getOrRegister(new ResourceLocation("biggerreactors", "textures/block/reactor_terminal_idle.png"));
        TextureRegistry.getOrRegister(new ResourceLocation("biggerreactors", "textures/block/reactor_casing_disassembled.png"));

        switch (OperationMode.mode()) {
            case GL45: {
                glCreateQueries(GL_ANY_SAMPLES_PASSED, layerQueries);

                chunkCullVAO = glCreateVertexArrays();
                chunkCullQueryCubeBuffer = glCreateBuffers();
                chunkCullQueryCubeElementBuffer = glCreateBuffers();

                // its a 16x16x16 cube
                // just trust me, its *magic*
                // ~~i forgot what exactly its doing by the time i was done with it~~
                //
                // to draw, bind the chunkCullVAO and program,
                // set uniform 0 to the offset (so, negative of player position)
                // set uniform 1 to the modelviewprojection matrix (there is a buffer for that in Renderer)
                // then draw with
                // glDrawElements(GL_TRIANGLES_STRIP, 14, GL_UNSIGNED_INT, 0)
                //
                glNamedBufferStorage(chunkCullQueryCubeBuffer, new float[]{
                        +0, +0, +0, // 1
                        +0, +0, 16, // 2
                        16, +0, +0, // 3
                        16, +0, 16, // 4
                        +0, 16, +0, // 5
                        +0, 16, 16, // 6
                        16, 16, 16, // 7
                        16, 16, +0, // 8
                }, 0);
                glNamedBufferStorage(chunkCullQueryCubeElementBuffer, new int[]{
                        0, 1, 4, 5, 6, 1, 3, 0, 2, 4, 7, 6, 2, 3,
                }, 0);

                glVertexArrayElementBuffer(chunkCullVAO, chunkCullQueryCubeElementBuffer);
                glVertexArrayVertexBuffer(chunkCullVAO, 0, chunkCullQueryCubeBuffer, 0, 12);
                glVertexArrayAttribBinding(chunkCullVAO, 0, 0);
                glVertexArrayAttribFormat(chunkCullVAO, 0, 3, GL_FLOAT, false, 0);
                glEnableVertexArrayAttrib(chunkCullVAO, 0);

                cubeVAO = glCreateVertexArrays();
                cubeBuffer = glCreateBuffers();
                cubeElementBuffer = glCreateBuffers();

                // faces here aren't as magic
                // ordering is low to high, x, y, z
                // UV coordiantes are 0,0 is top left of texture up or facing north looking at face for T/B
                // faceID is used for TBO lookups
                // see RenderChunk for more info on how thats used
                // to draw, you need to setup the VAO
                // glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0)
                glNamedBufferStorage(cubeBuffer, new float[]{
                        // x, y, z, u, v, faceID/vertexID

                        // west face
                        0, 0, 0, 0, 1, 0x00,
                        0, 1, 0, 0, 0, 0x10,
                        0, 0, 1, 1, 1, 0x20,
                        0, 1, 1, 1, 0, 0x30,

                        // east face
                        1, 0, 0, 1, 1, 0x01,
                        1, 1, 0, 1, 0, 0x11,
                        1, 0, 1, 0, 1, 0x21,
                        1, 1, 1, 0, 0, 0x31,

                        // bottom face
                        0, 0, 0, 0, 1, 0x02,
                        1, 0, 0, 1, 1, 0x12,
                        0, 0, 1, 0, 0, 0x22,
                        1, 0, 1, 1, 0, 0x32,

                        // top face
                        0, 1, 0, 0, 0, 0x03,
                        1, 1, 0, 1, 0, 0x13,
                        0, 1, 1, 0, 1, 0x23,
                        1, 1, 1, 1, 1, 0x33,

                        // north face
                        0, 0, 0, 1, 1, 0x04,
                        1, 0, 0, 0, 1, 0x14,
                        0, 1, 0, 1, 0, 0x24,
                        1, 1, 0, 0, 0, 0x34,

                        // south face
                        0, 0, 1, 0, 1, 0x05,
                        1, 0, 1, 1, 1, 0x15,
                        0, 1, 1, 0, 0, 0x25,
                        1, 1, 1, 1, 0, 0x35,

                }, 0);
                // elements for it all in CCW order
                glNamedBufferStorage(cubeElementBuffer, new int[]{
                        3, 1, 0, 0, 2, 3,
                        1 + 4, 3 + 4, 2 + 4, 2 + 4, 0 + 4, 1 + 4,
                        3 + 8, 2 + 8, 0 + 8, 0 + 8, 1 + 8, 3 + 8,
                        1 + 12, 0 + 12, 2 + 12, 2 + 12, 3 + 12, 1 + 12,
                        2 + 16, 3 + 16, 1 + 16, 1 + 16, 0 + 16, 2 + 16,
                        3 + 20, 2 + 20, 0 + 20, 0 + 20, 1 + 20, 3 + 20,
                }, 0);

                layerCullVAO = glCreateVertexArrays();
                layerCullBuffer = glCreateBuffers();

                glNamedBufferStorage(layerCullBuffer, new float[]{
                        -1024, 0, -1024,
                        1024, 0, -1024,
                        -1024, 0, 1024,
                        -1024, 0, 1024,
                        1024, 0, -1024,
                        1024, 0, 1024,

                        -1024, 16, -1024,
                        -1024, 16, 1024,
                        1024, 16, -1024,
                        1024, 16, -1024,
                        -1024, 16, 1024,
                        1024, 16, 1024,
                }, 0);

                glVertexArrayVertexBuffer(layerCullVAO, 0, layerCullBuffer, 0, 12);
                glVertexArrayAttribBinding(layerCullVAO, 0, 0);
                glVertexArrayAttribFormat(layerCullVAO, 0, 3, GL_FLOAT, false, 0);
                glEnableVertexArrayAttrib(layerCullVAO, 0);
            }
            break;
            case GL21:
                throw new IllegalStateException("GL21 not supported yet");
            default:
                throw new IllegalStateException("Unknown operation mode");
        }
        RenderChunk testChunk = new RenderChunk(new Vector3i(0, 0, 0));
        chunks.get(0).add(testChunk);
//        BlockRenderInfo info = new BlockRenderInfo();
//        info.location = new Vector3i(0, 0, 0);
//        info.x = 0;
//        info.y = 0;
//        info.z = 0;
//        testChunk.addBlocks(info);
        testBlock();
    }

    public static void testBlock() {
        BlockRenderInfo info = new BlockRenderInfo();
        info.x = info.z = 0;
        {
            info.location = new Vector3i(0, 2, 0);
            info.y = 2;
            info.textureOffsetRotation3 = 4;
        }
        chunks.get(0).get(0).setBlocks(info);
        {
            info.location = new Vector3i(0, 4, 0);
            info.y = 4;
            info.textureOffsetRotation3 = 4;
        }
        chunks.get(0).get(0).setBlocks(info);
        {
            info.location = new Vector3i(0, 6, 0);
            info.y = 6;
            info.textureOffsetRotation3 = 4;
        }
        chunks.get(0).get(0).setBlocks(info);
        chunks.get(0).get(0).removeBlocks(new Vector3i(0, 2, 0));
//        chunks.get(0).get(0).removeBlocks(new Vector3i(0, 6, 0));
    }

    public static void shutdown() {
        cullQueryProgram = null;
        blockProgram = null;
        chunks.clear();
        glDeleteBuffers(layerCullBuffer);
        glDeleteVertexArrays(layerCullVAO);
        glDeleteBuffers(cubeBuffer);
        glDeleteBuffers(cubeElementBuffer);
        glDeleteBuffers(chunkCullQueryCubeBuffer);
        glDeleteBuffers(chunkCullQueryCubeElementBuffer);
        glDeleteVertexArrays(chunkCullVAO);
        glDeleteQueries(layerQueries);
    }

    public static void draw() {
        assert Minecraft.getInstance().world != null;
        IProfiler iprofiler = Minecraft.getInstance().world.getProfiler();
        iprofiler.startSection("chunks");

        iprofiler.startSection("state query");

        glGetFloatv(GL_CURRENT_COLOR, fogColorBuffer);

        int fogStart = glGetInteger(GL_FOG_START);
        int fogEnd = glGetInteger(GL_FOG_END);
        float fogScale = 1.0f / (fogEnd - fogStart);

        iprofiler.endStartSection("state setup");

//        WorldRenderer.getCombinedLight()

//        float lightmapX = Minecraft.getInstance().world.getLightManager().getLightEngine(LightType.BLOCK).getLightFor(new BlockPos(0, 2, -1));
//        lightmapX /= 16;
//        float lightmapY = Minecraft.getInstance().world.getLightManager().getLightEngine(LightType.SKY).getLightFor(new BlockPos(0, 2, -1));
//        lightmapY /= 16;
//        lightmapY = 1 - lightmapY;


//        Minecraft.getInstance().world.


        {
            cullQueryProgram.bind();
            glUniformMatrix4fv(1, false, modelViewProjectionMatrixBuffer);

            blockProgram.bind();

            glUniform1i(1, 12); // textureIndexRotationBuffer
            glUniform1i(2, 13); // lightmap buffer

            TextureRegistry.bind(3);
            TextureRegistry.setupUniforms(3, 3, 4, 8);

            // this fucker yeets GL_TEXTURE2 from me, *soooo* thats nice...
            Minecraft.getInstance().gameRenderer.getLightTexture().enableLightmap();
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glUniform1i(12, 2); // lightmap texture

            glUniform4fv(13, fogColorBuffer);
            glUniform2f(14, fogScale, fogEnd);

            projectionMatrixBuffer.rewind();
            glUniformMatrix4fv(15, false, projectionMatrixBuffer);
            modelVewMatrixBuffer.rewind();
            glUniformMatrix4fv(19, false, modelVewMatrixBuffer);


            layerMinYVector.set(0, -1, -1, 1);
            layerMinYVector.mul(inverseModelViewProjectionMatrix);
            minLayerY = layerMinYVector.y;

            layerMaxYVector.set(0, 1, -1, 1);
            layerMaxYVector.mul(inverseModelViewProjectionMatrix);
            maxLayerY = layerMaxYVector.y;

            int firstLayer = (int) (playerPosition.y / 16);
            firstLayer = Math.min(Math.max(firstLayer, 0), 16);

            // before anything else, draw the first chunk
            // occlusion testing will fail, so force render it
            int playerFlooredX = (int) (playerPosition.x / 16);
            playerFlooredX -= playerPosition.x < 0 ? 1 : 0;
            int playerFlooredZ = (int) (playerPosition.z / 16);
            playerFlooredZ -= playerPosition.z < 0 ? 1 : 0;

            iprofiler.endStartSection("draw");
            for (RenderChunk renderChunk : chunks.get(firstLayer)) {
                if (renderChunk.position.x == playerFlooredX && renderChunk.position.z == playerFlooredZ) {
                    glEnable(GL_CULL_FACE);
                    renderChunk.draw(false);
                    break;
                }
            }

            queryLayer(firstLayer);
            drawLayer(firstLayer);
            int upperLayer = firstLayer + 1;
            int lowerLayer = firstLayer - 1;
            queryLayer(upperLayer);
            queryLayer(lowerLayer);

            // query/draw functions take care of out of bounds layers
            while (upperLayer < 16 || lowerLayer >= 0) {
                drawLayer(upperLayer);
                queryLayer(upperLayer + 1);
                drawLayer(lowerLayer);
                queryLayer(lowerLayer - 1);
                upperLayer++;
                lowerLayer--;
            }
        }

//        iprofiler.endStartSection("occlusionTest");
        cullQueryProgram.bind();
        glBindVertexArray(chunkCullVAO);

        glUniformMatrix4fv(1, false, modelViewProjectionMatrixBuffer);
        playerOffest.set(0, 0, 0); // chunkPos
        playerOffest.sub(playerPosition);
        glUniform3f(0, (float) playerOffest.x, (float) playerOffest.y, (float) playerOffest.z);
//        glUniform3f(0, 0, 0, 0);

//        glDrawElements(GL_TRIANGLE_STRIP, 14, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);

        blockProgram.bind();

        playerOffest.set(0, 0, 0); // chunkPos
        playerOffest.sub(playerPosition);
        glUniform3f(0, (float) playerOffest.x, (float) playerOffest.y, (float) playerOffest.z);



        glBindBuffer(GL_ARRAY_BUFFER, 0);
        iprofiler.endSection();
        Minecraft.getInstance().gameRenderer.getLightTexture().disableLightmap();
        iprofiler.endSection();
    }


    static final int[] layerQueries = new int[16];

    private static void queryLayer(int layer) {
        if (layer > 15 || layer < 0) {
            return;
        }
        if (!((layer + 1) * 16 > minLayerY) && !(layer * 16 < maxLayerY)) {
            return;
        }
        glDisable(GL_CULL_FACE);

        glDepthMask(false);
        glColorMask(false, false, false, false);

        glBeginQuery(GL_ANY_SAMPLES_PASSED, layerQueries[layer]);

        cullQueryProgram.bind();
        playerOffest.set(0, layer * 16, 0).sub(playerPosition);
        glUniform3f(0, (float) playerOffest.x % 1, (float) playerOffest.y, (float) playerOffest.z % 1);
        glBindVertexArray(layerCullVAO);
        glDrawArrays(GL_TRIANGLES, 0, 12);

        glEndQuery(GL_ANY_SAMPLES_PASSED);

        glColorMask(true, true, true, true);
        glDepthMask(true);
    }


    private static final ArrayList<RenderChunk> chunksToRender = new ArrayList<>(16);

    private static void drawLayer(int layer) {
        if (layer > 15 || layer < 0) {
            return;
        }
        if (!((layer + 1) * 16 > minLayerY) && !(layer * 16 < maxLayerY)) {
            return;
        }

        chunksToRender.clear();
        chunks.get(layer).forEach(chunk -> {
            if (chunk.clip()) {
                chunksToRender.add(chunk);
            }
        });

        glDepthMask(false);
        glColorMask(false, false, false, false);

        // TODO: benchmark GL_QUERY_WAIT vs GL_QUERY_NO_WAIT
        // im guessing it will make basically no difference
        glBeginConditionalRender(layerQueries[layer], GL_QUERY_WAIT);
        cullQueryProgram.bind();
        glBindVertexArray(chunkCullVAO);
        glDisable(GL_CULL_FACE);
        chunksToRender.forEach(RenderChunk::query);
        glEnable(GL_CULL_FACE);

        glEndConditionalRender();

        glColorMask(true, true, true, true);
        glDepthMask(true);

        blockProgram.bind();
        chunksToRender.forEach(RenderChunk::draw);
    }
}

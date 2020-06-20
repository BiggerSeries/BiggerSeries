package net.roguelogix.phosphophyllite.quartz_old.internal.chunk;

import net.roguelogix.phosphophyllite.quartz_old.internal.OperationMode;
import net.roguelogix.phosphophyllite.quartz_old.internal.blocks.BlockRenderInfo;
import net.roguelogix.phosphophyllite.threading.Event;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Math.*;
import static net.roguelogix.phosphophyllite.quartz_old.internal.Renderer.*;
import static net.roguelogix.phosphophyllite.quartz_old.internal.chunk.ChunkRendering.cubeBuffer;
import static net.roguelogix.phosphophyllite.quartz_old.internal.chunk.ChunkRendering.cubeElementBuffer;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryUtil.memAddress;

public class RenderChunk {
    final Vector3i position = new Vector3i();

    private final HashMap<Vector3i, Integer> blockBufferPositions = new HashMap<>();
    private final ArrayList<Vector3i> bufferPositions = new ArrayList<>(4096);
    private int nextBufferPosition = 0;
    private int bufferSize = 0;
    private final IntBuffer positionsBufferData = BufferUtils.createIntBuffer(4096 * 3);
    private final int positionsBuffer;
    private final IntBuffer textureBufferData = BufferUtils.createIntBuffer(4096 * 6);
    private final int textureBuffer;
    private final int textureBufferTexure;
    private final ShortBuffer lightingBufferData = BufferUtils.createShortBuffer(4096 * 6 * 5);
    private final int lightingBuffer;
    private final int lightingBufferTexture;
    private final IntBuffer drawCommandBufferData = BufferUtils.createIntBuffer(5);
    private final int drawCommandBuffer;
    private int instanceCount = 0;
    private long updateFence;
    // not final because the primary work queue has to make it
    private int drawVAO;
    private int cullQuery;

    private Event setupDone;

    public RenderChunk(Vector3i position) {
        position.set(position);
        for (int i = 0; i < 4096; i++) {
            bufferPositions.add(null);
        }
        switch (OperationMode.mode()) {
            case GL45: {
                positionsBuffer = glCreateBuffers();

                textureBuffer = glCreateBuffers();
                textureBufferTexure = glCreateTextures(GL_TEXTURE_BUFFER);
                glTextureBuffer(textureBufferTexure, GL_R32UI, textureBuffer);

                lightingBuffer = glCreateBuffers();
                lightingBufferTexture = glCreateTextures(GL_TEXTURE_BUFFER);
                glTextureBuffer(lightingBufferTexture, GL_R16UI, lightingBuffer);

                drawCommandBuffer = glCreateBuffers();
                drawCommandBufferData.put(72);
                drawCommandBufferData.put(0);
                drawCommandBufferData.put(0);
                drawCommandBufferData.put(0);
                drawCommandBufferData.put(0);
                drawCommandBufferData.rewind();
                glNamedBufferStorage(drawCommandBuffer, drawCommandBufferData, GL_DYNAMIC_STORAGE_BIT);

                setupDone = primaryWorkQueue.enqueue(() -> {
                    drawVAO = glCreateVertexArrays();
                    cullQuery = glCreateQueries(GL_ANY_SAMPLES_PASSED);

                    glVertexArrayElementBuffer(drawVAO, cubeElementBuffer);

                    glVertexArrayVertexBuffer(drawVAO, 0, cubeBuffer, 0, 24);
                    glVertexArrayAttribBinding(drawVAO, 0, 0);
                    glVertexArrayAttribBinding(drawVAO, 1, 0);
                    glVertexArrayAttribBinding(drawVAO, 2, 0);

                    glVertexArrayAttribFormat(drawVAO, 0, 3, GL_FLOAT, false, 0);
                    glVertexArrayAttribFormat(drawVAO, 1, 2, GL_FLOAT, false, 12);
                    glVertexArrayAttribFormat(drawVAO, 2, 1, GL_FLOAT, false, 20);

                    glVertexArrayVertexBuffer(drawVAO, 1, positionsBuffer, 0, 12);
                    glVertexArrayBindingDivisor(drawVAO, 1, 1);
                    glVertexArrayAttribBinding(drawVAO, 3, 1);
                    glVertexArrayAttribIFormat(drawVAO, 3, 3, GL_INT, 0);

                    glEnableVertexArrayAttrib(drawVAO, 0);
                    glEnableVertexArrayAttrib(drawVAO, 1);
                    glEnableVertexArrayAttrib(drawVAO, 2);
                    glEnableVertexArrayAttrib(drawVAO, 3);
                });

            }
            break;
            case GL21:
                throw new IllegalStateException("GL21 not supported yet");
            default:
                throw new IllegalStateException("Unknown operation mode");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        primaryWorkQueue.enqueue(() -> {
            glDeleteVertexArrays(drawVAO);
            glDeleteQueries(cullQuery);
        });
        secondaryWorkQueue.enqueue(() -> {
            glDeleteBuffers(positionsBuffer);
            glDeleteBuffers(textureBuffer);
            glDeleteBuffers(lightingBuffer);
            glDeleteSync(updateFence);
        });
    }

    private static int nextPowerOfTwo(int v) {
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v++;
        return v;
    }

    void setBlocks(BlockRenderInfo... blocks) {
        if (blocks.length == 0) {
            return;
        }
        //TODO: switch for GL21
        synchronized (blockBufferPositions) {
            for (BlockRenderInfo block : blocks) {
                int pos = blockBufferPositions.computeIfAbsent(new Vector3i(block.location), i -> {
                    instanceCount++;
                    return nextBufferPosition++;
                });
                bufferPositions.set(pos, block.location);
            }
            int minBufferPosition = 4096;
            int maxBufferPosition = 0;

            for (BlockRenderInfo block : blocks) {
                int bufferPosition = blockBufferPositions.get(block.location);
                minBufferPosition = min(minBufferPosition, bufferPosition);
                maxBufferPosition = max(maxBufferPosition, bufferPosition);
                positionsBufferData.put(bufferPosition * 3, block.x);
                positionsBufferData.put(bufferPosition * 3 + 1, block.y);
                positionsBufferData.put(bufferPosition * 3 + 2, block.z);

                textureBufferData.put(bufferPosition * 6, block.textureOffsetRotation0);
                textureBufferData.put(bufferPosition * 6 + 1, block.textureOffsetRotation1);
                textureBufferData.put(bufferPosition * 6 + 2, block.textureOffsetRotation2);
                textureBufferData.put(bufferPosition * 6 + 3, block.textureOffsetRotation3);
                textureBufferData.put(bufferPosition * 6 + 4, block.textureOffsetRotation4);
                textureBufferData.put(bufferPosition * 6 + 5, block.textureOffsetRotation5);

                lightingBufferData.put(bufferPosition * 24, block.lightmap00);
                lightingBufferData.put(bufferPosition * 24 + 1, block.lightmap01);
                lightingBufferData.put(bufferPosition * 24 + 2, block.lightmap02);
                lightingBufferData.put(bufferPosition * 24 + 3, block.lightmap03);
                lightingBufferData.put(bufferPosition * 24 + 4, block.lightmap10);
                lightingBufferData.put(bufferPosition * 24 + 5, block.lightmap11);
                lightingBufferData.put(bufferPosition * 24 + 6, block.lightmap12);
                lightingBufferData.put(bufferPosition * 24 + 7, block.lightmap13);
                lightingBufferData.put(bufferPosition * 24 + 8, block.lightmap20);
                lightingBufferData.put(bufferPosition * 24 + 9, block.lightmap21);
                lightingBufferData.put(bufferPosition * 24 + 10, block.lightmap22);
                lightingBufferData.put(bufferPosition * 24 + 11, block.lightmap23);
                lightingBufferData.put(bufferPosition * 24 + 12, block.lightmap30);
                lightingBufferData.put(bufferPosition * 24 + 13, block.lightmap31);
                lightingBufferData.put(bufferPosition * 24 + 14, block.lightmap32);
                lightingBufferData.put(bufferPosition * 24 + 15, block.lightmap33);
                lightingBufferData.put(bufferPosition * 24 + 16, block.lightmap40);
                lightingBufferData.put(bufferPosition * 24 + 17, block.lightmap41);
                lightingBufferData.put(bufferPosition * 24 + 18, block.lightmap42);
                lightingBufferData.put(bufferPosition * 24 + 19, block.lightmap43);
                lightingBufferData.put(bufferPosition * 24 + 20, block.lightmap50);
                lightingBufferData.put(bufferPosition * 24 + 21, block.lightmap51);
                lightingBufferData.put(bufferPosition * 24 + 22, block.lightmap52);
                lightingBufferData.put(bufferPosition * 24 + 23, block.lightmap53);
            }

            if (bufferSize < blockBufferPositions.size()) {
                // time to expannd the buffer on the GPU!
                int newBufferSize = nextPowerOfTwo(blockBufferPositions.size());
                // yes i know im using the unsafe methods
                // LWJGL doesnt give me the option to upload only *part* of the buffer
                // but the C API does
                nglNamedBufferData(positionsBuffer, newBufferSize * 3 * 4, memAddress(positionsBufferData), GL_STATIC_DRAW);
                nglNamedBufferData(textureBuffer, newBufferSize * 6 * 4, memAddress(textureBufferData), GL_STATIC_DRAW);
                nglNamedBufferData(lightingBuffer, newBufferSize * 6 * 8, memAddress(lightingBufferData), GL_STATIC_DRAW);
                bufferSize = newBufferSize;
            } else {
                int updatedPositionsCount = maxBufferPosition - minBufferPosition + 1;
                assert updatedPositionsCount > 0;
                nglNamedBufferSubData(positionsBuffer, (minBufferPosition * 12), updatedPositionsCount * 12, (minBufferPosition * 12) + memAddress(positionsBufferData));
                nglNamedBufferSubData(textureBuffer, (minBufferPosition * 24), updatedPositionsCount * 24, (minBufferPosition * 24) + memAddress(textureBufferData));
                nglNamedBufferSubData(lightingBuffer, (minBufferPosition * 48), updatedPositionsCount * 48, (minBufferPosition * 48) + memAddress(lightingBufferData));
            }
            drawCommandBufferData.put(1, instanceCount);
            glNamedBufferSubData(drawCommandBuffer, 0, drawCommandBufferData);
        }
    }

    void removeBlocks(Vector3i... positions) {
        if (positions.length == 0) {
            return;
        }
        synchronized (blockBufferPositions) {
            drawCommandBufferData.put(1, max(0, instanceCount - positions.length));
            glNamedBufferSubData(drawCommandBuffer, 0, drawCommandBufferData);
            for (Vector3i pos : positions) {
                Integer bufferLocation = blockBufferPositions.remove(pos);
                if (bufferLocation == null) {
                    // no gots, try again
                    return;
                }
                instanceCount--;
                nextBufferPosition = instanceCount;
                if (bufferLocation == instanceCount) {
                    // we were already the last one
                    continue;
                }
                // ok, move the one on the end to the new position
                Vector3i lastInstancePosition = bufferPositions.get(instanceCount);
                bufferPositions.set(bufferLocation, lastInstancePosition);
                blockBufferPositions.put(lastInstancePosition, bufferLocation);
                glCopyNamedBufferSubData(positionsBuffer, positionsBuffer, instanceCount * 12, bufferLocation * 12, 12);
                glCopyNamedBufferSubData(textureBuffer, textureBuffer, instanceCount * 24, bufferLocation * 24, 24);
                glCopyNamedBufferSubData(lightingBuffer, lightingBuffer, instanceCount * 48, bufferLocation * 48, 48);
                // make sure CPU side buffer mirrors GPU side buffer
                // its 84 bytes total, its not going to get much faster1
                for (int i = 0; i < 3; i++) {
                    positionsBufferData.put(bufferLocation * 3 + i, positionsBufferData.get(instanceCount * 3 + i));
                }
                for (int i = 0; i < 6; i++) {
                    textureBufferData.put(bufferLocation * 6 + i, textureBufferData.get(instanceCount * 6 + i));
                }
                for (int i = 0; i < 24; i++) {
                    lightingBufferData.put(bufferLocation * 24 + i, lightingBufferData.get(instanceCount * 24 + i));
                }
            }
            // if sufficiently smaller, go ahead and shrink the buffers
            // this is to avoid copying a large buffer every time you place/break one block
            // minimum blocks broken to get this to size down is 8
            if (instanceCount != 0 && instanceCount < bufferSize / 4) {
                int newBufferSize = nextPowerOfTwo(blockBufferPositions.size());
                // yes i know im using the unsafe methods
                // LWJGL doesnt give me the option to upload only *part* of the buffer
                // but the C API does
                nglNamedBufferData(positionsBuffer, newBufferSize * 12, memAddress(positionsBufferData), GL_STATIC_DRAW);
                nglNamedBufferData(textureBuffer, newBufferSize * 24, memAddress(textureBufferData), GL_STATIC_DRAW);
                nglNamedBufferData(lightingBuffer, newBufferSize * 48, memAddress(lightingBufferData), GL_STATIC_DRAW);
                bufferSize = newBufferSize;
            }
            drawCommandBufferData.put(1, instanceCount);
            glNamedBufferSubData(drawCommandBuffer, 0, drawCommandBufferData);
        }
    }

    /**
     * Draws the chunk assuming the current matrix state does all transforms except the world position shift
     */
    public void draw() {
        draw(true);
    }

    public void draw(boolean conditionallyRender) {
        if (setupDone != null) {
            if (!setupDone.ready()) {
                return;
            }
            setupDone = null;
        }
        glActiveTexture(GL_TEXTURE12);
        glBindTexture(GL_TEXTURE_BUFFER, textureBufferTexure);
        glActiveTexture(GL_TEXTURE13);
        glBindTexture(GL_TEXTURE_BUFFER, lightingBufferTexture);

        playerOffest.set(position).sub(playerPosition);
        glUniform3f(0, (float) playerOffest.x, (float) playerOffest.y, (float) playerOffest.z);
        glBindVertexArray(drawVAO);
        if (conditionallyRender) {
            glBeginConditionalRender(cullQuery, GL_QUERY_WAIT);
        }
        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, drawCommandBuffer);
        glDrawElementsIndirect(GL_TRIANGLES, GL_UNSIGNED_INT, 0);
        if (conditionallyRender) {
            glEndConditionalRender();
        }
    }

    /**
     * runs the chunk occlusion query assuming the current matrix state does all transforms except the world position shift
     * <p>
     * WARNING: assumes masking is set already
     */
    public void query() {
        glBeginQuery(GL_ANY_SAMPLES_PASSED, cullQuery);

        playerOffest.set(position).sub(playerPosition);
        glUniform3f(0, (float) playerOffest.x, (float) playerOffest.y, (float) playerOffest.z);
        glDrawElements(GL_TRIANGLE_STRIP, 14, GL_UNSIGNED_INT, 0);

        glEndQuery(GL_ANY_SAMPLES_PASSED);
    }

    private static final Vector4f chunkClipVector = new Vector4f();
    private static final Vector4f chunkClipVectorMin = new Vector4f();
    private static final Vector4f chunkClipVectorMax = new Vector4f();

    /**
     * oversized AABB clip check
     *
     * @return true if it should be rendered
     */
    public boolean clip() {
        playerOffest.set(position).sub(playerPosition);

        chunkClipVector.set(playerOffest.x, playerOffest.y, playerOffest.z, 1);
        chunkClipVector.mul(modelViewProjectionMatrix);
        chunkClipVector.div(abs(chunkClipVector.w));
        chunkClipVectorMin.set(chunkClipVector);
        chunkClipVectorMax.set(chunkClipVector);

        for (int i = 1; i < 8; i++) {
            chunkClipVector.set(playerOffest.x, playerOffest.y, playerOffest.z, 1);
            chunkClipVector.add(((i & 1) * 16), ((i & 2) * 8), ((i & 4) * 4), 0);
            chunkClipVector.mul(modelViewProjectionMatrix);
            chunkClipVector.div(abs(chunkClipVector.w));
            chunkClipVectorMin.min(chunkClipVector);
            chunkClipVectorMax.max(chunkClipVector);
        }

        return (chunkClipVectorMin.x <= 1 && chunkClipVectorMax.x >= -1 &&
                chunkClipVectorMin.y <= 1 && chunkClipVectorMax.y >= -1 &&
                chunkClipVectorMin.z <= 1 && chunkClipVectorMax.z >= -1
        );
    }
}
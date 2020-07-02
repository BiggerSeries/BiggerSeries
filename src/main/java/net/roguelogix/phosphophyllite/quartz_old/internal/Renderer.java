package net.roguelogix.phosphophyllite.quartz_old.internal;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.roguelogix.phosphophyllite.quartz_old.internal.chunk.ChunkRendering;
import net.roguelogix.phosphophyllite.quartz_old.internal.shaders.ShaderRegistry;
import net.roguelogix.phosphophyllite.quartz_old.internal.textures.TextureRegistry;
import net.roguelogix.phosphophyllite.threading.WorkQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;

import static net.roguelogix.phosphophyllite.Phosphophyllite.modid;
import static net.roguelogix.phosphophyllite.quartz_old.internal.GLConstants.MAX_TEXTURE_UNITS;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_BINDING_2D_ARRAY;

/**
 * Most of the things in here are public, and not final
 * and yes, you can fuck with them, please dont,
 * dont be a dick
 */
public class Renderer {
    /*
     * Main TODO list
     *       - Find a name
     *       - Mipmapping
     *       - Anisotropic Filtering
     *       - Ambient Occlusion
     *         - Diffuse light multiplier, 1.0 no blocks, .8 one block, .6 two blocks, .4 three blocks
     *       - Smooth light levels
     *       - Move to a 2D atlasing system
     *         - investigate a dynamic texture size atlas (i dont think i have the space available, but we will see
     *       - API for registering blocks to be rendered
     *       - Watching for chunk loads/unloads to add/remove blocks
     *       - Watching for lighting updates
     *       - Watching for onBlockPlaced and onBlockRemoved, or whatever they are called
     *       - GLSL 450 block shader new input packing
     *       - Render data packer
     *       - Face culling (shaders support face discarding)
     *       - GL3.3
     *       - GL2.1
     */
    
    public static final Logger LOGGER = LogManager.getLogger("Phosphophyllite/Quartz");
    
    public static final boolean useSecondaryThread = false;
    
    // run on the main OpenGL thread (Minecraft's)
    public static final WorkQueue primaryWorkQueue = new WorkQueue();
    // run on the secondary OpenGL thread (mine), context shared with MC's
    public static final WorkQueue secondaryWorkQueue = new WorkQueue();
    // run on daemon threads
    public static final WorkQueue tertiaryWorkQueue = new WorkQueue().addProcessingThreads(4);
    
    public static final Matrix4f projectionMatrix = new Matrix4f();
    public static final FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
    
    public static final Matrix4f modelVewMatrix = new Matrix4f();
    public static final FloatBuffer modelVewMatrixBuffer = BufferUtils.createFloatBuffer(16);
    
    public static final Matrix4f modelViewProjectionMatrix = new Matrix4f();
    public static final FloatBuffer modelViewProjectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
    public static final Matrix4f inverseModelViewProjectionMatrix = new Matrix4f();
    
    public static final Vector3d playerPosition = new Vector3d();
    public static final Vector3d playerOffset = new Vector3d();
    private static Thread secondaryThread;
    private static long secondaryContextHandle = 0;
    
    static void startup() {
        saveMCState();
        LOGGER.info("Starting up!");
        
        OperationMode.init();
        GLConstants.init();
        
        boundTextures1D = new int[MAX_TEXTURE_UNITS];
        boundTextures2D = new int[MAX_TEXTURE_UNITS];
        boundTextures2DArrays = new int[MAX_TEXTURE_UNITS];
        boundTextures3D = new int[MAX_TEXTURE_UNITS];
        
        // init secondary thread
        LOGGER.info("Creating secondary context");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        // TODO: error check this
        secondaryContextHandle = glfwCreateWindow(10, 10, "PhosphophylliteBackgroundRendering", 0, Minecraft.getInstance().getMainWindow().getHandle());
        LOGGER.info("Created secondary context");
        LOGGER.info("Starting secondary thread");
        secondaryThread = new Thread(Renderer::secondThreadFunc);
        secondaryThread.setName("BiggerCoreRenderer");
        if (useSecondaryThread) {
            secondaryThread.start();
        }
        LOGGER.info("Started secondary thread");
        
        
        ShaderRegistry.startup();
        TextureRegistry.startup();
        TextureRegistry.getOrRegister(new ResourceLocation(modid, "textures/test_texture.png"));
        
        ChunkRendering.startup();
        
        
        LOGGER.info("Running primary startup queue");
        primaryWorkQueue.runAll();
        LOGGER.info("Started!");
        loadMCState();
    }
    
    static void shutdown() {
        saveMCState();
        LOGGER.info("Shutting down!");
        ChunkRendering.shutdown();
        
        // secondary thread shutdown
        glfwSetWindowShouldClose(secondaryContextHandle, true);
        try {
            secondaryThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        TextureRegistry.shutdown();
        ShaderRegistry.shutdown();
        
        // RAII cleanup, or trying at least
        System.runFinalization();
        secondaryWorkQueue.runAll();
        primaryWorkQueue.runAll();
        
        LOGGER.info("Shutdown!");
        loadMCState();
    }
    
    static void secondThreadFunc() {
        LOGGER.info("Secondary thread started!");
        glfwMakeContextCurrent(secondaryContextHandle);
        GL.createCapabilities();
        glfwSwapInterval(1);
        while (!glfwWindowShouldClose(secondaryContextHandle)) {
            if (useSecondaryThread) {
                secondaryWorkQueue.runAll();
            }
            glfwSwapBuffers(secondaryContextHandle);
        }
        glfwMakeContextCurrent(0);
        LOGGER.info("Secondary thread shutdown!");
    }
    
    static void draw() {
        
        WorldManager.tick();
        
        glClear(GL_COLOR_BUFFER_BIT);
        assert Minecraft.getInstance().world != null;
        IProfiler iprofiler = Minecraft.getInstance().world.getProfiler();
        iprofiler.endStartSection("biggercorerender");
        iprofiler.startSection("savestate");
        saveMCState();
        {
            iprofiler.endStartSection("setup state");
            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);
            glDisableClientState(GL_VERTEX_ARRAY);
            
            projectionMatrixBuffer.rewind();
            glGetFloatv(GL_PROJECTION_MATRIX, projectionMatrixBuffer);
            projectionMatrixBuffer.rewind();
            projectionMatrix.set(projectionMatrixBuffer);
            
            
            ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.rotate(Vector3f.XP.rotationDegrees(renderInfo.getPitch()));
            matrixStack.rotate(Vector3f.YP.rotationDegrees(renderInfo.getYaw() + 180.0f));
            
            modelVewMatrixBuffer.rewind();
            matrixStack.getLast().getMatrix().write(modelVewMatrixBuffer);
            modelVewMatrixBuffer.rewind();
            modelVewMatrix.set(modelVewMatrixBuffer);
            
            modelViewProjectionMatrix.set(projectionMatrix).mul(modelVewMatrix);
            modelViewProjectionMatrixBuffer.rewind();
            modelViewProjectionMatrix.get(modelViewProjectionMatrixBuffer);
            
            inverseModelViewProjectionMatrix.set(modelViewProjectionMatrix);
            inverseModelViewProjectionMatrix.invert();
            
            Vec3d vec3d = renderInfo.getProjectedView();
            playerPosition.set(vec3d.x, vec3d.y, vec3d.z);
            
            iprofiler.endStartSection("draw");

//            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            ChunkRendering.draw();
//            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        
        iprofiler.endStartSection("work queue");
        primaryWorkQueue.runAll();
        if (!useSecondaryThread) {
            iprofiler.endStartSection("secondary work queue");
            secondaryWorkQueue.runAll();
        }
        iprofiler.endStartSection("loadstate");
        loadMCState();
        iprofiler.endSection();
        iprofiler.endStartSection("updatechunks");
    }
    
    private static int activeClientTexutre;
    private static int activeTexture;
    // they use the FFP, so its ok if i only save the amount the FFP can use
    private static int[] boundTextures1D;
    private static int[] boundTextures2D;
    private static int[] boundTextures2DArrays;
    private static int[] boundTextures3D;
    private static int activeProgram;
    
    private static void saveMCState() {
        activeClientTexutre = glGetInteger(GL_CLIENT_ACTIVE_TEXTURE);
        activeTexture = glGetInteger(GL_ACTIVE_TEXTURE);
        activeProgram = glGetInteger(GL_CURRENT_PROGRAM);
        for (int i = 0; i < MAX_TEXTURE_UNITS; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            boundTextures1D[i] = glGetInteger(GL_TEXTURE_BINDING_1D);
            boundTextures2D[i] = glGetInteger(GL_TEXTURE_BINDING_2D);
            boundTextures3D[i] = glGetInteger(GL_TEXTURE_BINDING_3D);
            if (OperationMode.mode() == OperationMode.GL45) {
                boundTextures2DArrays[i] = glGetInteger(GL_TEXTURE_BINDING_2D_ARRAY);
            }
        }
    }
    
    private static void loadMCState() {
        for (int i = 0; i < MAX_TEXTURE_UNITS; i++) {
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_1D, boundTextures1D[i]);
            glBindTexture(GL_TEXTURE_2D, boundTextures2D[i]);
            glBindTexture(GL_TEXTURE_3D, boundTextures3D[i]);
            if (OperationMode.mode() == OperationMode.GL45) {
                glBindTexture(GL_TEXTURE_2D_ARRAY, boundTextures2DArrays[i]);
            }
        }
        glUseProgram(activeProgram);
        glActiveTexture(activeTexture);
        glClientActiveTexture(activeClientTexutre);
    }
}

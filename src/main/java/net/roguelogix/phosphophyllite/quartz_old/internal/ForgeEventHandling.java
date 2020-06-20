package net.roguelogix.phosphophyllite.quartz_old.internal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.roguelogix.phosphophyllite.Phosphophyllite;
import net.roguelogix.phosphophyllite.quartz_old.internal.chunk.ChunkRendering;
import net.roguelogix.phosphophyllite.quartz_old.internal.overrides.QuartzChunkSection;
import net.roguelogix.phosphophyllite.quartz_old.internal.shaders.ShaderRegistry;

import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class ForgeEventHandling {

    public static void setupEvents() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeEventHandling::onClientSetup);
        MinecraftForge.EVENT_BUS.addListener(ForgeEventHandling::onRenderWorldLastEvent);
        MinecraftForge.EVENT_BUS.addListener(ForgeEventHandling::onChunkLoad);
        MinecraftForge.EVENT_BUS.addListener(ForgeEventHandling::onChunkUnload);
    }

    private static Field renderDispatcherField = null;
    private static ChunkRenderDispatcher renderDispatcher = null;

    private static Queue<Runnable> uploadTasks = null;

    public static void onClientSetup(final FMLClientSetupEvent e) {
        Field[] fields = Minecraft.getInstance().worldRenderer.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType().equals(ChunkRenderDispatcher.class)) {
                renderDispatcherField = field;
                renderDispatcherField.setAccessible(true);
                break;
            }
        }
        if (renderDispatcherField == null) {
            throw new IllegalStateException("Could not find WorldRenderer.renderDispatcher");
        }
        DeferredWorkQueue.runLater(() -> {
            try {
                Renderer.startup();
            } catch (Throwable t) {
                Renderer.shutdown();
                throw t;
            }
        });
    }

    private static int lastF5Code;

    public static void onRenderWorldLastEvent(final RenderWorldLastEvent e) {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        try {
            ChunkRenderDispatcher renderDispatcher = (ChunkRenderDispatcher) renderDispatcherField.get(Minecraft.getInstance().worldRenderer);
            if (ForgeEventHandling.renderDispatcher != renderDispatcher) {
                Field[] fields = renderDispatcher.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (field.getType().equals(Queue.class)) {
                        field.setAccessible(true);
                        Object queue = field.get(renderDispatcher);
                        if (queue.getClass().equals(ConcurrentLinkedQueue.class)) {
                            // trust me, not your IDE, this is fine
                            //noinspection unchecked
                            uploadTasks = (Queue<Runnable>) queue;
                            break;
                        }
                    }
                }
            }

        } catch (IllegalAccessException noSuchFieldException) {
            noSuchFieldException.printStackTrace();
        }

        uploadTasks.add(Renderer::draw);

        // its just for debugging purposes
        long window = Minecraft.getInstance().getMainWindow().getHandle();
        if (glfwGetKey(window, GLFW_KEY_KP_4) == GLFW_PRESS) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
        if (glfwGetKey(window, GLFW_KEY_KP_6) == GLFW_PRESS) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
        int F5Code = glfwGetKey(window, GLFW_KEY_KP_5);
        if (F5Code == GLFW_PRESS && lastF5Code == GLFW_RELEASE) {
            Phosphophyllite.LOGGER.info("Reloading Shaders");
            ShaderRegistry.reloadAll();
        }
        lastF5Code = F5Code;

        if (glfwGetKey(window, GLFW_KEY_KP_8) == GLFW_PRESS) {
            ChunkRendering.testBlock();
        }
    }

    public static void onChunkLoad(ChunkEvent.Load event) {
        IChunk iChunk = event.getChunk();
        IWorld world = iChunk.getWorldForge();
        if(world != null && world.isRemote()){
            // aight, client shit is safe now
            if(iChunk instanceof Chunk){
                Chunk chunk = (Chunk) iChunk;
                for (int i = 0; i < chunk.getSections().length; i++) {
                    chunk.getSections()[i] = new QuartzChunkSection(i * 16, chunk.getSections()[i]);
                }
                WorldManager.loadChunk(chunk);
            }
        }
    }

    public static void onChunkUnload(ChunkEvent.Unload event) {
        IChunk iChunk = event.getChunk();
        IWorld world = iChunk.getWorldForge();
        if(world != null && world.isRemote()){
            // aight, client shit is safe now
            if(iChunk instanceof Chunk){
                Chunk chunk = (Chunk) iChunk;
                WorldManager.unloadChunk(chunk);
            }
        }
    }

}

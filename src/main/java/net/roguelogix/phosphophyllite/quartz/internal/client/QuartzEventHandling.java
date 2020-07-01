package net.roguelogix.phosphophyllite.quartz.internal.client;

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

import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QuartzEventHandling {
    
    private static final ChunkRenderDispatcher renderDispatcher = null;
    private static Field renderDispatcherField = null;
    
    private static Queue<Runnable> uploadTasks = null;
    
    public static void onModLoad() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(QuartzEventHandling::onClientSetup);
        MinecraftForge.EVENT_BUS.addListener(QuartzEventHandling::onRenderWorldLastEvent);
        MinecraftForge.EVENT_BUS.addListener(QuartzEventHandling::onChunkLoad);
        MinecraftForge.EVENT_BUS.addListener(QuartzEventHandling::onChunkUnload);
    }
    
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
                QuartzRenderer.GLStartup();
            } catch (Throwable t) {
                QuartzRenderer.GLShutdown();
                throw t;
            }
        });
    }
    
    public static void onRenderWorldLastEvent(final RenderWorldLastEvent e) {
        try {
            ChunkRenderDispatcher renderDispatcher = (ChunkRenderDispatcher) renderDispatcherField.get(Minecraft.getInstance().worldRenderer);
            if (QuartzEventHandling.renderDispatcher != renderDispatcher) {
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
        
        uploadTasks.add(QuartzRenderer::draw);
    }
    
    public static void onChunkLoad(ChunkEvent.Load event) {
        IChunk iChunk = event.getChunk();
        IWorld world = iChunk.getWorldForge();
        if (world != null && world.isRemote()) {
            // aight, client shit is safe now
            if (iChunk instanceof Chunk) {
                Chunk chunk = (Chunk) iChunk;
                for (int i = 0; i < chunk.getSections().length; i++) {
//                    chunk.getSections()[i] = new QuartzChunkSection(i * 16, chunk.getSections()[i]);
                }
//                WorldManager.loadChunk(chunk);
            }
        }
    }
    
    public static void onChunkUnload(ChunkEvent.Unload event) {
    
    }
}

package net.roguelogix.biggerreactors;

import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;
import net.roguelogix.phosphophyllite.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(BiggerReactors.modid)
public class BiggerReactors {

    public static final String modid = "biggerreactors";

    private static final Logger LOGGER = LogManager.getLogger();
    public BiggerReactors() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onTextureStitch);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().register(RegistryEvents.class);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SuppressWarnings("unused")
    public static class RegistryEvents {
        @SuppressWarnings("unused")
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            Registry.registerBlocks(blockRegistryEvent);
        }

        @SuppressWarnings("unused")
        @SubscribeEvent
        public static void onItemRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
            Registry.registerItems(itemRegistryEvent);
        }

        @SuppressWarnings("unused")
        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> tileEntityTypeRegisteryEvent) {
            Registry.registerTileEntities(tileEntityTypeRegisteryEvent);
        }

        @SubscribeEvent
        public static void onModConfigEvent(final ModConfig.ModConfigEvent event) {
            System.out.println("CONFIG TIME!");
        }


    }

    public void onClientSetup(final FMLClientSetupEvent e) {
        Registry.onClientSetup(e);
    }

    public void onLoadComplete(final FMLLoadCompleteEvent e) { Registry.registerWorldGen(); }

    @SubscribeEvent
    public void onTextureStitch(final TextureStitchEvent.Pre event) {
        Registry.onTextureStitch(event);
    }

    public void onModelBake(final ModelBakeEvent event) {
        Registry.onModelBake(event);
    }

    public static final ArrayList<MultiblockController> controllersToTick = new ArrayList<>();
    public static final ArrayList<MultiblockTile> tilesToAttach = new ArrayList<>();

    // used to ensure i dont tick things twice
    private static long tick = 0;

    public static long tickNumber() {
        return tick;
    }

    public static long lastTime = 0;

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent e) {
        if (e.world.isRemote) {
            return;
        }
        long timeNow = System.nanoTime();
        float ms = (float) (timeNow - lastTime) / 1_000_000f;
        if (ms > 50) {
            System.out.println("Over tick time! " + (ms));
        }
        lastTime = timeNow;
        tick++;
        ArrayList<MultiblockController> controllersToTick = new ArrayList<>(BiggerReactors.controllersToTick);
        ArrayList<MultiblockTile> tilesToAttach = new ArrayList<>(BiggerReactors.tilesToAttach);
        BiggerReactors.tilesToAttach.clear();
        tilesToAttach.sort(Comparator.comparing(TileEntity::getPos));
        for (MultiblockController controller : controllersToTick) {
            if (controller != null) {
                controller.update();
            }
        }
        for (MultiblockTile toAttach : tilesToAttach) {
            if (toAttach != null) {
                toAttach.attachToNeighbors();
            }
        }
    }
}

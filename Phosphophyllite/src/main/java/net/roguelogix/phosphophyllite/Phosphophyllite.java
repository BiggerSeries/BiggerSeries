package net.roguelogix.phosphophyllite;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;
import net.roguelogix.phosphophyllite.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;

@Mod(Phosphophyllite.modid)
public class Phosphophyllite {
    public static final String modid = "phosphophyllite";
    
    public static final Logger LOGGER = LogManager.getLogger("Phosphophyllite/Main");
    public static final ArrayList<MultiblockController> controllersToTick = new ArrayList<>();
    public static final ArrayList<MultiblockTile> tilesToAttach = new ArrayList<>();
    public static long lastTime = 0;
    // used to ensure i dont tick things twice
    private static long tick = 0;
    
    public Phosphophyllite() {
        Registry.onModLoad();
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public static long tickNumber() {
        return tick;
    }
    
    @SubscribeEvent
    void onWorldUnload(final WorldEvent.Unload worldUnloadEvent) {
        if (!worldUnloadEvent.getWorld().isRemote()) {
            ArrayList<MultiblockController> controllersToTick = new ArrayList<>(Phosphophyllite.controllersToTick);
            for (MultiblockController multiblockController : controllersToTick) {
                multiblockController.suicide();
            }
        }
    }
    
    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent e) {
        if (!e.side.isServer()) {
            return;
        }
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        long timeNow = System.nanoTime();
        float ms = (float) (timeNow - lastTime) / 1_000_000f;
        if (ms > 50) {
//            System.out.println("Over tick time! " + (ms));
        }
//        System.out.println(ms);
        lastTime = timeNow;
        tick++;
        ArrayList<MultiblockController> controllersToTick = new ArrayList<>(Phosphophyllite.controllersToTick);
        ArrayList<MultiblockTile> tilesToAttach = new ArrayList<>(Phosphophyllite.tilesToAttach);
        Phosphophyllite.tilesToAttach.clear();
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

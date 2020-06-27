package net.roguelogix.phosphophyllite;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.config.ConfigLoader;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockController;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockTile;
import net.roguelogix.phosphophyllite.quartz.Quartz;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;

@Mod(Phosphophyllite.modid)
public class Phosphophyllite {
    public static final String modid = "phosphophyllite";

    public static final Logger LOGGER = LogManager.getLogger("Phosphophyllite/Main");

    public Phosphophyllite() {
//        Quartz.onModLoad();
    }
    
    void onWorldUnload(final WorldEvent.Unload worldUnloadEvent){
        if(!worldUnloadEvent.getWorld().isRemote()){
        
        }
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
        if (e.side != LogicalSide.SERVER) {
            return;
        }
        if (e.phase!= TickEvent.Phase.END) {
            return;
        }
        long timeNow = System.nanoTime();
        float ms = (float) (timeNow - lastTime) / 1_000_000f;
        if (ms > 50) {
//            System.out.println("Over tick time! " + (ms));
        }
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

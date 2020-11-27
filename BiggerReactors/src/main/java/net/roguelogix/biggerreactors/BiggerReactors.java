package net.roguelogix.biggerreactors;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.roguelogix.biggerreactors.classic.machine.client.CyaniteReprocessorScreen;
import net.roguelogix.biggerreactors.classic.machine.containers.CyaniteReprocessorContainer;
import net.roguelogix.biggerreactors.classic.reactor.client.*;
import net.roguelogix.biggerreactors.classic.reactor.containers.*;
import net.roguelogix.biggerreactors.classic.turbine.client.BladeRenderer;
import net.roguelogix.biggerreactors.classic.turbine.client.TurbineCoolantPortScreen;
import net.roguelogix.biggerreactors.classic.turbine.client.TurbineScreen;
import net.roguelogix.biggerreactors.classic.turbine.containers.TurbineContainer;
import net.roguelogix.biggerreactors.classic.turbine.containers.TurbineCoolantPortContainer;
import net.roguelogix.biggerreactors.classic.turbine.tiles.TurbineRotorBearingTile;
import net.roguelogix.phosphophyllite.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(BiggerReactors.modid)
public class BiggerReactors {
    
    public static final String modid = "biggerreactors";
    
    public static final Logger LOGGER = LogManager.getLogger();
    
    public BiggerReactors() {
        Registry.onModLoad();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.addListener(this::onWorldLoad);
        MinecraftForge.EVENT_BUS.addListener(this::onRenderWorldLast);
    }
    
    public void onWorldLoad(final TagsUpdatedEvent.CustomTagTypes tagsUpdatedEvent) {
        Config.loadRegistries(tagsUpdatedEvent.getTagManager().getBlockTags());
    }
    
    public void onClientSetup(final FMLClientSetupEvent e) {
        // TODO: 6/28/20 Registry.
        //  Since I already have the comment here, also need to do a capability registry. I have a somewhat dumb capability to register.
        ScreenManager.registerFactory(CyaniteReprocessorContainer.INSTANCE,
                CyaniteReprocessorScreen::new);
        ScreenManager.registerFactory(ReactorContainer.INSTANCE,
                ReactorScreen::new);
        ScreenManager.registerFactory(ControlRodContainer.INSTANCE,
                ControlRodScreen::new);
        ScreenManager.registerFactory(ReactorCoolantPortContainer.INSTANCE,
                ReactorCoolantPortScreen::new);
        ScreenManager.registerFactory(ReactorAccessPortContainer.INSTANCE,
                ReactorAccessPortScreen::new);
        ScreenManager.registerFactory(RedstonePortContainer.INSTANCE,
                RedstonePortScreen::new);
        ScreenManager.registerFactory(TurbineContainer.INSTANCE,
                TurbineScreen::new);
        ScreenManager.registerFactory(TurbineCoolantPortContainer.INSTANCE,
                TurbineCoolantPortScreen::new);
        
        ClientRegistry.bindTileEntityRenderer(TurbineRotorBearingTile.TYPE, BladeRenderer::new);
    }
    
    public static long lastRenderTime = 0;
    
    public void onRenderWorldLast(RenderWorldLastEvent event){
        lastRenderTime = System.nanoTime();
    }
    
}

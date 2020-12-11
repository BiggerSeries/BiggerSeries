package net.roguelogix.biggerreactors;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.resources.DataPackRegistries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.roguelogix.biggerreactors.classic.machine.client.CyaniteReprocessorScreen;
import net.roguelogix.biggerreactors.classic.machine.containers.CyaniteReprocessorContainer;
import net.roguelogix.biggerreactors.classic.reactor.ReactorModeratorRegistry;
import net.roguelogix.biggerreactors.classic.reactor.client.*;
import net.roguelogix.biggerreactors.classic.reactor.containers.*;
import net.roguelogix.biggerreactors.classic.turbine.TurbineCoilRegistry;
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
        MinecraftForge.EVENT_BUS.addListener(this::onTagsUpdatedEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListenerEvent);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.addListener(this::onRenderWorldLast);
        }

    }

    public static DataPackRegistries dataPackRegistries;

    public void onAddReloadListenerEvent(AddReloadListenerEvent serverAboutToStartEvent) {
        dataPackRegistries = serverAboutToStartEvent.getDataPackRegistries();
    }


    public void onTagsUpdatedEvent(final TagsUpdatedEvent.CustomTagTypes tagsUpdatedEvent) {
        ReactorModeratorRegistry.loadRegistry(tagsUpdatedEvent.getTagManager().getBlockTags());
        TurbineCoilRegistry.loadRegistry(tagsUpdatedEvent.getTagManager().getBlockTags());
    }

    public void onClientSetup(final FMLClientSetupEvent e) {
        // TODO: 6/28/20 Registry.
        //  Since I already have the comment here, also need to do a capability registry. I have a somewhat dumb capability to register.
        ScreenManager.registerFactory(CyaniteReprocessorContainer.INSTANCE,
                CyaniteReprocessorScreen::new);
        ScreenManager.registerFactory(TurbineContainer.INSTANCE,
                TurbineScreen::new);
        ScreenManager.registerFactory(TurbineCoolantPortContainer.INSTANCE,
                TurbineCoolantPortScreen::new);

        // Screens below this line are on the new GUI system:
        ScreenManager.registerFactory(ReactorTerminalContainer.INSTANCE,
                CommonReactorTerminalScreen::new);
        ScreenManager.registerFactory(ReactorCoolantPortContainer.INSTANCE,
                ReactorCoolantPortScreen::new);
        ScreenManager.registerFactory(ReactorAccessPortContainer.INSTANCE,
                ReactorAccessPortScreen::new);
        ScreenManager.registerFactory(ReactorControlRodContainer.INSTANCE,
                ReactorControlRodScreen::new);
        ScreenManager.registerFactory(ReactorRedstonePortContainer.INSTANCE,
                ReactorRedstonePortScreen::new);

        ClientRegistry.bindTileEntityRenderer(TurbineRotorBearingTile.TYPE, BladeRenderer::new);
    }

    public static long lastRenderTime = 0;

    public void onRenderWorldLast(RenderWorldLastEvent event) {
        lastRenderTime = System.nanoTime();
    }

}

package net.roguelogix.biggerreactors;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.roguelogix.biggerreactors.classic.machine.client.CyaniteReprocessorScreen;
import net.roguelogix.biggerreactors.classic.machine.containers.CyaniteReprocessorContainer;
import net.roguelogix.biggerreactors.classic.reactor.client.ReactorScreen;
import net.roguelogix.biggerreactors.classic.reactor.containers.ReactorContainer;
import net.roguelogix.phosphophyllite.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(BiggerReactors.modid)
public class BiggerReactors {
    
    public static final String modid = "biggerreactors";
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    public BiggerReactors() {
        Registry.onModLoad();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
    }
    
    public void onCommonSetup(final FMLCommonSetupEvent e) {
        Config.postLoad();
    }
    
    
    public void onClientSetup(final FMLClientSetupEvent e) {
        // TODO: 6/28/20 Registry.
        //  Since I already have the comment here, also need to do a capability registry. I have a somewhat dumb capability to register.
        ScreenManager.registerFactory(CyaniteReprocessorContainer.INSTANCE,
                CyaniteReprocessorScreen::new);
        ScreenManager.registerFactory(ReactorContainer.INSTANCE,
                ReactorScreen::new);
    }
    
}

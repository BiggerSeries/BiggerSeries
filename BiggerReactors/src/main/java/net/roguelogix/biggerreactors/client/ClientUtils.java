package net.roguelogix.biggerreactors.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;

public class ClientUtils {
    public static IResourceManager getResourceManager() {
        return Minecraft.getInstance().getResourceManager();
    }
}

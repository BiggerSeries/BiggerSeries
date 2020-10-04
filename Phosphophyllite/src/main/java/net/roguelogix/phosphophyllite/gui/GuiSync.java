package net.roguelogix.phosphophyllite.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.roguelogix.phosphophyllite.PhosphophylliteConfig;
import net.roguelogix.phosphophyllite.registry.OnModLoad;
import net.roguelogix.phosphophyllite.robn.ROBN;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static net.roguelogix.phosphophyllite.Phosphophyllite.modid;

public class GuiSync {
    
    public interface IGUIPacketProvider {
        
        @Nullable
        IGUIPacket getGuiPacket();
        
        
        /**
         * DO NOT OVERRIDE THIS METHOD!
         *
         * @param requestName The request to make.
         * @param requestData The payload to send.
         */
        default void runRequest(String requestName, Object requestData) {
            HashMap<String, Object> map = new HashMap<>();
            
            map.put("request", requestName);
            map.put("data", requestData);
            
            ArrayList<Byte> buf = ROBN.toROBN(map);
            
            GUIPacketMessage message = new GUIPacketMessage();
            message.bytes = new byte[buf.size()];
            for (int i = 0; i < buf.size(); i++) {
                message.bytes[i] = buf.get(i);
            }
            
            INSTANCE.sendToServer(message);
        }
        
        default void executeRequest(String requestName, Object requestData) {
        }
    }
    
    public interface IGUIPacket {
        void read(@Nonnull Map<?, ?> data);
        
        @Nullable
        Map<?, ?> write();
    }
    
    private static class GUIPacketMessage {
        public byte[] bytes;
        
        public GUIPacketMessage() {
        
        }
        
        public GUIPacketMessage(byte[] readByteArray) {
            bytes = readByteArray;
        }
    }
    
    private static final HashMap<PlayerEntity, IGUIPacketProvider> playerGUIs = new HashMap<>();
    
    public static synchronized void onContainerOpen(PlayerContainerEvent.Open e) {
        Container container = e.getContainer();
        if (container instanceof IGUIPacketProvider) {
            playerGUIs.put(e.getPlayer(), (IGUIPacketProvider) container);
        }
    }
    
    public static synchronized void onContainerClose(PlayerContainerEvent.Close e) {
        playerGUIs.remove(e.getPlayer());
    }
    
    private static IGUIPacketProvider currentGUI;
    
    public static synchronized void GuiOpenEvent(GuiOpenEvent e) {
        
        Screen gui = e.getGui();
        if (gui instanceof ContainerScreen) {
            Container container = ((ContainerScreen<?>) gui).getContainer();
            if (container instanceof IGUIPacketProvider) {
                currentGUI = (IGUIPacketProvider) container;
            }
        } else {
            currentGUI = null;
        }
    }
    
    private static final String PROTOCOL_VERSION = "0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(modid, "multiblock/guisync"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    
    @OnModLoad
    public static void onModLoad() {
        INSTANCE.registerMessage(1, GUIPacketMessage.class, GuiSync::encodePacket, GuiSync::decodePacket, GuiSync::handler);
        MinecraftForge.EVENT_BUS.addListener(GuiSync::onContainerClose);
        MinecraftForge.EVENT_BUS.addListener(GuiSync::onContainerOpen);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.addListener(GuiSync::GuiOpenEvent);
        }
        Thread updateThread = new Thread(() -> {
            while (true) {
                synchronized (GuiSync.class) {
                    playerGUIs.forEach((player, gui) -> {
                        try {
                            assert player instanceof ServerPlayerEntity;
                            IGUIPacket packet = gui.getGuiPacket();
                            if (packet == null) {
                                return;
                            }
                            Map<?, ?> packetMap = packet.write();
                            if (packetMap == null) {
                                return;
                            }
                            ArrayList<Byte> buf;
                            try {
                                buf = ROBN.toROBN(packetMap);
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                                return;
                            }
                            GUIPacketMessage message = new GUIPacketMessage();
                            message.bytes = new byte[buf.size()];
                            for (int i = 0; i < buf.size(); i++) {
                                message.bytes[i] = buf.get(i);
                            }
                            INSTANCE.sendTo(message, ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                try {
                    Thread.sleep(PhosphophylliteConfig.GUI.UpdateIntervalMS);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
    }
    
    private static void encodePacket(GUIPacketMessage packet, PacketBuffer buf) {
        buf.writeBytes(packet.bytes);
    }
    
    private static GUIPacketMessage decodePacket(PacketBuffer buf) {
        byte[] byteBuf = new byte[buf.readableBytes()];
        buf.readBytes(byteBuf);
        return new GUIPacketMessage(byteBuf);
    }
    
    private static void handler(GUIPacketMessage packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NetworkDirection direction = ctx.get().getDirection();
            IGUIPacketProvider currentGUI;
            ArrayList<Byte> buf = new ArrayList<>();
            for (byte aByte : packet.bytes) {
                buf.add(aByte);
            }
            Map<?, ?> map = (Map<?, ?>) ROBN.fromROBN(buf);
            
            if (direction == NetworkDirection.PLAY_TO_CLIENT) {
                currentGUI = GuiSync.currentGUI;
                if (currentGUI != null) {
                    currentGUI.getGuiPacket().read(map);
                }
            } else {
                currentGUI = playerGUIs.get(ctx.get().getSender());
                if (currentGUI != null) {
                    currentGUI.executeRequest((String) map.get("request"), map.get("data"));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

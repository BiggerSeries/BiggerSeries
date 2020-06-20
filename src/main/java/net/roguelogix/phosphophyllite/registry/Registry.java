package net.roguelogix.phosphophyllite.registry;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.roguelogix.phosphophyllite.config.ConfigLoader;
import net.roguelogix.phosphophyllite.multiblock.generic.MultiblockBakedModel;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

public class Registry {

    private static final HashMap<String, HashSet<Block>> blocksRegistered = new HashMap<>();
    private static final HashMap<Class<?>, HashSet<Block>> tileEntityBlocksToRegister = new HashMap<>();

    public static synchronized void registerBlocks(final RegistryEvent.Register<Block> blockRegistryEvent) {
        String callerClass = new Exception().getStackTrace()[1].getClassName();
        String callerPackage = callerClass.substring(0, callerClass.lastIndexOf("."));
        String modNamespace = callerPackage.substring(callerPackage.lastIndexOf(".") + 1);
        Reflections ref = new Reflections(callerPackage);
        Set<Class<?>> blocks = ref.getTypesAnnotatedWith(RegisterBlock.class);
        HashSet<Block> blocksRegistered = Registry.blocksRegistered.computeIfAbsent(modNamespace, k -> new HashSet<>());
        for (Class<?> block : blocks) {
            try {
                Constructor<?> constructor = block.getConstructor();
                constructor.setAccessible(true);
                Object newObject = constructor.newInstance();
                if (!(newObject instanceof Block)) {
                    // the fuck you doing
                    //todo print error
                    continue;
                }

                Block newBlock = (Block) newObject;
                RegisterBlock blockAnnotation = block.getAnnotation(RegisterBlock.class);
                newBlock.setRegistryName(modNamespace + ":" + blockAnnotation.name());
                blockRegistryEvent.getRegistry().register(newBlock);

                for (Field declaredField : block.getDeclaredFields()) {
                    if (declaredField.isAnnotationPresent(RegisterBlock.Instance.class)) {
                        declaredField.set(null, newBlock);
                    }
                }

                if (blockAnnotation.registerItem()) {
                    blocksRegistered.add(newBlock);
                }

                if (blockAnnotation.tileEntityClass() != RegisterBlock.class) {
                    tileEntityBlocksToRegister.computeIfAbsent(blockAnnotation.tileEntityClass(), k -> new HashSet<>()).add(newBlock);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }


    public static synchronized void registerItems(final RegistryEvent.Register<Item> itemRegistryEvent) {
        String callerClass = new Exception().getStackTrace()[1].getClassName();
        String callerPackage = callerClass.substring(0, callerClass.lastIndexOf("."));
        String modNamespace = callerPackage.substring(callerPackage.lastIndexOf(".") + 1);
        Reflections ref = new Reflections(callerPackage);
        Set<Class<?>> items = ref.getTypesAnnotatedWith(RegisterItem.class);
        HashSet<Block> blocksRegistered = Registry.blocksRegistered.get(modNamespace);
        for (Block block : blocksRegistered) {
            assert block.getClass().isAnnotationPresent(RegisterBlock.class);
//            RegisterBlock blockAnnotation = block.getClass().getAnnotation(RegisterBlock.class);
            //noinspection ConstantConditions
            itemRegistryEvent.getRegistry().register(new BlockItem(block, new Item.Properties()).setRegistryName(block.getRegistryName()));
        }

        for (Class<?> item : items) {
            try {
                Constructor<?> constructor = item.getConstructor();
                constructor.setAccessible(true);
                Object newObject = constructor.newInstance();
                if (!(newObject instanceof Item)) {
                    // the fuck you doing
                    //todo print error
                    continue;
                }

                Item newItem = (Item) newObject;
                RegisterItem itemAnnotation = item.getAnnotation(RegisterItem.class);
                newItem.setRegistryName(modNamespace + ":" + itemAnnotation.name());
                itemRegistryEvent.getRegistry().register(newItem);

                for (Field declaredField : item.getDeclaredFields()) {
                    if (declaredField.isAnnotationPresent(RegisterItem.Instance.class)) {
                        declaredField.set(null, newItem);
                    }
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> tileEntityTypeRegistryEvent) {
        String callerClass = new Exception().getStackTrace()[1].getClassName();
        String callerPackage = callerClass.substring(0, callerClass.lastIndexOf("."));
        String modNamespace = callerPackage.substring(callerPackage.lastIndexOf(".") + 1);
        Reflections ref = new Reflections(callerPackage);
        Set<Class<?>> tileEntities = ref.getTypesAnnotatedWith(RegisterTileEntity.class);

//        tileEntityTypeRegistryEvent.getRegistry().register();

        for (Class<?> tileEntity : tileEntities) {

            RegisterTileEntity tileEntityAnnotation = tileEntity.getAnnotation(RegisterTileEntity.class);

            HashSet<Block> blocks = tileEntityBlocksToRegister.computeIfAbsent(tileEntity, k -> new HashSet<>());
            if (blocks.isEmpty()) {
                continue;
            }
            try {
                Constructor<?> tileConstructor = tileEntity.getConstructor();
                tileConstructor.setAccessible(true);
                Supplier<? extends TileEntity> supplier = () -> {
                    try {
                        return (TileEntity) tileConstructor.newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return null;
                };
                @SuppressWarnings("rawtypes") Constructor<TileEntityType.Builder> constructor = TileEntityType.Builder.class.getDeclaredConstructor(Supplier.class, Set.class);
                constructor.setAccessible(true);
                @SuppressWarnings({"unchecked", "ConstantConditions"}) TileEntityType<?> tileEntityType = constructor.newInstance(supplier, ImmutableSet.copyOf(blocks)).build(null);
                tileEntityType.setRegistryName(modNamespace + ":" + tileEntityAnnotation.name());
                tileEntityTypeRegistryEvent.getRegistry().register(tileEntityType);
                for (Field field : tileEntity.getFields()) {
                    if (field.isAnnotationPresent(RegisterTileEntity.Type.class)) {
                        field.set(null, tileEntityType);
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }

    public static synchronized void onClientSetup(FMLClientSetupEvent clientSetupEvent) {
        String callerClass = new Exception().getStackTrace()[1].getClassName();
        String callerPackage = callerClass.substring(0, callerClass.lastIndexOf("."));
        String modNamespace = callerPackage.substring(callerPackage.lastIndexOf(".") + 1);
        HashSet<Block> blocksRegistered = Registry.blocksRegistered.get(modNamespace);
        for (Block block : blocksRegistered) {
            if (!block.isSolid(null)) {
                for (Method declaredMethod : block.getClass().getDeclaredMethods()) {
                    if (declaredMethod.isAnnotationPresent(RegisterBlock.RenderLayer.class)) {
                        try {
                            declaredMethod.setAccessible(true);
                            Object returned = declaredMethod.invoke(block);
                            if (returned instanceof RenderType) {
                                RenderTypeLookup.setRenderLayer(block, (RenderType) returned);
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private static final HashMap<Block, IBakedModel> bakedModelsToRegister = new HashMap<>();

    public static synchronized void registerBakedModel(Block block, IBakedModel model) {
        bakedModelsToRegister.put(block, model);
    }

    public static synchronized void onModelBake(ModelBakeEvent event) {
        String callerClass = new Exception().getStackTrace()[1].getClassName();
        String callerPackage = callerClass.substring(0, callerClass.lastIndexOf("."));
        String modNamespace = callerPackage.substring(callerPackage.lastIndexOf(".") + 1);
        HashSet<Block> blocksRegistered = Registry.blocksRegistered.get(modNamespace);
        for (Block block : blocksRegistered) {
            IBakedModel model = bakedModelsToRegister.get(block);
            if (model != null) {
                event.getModelRegistry().put(new ModelResourceLocation(block.getRegistryName(), ""), model);
            }
        }
    }

    public static synchronized void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
            return;
        }
        String callerClass = new Exception().getStackTrace()[1].getClassName();
        String callerPackage = callerClass.substring(0, callerClass.lastIndexOf("."));
        String modNamespace = callerPackage.substring(callerPackage.lastIndexOf(".") + 1);
        HashSet<Block> blocksRegistered = Registry.blocksRegistered.get(modNamespace);
        HashSet<ResourceLocation> sprites = new HashSet<>();
        for (Block block : blocksRegistered) {
            IBakedModel model = bakedModelsToRegister.get(block);
            if (model instanceof MultiblockBakedModel) {
                Stack<MultiblockBakedModel.TextureMap> mapStack = new Stack<>();
                mapStack.push(((MultiblockBakedModel) model).map);
                while (!mapStack.empty()) {
                    MultiblockBakedModel.TextureMap map = mapStack.pop();
                    sprites.add(map.spriteLocation);
                    for (MultiblockBakedModel.TextureMap value : map.map.values()) {
                        mapStack.push(value);
                    }
                }
            }
        }
        for (ResourceLocation sprite : sprites) {
            event.addSprite(sprite);
        }
    }

    public static void registerConfig(){
        String callerClass = new Exception().getStackTrace()[1].getClassName();
        String callerPackage = callerClass.substring(0, callerClass.lastIndexOf("."));
        Reflections ref = new Reflections(callerPackage);
        Set<Class<?>> configs = ref.getTypesAnnotatedWith(RegisterConfig.class);
        for (Class<?> config : configs) {
//            ConfigLoader.registerConfig(config);
        }
    }
}

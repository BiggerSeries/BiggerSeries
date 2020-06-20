package net.roguelogix.phosphophyllite.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.roguelogix.biggerreactors.Config;
import net.roguelogix.phosphophyllite.registry.RegisterBlock;
import org.reflections.Reflections;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ConfigLoader {

    public static void registerConfig(Class<?> config, ConfigType type, String path) {
        ConfigLoadingInfo loadingInfo = createConfigLoadInfo(config);
        ModConfig.Type configType = null;
        switch (type){
            case CLIENT:
                configType = ModConfig.Type.CLIENT;
                break;
            case COMMON:
                configType = ModConfig.Type.COMMON;
                break;
            case SERVER:
                configType = ModConfig.Type.SERVER;
                break;
        }
        Path configPath = FMLPaths.CONFIGDIR.get().resolve(path);
        CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync().autosave().writingMode(WritingMode.REPLACE).build();

        ModLoadingContext.get().registerConfig(configType, loadingInfo.spec);


    }

    private static class ConfigLoadingInfoBuilder {
        ForgeConfigSpec.Builder specBuilder = new ForgeConfigSpec.Builder();
        final ArrayList<Runnable> valueLoadFuncs = new ArrayList<>();

    }

    private static class ConfigLoadingInfo {
        ForgeConfigSpec spec;
        final ArrayList<Runnable> valueLoadFuncs = new ArrayList<>();
    }


    private static void buildConfigLoadInfo(Class<?> configClass, ConfigLoadingInfoBuilder loadingInfo, @Nullable final Object currentObject, ArrayList<Object> previousObjects) throws IllegalAccessException {
        Field[] fields = configClass.getDeclaredFields();

        if (previousObjects.contains(currentObject) || (currentObject == null && previousObjects.contains(configClass))) {
            throw new IllegalStateException("Config attempts to use same values twice");
        }
        if (currentObject == null) {
            previousObjects.add(configClass);
        } else {
            previousObjects.add(currentObject);
        }

        for (final Field field : fields) {
            if (!field.isAnnotationPresent(PhosphophylliteConfig.Value.class)) {
                continue;
            }
            // if we are loading static fields, then currentObject is null
            // if current object isn't null, we ignore static fields
            if (Modifier.isStatic(field.getModifiers()) != (currentObject == null)) {
                continue;
            }
            PhosphophylliteConfig.Value annotation = field.getAnnotation(PhosphophylliteConfig.Value.class);
            String name = field.getName();
            String comment = annotation.comment();
            if (!comment.equals("")) {
                loadingInfo.specBuilder.comment(comment);
            }
            // if for *whatever* reason you want to use a private value in your config, you can
            field.setAccessible(true);
            Runnable loader = null;
            Class<?> fieldClass = field.getType();
            {
                if (fieldClass.isAnnotationPresent(PhosphophylliteConfig.class)) {
                    // ok, this is a category object
                    if (!Modifier.isFinal(field.getModifiers())) {
                        throw new IllegalStateException("Category objects must be final");
                    }
                    Object fieldObject = field.get(currentObject);
                    loadingInfo.specBuilder.push(name);
                    buildConfigLoadInfo(fieldClass, loadingInfo, fieldObject, previousObjects);
                    loadingInfo.specBuilder.pop();
                    continue;
                }
                if (fieldClass.isPrimitive()) {
                    if (fieldClass == double.class) {
                        final double min = annotation.min();
                        final double max = annotation.max();
                        final double defaultValue = field.getDouble(currentObject);
                        final ForgeConfigSpec.DoubleValue value = loadingInfo.specBuilder.defineInRange(name, defaultValue, min, max);
                        loadingInfo.valueLoadFuncs.add(() -> {
                            try {
                                field.setDouble(currentObject, value.get());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
                        continue;
                    }
                    if (fieldClass == long.class) {
                        final long min = annotation.min();
                        final long max = annotation.max();
                        final long defaultValue = field.getLong(currentObject);
                        final ForgeConfigSpec.LongValue value = loadingInfo.specBuilder.defineInRange(name, defaultValue, min, max);
                        loadingInfo.valueLoadFuncs.add(() -> {
                            try {
                                field.setLong(currentObject, value.get());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
                        continue;
                    }
                    if (fieldClass == int.class) {
                        final int min = (int) annotation.min();
                        final int max = (int) annotation.max();
                        final int defaultValue = field.getInt(currentObject);
                        final ForgeConfigSpec.IntValue value = loadingInfo.specBuilder.defineInRange(name, defaultValue, min, max);
                        loadingInfo.valueLoadFuncs.add(() -> {
                            try {
                                field.setInt(currentObject, value.get());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
                        continue;
                    }
                    if (fieldClass == boolean.class) {
                        final boolean defaultValue = field.getBoolean(currentObject);
                        final ForgeConfigSpec.BooleanValue value = loadingInfo.specBuilder.define(name, defaultValue);
                        loadingInfo.valueLoadFuncs.add(() -> {
                            try {
                                field.setBoolean(currentObject, value.get());
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
                        continue;
                    }
                }
                if (fieldClass.isEnum()) {
                    @SuppressWarnings("unchecked") List<Enum> enumValues = (List<Enum>) Arrays.asList(fieldClass.getEnumConstants());
                    List<String> allowedValues = Arrays.asList(annotation.allowedValues());
                    enumValues.removeIf(enumValue -> !allowedValues.contains(enumValue.toString()));
                    Enum defaultValue = (Enum) field.get(currentObject);
                    @SuppressWarnings("unchecked") final ForgeConfigSpec.EnumValue<?> value = loadingInfo.specBuilder.defineEnum(name, defaultValue, enumValues);
                    loadingInfo.valueLoadFuncs.add(() -> {
                        try {
                            field.set(currentObject, value.get());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
                    continue;
                }
                if (fieldClass == String.class) {
                    final String defaultValue = (String) field.get(currentObject);
                    final ForgeConfigSpec.ConfigValue<String> value = loadingInfo.specBuilder.define(name, defaultValue);
                    loadingInfo.valueLoadFuncs.add(() -> {
                        try {
                            field.set(currentObject, value.get());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
                    continue;
                }
            }
            loadingInfo.valueLoadFuncs.add(loader);
        }
    }

    private static ConfigLoadingInfo createConfigLoadInfo(Class<?> config) {
        ConfigLoadingInfoBuilder loadingInfo = new ConfigLoadingInfoBuilder();

        try {
            buildConfigLoadInfo(config, loadingInfo, null, new ArrayList<>());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        ConfigLoadingInfo loadInfo = new ConfigLoadingInfo();

        loadInfo.spec = loadingInfo.specBuilder.build();
        loadInfo.valueLoadFuncs.addAll(loadingInfo.valueLoadFuncs);

        return loadInfo;
    }

    public static void onLoad(final ModConfig.Loading configEvent) {

    }

    public static void onReload(final ModConfig.Reloading configEvent) {
    }

}

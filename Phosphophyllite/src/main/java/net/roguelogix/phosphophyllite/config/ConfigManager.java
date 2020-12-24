package net.roguelogix.phosphophyllite.config;

import mcp.MethodsReturnNonnullByDefault;
import net.roguelogix.phosphophyllite.parsers.Element;
import net.roguelogix.phosphophyllite.parsers.JSON5;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConfigManager {
    
    static final Logger LOGGER = LogManager.getLogger("Phosphophyllite/Config");
    
    private static final HashSet<ModConfig> modConfigs = new HashSet<>();
    
    public static void registerConfig(Class<?> clazz, String modName) {
        if (clazz.isAnnotationPresent(PhosphophylliteConfig.class)) {
            PhosphophylliteConfig annotation = clazz.getAnnotation(PhosphophylliteConfig.class);
            if (!annotation.name().equals("")) {
                modName = annotation.name();
            }
        }
        
        ModConfig config = new ModConfig(clazz, modName);
        modConfigs.add(config);
        config.load();
    }
    
    private static class ModConfig {
        private final Class<?> configClazz;
        final PhosphophylliteConfig annotation;
        private final String modName;
        File baseFile;
        File actualFile = null;
        ConfigFormat actualFormat;
        
        private final ConfigSpec spec;
        
        ModConfig(Class<?> clazz, String name) {
            configClazz = clazz;
            modName = name;
            spec = new ConfigSpec(clazz);
            loadReflections();
            
            annotation = clazz.getAnnotation(PhosphophylliteConfig.class);
            baseFile = new File("config/" + annotation.folder() + "/" + name + "-" + annotation.type().toString().toLowerCase());
        }
        
        private Field enableAdvanced;
        private final HashSet<Method> preLoads = new HashSet<>();
        private final HashSet<Method> loads = new HashSet<>();
        private final HashSet<Method> postLoads = new HashSet<>();
        
        void loadReflections() {
            for (Field declaredField : configClazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(PhosphophylliteConfig.EnableAdvanced.class)) {
                    enableAdvanced = declaredField;
                    enableAdvanced.setAccessible(true);
                    Class<?> EAClass = enableAdvanced.getType();
                    if (EAClass != boolean.class && EAClass != Boolean.class) {
                        throw new ConfigSpec.DefinitionError("Advanced enable flag must be a boolean");
                    }
                    if (!Modifier.isStatic(declaredField.getModifiers())) {
                        throw new ConfigSpec.DefinitionError("Advanced enable flag must be static");
                    }
                    break;
                }
            }
            
            for (Method declaredMethod : configClazz.getDeclaredMethods()) {
                if (declaredMethod.getReturnType() != Void.TYPE) {
                    continue;
                }
                if (declaredMethod.getParameterCount() != 0) {
                    continue;
                }
                if (!Modifier.isStatic(declaredMethod.getModifiers())) {
                    continue;
                }
                if (declaredMethod.isAnnotationPresent(PhosphophylliteConfig.PreLoad.class)) {
                    declaredMethod.setAccessible(true);
                    preLoads.add(declaredMethod);
                }
                if (declaredMethod.isAnnotationPresent(PhosphophylliteConfig.Load.class)) {
                    declaredMethod.setAccessible(true);
                    loads.add(declaredMethod);
                }
                if (declaredMethod.isAnnotationPresent(PhosphophylliteConfig.PostLoad.class)) {
                    declaredMethod.setAccessible(true);
                    postLoads.add(declaredMethod);
                }
            }
        }
        
        void runPreLoads() {
            for (Method preLoad : preLoads) {
                try {
                    preLoad.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        
        void runLoads() {
            for (Method load : loads) {
                try {
                    load.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        
        void runPostLoads() {
            for (Method postLoad : postLoads) {
                try {
                    postLoad.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        
        void load() {
            if (actualFile == null) {
                findFile();
            }
            if (!actualFile.exists()) {
                generateFile();
                // if we just generated the file, its default values, no need to do anything else
                return;
            }
            Element tree = readFile();
            spec.writeElementTree(tree);
        }
        
        void findFile() {
            File file = null;
            ConfigFormat format = null;
            for (ConfigFormat value : ConfigFormat.values()) {
                File fullFile = new File(baseFile + "." + value.toString().toLowerCase());
                if (fullFile.exists()) {
                    if (file != null) {
                        // why the fuck are there multiple?
                        // if its the correct format, we will use it, otherwise, whatever we have is good enough
                        if (annotation.format() == value) {
                            file = fullFile;
                            format = value;
                        }
                    } else {
                        format = value;
                        file = fullFile;
                    }
                }
            }
            
            if (file == null) {
                file = new File(baseFile + "." + annotation.format().toString().toLowerCase());
                format = annotation.format();
            }
            
            actualFile = file;
            actualFormat = format;
        }
        
        void generateFile() {
            spec.writeDefaults();
            Element tree = spec.generateElementTree(false);
            String str = null;
            switch (annotation.format()) {
                case JSON5: {
                    str = JSON5.parseElement(tree);
                    break;
                }
                case TOML:
                    throw new RuntimeException("TOML not supported");
            }
            try {
                //noinspection ResultOfMethodCallIgnored
                actualFile.getParentFile().mkdirs();
                Files.write(Paths.get(String.valueOf(actualFile)), str.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        Element readFile() {
            String str;
            try {
                str = new String(Files.readAllBytes(Paths.get(String.valueOf(actualFile))));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to read config file");
            }
            
            Element elementTree = null;
            switch (actualFormat) {
                case JSON5: {
                    elementTree = JSON5.parseString(str);
                    break;
                }
                case TOML:
                    throw new RuntimeException("TOML not supported");
            }
            return elementTree;
        }
    }
}

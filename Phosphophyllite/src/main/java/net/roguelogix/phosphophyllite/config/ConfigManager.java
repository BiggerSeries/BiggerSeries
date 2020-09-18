package net.roguelogix.phosphophyllite.config;

import net.roguelogix.phosphophyllite.parsers.Element;
import net.roguelogix.phosphophyllite.parsers.JSON5;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ConfigManager {
    
    private static final HashMap<Class<?>, String> reloadableConfigClasses = new HashMap<>();
    private static final HashMap<Class<?>, String> configClasses = new HashMap<>();
    
    public static synchronized void registerConfig(Class<?> clazz, String modName) {
        if (clazz.isAnnotationPresent(PhosphophylliteConfig.class)) {
            PhosphophylliteConfig annotation = clazz.getAnnotation(PhosphophylliteConfig.class);
            if (!annotation.name().equals("")) {
                modName = annotation.name();
            }
            loadConfig(clazz, modName);
            configClasses.put(clazz, modName);
            if (annotation.reloadable()) {
                reloadableConfigClasses.put(clazz, modName);
            }
        }
    }
    
    public static synchronized void reloadConfigs() {
        reloadableConfigClasses.forEach(ConfigManager::loadConfig);
    }
    
    public static boolean modLoadingFinished = false;
    
    private static synchronized void loadConfig(Class<?> clazz, String name) {
        PhosphophylliteConfig annotation = clazz.getAnnotation(PhosphophylliteConfig.class);
        File baseFile = new File("config/" + annotation.folder() + "/" + name + "-" + annotation.type().toString().toLowerCase());
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
            Element element = ConfigLoader.buildCurrentElementTree(clazz);
            String str = null;
            switch (annotation.format()) {
                case JSON5: {
                    str = JSON5.parseElement(element);
                    break;
                }
                case TOML:
                    throw new RuntimeException("TOML not supported");
            }
            try {
                file.getParentFile().mkdirs();
                Files.write(Paths.get(String.valueOf(file)), str.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            return;
        }
        
        String str;
        try {
            str = new String(Files.readAllBytes(Paths.get(String.valueOf(file))));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        Element elementTree = null;
        switch (format) {
            case JSON5: {
                elementTree = JSON5.parseString(str);
                break;
            }
            case TOML:
                throw new RuntimeException("TOML not supported");
        }
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(PhosphophylliteConfig.PreLoad.class)) {
                if (Modifier.isStatic(declaredMethod.getModifiers())) {
                    if (declaredMethod.getParameters().length == 0) {
                        declaredMethod.setAccessible(true);
                        try {
                            declaredMethod.invoke(null);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        ConfigLoader.writeElementTree(elementTree, clazz);
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(PhosphophylliteConfig.Load.class)) {
                if (Modifier.isStatic(declaredMethod.getModifiers())) {
                    if (declaredMethod.getParameters().length == 0) {
                        declaredMethod.setAccessible(true);
                        try {
                            declaredMethod.invoke(null);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        if (modLoadingFinished) {
            runPostLoad(clazz);
        }
    }
    
    public static void runPostLoads() {
        for (Class<?> aClass : configClasses.keySet()) {
            runPostLoad(aClass);
        }
    }
    
    private static void runPostLoad(@Nonnull Class<?> clazz) {
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(PhosphophylliteConfig.PostLoad.class)) {
                if (Modifier.isStatic(declaredMethod.getModifiers())) {
                    if (declaredMethod.getParameters().length == 0) {
                        declaredMethod.setAccessible(true);
                        try {
                            declaredMethod.invoke(null);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}

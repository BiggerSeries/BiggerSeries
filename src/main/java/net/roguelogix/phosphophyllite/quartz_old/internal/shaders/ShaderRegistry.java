package net.roguelogix.phosphophyllite.quartz_old.internal.shaders;

import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

public class ShaderRegistry {
    private static HashMap<ResourceLocation, Program> loadedPrograms;

    public static void startup(){
        if(loadedPrograms == null){
            loadedPrograms = new HashMap<>();
        }
    }

    public static void shutdown(){
        loadedPrograms = null;
    }

    public static Program getOrLoadProgram(ResourceLocation location) {
        Program program = loadedPrograms.get(location);
        if(program == null){
            program = new Program(location);
            loadedPrograms.put(location, program);
        }
        return program;
    }

    public static void reloadAll(){
        loadedPrograms.forEach((location, program) -> program.reload());
    }
}

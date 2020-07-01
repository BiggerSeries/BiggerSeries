package net.roguelogix.phosphophyllite.quartz_old.internal;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

public enum OperationMode {
    
    GL45,
    GL21;
    
    private static OperationMode mode;
    
    public static void init() {
        GLCapabilities capabilities = GL.getCapabilities();
        if (capabilities.OpenGL45) {
            mode = GL45;
            return;
        }
        if (capabilities.OpenGL21) {
            mode = GL21;
            // not supporting GL.21 right now
//            return;
        }
        
        throw new IllegalStateException("Unsupported OpenGL mode required" +
                "\n\n" +
                "See github page linked for more info\n" +
                "\n" +
                "<said link, eventually>"
        );
    }
    
    public static OperationMode mode() {
        return mode;
    }
}

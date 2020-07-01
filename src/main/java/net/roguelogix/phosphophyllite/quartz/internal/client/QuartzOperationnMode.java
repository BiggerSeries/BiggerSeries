package net.roguelogix.phosphophyllite.quartz.internal.client;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

public enum QuartzOperationnMode {
    GL45,
    GL33,
    GL21,
    UNKNOWN;
    
    private static QuartzOperationnMode mode = UNKNOWN;
    
    public static QuartzOperationnMode mode() {
        return mode;
    }
    
    public static void onGLStartup() {
        GLCapabilities capabilities = GL.getCapabilities();
        if (capabilities.OpenGL45) {
            mode = GL45;
            return;
        }
        if (capabilities.OpenGL33 && capabilities.GL_ARB_explicit_uniform_location) {
            mode = GL33;
            return;
        }
        if (capabilities.OpenGL21 && capabilities.GL_EXT_texture_integer) {
            mode = GL21;
            return;
        }
        throw new IllegalStateException("\nUnable to select OpenGL State, see wiki on how to report");
    }
}

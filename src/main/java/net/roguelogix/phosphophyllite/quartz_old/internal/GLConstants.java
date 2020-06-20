package net.roguelogix.phosphophyllite.quartz_old.internal;


import static org.lwjgl.opengl.GL45.*;

public class GLConstants {

    public static void init(){
        switch (OperationMode.mode()){
            case GL45:
                GL45();
            case GL21:
                GL21();
                break;
        }
    }

    public static int MAX_ARRAY_TEXTURE_LAYERS = -1;

    private static void GL45(){
        MAX_ARRAY_TEXTURE_LAYERS = glGetInteger(GL_MAX_ARRAY_TEXTURE_LAYERS);
    }

    public static int MAX_TEXTURE_SIZE = -1;
    public static int MAX_3D_TEXTURE_SIZE = -1;
    public static int MAX_TEXTURE_UNITS = -1;
    public static int MAX_TEXTURE_IMAGE_UNITS = -1;

    private static void GL21(){
        MAX_TEXTURE_SIZE = glGetInteger(GL_MAX_TEXTURE_SIZE);
        MAX_3D_TEXTURE_SIZE = glGetInteger(GL_MAX_3D_TEXTURE_SIZE);
        MAX_TEXTURE_UNITS = glGetInteger(GL_MAX_TEXTURE_UNITS);
        MAX_TEXTURE_IMAGE_UNITS = glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS);
    }
}

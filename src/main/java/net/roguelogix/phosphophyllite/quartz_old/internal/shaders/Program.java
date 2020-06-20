package net.roguelogix.phosphophyllite.quartz_old.internal.shaders;

import net.minecraft.util.ResourceLocation;
import net.roguelogix.phosphophyllite.Phosphophyllite;
import net.roguelogix.phosphophyllite.threading.Event;

import static net.roguelogix.phosphophyllite.quartz_old.internal.Renderer.secondaryWorkQueue;
import static net.roguelogix.phosphophyllite.quartz_old.internal.Util.readResourceLocation;
import static org.lwjgl.opengl.GL21.*;

public class Program {

    private final ResourceLocation location;

    private int handle;

    public Program(ResourceLocation location) {
        this.location = location;
        handle = load(location);
        if (handle == 0) {
            throw new IllegalStateException("Unable to load shader: " + location.toString());
        }
    }

    public void bind() {
        glUseProgram(handle);
    }

    public Event reload() {
        return secondaryWorkQueue.enqueue(() -> {
            int newHandle = load(location);
            if (newHandle != 0) {
                unload(handle);
                handle = newHandle;
            }
        });
    }

    @Override
    protected void finalize() throws Throwable {
        unload(handle);
    }

    private static int load(ResourceLocation location) {
        ResourceLocation vertexLocation = new ResourceLocation(location.getNamespace(), location.getPath() + ".vert");
        ResourceLocation fragmentLocation = new ResourceLocation(location.getNamespace(), location.getPath() + ".frag");

        String vertexCode = readResourceLocation(vertexLocation);
        String fragmentCode = readResourceLocation(fragmentLocation);

        if (vertexCode == null && fragmentCode == null) {
            Phosphophyllite.LOGGER.warn("No code found for shader " + location.toString());
            return 0;
        }

        int vertexShader = 0;

        if (vertexCode != null) {
            vertexShader = glCreateShader(GL_VERTEX_SHADER);
            glShaderSource(vertexShader, vertexCode);
            glCompileShader(vertexShader);
            if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) != GL_TRUE) {
                Phosphophyllite.LOGGER.warn("Vertex compile error" + location.toString());
                Phosphophyllite.LOGGER.warn("\n" + glGetShaderInfoLog(vertexShader));
                glDeleteShader(vertexShader);
                return 0;
            }
        }

        int fragmentShader = 0;

        if (fragmentCode != null) {
            fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
            glShaderSource(fragmentShader, fragmentCode);
            glCompileShader(fragmentShader);
            if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) != GL_TRUE) {
                Phosphophyllite.LOGGER.warn("Fragment compile error" + location.toString());
                Phosphophyllite.LOGGER.warn("\n" + glGetShaderInfoLog(fragmentShader));
                glDeleteShader(vertexShader);
                glDeleteShader(fragmentShader);
                return 0;
            }
        }

        int program = glCreateProgram();

        if (vertexCode != null) {
            glAttachShader(program, vertexShader);
        }
        if (fragmentCode != null) {
            glAttachShader(program, fragmentShader);
        }

        glLinkProgram(program);

        if (glGetProgrami(program, GL_LINK_STATUS) != GL_TRUE) {
            Phosphophyllite.LOGGER.warn("Program link error" + location.toString());
            Phosphophyllite.LOGGER.warn("\n" + glGetProgramInfoLog(program));
            glDeleteProgram(program);
            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            return 0;
        }

        if (vertexCode != null) {
            glDetachShader(program, vertexShader);
            glDeleteShader(vertexShader);
        }
        if (fragmentCode != null) {
            glDetachShader(program, fragmentShader);
            glDeleteShader(fragmentShader);
        }

        return program;
    }

    private static void unload(int handle) {
        secondaryWorkQueue.enqueue(() -> {
            glDeleteProgram(handle);
        });
    }

}

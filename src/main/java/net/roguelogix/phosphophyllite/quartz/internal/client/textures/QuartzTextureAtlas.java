package net.roguelogix.phosphophyllite.quartz.internal.client.textures;

import net.roguelogix.phosphophyllite.quartz.internal.client.QuartzOperationnMode;
import net.roguelogix.phosphophyllite.quartz.internal.common.QuartzTextureAtlasDescriptor;

import static org.lwjgl.opengl.GL20C.glUniform1i;

public class QuartzTextureAtlas {


    public QuartzTextureAtlas(QuartzTextureAtlasDescriptor descriptor){

    }

    void addTexture(QuartzTexture texture){

    }

    void bind(int baseTextureUnit){

    }

    void setupUniforms(int baseTextureUnit){
        switch (QuartzOperationnMode.mode()){
            case GL45:
            case GL33:{
                glUniform1i(129, baseTextureUnit);
                break;
            }
            case GL21:{

            }
        }
    }
}

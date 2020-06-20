#pragma once
#if defined(VERSION_330) || defined(VERSION_450)
#define USING_TBO
//layout(location = 128) uniform usamplerBuffer textureIndexRotationBuffer;

//layout(location = 129) uniform usamplerBuffer atlasIndexing;
//layout(location = 130) uniform vec2[10] textureScales;
layout(location = 140) uniform sampler2D[10] textures;

in vec3 atlasCoordinate;
in flat int atlasIndex;

#define TEXTURE texture
#else
//uniform sampler1D atlasIndexing;
//uniform vec2[10] textureScales;
uniform sampler2D[10] textures;

varying vec2 atlasCoordinate;
varying flat uint atlasIndex;

#define TEXTURE texture2D
#endif

vec4 doAtlasTexture(vec4 currentColor){
    // AMDGPU is big dumb if i do this lookup directly, no clue why, but this works, soooo, meh
    vec4 textureColor = vec4(0, 0, 0, 1);
    switch (atlasIndex){
        case 0:{
            textureColor = TEXTURE(textures[0], atlasCoordinate);
            break;
        }
        case 1:{
            textureColor = TEXTURE(textures[1], atlasCoordinate);
            break;
        }
        case 2:{
            textureColor = TEXTURE(textures[2], atlasCoordinate);
            break;
        }
        case 3:{
            textureColor = TEXTURE(textures[3], atlasCoordinate);
            break;
        }
        case 4:{
            textureColor = TEXTURE(textures[4], atlasCoordinate);
            break;
        }
        case 5:{
            textureColor = TEXTURE(textures[5], atlasCoordinate);
            break;
        }
        case 6:{
            textureColor = TEXTURE(textures[6], atlasCoordinate);
            break;
        }
        case 7:{
            textureColor = TEXTURE(textures[7], atlasCoordinate);
            break;
        }
        case 8:{
            textureColor = TEXTURE(textures[8], atlasCoordinate);
            break;
        }
        case 9:{
            textureColor = TEXTURE(textures[9], atlasCoordinate);
            break;
        }
    }

    return textureColor * currentColor;
}
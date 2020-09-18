       
 #version 330
 #extension GL_ARB_explicit_uniform_location : enable
       
layout(location = 140) uniform sampler2D[10] textures;
in vec3 atlasCoordinate;
in flat int atlasIndex;
vec4 doAtlasTexture(vec4 currentColor){
    vec4 textureColor = vec4(0, 0, 0, 1);
    switch (atlasIndex){
        case 0:{
            textureColor = texture(textures[0], atlasCoordinate);
            break;
        }
        case 1:{
            textureColor = texture(textures[1], atlasCoordinate);
            break;
        }
        case 2:{
            textureColor = texture(textures[2], atlasCoordinate);
            break;
        }
        case 3:{
            textureColor = texture(textures[3], atlasCoordinate);
            break;
        }
        case 4:{
            textureColor = texture(textures[4], atlasCoordinate);
            break;
        }
        case 5:{
            textureColor = texture(textures[5], atlasCoordinate);
            break;
        }
        case 6:{
            textureColor = texture(textures[6], atlasCoordinate);
            break;
        }
        case 7:{
            textureColor = texture(textures[7], atlasCoordinate);
            break;
        }
        case 8:{
            textureColor = texture(textures[8], atlasCoordinate);
            break;
        }
        case 9:{
            textureColor = texture(textures[9], atlasCoordinate);
            break;
        }
    }
    return textureColor * currentColor;
}
       
in float fogCoord;
layout(location = 96) uniform vec4 fogColor;
layout(location = 97) uniform vec2 fogScaleEnd;
vec4 doFog(vec4 currentColor){
    float fogFactor = (fogScaleEnd.y - fogCoord) * fogScaleEnd.x;
    fogFactor = clamp(fogFactor, 0.0, 1.0);
    return mix(fogColor, currentColor, fogFactor);
}
       
in uvec4 lightLevels;
in vec2 lightPosition;
in float AOMultiplier;
layout(location = 64) uniform sampler2D lightmap;
vec4 doLighting(vec4 currentColor){
    vec2 lightmapPos0 = vec2((lightLevels.x >> 6) & 0x3Fu, lightLevels.x & 0x3Fu);
    vec2 lightmapPos1 = vec2((lightLevels.y >> 6) & 0x3Fu, lightLevels.y & 0x3Fu);
    vec2 lightmapPos2 = vec2((lightLevels.z >> 6) & 0x3Fu, lightLevels.z & 0x3Fu);
    vec2 lightmapPos3 = vec2((lightLevels.w >> 6) & 0x3Fu, lightLevels.w & 0x3Fu);
    lightmapPos0 = lightmapPos0 * lightPosition.x + lightmapPos1 * (1 - lightPosition.x);
    lightmapPos2 = lightmapPos2 * lightPosition.x + lightmapPos3 * (1 - lightPosition.x);
    lightmapPos0 = lightmapPos0 * lightPosition.y + lightmapPos2 * (1 - lightPosition.y);
    lightmapPos0 /= 4.0;
    lightmapPos0 = clamp(lightmapPos0, 0.0, 15.0);
    lightmapPos0 += 1.0/2.0;
    lightmapPos0 /= 16.0;
    vec4 lightmapColor = texture(lightmap, lightmapPos0);
    lightmapColor *= AOMultiplier;
    lightmapColor.z = 1;
    return currentColor * lightmapColor;
}
       
void doCutout(vec4 color){
    if(color.w < 0.1){
        discard;
    }
}
layout(location = 0) out vec4 color;
void main() {
    vec4 fragColor = vec4(1, 1, 1, 1);
    fragColor = doLighting(fragColor);
    fragColor = doAtlasTexture(fragColor);
    fragColor = doFog(fragColor);
    doCutout(fragColor);
    color = fragColor;
}

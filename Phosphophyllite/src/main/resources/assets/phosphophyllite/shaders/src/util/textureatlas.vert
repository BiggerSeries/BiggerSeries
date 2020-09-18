#pragma once
#if defined(VERSION_330) || defined(VERSION_450)
#define USING_TBO

layout(location = 128) uniform usamplerBuffer textureIndexRotationBuffer;

layout(location = 129) uniform samplerBuffer atlasIndexing;
layout(location = 130) uniform vec2[10] textureScales;
//layout(location = 140) uniform sampler2DArray[10] textures;

out vec2 atlasCoordinate;
out flat uint atlasIndex;

#else

uniform sampler1D atlasIndexing;
uniform vec2[10] textureScales;
//uniform sampler2DArray[10] textures;

varying vec2 atlasCoordinate;
varying flat uint atlasIndex;

#endif

vec2 rotateUV(vec2 uv, uint rotation){
    rotation &= 3u;// 0b11
    // NO BRANCHING!!!! no, really, dont
    uv -= 0.5;
    {
        float newX = uv.x * (~rotation & 1u) + uv.y * (rotation & 1u);
        newX *= .5 * (~rotation & 2u) + -.5 * (rotation & 2u);
        float newY = uv.y * (~rotation & 1u) + uv.x * (rotation & 1u);
        uint negateY = (rotation >> 1u) ^ (rotation & 1u);
        newY *= -1 * float(negateY) + 1 * (~negateY & 1u);
        uv = vec2(newX, newY);
    }
    uv += 0.5;
    return uv;
}

void doAtlasTexture(vec2 textureCoordinate, uint faceID){
    #ifdef USING_TBO
    uint textureIndexRotationPacked = texelFetch(textureIndexRotationBuffer, int(6 * gl_InstanceID + faceID)).x;

    // rotation
    uint rotation = textureIndexRotationPacked & 0x3u;
    vec2 rotatedCoordinate = rotateUV(textureCoordinate, rotation);

    // helps with sampling on the edge of the block, ensuring it doesnt sample the next texture
    rotatedCoordinate *= 0.999;

    int textureIndex = int((textureIndexRotationPacked >> 2) & 0x3FFFu);

    vec3 atlasLookupInfo = texelFetch(atlasIndexing, textureIndex).xyz;
    atlasIndex = int(atlasLookupInfo.z);

    atlasCoordinate = rotatedCoordinate * textureScales[atlasIndex];
    atlasCoordinate += atlasLookupInfo.xy;
    #else

    // yes, im assuming thats what you gave me with GL 2.1
    uint textureIndexRotationPacked = faceID;

    // rotation is the same, thankfully
    uint rotation = textureIndexRotationPacked & 0x3u;
    vec2 rotatedCoordinate = rotateUV(textureCoordinate, rotation);

    // again with the normalization
    float textureIndexNormalized = textureIndexRotationPacked >> 2;
    // and again with the assumptions
    textureIndexNormalized /= 16384;

    vec3 atlasLookupInfo = texture1DLod(atlasIndexing, textureIndexNormalized, 0).xyz;

    atlasCoordinate = rotatedCoordinate * textureScales[atlasIndex];
    atlasCoordinate += atlasLookupInfo.xy;

    #endif
}
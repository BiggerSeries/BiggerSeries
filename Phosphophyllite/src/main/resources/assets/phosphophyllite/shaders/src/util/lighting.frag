#pragma once

#ifdef VERSION_330_OR_GREATER
in uvec4 lightLevels;
in vec2 lightPosition;
in float AOMultiplier;

layout(location = 64) uniform sampler2D lightmap;
#else
varying uvec4 lightLevels;
varying vec2 lightPosition;
varying float AOMultiplier;

uniform sampler2D lightmap;
#endif


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

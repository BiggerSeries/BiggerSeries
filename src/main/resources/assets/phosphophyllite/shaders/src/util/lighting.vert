#pragma once
#ifdef VERSION_330_OR_GREATER
out uvec4 lightLevels;
out vec2 lightPosition;
out float AOMultiplier;

//layout(location = 64) uniform sampler2D lightmap;
#else
varying uvec4 lightLevels;
varying vec2 lightPosition;
varying float AOMultiplier;

//uniform sampler2D lightmap;
#endif

void doLighting(uvec4 levels, vec2 position, float multiplier){
    lightLevels = levels;
    lightPosition = position;
    AOMultiplier = multiplier;
}
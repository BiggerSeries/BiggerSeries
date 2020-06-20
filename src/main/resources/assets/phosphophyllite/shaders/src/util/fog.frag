#pragma once
#ifdef VERSION_330_OR_GREATER
in float fogCoord;

layout(location = 96) uniform vec4 fogColor;
layout(location = 97) uniform vec2 fogScaleEnd;
#else
varying float fogCoord;

uniform vec4 fogColor;
uniform vec2 fogScaleEnd;
#endif

vec4 doFog(vec4 currentColor){
    float fogFactor = (fogScaleEnd.y - fogCoord) * fogScaleEnd.x;
    fogFactor = clamp(fogFactor, 0.0, 1.0);
    return mix(fogColor, currentColor, fogFactor);
}
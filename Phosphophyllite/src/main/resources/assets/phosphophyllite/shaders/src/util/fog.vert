#pragma once
#ifdef VERSION_330_OR_GREATER
out float fogCoord;

//layout(location = 96) uniform vec4 fogColor;
//layout(location = 97) uniform vec2 fogScaleEnd;
#else
varying float fogCoord;

//uniform vec4 fogColor;
//uniform vec2 fogScaleEnd;
#endif

void doFog(mat4 modelViewMatrix, vec3 position){
    fogCoord = length(modelViewMatrix * vec4(position, 1));
}
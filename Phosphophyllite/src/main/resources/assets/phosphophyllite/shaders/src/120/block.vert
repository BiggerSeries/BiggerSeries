#define VERSION_120
#include "util/versioning.glsl"
#include "util/textureatlas.vert"
#include "util/fog.vert"
#include "util/lighting.vert"
#include "util/transform.vert"

attribute vec3 vertexLocation;
attribute vec3 textureLocation;
attribute uvec4 lightLevels;
attribute float AOMultiplier;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

void main() {

    doLighting(lightLevels, textureLocation.xy, AOMultiplier);
    doAtlasTexture(textureLocation.xy, textureLocation.z);
    doFog(modelViewMatrix, vertexLocation.xyz);

    gl_Position = doTransform(projectionMatrix * modelViewMatrix, vertexLocation);
}

       
 #version 330
 #extension GL_ARB_explicit_uniform_location : enable
       
layout(location = 128) uniform usamplerBuffer textureIndexRotationBuffer;
layout(location = 129) uniform samplerBuffer atlasIndexing;
layout(location = 130) uniform vec2[10] textureScales;
out vec2 atlasCoordinate;
out flat uint atlasIndex;
vec2 rotateUV(vec2 uv, uint rotation){
    rotation &= 3u;
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
    uint textureIndexRotationPacked = texelFetch(textureIndexRotationBuffer, int(6 * gl_InstanceID + faceID)).x;
    uint rotation = textureIndexRotationPacked & 0x3u;
    vec2 rotatedCoordinate = rotateUV(textureCoordinate, rotation);
    rotatedCoordinate *= 0.999;
    int textureIndex = int((textureIndexRotationPacked >> 2) & 0x3FFFu);
    vec3 atlasLookupInfo = texelFetch(atlasIndexing, textureIndex).xyz;
    atlasIndex = int(atlasLookupInfo.z);
    atlasCoordinate = rotatedCoordinate * textureScales[atlasIndex];
    atlasCoordinate += atlasLookupInfo.xy;
}
       
out float fogCoord;
void doFog(mat4 modelViewMatrix, vec3 position){
    fogCoord = length(modelViewMatrix * vec4(position, 1));
}
       
out uvec4 lightLevels;
out vec2 lightPosition;
out float AOMultiplier;
void doLighting(uvec4 levels, vec2 position, float multiplier){
    lightLevels = levels;
    lightPosition = position;
    AOMultiplier = multiplier;
}
       
vec4 doTransform(mat4 MVPMatrix, vec3 vertex) {
    return MVPMatrix * vec4(vertex, 1.0);
}
layout(location = 0) in vec3 vertex;
layout(location = 1) in vec2 textureCoordinate;
layout(location = 2) in uint faceVertexID;
layout(location = 3) in uvec3 positionIn;
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

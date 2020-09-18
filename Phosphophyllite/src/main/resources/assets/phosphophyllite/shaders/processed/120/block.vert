       
 #version 120
 #extension GL_EXT_texture_integer : enable
       
uniform sampler1D atlasIndexing;
uniform vec2[10] textureScales;
varying vec2 atlasCoordinate;
varying flat uint atlasIndex;
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
    uint textureIndexRotationPacked = faceID;
    uint rotation = textureIndexRotationPacked & 0x3u;
    vec2 rotatedCoordinate = rotateUV(textureCoordinate, rotation);
    float textureIndexNormalized = textureIndexRotationPacked >> 2;
    textureIndexNormalized /= 16384;
    vec3 atlasLookupInfo = texture1DLod(atlasIndexing, textureIndexNormalized, 0).xyz;
    atlasCoordinate = rotatedCoordinate * textureScales[atlasIndex];
    atlasCoordinate += atlasLookupInfo.xy;
}
       
varying float fogCoord;
void doFog(mat4 modelViewMatrix, vec3 position){
    fogCoord = length(modelViewMatrix * vec4(position, 1));
}
       
varying uvec4 lightLevels;
varying vec2 lightPosition;
varying float AOMultiplier;
void doLighting(uvec4 levels, vec2 position, float multiplier){
    lightLevels = levels;
    lightPosition = position;
    AOMultiplier = multiplier;
}
       
vec4 doTransform(mat4 MVPMatrix, vec3 vertex) {
    return MVPMatrix * vec4(vertex, 1.0);
}
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

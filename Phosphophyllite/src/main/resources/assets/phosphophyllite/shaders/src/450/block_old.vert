//#version 450
//#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec3 vertex;
layout(location = 1) in vec2 textureCoordinate;
layout(location = 2) in float faceVertexIDFloat;
layout(location = 3) in uvec3 positionIn;

layout(location = 0) uniform vec3 playerOffset;
layout(location = 1) uniform usamplerBuffer textureIndexRotationBuffer;
//layout(location = 2) uniform usamplerBuffer lightmapBuffer;
layout(location = 3) uniform sampler2D indicesLookup;
layout(location = 4) uniform vec2[4] textureScales;
//layout(location = 8) uniform sampler2DArray[4] textures;
//layout(location = 12) uniform sampler2D lightmap;
//layout(location = 13) uniform vec4 fogColor;
//layout(location = 14) uniform vec2 fogScaleEnd;
layout(location = 15) uniform mat4 projectionMatrix;
layout(location = 19) uniform mat4 modelViewMatrix;

layout(location = 0) out vec3 atlasCoordinate;
layout(location = 5, component = 3) out flat int atlasIndex;
layout(location = 1) out vec2 lightAveragePos;
layout(location = 2, component = 3) out float fogCoord;
layout(location = 3) out flat uint lightmapBufferOffset;
layout(location = 4) out flat float diffuseMultiplier;

vec2 rotateUV(vec2 uv, uint rotation)
{
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

void main() {

    // id
    uint faceVertexID = uint(faceVertexIDFloat);
    uint faceID = faceVertexID & 0x0Fu;
    uint vertexID = (faceVertexID >> 4u) & 0xFu;

    // test


    // hidden face unpacking
    vec3 position = positionIn & 0xFu;
    uint hiddenFaces;
    hiddenFaces += positionIn.z >> 4;
    hiddenFaces << 2;
    hiddenFaces += positionIn.y >> 4;
    hiddenFaces << 2;
    hiddenFaces += positionIn.x >> 4;

    // position
    vec4 worldPos =  vec4(playerOffset + vertex + position, 1.0);
    vec4 viewPos = modelViewMatrix * worldPos;
    gl_Position = projectionMatrix * viewPos;

    // fog
    fogCoord = length(viewPos);

    // face hiding
    uint hideFace = hiddenFaces & (1u << faceID);
    gl_Position.z = (hideFace * 2) + ((1 - hideFace) * gl_Position.z);
    gl_Position.w = (hideFace * 1) + ((1 - hideFace) * gl_Position.w);

    // lightmap
    lightAveragePos = textureCoordinate;
    lightmapBufferOffset = (24 * gl_InstanceID) + (4 * faceID);


    // texture
    uint textureIndexRotationPacked = texelFetch(textureIndexRotationBuffer, int(6 * gl_InstanceID + faceID)).x;
    {
        // rotation
        uint rotation = textureIndexRotationPacked & 0x3u;
        vec2 rotatedCoordinate = rotateUV(textureCoordinate, rotation);

        // atlas
        ivec2 textureIndex = ivec2((textureIndexRotationPacked >> 17) & 0x7Fu, (textureIndexRotationPacked >> 2) & 0x7Fu);
        //                ivec2 textureIndex = ivec2(0, 1);
        vec4 atlasLookupInfo = texelFetch(indicesLookup, textureIndex, 0);
        atlasIndex = int(ceil(atlasLookupInfo.w));
        // helps with sampling on the edge of the block, ensuring it doesnt sample the next texture
        rotatedCoordinate *= 0.999;
        rotatedCoordinate *= textureScales[int(atlasIndex)];
        atlasCoordinate = vec3(rotatedCoordinate, 0.5) + atlasLookupInfo.xyz;
    }

    // diffuse
    uint faceBit = 1u << faceID;
    float diffuse = 0;
    // west
    diffuse += ((faceBit >> 0u) & 1u) * 0.6;
    diffuse += ((faceBit >> 1u) & 1u) * 0.6;
    diffuse += ((faceBit >> 2u) & 1u) * 0.4;
    diffuse += ((faceBit >> 3u) & 1u) * 1.0;
    diffuse += ((faceBit >> 4u) & 1u) * 0.8;
    diffuse += ((faceBit >> 5u) & 1u) * 0.8;
    diffuseMultiplier = diffuse;
}

//#version 450
//#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec3 atlasCoordinate;
layout(location = 5, component = 3) in flat int atlasIndex;
layout(location = 1) in vec2 lightingAveragePos;
layout(location = 2, component = 3) in float fogCoord;
layout(location = 3) in flat uint lightmapBufferOffset;
layout(location = 4) in flat float diffuseMultiplier;


//layout(location = 0) uniform vec3 playerOffset;
//layout(location = 1) uniform usamplerBuffer textureIndexRotationBuffer;
layout(location = 2) uniform usamplerBuffer lightmapBuffer;
//layout(location = 3) uniform sampler2D indicesLookup;
//layout(location = 4) uniform vec2[4] textureScales;
layout(location = 8) uniform sampler2DArray[4] textures;
layout(location = 12) uniform sampler2D lightmap;
layout(location = 13) uniform vec4 fogColor;
layout(location = 14) uniform vec2 fogScaleEnd;
//layout(location = 15) uniform mat4 projectionMatrix;
//layout(location = 19) uniform mat4 modelViewMatrix;

layout(location = 0) out vec4 fragColor;

void main() {
    fragColor = vec4(1, 1, 1, 1) * diffuseMultiplier;

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
    }

    fragColor *= textureColor;


    {
        // TODO: 6 bit input for the two lighting coords, space is there already
        // TODO: AO color multiplier input, use two of above extra 4 bits
        uint lightmapPacked0 = texelFetch(lightmapBuffer, int(lightmapBufferOffset + 0)).x;
        uint lightmapPacked1 = texelFetch(lightmapBuffer, int(lightmapBufferOffset + 1)).x;
        uint lightmapPacked2 = texelFetch(lightmapBuffer, int(lightmapBufferOffset + 2)).x;
        uint lightmapPacked3 = texelFetch(lightmapBuffer, int(lightmapBufferOffset + 3)).x;

        vec2 lightmapPos0 = vec2((lightmapPacked0 >> 6) & 0x3Fu, lightmapPacked0 & 0x3Fu);
        vec2 lightmapPos1 = vec2((lightmapPacked1 >> 6) & 0x3Fu, lightmapPacked1 & 0x3Fu);
        vec2 lightmapPos2 = vec2((lightmapPacked2 >> 6) & 0x3Fu, lightmapPacked2 & 0x3Fu);
        vec2 lightmapPos3 = vec2((lightmapPacked3 >> 6) & 0x3Fu, lightmapPacked3 & 0x3Fu);

        lightmapPos0 = lightmapPos0 * lightingAveragePos.x + lightmapPos1 * (1 - lightingAveragePos.x);
        lightmapPos2 = lightmapPos2 * lightingAveragePos.x + lightmapPos3 * (1 - lightingAveragePos.x);
        lightmapPos0 = lightmapPos0 * lightingAveragePos.y + lightmapPos2 * (1 - lightingAveragePos.y);


        lightmapPos0 /= 4.0;
        lightmapPos0 = clamp(lightmapPos0, 0.0, 15.0);
        lightmapPos0 += 1.0/2.0;
        lightmapPos0 /= 16.0;

        //        lightmapPos0.x += 1;
        vec4 lightmapColor = texture(lightmap, lightmapPos0);
        //        vec4 lightmapColor = texture(lightmap, lightingAveragePos);

        vec4 lightColor = lightmapColor;

        fragColor *= lightColor;
    }


    float fogFactor = (fogScaleEnd.y - fogCoord) * fogScaleEnd.x;
    fogFactor = clamp(fogFactor, 0.0, 1.0);
    fragColor = mix(fogColor, fragColor, fogFactor);

    //    fragColor = vec4(atlasIndex, 0, 0, 1);
    //
    //    fragColor = vec4(1, 1, 1, 1);

    //    fragColor = vec4(lightColor, 1);


    //    if(textureColor.a < .5){
    //        discard;
    //    }
}



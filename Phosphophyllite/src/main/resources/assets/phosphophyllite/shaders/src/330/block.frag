#define VERSION_330
#include "util/versioning.glsl"
#include "util/textureatlas.frag"
#include "util/fog.frag"
#include "util/lighting.frag"
#include "util/cutout.frag"

layout(location = 0) out vec4 color;

void main() {
    vec4 fragColor = vec4(1, 1, 1, 1);

    fragColor = doLighting(fragColor);
    fragColor = doAtlasTexture(fragColor);
    fragColor = doFog(fragColor);

    doCutout(fragColor);

    color = fragColor;
}

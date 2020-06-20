layout(location = 0) in vec3 position;
layout(location = 0) uniform vec3 positionOffset;
layout(location = 1) uniform mat4 mvpMatrix;
void main() {
    gl_Position = mvpMatrix * vec4(positionOffset + position, 1);
}

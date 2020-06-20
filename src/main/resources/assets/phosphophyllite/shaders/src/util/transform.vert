#pragma once
vec4 doTransform(mat4 MVPMatrix, vec3 vertex) {
    return MVPMatrix * vec4(vertex, 1.0);
}

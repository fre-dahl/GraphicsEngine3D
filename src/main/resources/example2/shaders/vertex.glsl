#version 330

layout (location=0) in vec3 v_in_position;
layout (location=1) in vec2 v_in_texCoords;
layout (location=2) in vec3 v_in_normal;

out vec2 texCoords;
out vec3 mvVertexPos;
out vec3 mvVertexNormal;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform mat3 normalMatrix;

void main()
{
    vec4 mvPos = modelViewMatrix * vec4(v_in_position, 1.0);
    gl_Position = projectionMatrix * mvPos;
    texCoords = v_in_texCoords;
    mvVertexNormal = normalize(normalMatrix * v_in_normal);
    //mvVertexNormal = normalize(modelViewMatrix * vec4(v_in_normal, 0.0)).xyz;
    mvVertexPos = mvPos.xyz;
}

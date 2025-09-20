#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform float GameTime;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

void main() {
    // Use mod to keep time manageable
    float time = mod(GameTime * 1000.0, 1000.0);

    // Pixelate
    vec2 uv = floor(texCoord0 * 10.0) / 10.0;

    // Create falling motion
    float fall = uv.y - time * 0.001;

    // Multiple columns of falling particles
    float col1 = hash(vec2(floor(uv.x * 10.0), floor(fall * 20.0)));
    float col2 = hash(vec2(floor(uv.x * 10.0 + 0.5), floor(fall * 20.0 + 10.0)));
    float col3 = hash(vec2(floor(uv.x * 10.0 - 0.3), floor(fall * 20.0 + 5.0)));

    // Create threshold for visible particles
    float particles = 0.0;
    if (col1 > 0.6) particles += 1.0;
    if (col2 > 0.65) particles += 0.8;
    if (col3 > 0.7) particles += 0.6;

    float intensity = particles * 0.4;

    // Red waterfall color
    vec3 color = vec3(intensity * 0.9, intensity * 0.1, intensity * 0.05);

    fragColor = vec4(color, 1.0);
}
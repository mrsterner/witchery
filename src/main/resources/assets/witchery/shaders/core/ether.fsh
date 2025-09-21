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
    float time = GameTime * 800.0;

    float pixelSize = 0.004;
    vec2 uv = floor(texCoord0 / pixelSize) * pixelSize;

    float fall = time * 0.9;
    float x = uv.x * 15.0;
    float y = uv.y * 3.0 - fall;

    float columnOffset = hash(vec2(floor(x), 0.0)) * 5.0;
    float yVar = floor(y * 16.0 + columnOffset);

    float col1 = step(0.25, fract(sin(floor(x)) * 43.7 + yVar));
    float col2 = step(0.2, fract(sin(floor(x * 0.7 + 2.3)) * 67.3 + yVar * 1.1));
    float col3 = step(0.15, fract(sin(floor(x * 1.3 - 1.7)) * 31.4 + yVar * 0.9));
    float col4 = step(0.3, fract(sin(floor(x * 0.9 + 4.1)) * 89.2 + yVar));
    float col5 = step(0.25, fract(sin(floor(x * 1.1 - 3.2)) * 52.1 + yVar * 1.2));
    float col6 = step(0.2, fract(sin(floor(x * 1.5 + 1.7)) * 71.3 + yVar * 0.8));
    float col7 = step(0.18, fract(sin(floor(x * 0.8 - 2.1)) * 29.7 + yVar * 1.15));

    float pixelNoise = hash(vec2(floor(x * 8.0), floor(y * 8.0))) * 0.3;

    float falls = (col1 + col2 * 0.9 + col3 * 0.85 + col4 * 0.9 + col5 * 0.8 + col6 * 0.85 + col7 * 0.75) + pixelNoise;

    float pulse = 0.85 + 0.15 * sin(time * 0.02 + x);
    falls *= pulse;

    float splashY = floor(y * 4.0 - columnOffset * 0.5);
    float splash = step(0.4, hash(vec2(floor(x * 2.0), splashY)));
    float splash2 = step(0.5, hash(vec2(floor(x * 3.0 + 1.0), splashY + 1.0)));
    float splash3 = step(0.45, hash(vec2(floor(x * 2.5 - 0.5), splashY - 1.0)));

    float intensity = 0.2 + falls * 0.5 + splash * 0.2 + splash2 * 0.15 + splash3 * 0.18;

    vec3 color = vec3(intensity * 1.0, intensity * 0.08, intensity * 0.04);

    if (intensity > 0.8) {
        color += vec3(0.1, 0.05, 0.05) * (intensity - 0.8);
    }

    fragColor = vec4(color, 1.0);
}
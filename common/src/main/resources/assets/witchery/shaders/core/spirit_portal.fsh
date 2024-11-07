#version 150

#moj_import <fog.glsl>

#define PI 3.1415926538

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

void main() {

    float time = GameTime * 500.0;
    vec2 texCopy = texCoord0;

    float aspectRatio = 9.0 / 9.0;
    texCopy.y /= aspectRatio;

    float pixelationFactor = 200.0;
    texCopy = floor(texCopy * pixelationFactor) / pixelationFactor;

    texCopy *= vec2(5.0, 5.0);
    vec2 p = mod(texCopy * PI * 2.0, PI * 2.0) - 256.0;

    vec2 i = vec2(p);
    float c = 1.0;
    float inten = 0.005;
    float alpha = 0.1;

    for (int n = 0; n < 5; n++) {
        float t = time * (1.0 - (3.5 / float(n + 1)));
        i = p + vec2(cos(t - i.x) + sin(t + i.y), sin(t - i.y) + cos(t + i.x));
        c += 1.0 / length(vec2(p.x / (sin(i.x + t) / inten), p.y / (cos(i.y + t) / inten)));
    }
    c /= 5.0;
    c = 1.17 - pow(c, 1.4);
    vec3 colour = vec3(pow(abs(c), 8.0));
    colour = clamp(colour + vec3(0.3, 0.7, 0.7), 0.0, 1.0);

    if (length(colour.rgb) < 0.1 * 3.0) {
        discard;
    }

    fragColor = vec4(colour, max(colour.r * 0.1, max(colour.g * 0.1, colour.b)) * 0.5);
}
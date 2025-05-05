#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform float GameTime;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;

in float vertexDistance;
in vec4 vertexColor;
in vec4 overlayColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 baseColor = texture(Sampler0, texCoord0);
    if (baseColor.a < 0.1) {
        discard;
    }

    float pulse = 0.5 + 0.5 * sin(GameTime * 5000.0);
    vec3 emissiveColor = mix(vec3(0.65, 0.0, 0.85), vec3(0.55, 0.0, 0.7), pulse);

    float brightness = baseColor.r;

    float intensityFactor = 0.75;

    vec3 finalColor = emissiveColor * brightness * 2.75 * intensityFactor;

    finalColor = mix(overlayColor.rgb, finalColor, overlayColor.a);

    fragColor = vec4(finalColor, 1.0) * baseColor.a * ColorModulator.a;
}
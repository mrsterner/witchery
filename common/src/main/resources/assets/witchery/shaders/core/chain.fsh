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

    float pulse = 0.5 + 0.5 * sin(GameTime * 300.0);
    vec3 emissiveColor = mix(vec3(1.0, 0.85, 0.2), vec3(1.0, 0.65, 0.0), pulse);

    vec4 lightningColor = vec4(emissiveColor * 2.5, 1.0);

    lightningColor.rgb = mix(overlayColor.rgb, lightningColor.rgb, overlayColor.a);

    fragColor = lightningColor * baseColor.a * ColorModulator.a;
}
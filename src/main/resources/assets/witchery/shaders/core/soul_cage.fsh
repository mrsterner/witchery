#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

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

    vec3 emissiveColor = vec3(0.5, 0.7, 1.0);

    vec3 tintedColor = mix(baseColor.rgb, emissiveColor, 0.3);

    float intensityFactor = 1.25;
    vec3 finalColor = tintedColor * intensityFactor;

    float alpha = 0.7 * baseColor.a;

    fragColor = vec4(finalColor, alpha);
}
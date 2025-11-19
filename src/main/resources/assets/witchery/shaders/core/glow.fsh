#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 baseColor = texture(Sampler0, texCoord0);

    if (baseColor.a < 0.01) {
        discard;
    }

    // Make the glow emit light (ignore lighting)
    vec3 glowColor = baseColor.rgb * 2.0;

    // Apply alpha from ColorModulator (this is where we control fade in/out)
    float alpha = baseColor.a * vertexColor.a * ColorModulator.a;

    // Mix with overlay if present
    glowColor = mix(overlayColor.rgb, glowColor, overlayColor.a);

    vec4 color = vec4(glowColor, alpha);

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
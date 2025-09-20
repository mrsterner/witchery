#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform float GameTime;

uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform float Alpha;

in vec2 texCoord0;
in float vertexDistance;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture(Sampler0, texCoord0);

    float time = mod(GameTime * 500.0, 628.318);
    float time2 = mod(GameTime * 300.0, 628.318);
    float time3 = mod(GameTime * 700.0, 628.318);

    float wave1 = sin(texCoord0.x * 15.0 + time) * 0.5 + 0.5;
    float wave2 = sin(texCoord0.y * 15.0 - time2) * 0.5 + 0.5;
    float wave3 = sin((texCoord0.x + texCoord0.y) * 10.0 + time3) * 0.5 + 0.5;

    float combined = (wave1 + wave2 + wave3) / 3.0;

    float dist = length(texCoord0 - 0.5);
    float ripple = sin(dist * 30.0 - time * 2.0) * 0.5 + 0.5;

    float pattern = mix(combined, ripple, 0.5);

    vec3 color1 = vec3(0.1, 0.4, 1.0);
    vec3 color2 = vec3(0.5, 0.2, 0.9);
    vec3 color3 = vec3(0.2, 0.8, 1.0);

    float colorPhase = mod(GameTime * 100.0, 6.28318);
    vec3 magicColor = mix(color1, color2, pattern);
    magicColor = mix(magicColor, color3, sin(colorPhase) * 0.5 + 0.5);

    vec3 finalColor = textureColor.rgb * magicColor * 2.5;

    float sparklePhase = mod(GameTime * 1000.0, 6.28318);
    float sparkle = pow(pattern, 8.0) * sin(sparklePhase);
    finalColor += vec3(sparkle * 0.3);

    float finalAlpha = textureColor.a * Alpha;

    vec4 color = vec4(finalColor, finalAlpha);
    color.rgb *= color.a;

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
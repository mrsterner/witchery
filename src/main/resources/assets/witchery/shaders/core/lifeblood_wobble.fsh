#version 150

uniform sampler2D Sampler0;
uniform float GameTime;
uniform float Intensity;
uniform float Speed;
uniform float Frequency;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    float time = GameTime * Speed * 1000.0;

    vec2 center = vec2(0.5, 0.5);
    vec2 uv = texCoord0 - center;

    float rotationSpeed = 10.0;
    float angle = sin(time * 0.3) * 0.05;
    float cosAngle = cos(angle);
    float sinAngle = sin(angle);

    vec2 rotatedUV = vec2(
    uv.x * cosAngle - uv.y * sinAngle,
    uv.x * sinAngle + uv.y * cosAngle);

    float scale = 1.0 + sin(time * 0.4) * 0.02;
    rotatedUV *= scale;

    float waveX = sin(time + texCoord0.y * Frequency) * Intensity;
    float waveY = sin(time * 1.3 + texCoord0.x * Frequency * 0.7) * Intensity * 0.5;

    vec2 distortedUV = rotatedUV + center + vec2(waveX, waveY);

    vec4 color = texture(Sampler0, distortedUV);

    fragColor = color;
}
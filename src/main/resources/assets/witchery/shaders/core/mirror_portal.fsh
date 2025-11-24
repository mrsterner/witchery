#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform float GameTime;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord1;
in vec2 texCoord2;
in vec4 vertexPosition;
in vec3 vertexNormal;
in float vertexDistance;

out vec4 fragColor;

vec3 mod289(vec3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec2 mod289(vec2 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec3 permute(vec3 x) { return mod289(((x*34.0)+1.0)*x); }

float snoise(vec2 v) {
    const vec4 C = vec4(0.211324865405187, 0.366025403784439, -0.577350269189626, 0.024390243902439);
    vec2 i = floor(v + dot(v, C.yy));
    vec2 x0 = v - i + dot(i, C.xx);
    vec2 i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    vec4 x12 = x0.xyxy + C.xxzz;
    x12.xy -= i1;
    i = mod289(i);
    vec3 p = permute(permute(i.y + vec3(0.0, i1.y, 1.0)) + i.x + vec3(0.0, i1.x, 1.0));
    vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
    m = m*m;
    m = m*m;
    vec3 x = 2.0 * fract(p * C.www) - 1.0;
    vec3 h = abs(x) - 0.5;
    vec3 ox = floor(x + 0.5);
    vec3 a0 = x - ox;
    m *= 1.79284291400159 - 0.85373472095314 * (a0*a0 + h*h);
    vec3 g;
    g.x = a0.x * x0.x + h.x * x0.y;
    g.yz = a0.yz * x12.xz + h.yz * x12.yw;
    return 130.0 * dot(m, g);
}

float snoise3d(vec3 v) {
    return snoise(v.xy + v.z * 0.5) * 0.5 + snoise(v.yz + v.x * 0.3) * 0.5;
}

void main() {
    float time = GameTime * 1000.0;

    vec3 viewDir = normalize(vertexPosition.xyz);
    vec2 parallaxOffset = viewDir.xy * 0.1;

    vec3 depthColor = vec3(0.0);
    float totalWeight = 0.0;

    for (int layer = 0; layer < 8; layer++) {
        float depth = float(layer) / 8.0;
        float depthZ = depth * 2.0;

        vec2 layerUV = texCoord0 + parallaxOffset * depth * 2.0;

        vec3 coord3d = vec3(layerUV * 2.0, depthZ - time * 0.05);

        float noise1 = snoise3d(coord3d * 2.0 + time * 0.02) * 0.5 + 0.5;
        float noise2 = snoise3d(coord3d * 4.0 - time * 0.015) * 0.5 + 0.5;
        float noise3 = snoise3d(coord3d * 8.0 + time * 0.01) * 0.5 + 0.5;

        float combinedNoise = noise1 * 0.5 + noise2 * 0.3 + noise3 * 0.2;

        vec3 layerColor = mix(
        vec3(0.2, 0.3, 0.5),
        vec3(0.6, 0.7, 0.9),
        combinedNoise
        );

        float depthFade = exp(-depth * 1.5);
        float weight = depthFade * (combinedNoise * 0.5 + 0.5);

        depthColor += layerColor * weight;
        totalWeight += weight;
    }

    depthColor /= max(totalWeight, 0.001);

    vec2 particleUV = texCoord0 * 3.0;
    float particles = 0.0;
    for (int i = 0; i < 4; i++) {
        float angle = float(i) * 1.5708 + time * 0.1;
        vec2 offset = vec2(cos(angle), sin(angle)) * 0.5;
        float particle = snoise(particleUV + offset + time * 0.03);
        particles += max(0.0, particle - 0.7) * 5.0; // Only bright spots
    }

    depthColor += vec3(0.5, 0.6, 0.8) * particles * 0.3;

    vec2 edgeUV = texCoord0 * 2.0 - 1.0;
    float edgeDist = length(edgeUV);
    float edgeGlow = smoothstep(0.5, 1.0, edgeDist) * 0.3;
    depthColor += vec3(0.4, 0.5, 0.7) * edgeGlow;

    vec4 texColor = texture(Sampler0, texCoord0 * 0.5 + time * 0.01);
    depthColor = mix(depthColor, texColor.rgb * vec3(0.4, 0.5, 0.7), 0.2);

    float fogFactor = smoothstep(FogStart, FogEnd, vertexDistance);
    vec3 finalColor = mix(depthColor, FogColor.rgb, fogFactor * 0.4);

    float pulse = sin(time * 0.05) * 0.5 + 0.5;
    finalColor += vec3(0.3, 0.4, 0.6) * pulse * 0.1;

    finalColor = pow(finalColor, vec3(0.9)) * 1.3;

    float alpha = 0.85 + snoise(texCoord0 * 5.0 + time * 0.02) * 0.1;

    fragColor = vec4(finalColor, alpha * vertexColor.a);
}
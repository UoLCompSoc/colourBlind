#version 110

#ifdef GL_ES 
#define LOW lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOW
#define HIGH
#endif

#define FLASHLIGHT_FLOAT_COUNT 3
#define FLASHLIGHT_COUNT_MAX 24

uniform float flashLight; // 1.0 if the flash light is on, 0.0 otherwise
uniform float flashLightSize; // the size of the flash light's beam.
uniform vec2 lightCoord; // coordinates of the light source if the flash light is on
uniform float platform; // 1.0 if we're drawing platforms, 0.0 otherwise.

// An array of vectors, laid out as:
// i*FLASHLIGHT_FLOAT_COUNT + 0 = flashlights.x = x coordinate
// i*FLASHLIGHT_FLOAT_COUNT + 1 = flashlights.y = y coordinate
// i*FLASHLIGHT_FLOAT_COUNT + 2 = flashlights.z = radius
uniform float flashlights[FLASHLIGHT_COUNT_MAX];
uniform int lightsOn;

uniform vec4 inputColour;
uniform sampler2D u_texture;

varying vec2 vTexCoord0;
varying vec4 vColour;
varying vec2 vPosition;

void main() {
    vec4 texColour = texture2D(u_texture, vTexCoord0.st);
    
    if(inputColour.a > 0.0) {
        gl_FragColor = vec4(inputColour.rgb, texColour.a);
        return;
    }
    
    for(int i = 0; i < lightsOn; i++) {
        vec2 pos = vec2(flashlights[i * FLASHLIGHT_FLOAT_COUNT + 0],
                        flashlights[i * FLASHLIGHT_FLOAT_COUNT + 1]);
        float radius = flashlights[i*FLASHLIGHT_FLOAT_COUNT + 2];
        
        float dist = distance(vPosition.xy, pos.xy);
        float perc = (radius - dist) / radius;
        
        if(perc > 0.0) {
            gl_FragColor = vec4(vColour.rgb * perc, texColour.a);
            return;
        }
    }
    
    gl_FragColor = vec4(texColour); // if we get here just output the texture colour
}

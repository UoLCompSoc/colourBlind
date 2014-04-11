#version 110

#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

#define PI 3.14 

uniform float flashLight; // 1.0 if the flash light is on, 0.0 otherwise
uniform float flashLightSize; // the size of the flash light's beam.
uniform vec2 lightCoord; // coordinates of the light source if the flash light is on
uniform float platform; // 1.0 if we're drawing platforms, 0.0 otherwise.

uniform vec4 inputColour;
uniform sampler2D u_texture;

varying vec2 vTexCoord0;
varying vec4 vColour;
varying vec2 vPosition;
 
void main() {
	vec4 texColour = texture2D(u_texture, vTexCoord0.st);
	/*
     * If player: Ignore texture colour except alpha and draw colour
     * If platform: Draw true colour for flashLight > 0.5 or
     * else draw texture.
     */
    
	if(platform > 0.5) {
        if(flashLight > 0.5) {
            float dist = distance(lightCoord.xy, vPosition.xy);
            float perc = (flashLightSize-dist)/flashLightSize; 
            if(perc > 0.0) {
                // if distance between this point and the centre of light is smaller
                // than threshold, reveal the point's true colour, or else black texture.
                gl_FragColor = vec4(vColour.rgb * perc, texColour.a);
            } else {
         	   gl_FragColor = texColour;
       	 	}
        } else {
            gl_FragColor = texColour;
        }
    } else if(texColour.a > 0.5) {
		gl_FragColor = vec4(inputColour.rgb, texColour.a);
	} else {
		gl_FragColor = texColour;
	}
}

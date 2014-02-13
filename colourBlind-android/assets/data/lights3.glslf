#define PI 3.14 

uniform vec4 inputColour;
uniform float platform;
uniform vec4 platformColour;

varying vec2 vTexCoord0;
varying vec4 vColour;

uniform sampler2D u_texture;
 
void main(void) {
	vec4 texColour = texture2D(u_texture, vTexCoord0.st);
	
	/*
	 * If player: Ignore texture colour except alpha and draw colour
	 * If platform: Draw true colour for flashlight (vColour.a > 0.0) or
	 * else draw texture.
	 */
	
	if(platform > 0.5) {
		gl_FragColor = platformColour;
	} else if(texColour.a > 0.5) {
		gl_FragColor = vec4(inputColour.rgb,texColour.a);
	} else {
		gl_FragColor = texColour;
	}
	

	/*
	vec4 ocolour = texture2D(u_texture, vTexCoord0.st);
	
	if(ocolour.a > 0.5) {
		if(platform > 0.5) {
			if(vColour.a > 0.5) {
				// vColour.a > 0.5 when we should display the "true" colour
				gl_FragColor = vec4(texture2D(u_texture, vTexCoord0.st).rgb, vColour.a);
			} else {
				gl_FragColor = ocolour;
			}
		} else {
			gl_FragColor = clamp(ocolour + inputColour, 0.0, 1.0);
		} 
	} else {
		gl_FragColor = ocolour;
	}
	*/
}
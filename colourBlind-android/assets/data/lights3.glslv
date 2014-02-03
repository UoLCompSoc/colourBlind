attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

uniform float flashLight; // 1.0 if the flash light is on, 0.0 otherwise
uniform float flashLightSize; // the size of the flash light's beam.
uniform vec2 lightCoord; // coordinates of the light source if the flash light is on
uniform float platform; // 1.0 if we're drawing platforms, 0.0 otherwise.
 
varying vec2 vTexCoord0;
varying vec4 vColour;
 
void main() {
	if(flashLight > 0.5
	&& platform > 0.5
	&& distance(lightCoord, a_position.xy) <= flashLightSize) {
		// if distance between this point and the centre of light is smaller
		// than threshold, reveal the point's true colour, or else black texture.
		
		vColour = vec4(1.0, 0.0, 0.0, 1.0);
	} else {
		vColour = vec4(0.0, 0.0, 0.0, 0.0);
	}
	
	vTexCoord0 = a_texCoord0;
	gl_Position = u_projTrans * a_position;
}
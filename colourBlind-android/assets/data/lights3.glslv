#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

attribute vec4 a_position; // position of the place we're rendering
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

uniform float flashLight; // 1.0 if the flash light is on, 0.0 otherwise
uniform float flashLightSize; // the size of the flash light's beam.
uniform vec2 lightCoord; // coordinates of the light source if the flash light is on
uniform float platform; // 1.0 if we're drawing platforms, 0.0 otherwise.
 
varying vec2 vTexCoord0;
varying vec2 vPosition;
varying vec4 vColour;
 
void main() {
    vPosition = a_position.xy;
	vTexCoord0 = a_texCoord0;
	vColour = a_color;
	gl_Position = u_projTrans * a_position;
}
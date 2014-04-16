#version 110

#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

#define FLASHLIGHT_COUNT_MAX 24

attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
 
uniform float flashlights[FLASHLIGHT_COUNT_MAX];
uniform int lightsOn;

uniform vec4 inputColour;

varying vec2 vTexCoord0;
varying vec2 vPosition;
varying vec4 vColour;
 
void main() {
    vPosition = a_position.xy;
	vTexCoord0 = a_texCoord0;
	vColour = a_color;
	gl_Position = u_projTrans * a_position;
}
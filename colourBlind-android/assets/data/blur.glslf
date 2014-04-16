/**
 * Copyright (c) 2012, Matt DesLauriers All rights reserved.
 *
 *	Redistribution and use in source and binary forms, with or without
 *	modification, are permitted provided that the following conditions are met:
 *
 *	* Redistributions of source code must retain the above copyright notice, this
 *	  list of conditions and the following disclaimer.
 *
 *	* Redistributions in binary
 *	  form must reproduce the above copyright notice, this list of conditions and
 *	  the following disclaimer in the documentation and/or other materials provided
 *	  with the distribution.
 *
 *	* Neither the name of the Matt DesLauriers nor the names
 *	  of his contributors may be used to endorse or promote products derived from
 *	  this software without specific prior written permission.
 *
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *	IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *	LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *	CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *	SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *	INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *	CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *	ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *	POSSIBILITY OF SUCH DAMAGE.
 * 
 *  Modified by Ashley Davis (SgtCoDFish) for use in colourBlind.
 */

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

#define PI 3.141592563

varying LOWP vec4 vColor;
varying vec2 vTexCoord0;

uniform sampler2D u_texture;
uniform float resolution;
uniform float radius;
uniform float timeVal;
uniform vec4 inputColour;

void main() {
    vec4 sum = vec4(0.0);

    vec2 tc = vTexCoord0;
    float alpha = texture2D(u_texture, tc).a;

    //the amount to blur, i.e. how far off center to sample from 
    //1.0 -> blur by one pixel
    //2.0 -> blur by two pixels, etc.
    float t = timeVal;
    float sinVal = (sin(t*4.0) + 1.0);
    sinVal = clamp(sinVal, 0.0, 1.0);
    float blur = radius/resolution * sinVal;
    
    sum += texture2D(u_texture, vec2(tc.x - 4.0*blur, tc.y)) * 0.0162162162;
    sum += texture2D(u_texture, vec2(tc.x - 3.0*blur, tc.y)) * 0.0540540541;
    sum += texture2D(u_texture, vec2(tc.x - 2.0*blur, tc.y)) * 0.1216216216;
    sum += texture2D(u_texture, vec2(tc.x - 1.0*blur, tc.y)) * 0.1945945946;

    sum += texture2D(u_texture, vec2(tc.x, tc.y)) * 0.2270270270;

    sum += texture2D(u_texture, vec2(tc.x + 1.0*blur, tc.y)) * 0.1945945946;
    sum += texture2D(u_texture, vec2(tc.x + 2.0*blur, tc.y)) * 0.1216216216;
    sum += texture2D(u_texture, vec2(tc.x + 3.0*blur, tc.y)) * 0.0540540541;
    sum += texture2D(u_texture, vec2(tc.x + 4.0*blur, tc.y)) * 0.0162162162;

    vec4 col = vec4(sum.r * inputColour.r, sum.g * inputColour.g, sum.b * inputColour.b, alpha);
    gl_FragColor = vec4(sum.rgb, alpha);
}
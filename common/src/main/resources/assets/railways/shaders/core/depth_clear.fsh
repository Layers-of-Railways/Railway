#version 150

in vec4 vertexColor;

uniform vec4 ColorModulator;

out vec4 fragColor;

void main() {
    vec4 color = vertexColor;
    gl_FragDepth = 1.0;
    //discard;
    
    if (color.a == 0.0) {
        discard;
    }
    // Avoid UB just in case
    fragColor = color * ColorModulator;
}

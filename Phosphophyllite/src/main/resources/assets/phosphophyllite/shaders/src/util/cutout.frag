#pragma once
void doCutout(vec4 color){
    if(color.w < 0.1){
        discard;
    }
}
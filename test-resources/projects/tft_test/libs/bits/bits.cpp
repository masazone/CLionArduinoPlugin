//
// Created by Vladimir Schneider on 2018-11-06.
//
#include "bits.h"

uint8_t bitCount(uint8_t v) {
    if (!v) return 0;

    v = v - ((v >> 1) & 0x55);
    v = ((v >> 2) & 0x33) + (v & 0x33);
    v = ((v >> 4) + v) & 0x0f;
    return v;
}


//
// Created by Vladimir Schneider on 2018-11-02.
//

#ifndef TFT_LIFE_LIFEDEFS_H
#define TFT_LIFE_LIFEDEFS_H

#define CELLXY 2

#define GRIDX (128/CELLXY)
#define GRIDY (160/CELLXY)

#define GRIDX_BYTES ((GRIDX+7)/8)

#define GEN_DELAY 0

typedef uint8_t grid_t[GRIDY][GRIDX_BYTES];

typedef uint8_t grid_t[GRIDY][GRIDX_BYTES];

#endif //TFT_LIFE_LIFEDEFS_H

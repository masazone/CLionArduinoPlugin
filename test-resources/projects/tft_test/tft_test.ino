//-----------------------------------------------------------------------------
// TFT: Experiments interfacing ATmega328 to an ST7735 1.8" LCD TFT display
//
// Author : Bruce E. Hall <bhall66@gmail.com>
// Website : w8bh.net
// Version : 1.0
// Date : 04 May 2014
// Target : ATmega328P microcontroller
// Language : C, using AVR studio 6
// Size : 3622 bytes
//
// Fuse settings: 8 MHz osc with 65 ms Delay, SPI enable; *NO* clock/8
//
// Connections from LCD to DC Boarduino:
//
// TFT pin 1 (backlight)              +5V
// TFT pin 2 (MISO)                   n/c
// TFT pin 3 (SCK)                    digital13, PB5(SCK)
// TFT pin 4  (MOSI)                 digital11, PB3(MOSI)
// TFT pin 5  (TFT_Select)           gnd
// TFT pin 7  (DC)                   digital8,  PB0
// TFT pin 8  (Reset)                digital9,  PB1
// TFT pin 9  (Vcc)                  +5V
// TFT pin 10 (gnd)                  gnd
//
// Connections from LCD (KMR-1.8 SPI) clone board to Pro Mini 5V
// for the 3.3V version no resistors and can connect MISO (D12) to MOSI
// to allow read commands to read data from the LCD controller
//
// LED-     GND
// LED+     to +5V through a 120 ohm resistor, can increase resistance to dim the back light
// CS       Arduino D10 (CS), through a 1k resistor
// SCL      Arduino D13 (SCK), through a 1k resistor
// SDA      Arduino D11 (MOSI), through a 1k resistor and a 2k to ground (effectively forming a voltage divider for the signal).
// A0       Arduino D8 (DC), through a 1k resistor
// Reset    Arduino D9 , through a 1k resistor
// VCC      +5V, NOTE: that the display has a J1 on the back, open means 5V, closed 3.3V.
// GND      GND
//

//---------------------------------------------------------------------------
// GLOBAL DEFINES
#define F_CPU       (16000000L/2)       // run CPU at 8 MHz or 16 MHz
#define LED         5                   // Boarduino LED on PB5
#define clearBit(x, y) x &= ~_BV(y)     // equivalent to cbi(x,y)
#define setBit(x, y) x |= _BV(y)        // equivalent to sbi(x,y)

#define PORTB_OUT 0x2E

#define TFT_DC_PORT  PORTD              // DC 8
#define TFT_DC_BIT  5                       // DC 8

// DC 10
#define TFT_CS_PORT  PORTB
#define TFT_CS_BIT  2

// DC 9
#define TFT_RST_PORT PORTB
#define TFT_RST_BIT 1

// so we can turn off SPI and use these manually to try and read pixels
// DC 12
#define TFT_MISO_PORT PORTB
#define TFT_MISO_BIT 4

// DC 13
#define TFT_SCK_PORT  PORTB
#define TFT_SCK_BIT 5

#define RGB565(r, g, b) ((uint16_t)((((r) & 0xF8) << 8) | ((int)((g) & 0xFC) << 3) | (((b) & 0xF8) >> 3)))

#define RGB666(r, g, b) ((uint8_t)(r) << 16 | (uint8_t)(g) << 8 | (uint8)(b))

#define RGB565_TO_RGB888(c) (((long)((c) & 0xF800) << 8) | (((long)((c) & 0x07E0) << 5)) | (((long)((c) & 0x001F) << 1)))

#define READ_TFT

// ---------------------------------------------------------------------------
// INCLUDES
#include <Arduino.h>
#include <avr/io.h>         // deal with port registers
#include <avr/interrupt.h>  // deal with interrupt calls
#include <avr/pgmspace.h>   // put character data into progmem
#include <util/delay.h>     // used for _delay_ms function
#include <string.h>         // string manipulation routines
#include <avr/sleep.h>      // used for sleep functions
#include <stdlib.h>
#include "bits.h"

// ---------------------------------------------------------------------------
// TYPEDEFS
typedef uint8_t byte; // I just like byte & sbyte better
typedef int8_t sbyte;

// ---------------------------------------------------------------------------
// GLOBAL VARIABLES
const byte FONT_CHARS[96][5] PROGMEM = {
        {0x00, 0x00, 0x00, 0x00, 0x00}, // (space)
        {0x00, 0x00, 0x5F, 0x00, 0x00}, // !
        {0x00, 0x07, 0x00, 0x07, 0x00}, // "
        {0x14, 0x7F, 0x14, 0x7F, 0x14}, // #
        {0x24, 0x2A, 0x7F, 0x2A, 0x12}, // $
        {0x23, 0x13, 0x08, 0x64, 0x62}, // %
        {0x36, 0x49, 0x55, 0x22, 0x50}, // &
        {0x00, 0x05, 0x03, 0x00, 0x00}, // '
        {0x00, 0x1C, 0x22, 0x41, 0x00}, // (
        {0x00, 0x41, 0x22, 0x1C, 0x00}, // )
        {0x08, 0x2A, 0x1C, 0x2A, 0x08}, // *
        {0x08, 0x08, 0x3E, 0x08, 0x08}, // +
        {0x00, 0x50, 0x30, 0x00, 0x00}, // ,
        {0x08, 0x08, 0x08, 0x08, 0x08}, // -
        {0x00, 0x60, 0x60, 0x00, 0x00}, // .
        {0x20, 0x10, 0x08, 0x04, 0x02}, // /
        {0x3E, 0x51, 0x49, 0x45, 0x3E}, // 0
        {0x00, 0x42, 0x7F, 0x40, 0x00}, // 1
        {0x42, 0x61, 0x51, 0x49, 0x46}, // 2
        {0x21, 0x41, 0x45, 0x4B, 0x31}, // 3
        {0x18, 0x14, 0x12, 0x7F, 0x10}, // 4
        {0x27, 0x45, 0x45, 0x45, 0x39}, // 5
        {0x3C, 0x4A, 0x49, 0x49, 0x30}, // 6
        {0x01, 0x71, 0x09, 0x05, 0x03}, // 7
        {0x36, 0x49, 0x49, 0x49, 0x36}, // 8
        {0x06, 0x49, 0x49, 0x29, 0x1E}, // 9
        {0x00, 0x36, 0x36, 0x00, 0x00}, // :
        {0x00, 0x56, 0x36, 0x00, 0x00}, // ;
        {0x00, 0x08, 0x14, 0x22, 0x41}, // <
        {0x14, 0x14, 0x14, 0x14, 0x14}, // =
        {0x41, 0x22, 0x14, 0x08, 0x00}, // >
        {0x02, 0x01, 0x51, 0x09, 0x06}, // ?
        {0x32, 0x49, 0x79, 0x41, 0x3E}, // @
        {0x7E, 0x11, 0x11, 0x11, 0x7E}, // A
        {0x7F, 0x49, 0x49, 0x49, 0x36}, // B
        {0x3E, 0x41, 0x41, 0x41, 0x22}, // C
        {0x7F, 0x41, 0x41, 0x22, 0x1C}, // D
        {0x7F, 0x49, 0x49, 0x49, 0x41}, // E
        {0x7F, 0x09, 0x09, 0x01, 0x01}, // F
        {0x3E, 0x41, 0x41, 0x51, 0x32}, // G
        {0x7F, 0x08, 0x08, 0x08, 0x7F}, // H
        {0x00, 0x41, 0x7F, 0x41, 0x00}, // I
        {0x20, 0x40, 0x41, 0x3F, 0x01}, // J
        {0x7F, 0x08, 0x14, 0x22, 0x41}, // K
        {0x7F, 0x40, 0x40, 0x40, 0x40}, // L
        {0x7F, 0x02, 0x04, 0x02, 0x7F}, // M
        {0x7F, 0x04, 0x08, 0x10, 0x7F}, // N
        {0x3E, 0x41, 0x41, 0x41, 0x3E}, // O
        {0x7F, 0x09, 0x09, 0x09, 0x06}, // P
        {0x3E, 0x41, 0x51, 0x21, 0x5E}, // Q
        {0x7F, 0x09, 0x19, 0x29, 0x46}, // R
        {0x46, 0x49, 0x49, 0x49, 0x31}, // S
        {0x01, 0x01, 0x7F, 0x01, 0x01}, // T
        {0x3F, 0x40, 0x40, 0x40, 0x3F}, // U
        {0x1F, 0x20, 0x40, 0x20, 0x1F}, // V
        {0x7F, 0x20, 0x18, 0x20, 0x7F}, // W
        {0x63, 0x14, 0x08, 0x14, 0x63}, // X
        {0x03, 0x04, 0x78, 0x04, 0x03}, // Y
        {0x61, 0x51, 0x49, 0x45, 0x43}, // Z
        {0x00, 0x00, 0x7F, 0x41, 0x41}, // [
        {0x02, 0x04, 0x08, 0x10, 0x20}, // "\"
        {0x41, 0x41, 0x7F, 0x00, 0x00}, // ]
        {0x04, 0x02, 0x01, 0x02, 0x04}, // ^
        {0x40, 0x40, 0x40, 0x40, 0x40}, // _
        {0x00, 0x01, 0x02, 0x04, 0x00}, // `
        {0x20, 0x54, 0x54, 0x54, 0x78}, // a
        {0x7F, 0x48, 0x44, 0x44, 0x38}, // b
        {0x38, 0x44, 0x44, 0x44, 0x20}, // c
        {0x38, 0x44, 0x44, 0x48, 0x7F}, // d
        {0x38, 0x54, 0x54, 0x54, 0x18}, // e
        {0x08, 0x7E, 0x09, 0x01, 0x02}, // f
        {0x08, 0x14, 0x54, 0x54, 0x3C}, // g
        {0x7F, 0x08, 0x04, 0x04, 0x78}, // h
        {0x00, 0x44, 0x7D, 0x40, 0x00}, // i
        {0x20, 0x40, 0x44, 0x3D, 0x00}, // j
        {0x00, 0x7F, 0x10, 0x28, 0x44}, // k
        {0x00, 0x41, 0x7F, 0x40, 0x00}, // l
        {0x7C, 0x04, 0x18, 0x04, 0x78}, // m
        {0x7C, 0x08, 0x04, 0x04, 0x78}, // n
        {0x38, 0x44, 0x44, 0x44, 0x38}, // o
        {0x7C, 0x14, 0x14, 0x14, 0x08}, // p
        {0x08, 0x14, 0x14, 0x18, 0x7C}, // q
        {0x7C, 0x08, 0x04, 0x04, 0x08}, // r
        {0x48, 0x54, 0x54, 0x54, 0x20}, // s
        {0x04, 0x3F, 0x44, 0x40, 0x20}, // t
        {0x3C, 0x40, 0x40, 0x20, 0x7C}, // u
        {0x1C, 0x20, 0x40, 0x20, 0x1C}, // v
        {0x3C, 0x40, 0x30, 0x40, 0x3C}, // w
        {0x44, 0x28, 0x10, 0x28, 0x44}, // x
        {0x0C, 0x50, 0x50, 0x50, 0x3C}, // y
        {0x44, 0x64, 0x54, 0x4C, 0x44}, // z
        {0x00, 0x08, 0x36, 0x41, 0x00}, // {
        {0x00, 0x00, 0x7F, 0x00, 0x00}, // |
        {0x00, 0x41, 0x36, 0x08, 0x00}, // }
        {0x08, 0x08, 0x2A, 0x1C, 0x08}, // ->
        {0x08, 0x1C, 0x2A, 0x08, 0x08}, // <-
};

// ---------------------------------------------------------------------------
// MISC ROUTINES
// put into a routine to remove code inlining at cost of timing accuracy
void msDelay(int delay) {
    for (int i = 0; i < delay; i++) _delay_ms(1);
}

// flash the on-board LED at ~ 3 Hz
void FlashLED(byte count) {
    for (; count > 0; count--) {
        setBit(PORTB, LED);      // turn LED on
        msDelay(150);           // wait
        clearBit(PORTB, LED);    // turn LED off
        msDelay(150);           // wait
    }
}

// calculate integer value of square root
unsigned long intsqrt(unsigned long val) {
    unsigned long mulMask = 0x0008000;
    unsigned long retVal = 0;
    if (val > 0) {
        while (mulMask != 0) {
            retVal |= mulMask;
            if ((retVal * retVal) > val)
                retVal &= ~mulMask;
            mulMask >>= 1;
        }
    }
    return retVal;
}

// ---------------------------------------------------------------------------
// SPI ROUTINES
//
//       b7   b6  b5   b4   b3   b2   b1   b0
// SPCR: SPIE SPE DORD MSTR CPOL CPHA SPR1 SPR0
//       0    1   0    1 .  0    0    0    0
//       0    1   0    1 .  1    1    0    0
//
// Both CPOL = CPHA = 0 or 1 work, for RAMRD with 0 reads all 1's, with 1 reads all 0's
// other read commands work in both modes
//
// SPIE - enable SPI interrupt
// SPE - enable SPI
// DORD - 0=MSB first, 1=LSB first
// MSTR - 0=slave, 1=master
// CPOL - 0=clock starts low, 1=clock starts high
// CPHA - 0=read on rising-edge, 1=read on falling-edge
// SPRx - 00=osc/4, 01=osc/16, 10=osc/64, 11=osc/128 //
// SPCR = 0x50: SPI enabled as Master, mode 0, at 8/4 = 2 MHz or 2x 4MHz
// SPCR = 0x5E: SPI enabled as Master, mode 3, at 8/64 = 125 KHz
// SPCR = 0x5F: SPI enabled as Master, mode 3, at 8/128 = 62.5 KHz

#define SPCR_NORMAL     0x50
#define SPCR_READ       0x50
#define SPCR_MANUAL     0x00
#define CS_DELAY        2.0

// Use 2x for transmit, read will occur at 1x
#define SPI_2X

void OpenSPI() {
    SPCR = SPCR_NORMAL;       // SPI enabled as Master, Mode0 at 4 MHz
#ifdef SPI_2X
    setBit(SPSR, SPI2X);      // double the SPI rate: 4-->8 MHz
#endif
    clearBit(TFT_CS_PORT, TFT_CS_BIT);  // select TFT CS

//     SetBit(PORTB, 4);      // set pull up for PB3 MOSI when in input mode
    clearBit(PORTB, 4);      // disable pull up for PB3 MOSI when in input mode
}

void closeSPI() {
    SPCR = 0x00;              // clear SPI enable bit
    setBit(TFT_CS_PORT, TFT_CS_BIT);    // deselect TFT CS
}

byte writeByte(byte data) {
    SPDR = data;            // initiate transfer
    while (!(SPSR & 0x80)); // wait for transfer to complete
    return SPDR;
}

// ---------------------------------------------------------------------------
// ST7735 ROUTINES
#define SWRESET     0x01        // software reset
#define RDDID       0x04        // read id
#define SLPIN       0x10        // sleep in
#define SLPOUT      0x11        // sleep out
#define INVOFF      0x20        // inv off
#define INVON       0x21        // inv on
#define GAMSET      0x26        // gama set
#define DISPOFF     0x28        // display off
#define DISPON      0x29        // display on
#define CASET       0x2A        // column address set
#define RASET       0x2B        // row address set
#define RAMWR       0x2C        // RAM write
#define RAMRD       0x2E        // RAM read
#define MADCTL      0x36        // axis control
#define IDMOFF      0x38        // idle off
#define IDMON       0x39        // idle on
#define COLMOD      0x3A        // color mode

#define RDDID1      0xDA        // read id1
#define RDDID2      0xDB        // read id2
#define RDDID3      0xDC        // read id3

// panel commands do not work (probably EXTC is open so only display functions available)
#define WRID2       0xD1        // write id2
#define WRID3       0xD2        // write id3

#define RDDCOLMOD   0x0C        // read pixel format

// 1.8" TFT display constants
#define XSIZE   128
#define YSIZE   160
#define XMAX    XSIZE-1
#define YMAX    YSIZE-1

// Color constants
#define BLACK   0x0000
#define BLUE    0x001F
#define RED     0xF800
#define GREEN   0x0400
#define LIME    0x07E0
#define CYAN    0x07FF
#define MAGENTA 0xF81F
#define YELLOW  0xFFE0
#define WHITE   0xFFFF

// mode 5 = 16bit pixels (RGB565)
// mode 6 = 18bit pixels (RGB666)
#define COLMOD_PARAM 5

// 4 LSB's one of selects GC0 - GC3
#define GAMSET_PARAM 1

#if COLMOD_PARAM == 5
#define RGB(r, g, b) RGB565(r,g,b)
#else
#define RGB(r,g,b) RGB666(r,g,b)
#endif

byte curX, curY; // current x & y cursor position
int foreground = WHITE;
int background = BLACK;
long alphaBackground = RGB565_TO_RGB888(BLACK);

void WriteCmd(byte cmd) {
//    SetBit(TFT_CS_PORT, TFT_CS);  // deselect TFT CS
    clearBit(TFT_DC_PORT, TFT_DC_BIT);  // B1=DC; 0=command, 1=data
//    ClearBit(TFT_CS_PORT, TFT_CS);  // select TFT CS
    writeByte(cmd);
    setBit(TFT_DC_PORT, TFT_DC_BIT); // return DC high
}

long ReadID() {
    WriteCmd(RDDID);

    DDRB = PORTB_OUT & ~0x08;              // 0010.0111; set B0-B2, B5 as outputs, B3 set SPI MOSI as input

    SPDR = 0;                 // dummy read
    while (!(SPSR & 0x80));   // wait for transfer to complete
    long id = (long)SPDR << 24;

    SPDR = 0;                 // dummy read
    while (!(SPSR & 0x80));   // wait for transfer to complete
    id |= (long)SPDR << 16;

    SPDR = 0;                 // dummy read
    while (!(SPSR & 0x80));   // wait for transfer to complete
    id |= (long)SPDR << 8;

    SPDR = 0;                 // dummy read
    while (!(SPSR & 0x80));   // wait for transfer to complete
    id |= (long)SPDR;

    DDRB = PORTB_OUT;              // 0010.1111; set B0-B3, B5 as outputs, set SPI MOSI as output

    return id;
}

void WriteWord(int w) {
    writeByte(w >> 8);   // write upper 8 bits
    writeByte(w & 0xFF); // write lower 8 bits
}

void Write888(long data, int count) {
    byte red = data >> 16;              // red = upper 8 bits
    byte green = (data >> 8) & 0xFF;    // green = middle 8 bits
    byte blue = data & 0xFF;            // blue = lower 8 bits
    for (; count > 0; count--) {
        writeByte(red);
        writeByte(green);
        writeByte(blue);
    }
}

void WriteColor(int color) {
#if COLMOD_PARAM == 5
    SPDR = (color >> 8); // write hi byte
    while (!(SPSR & 0x80)); // wait for transfer to complete
    SPDR = (color & 0xFF); // write lo byte
    while (!(SPSR & 0x80)); // wait for transfer to complete
#else
    byte cBlue = color << 3;
    byte cGreen = (color >> 3) & 0xFC;
    byte cRed = (color >> 8) & 0xF8;
    SPDR = cRed; // write hi byte
    while (!(SPSR & 0x80)); // wait for transfer to complete
    SPDR = cGreen; // write lo byte
    while (!(SPSR & 0x80)); // wait for transfer to complete
    SPDR = cBlue; // write lo byte
    while (!(SPSR & 0x80)); // wait for transfer to complete
#endif
}

// send 16-bit pixel data to the controller
// note: inlined spi xfer for optimization
void Write565(int color, unsigned int count) {
    WriteCmd(RAMWR);
    for (; count > 0; count--) {
        WriteColor(color);
    }
}

// read pixel color in 666 format.
// read is always 18 bit or 24 bits with only high 6 bits of each being the color
long Read888() {
#ifndef READ_TFT
    return alphaBackground;
#else
    long color = 0;

    setBit(TFT_CS_PORT, TFT_CS_BIT);  // deselect TFT CS
    SPCR = SPCR_READ;              // SPI enabled as Master, Mode0 at 4 MHz
#ifdef SPI_2X
    clearBit(SPSR, SPI2X); // double the SPI rate: 4-->8 MHz
#endif
    clearBit(TFT_CS_PORT, TFT_CS_BIT);  // select TFT CS
    WriteCmd(RAMRD);
    DDRB = PORTB_OUT & ~0x08; // 0010.0111; set B0-B2, B5 as outputs, B3 set SPI MOSI as input
    color = ReadLong();
    DDRB = PORTB_OUT; // 0010.1111; set B0-B3, B5 as outputs, set SPI MOSI as output
    // WriteCmd(COLMOD); // select color mode:
    // WriteByte(0x05); // mode 5 = 16bit pixels (RGB565)
#ifdef SPI_2X
    setBit(SPSR, SPI2X);      // double the SPI rate: 4-->8 MHz
#endif
    SPCR = SPCR_NORMAL;              // SPI enabled as Master, Mode0 at 4 MHz
    return color;
#endif
}

// read 4 bytes
long ReadLong() {
    long color = 0;

    // dummy parameter
    SPDR = 0; // dummy read
    while (!(SPSR & 0x80)); // wait for transfer to complete
    color |= ((long)SPDR & 0xFF) << 24;

    // red byte
    SPDR = 32; // dummy read
    while (!(SPSR & 0x80)); // wait for transfer to complete
    color |= ((long)SPDR & 0xFF) << 16;

    // green byte
    SPDR = 64; // dummy read
    while (!(SPSR & 0x80)); // wait for transfer to complete
    color |= ((long)SPDR & 0xFF) << 8;

    // blue byte
    SPDR = 128; // dummy read
    while (!(SPSR & 0x80)); // wait for transfer to complete
    color |= ((long)SPDR & 0xFF);

    // invert bits if level shifter inversion
    return color;
}

int ReadWord() {
    int color = 0;

    // byte
    SPDR = 0; // dummy read
    while (!(SPSR & 0x80)); // wait for transfer to complete
    color |= ((int)SPDR & 0xFF) << 8;

    // byte
    SPDR = 0; // dummy read
    while (!(SPSR & 0x80)); // wait for transfer to complete
    color |= ((int)SPDR & 0xFF);

    // invert bits if level shifter inversion
    return color;
}

byte ReadByte() {
    int color = 0;

    // byte
    SPDR = 0; // dummy read
    while (!(SPSR & 0x80)); // wait for transfer to complete
    return SPDR;
}

void HardwareReset() {
    clearBit(TFT_RST_PORT, TFT_RST_BIT); // pull PB0 (digital 8) low
    msDelay(1); // 1mS is enough
    setBit(TFT_RST_PORT, TFT_RST_BIT); // return PB0 high
    msDelay(150); // wait 150mS for reset to finish
}

void InitDisplay() {
    HardwareReset();    // initialize display controller
    msDelay(150);       // wait 150mS for TFT driver circuits
    WriteCmd(SLPOUT);   // take display out of sleep mode
    WriteCmd(COLMOD);   // select color mode:
    writeByte(COLMOD_PARAM);  // mode 5 = 16bit pixels (RGB565)
    WriteCmd(GAMSET);       // select gama
    writeByte(GAMSET_PARAM);
    WriteCmd(DISPON);   // turn display on!
}

void SetAddrWindow(byte x0, byte y0, byte x1, byte y1) {
    WriteCmd(CASET);      // set column range (x0,x1)
    WriteWord(x0);
    WriteWord(x1);
    WriteCmd(RASET);      // set row range (y0,y1)
    WriteWord(y0);
    WriteWord(y1);
}

void ClearScreen() {
    SetAddrWindow(0, 0, XMAX, YMAX); // set window to entire display
    WriteCmd(RAMWR);
    for (unsigned int i = 128 * 160; i > 0; --i) // byte count = 128*160*2
    {
        WriteColor(background);
    }
}

// ---------------------------------------------------------------------------
// SIMPLE GRAPHICS ROUTINES
//
// note: many routines have byte parameters, to save space,
// but these can easily be changed to int params for larger displays.
byte Blend(byte comp1, byte comp2, byte alpha) {
    if (alpha == 0) return comp1;
    if (alpha == 255) return comp2;

    int color = (int)comp1 * (256 - alpha) + (int)comp2 * alpha;
    return color >> 8;
}

int BlendColors(int c1, int c2, byte alpha) {
    long pColor = RGB565_TO_RGB888(c1);

    byte bRed = Blend(pColor, (c2 >> 8) & 0xF8, alpha) & 0xF8;
    byte bGreen = Blend((pColor >> 8), (c2 >> 3) & 0xFC, alpha) & 0xFC;
    byte bBlue = Blend((pColor >> 16), c2 << 3, alpha) & 0xF8;

    return RGB565(bRed, bGreen, bBlue);
}

void DrawPixelAlpha(byte x, byte y, int color, byte alpha) {
    long pColor = Read888();

    byte bRed = Blend(pColor, (color >> 8) & 0xF8, alpha) & 0xF8;
    byte bGreen = Blend((pColor >> 8), (color >> 3) & 0xFC, alpha) & 0xFC;
    byte bBlue = Blend((pColor >> 16), color << 3, alpha) & 0xF8;

    int aColor = RGB565(bRed, bGreen, bBlue);

    Write565(aColor, 1);
}

// draw pixel with alpha
// if alpha < 16 then it is treated as fully transparent
// if alpha >= 240 then it is treated as fully opaque
// else the current pixel color is blended with given color using proportional component blending
void DrawPixel(byte x, byte y, int color, byte alpha) {
    if (alpha >= 16) {
        SetAddrWindow(x, y, x, y);
        if (alpha >= 240) {
            Write565(color, 1);
        } else {
            DrawPixelAlpha(x, y, color, alpha);
        }
    }
}

void HLine(byte x0, byte x1, byte y, int color, byte alpha) {
    if (alpha >= 16) {
        if (alpha >= 240) {
            byte width = x1 - x0 + 1;
            SetAddrWindow(x0, y, x1, y);
            Write565(color, width);
        } else {
            for (byte x = x0; x <= x1; x++) DrawPixel(x, y, color, alpha);
        }
    }
}

// draws a vertical line in given color
void VLine(byte x, byte y0, byte y1, int color, byte alpha) {
    if (alpha >= 16) {
        if (alpha >= 240) {
            byte height = y1 - y0 + 1;
            SetAddrWindow(x, y0, x, y1);
            Write565(color, height);
        } else {
            for (byte y = y0; y <= y1; y++) DrawPixel(x, y, color, alpha);
        }
    }
}

// an elegant implementation of the Bresenham algorithm, with alpha
void Line(int x0, int y0, int x1, int y1, int color, byte alpha) {
    if (alpha >= 16) {
        int dx = abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = (dx > dy ? dx : -dy) / 2, e2;
        for (;;) {
            DrawPixel(x0, y0, color, alpha);
            if (x0 == x1 && y0 == y1) break;
            e2 = err;
            if (e2 > -dx) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dy) {
                err += dx;
                y0 += sy;
            }
        }
    }
}

// draws a rectangle in given color
void DrawRect(byte x0, byte y0, byte x1, byte y1, int color, byte alpha) {
    HLine(x0, x1, y0, color, alpha);
    HLine(x0, x1, y1, color, alpha);
    VLine(x0, y0, y1, color, alpha);
    VLine(x1, y0, y1, color, alpha);
}

void FillRect(byte x0, byte y0, byte x1, byte y1, int color, byte alpha) {
    if (alpha >= 16) {
        if (alpha >= 240) {
            byte width = x1 - x0 + 1;
            byte height = y1 - y0 + 1;
            SetAddrWindow(x0, y0, x1, y1);
            Write565(color, width * height);
        } else {
            for (byte y = y0; y <= y1; y++) HLine(x0, x1, y, color, alpha);
        }
    }
}

// draws circle quadrant(s) centered at x,y with given radius & color
// quad is a bit-encoded representation of which cartesian quadrant(s) to draw. // Remember that the y axis on our display is 'upside down':
// bit 0: draw quadrant I (lower right)
// bit 1: draw quadrant IV (upper right)
// bit 2: draw quadrant II (lower left)
// bit 3: draw quadrant III (upper left)
void CircleQuadrant(byte xPos, byte yPos, byte radius, byte quad, int color, byte alpha) {
    int x, xEnd = (707 * radius) / 1000 + 1;
    for (x = 0; x < xEnd; x++) {
        byte y = intsqrt(radius * radius - x * x);
        if (quad & 0x01) {
            DrawPixel(xPos + x, yPos + y, color, alpha);   // lower right
            DrawPixel(xPos + y, yPos + x, color, alpha);
        }
        if (quad & 0x02) {
            DrawPixel(xPos + x, yPos - y, color, alpha);  // upper right
            DrawPixel(xPos + y, yPos - x, color, alpha);
        }
        if (quad & 0x04) {
            DrawPixel(xPos - x, yPos + y, color, alpha);  // lower left
            DrawPixel(xPos - y, yPos + x, color, alpha);
        }
        if (quad & 0x08) {
            DrawPixel(xPos - x, yPos - y, color, alpha);  // upper left
            DrawPixel(xPos - y, yPos - x, color, alpha);
        }
    }
}

// draws circle at x,y with given radius & color
void Circle(byte xPos, byte yPos, byte radius, int color, byte alpha) {
    CircleQuadrant(xPos, yPos, radius, 0x0F, color, alpha); // do all 4 quadrants
}

// draws a rounded rectangle with corner radius r.
// coordinates: top left = x0,y0; bottom right = x1,y1
void RoundRect(byte x0, byte y0, byte x1, byte y1, byte r, int color, byte alpha) {
    HLine(x0 + r, x1 - r, y0, color, alpha); // top side
    HLine(x0 + r, x1 - r, y1, color, alpha); // bottom side
    VLine(x0, y0 + r, y1 - r, color, alpha); // left side
    VLine(x1, y0 + r, y1 - r, color, alpha); // right side
    CircleQuadrant(x0 + r, y0 + r, r, 8, color, alpha); // upper left corner
    CircleQuadrant(x1 - r, y0 + r, r, 2, color, alpha); // upper right corner
    CircleQuadrant(x0 + r, y1 - r, r, 4, color, alpha); // lower left corner
    CircleQuadrant(x1 - r, y1 - r, r, 1, color, alpha); // lower right corner
}

// draws filled circle at x,y with given radius & color
void FillCircle(byte xPos, byte yPos, byte radius, int color, byte alpha) {
    long r2 = radius * radius;
    for (int x = 0; x <= radius; x++) {
        byte y = intsqrt(r2 - x * x);
        byte y0 = yPos - y;
        byte y1 = yPos + y;
        VLine(xPos + x, y0, y1, color, alpha);
        VLine(xPos - x, y0, y1, color, alpha);
    }
}

// draws an ellipse of given width & height
// two-part Bresenham method
// note: slight discontinuity between parts on some (narrow) ellipses.
void Ellipse(int x0, int y0, int width, int height, int color, byte alpha) {
    int a = width / 2, b = height / 2;
    int x = 0, y = b;
    long a2 = (long)a * a * 2;
    long b2 = (long)b * b * 2;
    long error = (long)a * a * b;
    long stopY = 0, stopX = a2 * b;
    while (stopY <= stopX) {
        DrawPixel(x0 + x, y0 + y, color, alpha);
        DrawPixel(x0 + x, y0 - y, color, alpha);
        DrawPixel(x0 - x, y0 + y, color, alpha);
        DrawPixel(x0 - x, y0 - y, color, alpha);
        x++;
        error -= b2 * (x - 1);
        stopY += b2;
        if (error < 0) {
            error += a2 * (y - 1);
            y--;
            stopX -= a2;
        }
    }
    x = a;
    y = 0;
    error = b * b * a;
    stopY = a * b2;
    stopX = 0;
    while (stopY >= stopX) {
        DrawPixel(x0 + x, y0 + y, color, alpha);
        DrawPixel(x0 + x, y0 - y, color, alpha);
        DrawPixel(x0 - x, y0 + y, color, alpha);
        DrawPixel(x0 - x, y0 - y, color, alpha);
        y++;
        error -= a2 * (y - 1);
        stopX += a2;
        if (error < 0) {
            error += b2 * (x - 1);
            x--;
            stopY -= b2;
        }
    }
}

// draws a filled ellipse of given width & height
void FillEllipse(int xPos, int yPos, int width, int height, int color, byte alpha) {
    int a = width / 2, b = height / 2; // get x & y radii
    int x1, x0 = a, y = 1, dx = 0;
    long a2 = a * a, b2 = b * b;
    long a2b2 = a2 * b2; // need longs: big numbers!
    HLine(xPos - a, xPos + a, yPos, color, alpha); // draw centerline
    while (y <= b) { // draw horizontal lines...
        for (x1 = x0 - (dx - 1); x1 > 0; x1--)
            if (b2 * x1 * x1 + a2 * y * y <= a2b2) break;

        dx = x0 - x1;
        x0 = x1;
        HLine(xPos - x0, xPos + x0, yPos + y, color, alpha);
        HLine(xPos - x0, xPos + x0, yPos - y, color, alpha);
        y += 1;
    }
}

// ---------------------------------------------------------------------------
// TEXT ROUTINES
//
// Each ASCII character is 5x7, with one pixel space between characters
// So, character width = 6 pixels & character height = 8 pixels. //

// In portrait mode:
//    Display width = 128 pixels, so there are 21 chars/row (21x6 = 126).
//    Display height = 160 pixels, so there are 20 rows (20x8 = 160).
//    Total number of characters in portait mode = 21 x 20 = 420. //

// In landscape mode:
//    Display width is 160, so there are 26 chars/row (26x6 = 156).
//    Display height is 128, so there are 16 rows (16x8 = 128).
//    Total number of characters in landscape mode = 26x16 = 416.

// position cursor on character x,y grid, where 0<x<20, 0<y<19.
void GotoXY(byte x, byte y) {
    curX = x;
    curY = y;
}

// position character cursor to start of line y, where 0<y<19.
void GotoLine(byte y) {
    curX = 0;
    curY = y;
}

// moves character cursor to next position, assuming portrait orientation
void AdvanceCursor() {
    curX++;             // advance x position
    if (curX > 20) {    // beyond right margin?
        curY++;         // go to next line
        curX = 0;       // at left margin
    }
    if (curY > 19)      // beyond bottom margin?
        curY = 0;       // start at top again
}

// Set the display orientation to 0,90,180,or 270 degrees
void SetOrientation(int degrees) {
    byte arg;
    switch (degrees) {
        case 90:
            arg = 0x60;
            break;
        case 180:
            arg = 0xC0;
            break;
        case 270:
            arg = 0xA0;
            break;
        default:
            arg = 0x00;
            break;
    }
    WriteCmd(MADCTL);
    writeByte(arg);
}

#define OPTIMIZE_CHAR

// write ch to display X,Y coordinates using ASCII 5x7 font
void PutCh(char ch, byte x, byte y) {
#ifdef OPTIMIZE_CHAR
    uint8_t charBits[7];
    *((uint32_t *)(charBits + 1)) = pgm_read_dword(FONT_CHARS[ch - 32]);
//    *((uint32_t *)charBits+1) = pgm_read_dword(FONT_CHARS[ch - 32]+4);
    charBits[5] = pgm_read_byte(FONT_CHARS[ch - 32] + 4);
    charBits[0] = charBits[6] = 0;
#endif
    byte row, col, data, mask;
    SetAddrWindow(x, y, x + 4, y + 6);
    WriteCmd(RAMWR);
    for (row = 0, mask = 0x01; row < 7; row++, mask <<= 1) {
        for (col = 0; col < 5; col++) {
#ifdef OPTIMIZE_CHAR
            data = *(charBits + 1 + col);
            WriteColor((data & mask) ? foreground : background);
#else
            data = pgm_read_byte(&(FONT_CHARS[ch - 32][col]));
            WriteColor((data & mask) ? foreground : background);
#endif
        }
    }
}

// writes character to display at current cursor position.
void WriteChar(char ch) {
    PutCh(ch, curX * 6, curY * 8);
    AdvanceCursor();
}

// writes string to display at current cursor position.
void WriteString(const char *text) {
    for (; *text; text++) // for all non-nul chars
        WriteChar(*text); // write the char
}

// writes integer i at current cursor position
void WriteInt(int i) {
    char str[8]; // buffer for string result
    itoa(i, str, 10); // convert to string, base 10
    WriteString(str);
}

void WriteDig(int dig) {
    dig &= 0x0f;
    char c = dig > 9 ? 'A' - 10 + dig : '0' + dig;
    WriteChar(c);
}

// writes hexadecimal value of integer i at current cursor position
void WriteHex(int i) {
    // char str[8]; // buffer for string result
    // itoa(i, str, 16); // convert to base 16 (hex)
    // WriteString(str, color);

    WriteDig(i >> 12);
    WriteDig(i >> 8);
    WriteDig(i >> 4);
    WriteDig(i);
}

void WriteLongHex(long i) {
    WriteHex((int)(i >> 16));
    WriteChar('.');
    WriteHex((int)i);
}

void WriteLong(long i) {
    char buff[11];
    ltoa(i, buff, 10);
    WriteString(buff);
}

// --------------------------------------------------------------------------- // TEST ROUTINES
// draws 4000 pixels on the screen
void PixelTest(byte alpha) {
    for (int i = 4000; i > 0; i--) // do a whole bunch:
    {
        int x = rand() % XMAX; // random x coordinate
        int y = rand() % YMAX; // random y coordinate
        DrawPixel(x, y, YELLOW, alpha); // draw pixel at x,y
    }
}

// sweeps Line routine through all four quadrants.
void LineTest(int color, byte alpha) {
    ClearScreen();
    int x, y, x0 = 64, y0 = 80;
    for (x = 0; x < XMAX; x += 2) Line(x0, y0, x, 0, YELLOW, alpha);
    for (y = 0; y < YMAX; y += 2) Line(x0, y0, XMAX, y, CYAN, alpha);
    for (x = XMAX; x > 0; x -= 2) Line(x0, y0, x, YMAX, YELLOW, alpha);
    for (y = YMAX; y > 0; y -= 2) Line(x0, y0, 0, y, CYAN, alpha);
    msDelay(2000);
}

// draw series of concentric circles
void CircleTest(int color, byte alpha) {
    ClearScreen();
    for (int radius = 6; radius < 60; radius += 2)
        Circle(60, 80, radius, YELLOW, alpha);
    msDelay(2000);
}

#define rad 40

void ColorCircleTest() {
    alphaBackground = RGB565(255, 255, 0);

    byte cx1 = 64;
    byte cx2 = 64 - rad / 2;
    byte cx3 = 64 + rad / 2;
    byte cy1 = 84 - (433 * rad) / 750; // sqrt(3)/3 * rad
    byte cy2 = 84 + (433 * rad) / 1500; // sqrt(3)/6 * rad
    byte cy3 = cy2;

    FillCircle(64, 84, rad * 3 / 2, RGB565(255, 255, 255), 128);
    msDelay(2000);
    FillCircle(64, 84, rad, RGB565(0, 0, 0), 128);
    msDelay(2000);
    FillCircle(64, 84, rad / 2, RGB565(0, 0, 0), 255);
    msDelay(2000);

    FillCircle(cx1, cy1, rad, RGB565(255, 0, 0), 128);
    msDelay(2000);
    FillCircle(cx2, cy2, rad, RGB565(0, 255, 0), 128);
    msDelay(2000);
    FillCircle(cx3, cy3, rad, RGB565(0, 0, 255), 128);
    msDelay(4000);
}

// Writes 420 characters (5x7) to screen in portrait mode
void PortraitChars() {
    ClearScreen();
    for (int i = 420; i > 0; i--) {
        byte x = i % 21;
        byte y = i / 21;
        char ascii = (i % 96) + 32;
        PutCh(ascii, x * 6, y * 8);
    }
    msDelay(2000);
}

void RGB565Comps(int16_t color, uint8_t *r, uint8_t *g, uint8_t *b) {
    long pColor = RGB565_TO_RGB888(color);

    *r = (pColor >> 8) & 0xF8;
    *g = (pColor >> 3) & 0xFC;
    *b = (pColor << 3) & 0xF8;
}

uint8_t red = 31, green = 0, blue = 0, state = 0;

void setGama(uint8_t gama) {
    gama &= 0x0f;
    if (!gama) {
        gama = 1;
    }

    WriteCmd(GAMSET);       // select gama
    writeByte(gama);
}

void RainbowFill() {
    uint16_t color = RGB565(red, green, blue);

    for (int i = 0; i < 160; i++) {
        HLine(0, 128, i, color, 255);
        // test gama
//        setGama(1);
//        HLine(0, 32, i, color, 255);
//        setGama(2);
//        HLine(32, 64, i, color, 255);
//        setGama(4);
//        HLine(64, 96, i, color, 255);
//        setGama(8);
//        HLine(96, 128, i, color, 255);

        switch (state) {
            case 0:
                green += 2;
                if (green == 64) {
                    green = 63;
                    state = 1;
                }
                break;
            case 1:
                red -= 2;
                if (red >= 254) {
                    red = 0;
                    state = 2;
                }
                break;
            case 2:
                blue += 1;
                if (blue == 32) {
                    blue = 31;
                    state = 3;
                }
                break;
            case 3:
                green -= 2;
                if (green >= 254) {
                    green = 0;
                    state = 4;
                }
                break;
            case 4:
                red += 1;
                if (red == 32) {
                    red = 31;
                    state = 5;
                }
                break;
            case 5:
                blue -= 1;
                if (blue >= 254) {
                    blue = 0;
                    state = 0;
                }
                break;
        }

        color = red << 11 | green << 5 | blue;
    }
    setGama(1);
}

#define SHOW_TIMING

#ifdef SHOW_TIMING
uint32_t timer = 0;

uint32_t time() {
    cli();
    uint32_t val = timer;
    sei();
    return val;
}

ISR(TIMER0_COMPA_vect) {//timer0 interrupt 2kHz toggles pin 8
//generates pulse wave of frequency 2kHz/2 = 1kHz (takes two cycles for full wave- toggle high then toggle low)
    timer++;
}

#endif

#define ULTRA_TRG_PORT PORTD
#define ULTRA_TRG  6

#define ULTRA_ECHO_PORT PORTB
#define ULTRA_ECHO  0

void SetupPorts() {
    DDRB = PORTB_OUT; // 0010.1111; set B1, B2-B3, B5 as outputs
    DDRC = 0x00; // 0000.0000; set PORTC as inputs
    DDRD = 0x70; // 0000.0000; set PORTD 5,6 as output
    PORTD = 0;
    setBit(TFT_RST_PORT, TFT_RST_BIT); // start with TFT reset line inactive high
    setBit(TFT_CS_PORT, TFT_CS_BIT);  // deselect TFT CS
    clearBit(TFT_SCK_PORT, TFT_SCK_BIT);  // TFT SCK Low
}

int readPulse(int &manual) {
    // clear counter 1
    TCNT1H = 0;
    TCNT1L = 0;

    uint16_t loop = 2000;

    loop = 3200;
    while ((PINB & (0x01 << ULTRA_ECHO))) {
        if (!loop) {
            return -5;
        }
        delayMicroseconds(10);
        loop--;
    }

    // trigger ultrasonic pulse
    setBit(ULTRA_TRG_PORT, ULTRA_TRG);
    delayMicroseconds(10);
    clearBit(ULTRA_TRG_PORT, ULTRA_TRG);

    TIFR1 = 0; // clear possible overflow

    // wait for echo to go high
    //    while (!(PORTD & (1 << 5)));

    loop = 3200;
    while (!(PINB & (0x01 << ULTRA_ECHO))) {
        if (!loop) {
            return -2;
        }
        delayMicroseconds(10);
        loop--;
    }

    // wait for echo to go low or overflow
    // clear timer counter register
    ICR1H = 0;
    ICR1L = 0;
    TCNT1H = 0x80;
    TCNT1L = 0;

    uint16_t end = 0x8000;
    do {
        if (end < 0x8000) {
            manual = end;
            return -3;
        }
        delayMicroseconds(10);
        end = (uint8_t)(TCNT1L);
        end |= ((uint8_t)TCNT1H) << 8;
    } while ((PINB & (0x01 << ULTRA_ECHO)));

    manual = end - 0x8000;

    if (TIFR1 & TOV1) {
        return -1;
    }

    if (!(TIFR1 & ICF1)) {
        return -4;
    }

    int pulse = (uint8_t)(ICR1L);
    pulse |= ((uint8_t)ICR1H) << 8;
    return (uint16_t)pulse - 0x8000;
}

// --------------------------------------------------------------------------- // MAIN PROGRAM
int main() {
#ifdef SHOW_TIMING
    cli();

    //set timer0 interrupt at 1kHz at 8MHz or 2kHz at 16MHz
    TCCR0A = 0;// set entire TCCR0A register to 0
    TCCR0B = 0;// same for TCCR0B
    TCNT0 = 0;//initialize counter value to 0

    // set compare match register for 1khz increments
    OCR0A = 62;// = (16*10^6) / (2000*64) - 1 (must be <256)
    // turn on CTC mode
    TCCR0A |= (1 << WGM01);
    // Set CS01 and CS00 bits for 64 prescaler
    TCCR0B |= (1 << CS01) | (1 << CS00);
    // enable timer compare interrupt
    TIMSK0 |= (1 << OCIE0A);

    // setup pulse measurement using 16bit COUNTER 1
//    TCCR1A = 0;
//    TCCR1B = 0;
//    TCNT1H  = 0;
//    TCNT1L  = 0;

    //OCR1AL = 0; //
    //OCR1AH = 0; //
    TCCR1A = 0;
    TCCR1B = 0x02; // no noise canceller (ICEN1-0), negative edge triggered (ICES1 = 1), Waveform gen = 0, prescaler = 8 (1 usec clock on 8MHz CPu)

    sei();
#endif

    SetupPorts();                               // use PortB for LCD interface
    FlashLED(1);                                // indicate program start
    OpenSPI();                                  // start communication to TFT
    InitDisplay();                              // initialize TFT controller
    foreground = CYAN;
    // PortraitChars();                            // show full screen of ASCII chars

/*
    CircleTest(0, 255);
    CircleTest(0, 128);
    CircleTest(RGB565(255, 0, 0), 128);
    CircleTest(RGB565(0, 255, 0), 128);
    CircleTest(RGB565(0, 0, 255), 128);

//*/
    // LineTest(0, 255);
//*
    LineTest(0, 128);
    LineTest(RGB565(255, 0, 0), 128);
    LineTest(RGB565(0, 255, 0), 128);
    LineTest(RGB565(0, 0, 255), 128);
//*/

    //ClearScreen();
    //ColorCircleTest();

    long id, id2, id3;

/*
    FillEllipse(60, 75, 100, 50, BLACK, 255);   // erase an oval in center
    Ellipse(60, 75, 100, 50, LIME, 255);        // outline the oval in green

    const char *str = "Hello, World!";         // text to display
    GotoXY(4, 9);                                 // position text cursor
    WriteString(str, YELLOW);                   // display text inside oval
    // WriteHex((int)(id >> 16), YELLOW);
    // WriteChar(' ', YELLOW);
    // WriteHex((int)id, YELLOW);
    msDelay(2000);

    id = 0xF0E1D2C3; //ReadID();

    FillEllipse(60, 75, 100, 50, BLACK, 255);   // erase an oval in center
    GotoXY(5, 9);                                 // position text cursor
    WriteHex((int)(id >> 16), YELLOW);
    WriteChar(' ', YELLOW);
    WriteHex((int)id, YELLOW);
    msDelay(2000);
//*/

    // FillEllipse(60, 75, 100, 50, BLACK, 255);   // erase an oval in center

    foreground = RGB565(255, 255, 128);
    background = RGB565(32, 32, 160);

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wmissing-noreturn"
    for (uint8_t iter = 0;; iter++) {
        int manual;
        int pulse = readPulse(manual);

        RainbowFill();

        byte col = 3;
        byte line = 4;

        // DrawPixel(64, 84, RGB565(255,196,128), 255);
        SetAddrWindow(1, 1, 1, 1);

#if COLMOD_PARAM == 5
        WriteCmd(COLMOD); // select color mode:
        writeByte(0x06); // mode 6 = 18bit pixels for reads
#endif

        SPCR = SPCR_READ;              // SPI enabled as Master, Mode0 at 4 MHz
#ifdef SPI_2X
        clearBit(SPSR, SPI2X); // slow rate
#endif

        setBit(TFT_CS_PORT, TFT_CS_BIT);    // deselect TFT CS
        _delay_us(CS_DELAY);
        clearBit(TFT_CS_PORT, TFT_CS_BIT);  // select TFT CS

        WriteCmd(RAMRD);
        DDRB = (PORTB_OUT & ~0x08); // 0010.0111; set B0-B2, B5 as outputs, B3 set SPI MOSI as input

//        ClearBit(TFT_SCK_PORT, TFT_SCK);        // init clock pin low
//        SPCR = SPCR_MANUAL;              // SPI disabled as Master, Mode0 at 4 MHz
//        SetBit(TFT_SCK_PORT, TFT_SCK);          // toggle clock high to skip one bit
//        ClearBit(TFT_SCK_PORT, TFT_SCK);        // toggle clock high to skip one bit
//        SPCR = SPCR_READ;                // SPI enabled as Master, Mode0 at 4 MHz

        id = ReadLong();
//        SetBit(TFT_CS_PORT, TFT_CS);  // select TFT CS
//        _delay_us(CS_DELAY);
//        ClearBit(TFT_CS_PORT, TFT_CS);  // select TFT CS
        id2 = 0; //ReadLong();
        id2 = ReadLong();
        DDRB = PORTB_OUT; // 0010.1111; set B0-B3, B5 as outputs, set SPI MOSI as output

        // Read ID
        SPCR = SPCR_NORMAL;              // SPI enabled as Master, Mode0 at 4 MHz
        setBit(TFT_CS_PORT, TFT_CS_BIT);           // deselect TFT CS
        _delay_us(2 * CS_DELAY);
        clearBit(TFT_CS_PORT, TFT_CS_BIT);        // select TFT CS
        WriteCmd(RDDID);
        DDRB = (PORTB_OUT & ~0x08); // 0010.0111; set B0-B2, B5 as outputs, B3 set SPI MOSI as input

        clearBit(TFT_SCK_PORT, TFT_SCK_BIT);        // toggle clock high to skip one bit
        SPCR = SPCR_MANUAL;              // SPI disabled as Master, Mode0 at 4 MHz
        setBit(TFT_SCK_PORT, TFT_SCK_BIT);          // toggle clock high to skip one bit
        clearBit(TFT_SCK_PORT, TFT_SCK_BIT);        // toggle clock high to skip one bit
        SPCR = SPCR_NORMAL;              // SPI enabled as Master, Mode0 at 4 MHz

        id3 = ReadLong();
        setBit(TFT_CS_PORT, TFT_CS_BIT);    // deselect TFT CS
        _delay_us(CS_DELAY);
        clearBit(TFT_CS_PORT, TFT_CS_BIT);  // select TFT CS
        DDRB = PORTB_OUT; // 0010.1111; set B0-B3, B5 as outputs, set SPI MOSI as output

        SPCR = SPCR_NORMAL;              // SPI enabled as Master, Mode0 at 4 MHz

#ifdef SPI_2X
        setBit(SPSR, SPI2X);      // double the SPI rate: 4-->8 MHz
#endif

#if COLMOD_PARAM == 5
        WriteCmd(COLMOD); // select color mode:
        writeByte(0x05); // mode 5 = 16bit pixels for writes
#endif

#ifdef SHOW_TIMING
        FillRect(col * 6 - 1, line * 8 - 1, (col + 15) * 6, (line + 13) * 8, background, 255);
        uint32_t start = time();

#else
        FillRect(col * 6 - 1, line * 8 - 1, (col + 15) * 6, (line + 12) * 8, background, 255);
#endif

        GotoXY(col, line++);                                 // position text cursor
        WriteString("COLR1 ");
        WriteHex((int)(id >> 16));
        WriteChar('.');
        WriteHex((int)id);

        id = id2;
        GotoXY(col, line++);                                 // position text cursor
        WriteString("COLR2 ");
        WriteHex((int)(id >> 16));
        WriteChar('.');
        WriteHex((int)id);

        id = id3;
        GotoXY(col, line++);                                 // position text cursor
        WriteString("RDDID ");
        WriteHex((int)(id >> 16));
        WriteChar('.');
        WriteHex((int)id);

#ifdef SPI_2X
        clearBit(SPSR, SPI2X); // double the SPI rate: 4-->8 MHz
#endif
        WriteCmd(RDDID1);
        DDRB = (PORTB_OUT & ~0x08); // 0010.0111; set B0-B2, B5 as outputs, B3 set SPI MOSI as input
        id = ReadByte();
        DDRB = PORTB_OUT; // 0010.1111; set B0-B3, B5 as outputs, set SPI MOSI as output

        WriteCmd(RDDID2);
        DDRB = (PORTB_OUT & ~0x08); // 0010.0111; set B0-B2, B5 as outputs, B3 set SPI MOSI as input
        id2 = ReadByte();
        DDRB = PORTB_OUT; // 0010.1111; set B0-B3, B5 as outputs, set SPI MOSI as output

        WriteCmd(RDDID3);
        DDRB = (PORTB_OUT & ~0x08); // 0010.0111; set B0-B2, B5 as outputs, B3 set SPI MOSI as input
        id3 = ReadByte();
        DDRB = PORTB_OUT; // 0010.1111; set B0-B3, B5 as outputs, set SPI MOSI as output
#ifdef SPI_2X
        setBit(SPSR, SPI2X);      // double the SPI rate: 4-->8 MHz
#endif

        GotoXY(col, line++);                                 // position text cursor
        WriteString("  ID1 ");
        WriteLongHex(id);

        id = id2;
        GotoXY(col, line++);                                 // position text cursor
        WriteString("  ID2 ");
        WriteLongHex(id);

        id = id3;
        GotoXY(col, line++);                                 // position text cursor
        WriteString("  ID3 ");
        WriteLongHex(id);

#ifdef SPI_2X
        clearBit(SPSR, SPI2X); // double the SPI rate: 4-->8 MHz
#endif
        WriteCmd(RDDCOLMOD);
        DDRB = (PORTB_OUT & ~0x08); // 0010.0111; set B0-B2, B5 as outputs, B3 set SPI MOSI as input
        id = ReadByte();
        DDRB = PORTB_OUT; // 0010.1111; set B0-B3, B5 as outputs, set SPI MOSI as output
#ifdef SPI_2X
        setBit(SPSR, SPI2X);      // double the SPI rate: 4-->8 MHz
#endif

        GotoXY(col, line++);                                 // position text cursor
        WriteString("COLMD ");
        WriteLongHex(id);

        GotoXY(col, line++);                                 // position text cursor
        WriteString("  MAN ");
        WriteLong(manual);

        GotoXY(col, line++);                                 // position text cursor
        WriteString("PULSE ");
        WriteLong(pulse);

        GotoXY(col, line++);                                 // position text cursor
        WriteString(" ECHO ");
        if (pulse > 0) {
            id = (long)pulse * 100 / 58;
            int cm = id / 100;
            int mm = mm % 100;
            WriteLong(cm);
            WriteString(".");
            WriteLong(mm);
            WriteString(" cm");
        } else {
            WriteString("");
        }


//        DDRB = PORTB_OUT;
//        SetBit(ULTRA_TRG_PORT, ULTRA_TRG);
//        GotoXY(col, line++);                                 // position text cursor
//        WriteString("ECHOH ");
//        WriteString(PINB & (0x01 << ULTRA_ECHO) ? "HIGH" : "LOW");
//
//        DDRB = PORTB_OUT;
//        ClearBit(ULTRA_TRG_PORT, ULTRA_TRG);
//        GotoXY(col, line++);                                 // position text cursor
//        WriteString("ECHOL ");
//        WriteString(PINB  & (0x01 << ULTRA_ECHO) ? "HIGH" : "LOW");


//        id = RGB565_TO_RGB888(RED);
//        GotoXY(col, line++);                                 // position text cursor
//        WriteString("  RED ");
//        WriteLongHex(id);
//
//        id = RGB565_TO_RGB888(LIME);
//        GotoXY(col, line++);                                 // position text cursor
//        WriteString(" LIME ");
//        WriteLongHex(id);
//
//        id = RGB565_TO_RGB888(BLUE);
//        GotoXY(col, line++);                                 // position text cursor
//        WriteString(" BLUE ");
//        WriteLongHex(id);
//
//        id = RGB565_TO_RGB888(CYAN);
//        GotoXY(col, line++);                                 // position text cursor
//        WriteString(" CYAN ");
//        WriteLongHex(id);

//        id = gama;
//        GotoXY(col, line++);                                 // position text cursor
//        WriteString(" GAMA ");
//        WriteLongHex(id);

#ifdef SHOW_TIMING
        GotoXY(col, line++);                                 // position text cursor
        uint32_t end = time();
        WriteString(" TIME ");
        WriteLong(end - start);
#endif

        GotoXY(col, line++);                                 // position text cursor
        WriteString("---------------");

//        WriteCmd((iter & 1) ? IDMON : IDMOFF);   // take display out of sleep mode
        msDelay(1000);
//        WriteCmd(IDMOFF);   // take display out of sleep mode
    }
#pragma clang diagnostic pop

    closeSPI();                                 // close communication with TFT
    FlashLED(3);                                // indicate program end
}

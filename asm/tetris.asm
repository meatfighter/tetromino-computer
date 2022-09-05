define FALL_SPEED 01

define MODE_ATTRACT     00
define MODE_PLAY        01
define MODE_CLEAR_LINES 02

define ACTION_DRAW 22 ; BNE
define ACTION_TEST 23 ; BEQ

define CELL_EMPTY 00
define CELL_SOLID FF ; Any nonzero cell is solid

define PLAYFIELD_WIDTH 0B

define SPAWN_X 05
define SPAWN_Y 02
define SPAWN_ROTATION 00

segment 0000
playfield:
;  0  1  2  3  4  5  6  7  8  9 10
  00 00 00 00 00 00 00 00 00 00 FF  ;  0
  00 00 00 00 00 00 00 00 00 00 FF  ;  1
  00 00 00 00 00 00 00 00 00 00 FF  ;  2
  00 00 00 00 00 00 00 00 00 00 FF  ;  3
  00 00 00 00 00 00 00 00 00 00 FF  ;  4
  00 00 00 00 00 00 00 00 00 00 FF  ;  5
  00 00 00 00 00 00 00 00 00 00 FF  ;  6
  00 00 00 00 00 00 00 00 00 00 FF  ;  7
  00 00 00 00 00 00 00 00 00 00 FF  ;  8
  00 00 00 00 00 00 00 00 00 00 FF  ;  9
  00 00 00 00 00 00 00 00 00 00 FF  ; 10
  00 00 00 00 00 00 00 00 00 00 FF  ; 11
  00 00 00 00 00 00 00 00 00 00 FF  ; 12
  00 00 00 00 00 00 00 00 00 00 FF  ; 13
  00 00 00 00 00 00 00 00 00 00 FF  ; 14
  00 00 00 00 00 00 00 00 00 00 FF  ; 15
  00 00 00 00 00 00 00 00 00 00 FF  ; 16
  00 00 00 00 00 00 00 00 00 00 FF  ; 17
  00 00 00 00 00 00 00 00 00 00 FF  ; 18
  00 00 00 00 00 00 00 00 00 00 FF  ; 19
  00 00 00 00 00 00 00 00 00 00 FF  ; 20
  00 00 00 00 00 00 00 00 00 00 FF  ; 21
  FF FF FF FF FF FF FF FF FF FF 00  ; 22

segment 00FD
drawFrame:           00 ; 00 = generating frame; otherwise, finished generating frame
leftButton:          00 ; 00 = released; otherwise, pressed
rightButton:         00 ; 00 = released; otherwise, pressed

segment 0100
tetriminos:
;  0  1  2  3
  FF 00 01 0B ;  0 td {  -1,   0,   1,  11 }
  F5 FF 00 0B ;  1 tl { -11,  -1,   0,  11 }
  FF 00 01 F5 ;  2 tu {  -1,   0,   1, -11 }
  F5 00 01 0B ;  3 tr { -11,   0,   1,  11 }

  FF 00 01 0C ;  4 jd {  -1,   0,   1,  12 }
  F5 00 0A 0B ;  5 jl { -11,   0,  10,  11 }
  F4 FF 00 01 ;  6 ju { -12,  -1,   0,   1 }
  F5 F6 00 0B ;  7 jr { -11, -10,   0,  11 }

  FF 00 0B 0C ;  8 zh {  -1,   0,  11,  12 }
  F6 00 01 0B ;  9 zv { -10,   0,   1,  11 }
  FF 00 0B 0C ; 10 zh {  -1,   0,  11,  12 }
  F6 00 01 0B ; 11 zv { -10,   0,   1,  11 }

  FF 00 0A 0B ; 12 o  {  -1,   0,  10,  11 }
  FF 00 0A 0B ; 13 o  {  -1,   0,  10,  11 }
  FF 00 0A 0B ; 14 o  {  -1,   0,  10,  11 }
  FF 00 0A 0B ; 15 o  {  -1,   0,  10,  11 }

  00 01 0A 0B ; 16 sh {   0,   1,  10,  11 }
  F5 00 01 0C ; 17 sv { -11,   0,   1,  12 }
  00 01 0A 0B ; 18 sh {   0,   1,  10,  11 }
  F5 00 01 0C ; 19 sv { -11,   0,   1,  12 }

  FF 00 01 0A ; 20 ld {  -1,   0,   1,  10 }
  F4 F5 00 0B ; 21 ll { -12, -11,   0,  11 }
  F6 FF 00 01 ; 22 lu { -10,  -1,   0,   1 }
  F5 00 0B 0C ; 23 lr { -11,   0,  11,  12 }

  FE FF 00 01 ; 24 ih {  -2,  -1,   0,   1 }
  EA F5 00 0B ; 25 iv { -22, -11,   0,  11 }
  FE FF 00 01 ; 26 ih {  -2,  -1,   0,   1 }
  EA F5 00 0B ; 27 iv { -22, -11,   0,  11 }

segment 0170
startButton:         00 ; 00 = released; otherwise, pressed
ccwRotateButton:     00 ; 00 = released; otherwise, pressed 
cwRotateButton:      00 ; 00 = released; otherwise, pressed
downButton:          00 ; 00 = released; otherwise, pressed

tetriminoType:       00 ; 00--06 (T, J, Z, O, S, L, I)
tetriminoRotation:   00 ; 00--03
tetriminoX:          00 ; 00--09
tetriminoY:          00 ; 02--15

lastRotation:        00 ; 00--03
lastX:               00 ; 00--09

frameCounter:        00 ; 00--FF (wraps around)

seedHigh:            89 ; randomizer
seedLow:             88
nextBit:             00

i:                   00 ; loops index
origin:              00 ; playfield index corresponding to Tetrimino center
tetriminosIndex:     00 ; tetriminos table index corresponding to a Tetrimino block

fallTimer:           00 ; 00 = drop Tetrimino
mode:                00 ; 00 = attract, 01 = play, 02 = clear lines
minY:                00 ; minimal locked Tetrimino Y (00--16)

main: ; ------------------------------------------------------------------------------------------------------

SMN drawFrame
SEA 01
STA                     ; render frame

SMN frameCounter
LDA
INC
STA                     ; ++frameCounter;

SMN mode
LDB
SEA MODE_PLAY
SUB                     ; if (mode == MODE_PLAY) {
BEQ playing             ;   goto playing;
                        ; }
SEA MODE_CLEAR_LINES
SUB                     ; if (mode == MODE_CLEAR_LINES) {
BEQ clearLines          ;   goto clearLines;
                        ; }
SMN startButton
LDA                     ; if (startButton == 0) {
BEQ main;               ;   goto main;
                        ; }

SMN minY                ; // Start button pressed
SEA 16
STA                     ; minY = 22;

SEA F1                  ; A = 0xF1; // 22 * PLAYFIELD_WIDTH - 1, index of last element of row 21
SEB CELL_EMPTY                  
SMN playfield
clearLoop:
TAN
STB                     ; playfield[A] = CELL_EMPTY;
DEC                     ; if (--A != 0) {
BNE clearLoop           ;   goto clearLoop;
                        ; }
STB                     ; playfield[0] = CELL_EMPTY;

SMN 00F1                ; MN = 0x00F1; // 22 * PLAYFIELD_WIDTH - 1, address of last element of row 21 
SEB CELL_SOLID                  
edgeLoop:
STB                     ; *MN = CELL_SOLID;
TNA
SEB PLAYFIELD_WIDTH
SUB
TAN                     ; MN -= PLAYFIELD_WIDTH;
SEB FF
SUB                     ; if (*MN != -1) {
BNE edgeLoop            ;   goto edgeLoop;
                        ; }
spawn:
SMN mode
SEA MODE_PLAY
STA                     ; mode = MODE_PLAY;
SMN tetriminoRotation
SEA SPAWN_ROTATION
STA                     ; tetriminoRotation = SPAWN_ROTATION;
SMN tetriminoX
SEA SPAWN_X
STA                     ; tetriminoX = SPAWN_X;
SMN tetriminoY
SEA SPAWN_Y
STA                     ; tetriminoY = SPAWN_Y;
SMN fallTimer
SEA FALL_SPEED
STA                     ; fallTimer = FALL_SPEED;

randomlyChoose:
SEB 02
SMN seedLow
LDA
AND
SMN nextBit
STA
SMN seedHigh
LDA
AND
SMN nextBit
LDB
XOR
BEQ bit9Clear
SEA 80
bit9Clear:
STA                     ; nextBit = ((seedHigh & 0x02) ^ (seedLow & 0x02)) << 6;

SMN seedHigh
LDA
SEB 01
AND
BEQ bit8Clear
SEB 80
bit8Clear:
SMN seedLow
LDA
RS1
OR
STA                     ; seedLow = (seedHigh << 7) | (seedLow >>> 1);

SMN nextBit
LDB
SMN seedHigh
LDA
RS1
OR
STA                     ; seedHigh = nextBit | (seedHigh >>> 1);

SMN frameCounter
LDB
XOR
SEB 1F
AND
TAB
LS3
SUB
RS5                     
TAB                     ; B = ((seedHigh ^ frameCounter) & 0x1F) * 7 / 32;
SMN tetriminoType
LDA
SUB                     ; if (B == tetriminoType) {
BEQ randomlyChoose      ;   goto randomlyChoose;
                        ; }
STB                     ; tetriminoType = B;

SMN drawOrTest
SEA ACTION_TEST
STA                     ; drawOrTest = ACTION_TEST;
JSR drawOrTestTetrimino ; if (drawOrTestTetrimino()) { // verify Tetrimino spawned into empty space
BEQ keepPosition        ;   goto playing;
                        ; }

SMN mode                ; // Bad Tetrimino position. It's game over.
SEA MODE_ATTRACT
STA                     ; mode = MODE_ATTRACT;

JMP endFall             ; goto endFall; // draws the Tetrimino that failed to spawn


playing:                ; // Play handler
SMN drawOrTest
SEA ACTION_DRAW
STA                     ; drawOrTest = ACTION_DRAW;
SMN drawCell+1
SEA CELL_EMPTY
STA                     ; *(drawCell+1) = CELL_EMPTY;
JSR drawOrTestTetrimino ; drawOrTestTetrimino(); // erase Tetrimino

SMN tetriminoRotation
LDA
SMN lastRotation
STA                     ; lastRotation = tetriminoRotation;
SMN tetriminoX
LDA
SMN lastX
STA                     ; lastX = tetriminoX;

SMN leftButton
LDA                     ; if (leftButton == 0) {
BEQ testRightButton     ;   goto testRightButton;
                        ; }

SMN tetriminoX          ; // Left button pressed
LDA
DEC
STA                     ; --tetriminoX;
JMP testCcwRotateButton ; goto testCcwRotateButton;

testRightButton:
SMN rightButton
LDA                     ; if (rightButton == 0) {
BEQ testCcwRotateButton ;   goto testCcwRotateButton;
                        ; }

SMN tetriminoX          ; // Right button pressed
LDA
INC
STA                     ; ++tetriminoX;

testCcwRotateButton:
SEB 03                  ; B = 3;
SMN ccwRotateButton
LDA                     ; if (ccwRotateButton == 0) {
BEQ testCwRotateButton  ;   goto testCwRotateButton;
                        ; }

SMN tetriminoRotation   ; // CCW button pressed
LDA
DEC
AND
STA                     ; tetriminoRotation = (tetriminoRotation - 1) & 3;

JMP validatePosition    ; goto validatePosition;

testCwRotateButton:
SMN cwRotateButton
LDA                     ; if (cwRotateButton == 0) {
BEQ validatePosition    ;   goto validatePosition;
                        ; }

SMN tetriminoRotation   ; // CW button pressed
LDA
INC
AND
STA                     ; tetriminoRotation = (tetriminoRotation + 1) & 3;

validatePosition: 
SMN drawOrTest
SEA ACTION_TEST
STA                     ; drawOrTest = ACTION_TEST;
JSR drawOrTestTetrimino ; if (drawOrTestTetrimino()) { // verify Tetrimino shifted/rotated into empty space
BEQ keepPosition        ;   goto keepPosition;
                        ; }

SMN lastRotation        ; // Bad Tetrimino position. Undo shift or rotation.
LDA
SMN tetriminoRotation
STA                     ; tetriminoRotation = lastRotation;
SMN lastX
LDA
SMN tetriminoX
STA                     ; tetriminoX = lastX;

keepPosition:
SMN downButton
LDA                     
SMN fallTimer           ; if (downButton == 0) {
BEQ updateFallTimer     ;   goto updateFallTimer;
                        ; }

SEA 00                  ; // Down button pressed
STA                     ; fallTimer = 0;                  

updateFallTimer:
LDA                     ; if (fallTimer != 0) {
BNE decFallTimer        ;   goto decFallTimer;
SEA FALL_SPEED          ; } 
STA                     ; fallTimer = FALL_SPEED;

SMN tetriminoY          ; // Drop Tetrimino
LDA
INC
STA                     ; ++tetriminoY;
JSR drawOrTestTetrimino ; if (drawOrTestTetrimino()) { // verify Tetrimino dropped into empty space
BEQ endFall             ;   goto endFall;
                        ; }

SMN tetriminoY          ; // Bad Tetrimino position. Undo drop and lock Tetrimino in place.
LDA
DEC
STA                     ; --tetriminoY;

TAB
SMN minY
LDA
SUB
BMI keepMinY
STB                     ; minY = min(minY, tetriminoY);

keepMinY:
SMN mode
SEA MODE_CLEAR_LINES
STA                     ; mode = MODE_CLEAR_LINES;

decFallTimer:
SMN fallTimer
LDA
DEC
STA                     ; --fallTimer;

endFall:
SMN drawOrTest
SEA ACTION_DRAW
STA                     ; drawOrTest = ACTION_DRAW;
SMN tetriminoType
LDA
INC
SMN drawCell+1
STA                     ; *(drawCell+1) = tetriminoType + 1;
JSR drawOrTestTetrimino ; drawOrTestTetrimino(); // draw Tetrimino

JMP main                ; goto main;


clearLines:             ; // Clear lines handler
SMN i
SEA 03
STA                     ; i = 3; // loop 4 times, from i = 3 down to 0.

SMN origin
LDA
SMN tetriminoX
LDB
SUB
SEB PLAYFIELD_WIDTH
ADD
SMN origin
STA                     ; origin = PLAYFIELD_WIDTH * (tetriminoY + 1); // row below Tetrimino center

clearLinesLoop:
SMN minY
LDA
LS3
LDB
ADD
ADD
ADD
SEB 16
SUB
BNE notLine0
SEA PLAYFIELD_WIDTH
notLine0:
DEC
SMN minN+1
STA                     ; *(minN+1) = PLAYFIELD_WIDTH * max(1, minY - 2) - 1; // minimum index to copy from

SMN origin
LDA
SMN playfield
TAN                     ; MN = playfield + origin;

SEB 0A                  ; B = PLAYFIELD_WIDTH - 1;
scanLine:
LDA                     ; if (*MN == CELL_EMPTY) {
BEQ continueClearLines  ;   goto continueClearLines;
                        ; }
TBA
DEC                     ; if (--B < 0) { 
BMI copyLines           ;   goto copyLines; // Found 10 consecutive empty cells         
TAB                     ; }

TNA
INC
TAN                     ; ++N;

JMP scanLine            ; goto scanLine;

TBA
DEC
TAB                     ; if (--B >= 0) {
BPL scanLine            ;   goto scanLine;
                        ; }

copyLines:              ; // Clear line by copying down the lines above it
SEB PLAYFIELD_WIDTH
TNA
SUB
TAN                     ; N -= PLAYFIELD_WIDTH;
LDA                     
TAM                     ; M = *MN;
TNA
ADD
TAN                     ; N += PLAYFIELD_WIDTH;
TMA                     ; A = M;
SEB 00
TBM                     ; M = 0;
STA                     ; *MN = A;

TNA
DEC
TAN
minN:
SEB 00                  ; *** self-modifying code [minN] ***
SUB                     ; if (--N != minN) {
BNE copyLines           ;   goto copyLines;
                        ; }
SEA 09
TAN                     ; N = 9;
SEB CELL_EMPTY
clearTopLine:
STB                     ; *MN = CELL_EMPTY;

TNA
DEC
TAN                     ; if (--N >= 0) {
BPL clearTopLine        ;   goto clearTopLine;
                        ; }
SMN minY
LDA
INC
STA                     ; ++minY;

JMP continueClear       ; goto continueClear;

continueClearLines:
SMN origin
LDA
SEB PLAYFIELD_WIDTH
SUB
STA                     ; origin -= PLAYFIELD_WIDTH;

continueClear:
SMN i
LDA
DEC
STA                     ; if (--i >= 0) {
BPL clearLinesLoop      ;   goto clearLinesLoop;
                        ; } else {
JMP spawn               ;   goto spawn;
                        ; }

drawOrTestTetrimino: ; ---------------------------------------------------------------------------------------
; drawOrTest        - 22 = draw, 23 = test
; drawCell+1        - cell to draw
;
; tetriminoType     - type
; tetriminoRotation - rotation
; tetriminoX        - x
; tetriminoY        - y
;
; z: 0 = some solid, 1 = all empty

SMN i
SEA 03
STA                     ; i = 3; // Loop 4 times, once for each Tetrimino block

SMN tetriminoY
LDA
LS3
LDB
ADD
ADD
ADD
SMN tetriminoX
LDB
ADD
SMN origin
STA                     ; origin = PLAYFIELD_WIDTH * tetriminoY + tetriminoX;

SMN tetriminoType
LDA
LS4
TAB
SMN tetriminoRotation
LDA
LS2
ADD                     ; A = 16 * tetriminoType + 4 * tetriminoRotation;
SMN tetriminosIndex
STA                     ; tetriminosIndex = A;

SMN origin
LDB                     ; B = origin;

drawLoop:

SMN tetriminos
TAN
LDA
ADD
SMN playfield
TAN

drawOrTest:
BNE drawCell            ; *** self-modifying code [BNE = draw, BEQ = test] ***

LDA                     ; if (playfield[tetriminos[tetriminosIndex] + origin] != 0) {
BNE endDrawLoop         ;   goto endDrawLoop;
                        ; } else {
JMP incDrawLoop         ;   goto incDrawLoop;
                        ; }

drawCell:
SEA 00                  ; *** self-modifying code [ 00 = empty; otherwise solid ] ***
STA                     ; playfield[tetriminos[tetriminosIndex] + origin] = [drawCell+1];

incDrawLoop:
SMN i
LDA                     ; if (i == 0) {
BEQ endDrawLoop         ;   goto endDrawLoop;
                        ; }
DEC                     
STA                     ; --i;

SMN tetriminosIndex
LDA                     
INC
STA                     ; A = ++tetriminosIndex;

JMP drawLoop            ; goto drawLoop;

endDrawLoop:
 
RTS                     ; return; // -------------------------------------------------------------------------
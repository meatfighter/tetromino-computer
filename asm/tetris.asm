define RENDER           FB00
define RENDER_VALUE     FF
define EMPTY            00
define SOLID            01
define BACKGROUND       02
define CURTAIN          03
define RELEASED_VALUE   00
define PRESSED_VALUE    FF
define LONG_REPEAT_X    10
define SHORT_REPEAT_X   0A

define BUTTON_LEFT      FB25
define BUTTON_RIGHT     FB27
define BUTTON_DOWN      FB28
define BUTTON_CCW       FB5A
define BUTTON_CW        FB58
define BUTTON_0         FB30
define BUTTON_1         FB31
define BUTTON_2         FB32
define BUTTON_3         FB33
define BUTTON_4         FB34
define BUTTON_5         FB35
define BUTTON_6         FB36
define BUTTON_7         FB37
define BUTTON_8         FB38
define BUTTON_9         FB39

define STATE_GAME_OVER  00
define STATE_PLAYING    01

segment 0000
tetriminos:
; X0 Y0  X1 Y1  X2 Y2  X3 Y3
  FF 00  00 00  01 00  00 01  ; 00 Td
  00 FF  FF 00  00 00  00 01  ; 01 Tl
  FF 00  00 00  01 00  00 FF  ; 02 Tu
  00 FF  00 00  01 00  00 01  ; 03 Tr

  FF 00  00 00  01 00  01 01  ; 04 Jd
  00 FF  00 00  FF 01  00 01  ; 05 Jl
  FF FF  FF 00  00 00  01 00  ; 06 Ju
  00 FF  01 FF  00 00  00 01  ; 07 Jr

  FF 00  00 00  00 01  01 01  ; 08 Zh
  01 FF  00 00  01 00  00 01  ; 09 Zv
  FF 00  00 00  00 01  01 01  ; 0A Zh
  01 FF  00 00  01 00  00 01  ; 0B Zv

  FF 00  00 00  FF 01  00 01  ; 0C O
  FF 00  00 00  FF 01  00 01  ; 0D O
  FF 00  00 00  FF 01  00 01  ; 0E O
  FF 00  00 00  FF 01  00 01  ; 0F O

  00 00  01 00  FF 01  00 01  ; 10 Sh
  00 FF  00 00  01 00  01 01  ; 11 Sv
  00 00  01 00  FF 01  00 01  ; 12 Sh
  00 FF  00 00  01 00  01 01  ; 13 Sv

  FF 00  00 00  01 00  FF 01  ; 14 Ld
  FF FF  00 FF  00 00  00 01  ; 15 Ll
  01 FF  FF 00  00 00  01 00  ; 16 Lu
  00 FF  00 00  00 01  01 01  ; 17 Lr

  FE 00  FF 00  00 00  01 00  ; 18 Ih
  00 FE  00 FF  00 00  00 01  ; 19 Iv
  FE 00  FF 00  00 00  01 00  ; 1A Ih
  00 FE  00 FF  00 00  00 01  ; 1B Iv

segment 0100
;                   0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20
playfieldRowsHigh: FC FC FD FD FD FD FD FD FD FD FE FE FE FE FE FE FE FE FF FF FF
playfieldRowsLow:  CC EC 0C 2C 4C 6C 8C AC CC EC 0C 2C 4C 6C 8C AC CC EC 0C 2C 4C

segment 0200
;       level:  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30
framesPerDrop: 30 2B 26 21 1C 17 12 0D 08 06 05 05 05 04 04 04 03 03 03 02 02 02 02 02 02 02 02 02 02 01

state:             00
level:             00
fallTimer:         00
holdDownPoints:    00
autorepeatX:       00
autorepeatY:       00
dropSpeed:         00

tetriminoType:     00 ; 00--06 (T, J, Z, O, S, L, I)
tetriminoRotation: 00 ; 00--03
tetriminoX:        00 ; 00--09
tetriminoY:        00 ; 00--13

tetriminoNextType: 00 ; 00--06 (T, J, Z, O, S, L, I)

lastLeft:          00 ; prior value of left button
lastRight:         00 ; prior value of right button
lastDown:          00 ; prior value of down button
lastCcw:           00 ; prior value of counterclockwise rotation button
lastCw:            00 ; prior value of clockwise rotation button

seedHigh:          89 ; RNG high
seedLow:           88 ; RNG low
nextBit:           00 ; RNG bit
randomType0:       00 ; Randomly selected tetrimino type
randomType1:       03 ; Randomly selected tetrimino type

tetriminoRow:      00 ; (type << 5) + (rotation << 3)
i:                 00
blockX:            00
blockY:            00
tableIndex:        00
addressHigh:       00
originalValue:     00

main: ; ----------------------------------------------------------------------------------------------------------------

mainLoop:

SMN RENDER              ; Render frame
SEA RENDER_VALUE
STA

; Update RNG -----------------------------------------------------------------------------------------------------------
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
RSH
OR
STA                     ; seedLow = (seedHigh << 7) | (seedLow >> 1);

SMN nextBit
LDB
SMN seedHigh
LDA
RSH
OR
STA                     ; seedHigh = nextBit | (seedHigh >> 1);

                        ; if (randomBit is 1) {
JMI updateRandomType0   ;   updateRandomType0();
                        ; }
JSR updateRandomType1;  ; updateRandomType1(); // ----------------------------------------------------------------------


SMN state
LDA
SEB STATE_PLAYING
SUB
BEQ statePlaying

SMN BUTTON_0
SEA 09                  ; A = 9;

startLoop:
TAB
SEA 30
ADD
TAN                     
TBA
LDB                     ; if (*((M << 8) | (0x30 + A)) != RELEASED_VALUE) {
BNE startButtonPressed  ;   goto startButtonPressed;
                        ; }
DEC                     ; if (--A >= 0) {
BPL startLoop           ;   goto startLoop;
                        ; }
JMP mainLoop            ; goto mainLoop;

startButtonPressed:
DEC                     ; if (--A >= 0) {
BPL startPositive       ;   goto startPositive;
                        ; }
SEA 0A                  ; A = 10;

startPositive:
SMN BUTTON_CCW
LDB                     ; if (BUTTON_CCW == RELEASED_VALUE) {
BEQ ccwNotPressed       ;   goto ccwNotPressed;
                        ; }
SEB 0A
ADD                     ; A += 10;

ccwNotPressed:
SMN level
STA                     ; level = A;

SMN state
SEA STATE_PLAYING
STA                     ; state = STATE_PLAYING;

; TODO <<<<---- CLEAR THE CURTAIN AND INIT PLAYING !!!!

statePlaying:
SEA EMPTY
SMN drawTile+1
STA                     ; [drawTile+1] = EMPTY;
JSR drawTetrimino       ; drawTetrimino();

; Handle shifting ------------------------------------------------------------------------------------------------------
SMN tetriminoX
LDA
SMN originalValue
STA                     ; originalValue = tetriminoX;

SMN BUTTON_DOWN
LDA                     ; if (BUTTON_DOWN != RELEASED_VALUE) { 
BNE endShift            ;   goto endShift;
                        ; }

SMN BUTTON_LEFT
LDA                     ; if (BUTTON_LEFT == RELEASED_VALUE) {
BEQ notPressingLeft     ;   goto notPressingLeft;
                        ; }
SMN lastLeft
LDA                     ; if (lastLeft == RELEASED_VALUE) {
BEQ resetAutorepeatX    ;   goto resetAutorepeatX;
                        ; }

JMP incAutorepeatX      ; goto incAutorepeatX;

notPressingLeft:
SMN BUTTON_RIGHT
LDA                     ; if (BUTTON_RIGHT == RELEASED_VALUE) {
BEQ endShift            ;   goto endShift;
                        ; }

SMN lastRight
LDA                     ; if (lastRight == RELEASED_VALUE) {
BEQ resetAutorepeatX    ;   goto resetAutorepeatX;
                        ; }
incAutorepeatX:
SMN autorepeatX
LDA
INC
STA                     
SEB LONG_REPEAT_X
SUB                     ; if (++autorepeatX != 16) {
BNE endShift            ;   goto endShift;
                        ; }
SEA SHORT_REPEAT_X
STA                     ; autorepeatX = 10;

JMP buttonHeldDown      ; goto buttonHeldDown;

resetAutorepeatX:
SMN autorepeatX
SEA 00
STA                     ; autorepeatX = 0;

buttonHeldDown:
SMN BUTTON_RIGHT
LDA                     ; if (BUTTON_RIGHT == RELEASED_VALUE) {
BEQ notPressingRight    ;   goto notPressingRight;
                        ; }
SMN tetriminoX
LDA
INC                    
STA                     ; ++tetriminoX;

JSR testTetrimino       ; if (!testTetrimino()) {
BNE restoreX            ;   goto restoreX;
                        ; }

JSR updateRandomType0;  ; updateRandomType0();
JMP endShift            ; goto endShift;

notPressingRight:
SMN BUTTON_LEFT
LDA                     ; if (BUTTON_LEFT == RELEASED_VALUE) {
BEQ endShift            ;   goto endShift;
                        ; }
SMN tetriminoX
LDA
DEC                    
STA                     ; --tetriminoX;

JSR testTetrimino       ; if (!testTetrimino()) {
BNE restoreX            ;   goto restoreX;
                        ; }

JSR updateRandomType1;  ; updateRandomType1();
JMP endShift            ; goto endShift;

restoreX:
SMN originalValue
LDA
SMN tetriminoX
STA                     ; tetriminoX = originalValue;

endShift:
SMN BUTTON_LEFT
LDA
SMN lastLeft            ; lastLeft = BUTTON_LEFT;
STA
SMN BUTTON_RIGHT
LDA
SMN lastRight           
STA                     ; lastRight = BUTTON_RIGHT; // ------------------------------------------------------------------


; Handle counterclockwise rotation -------------------------------------------------------------------------------------
SMN BUTTON_CCW
LDA                     ; if (BUTTON_CCW == RELEASED_VALUE) {
BEQ endCcw              ;   goto endCcw;
                        ; }
SMN lastCcw
LDA                     ; if (lastCcw != RELEASED_VALUE) {
BNE endCcw              ;   goto endCcw;
                        ; }
SMN tetriminoRotation
LDA
SMN originalValue
STA                     ; originalValue = tetriminoRotation;

SMN tetriminoRotation
LDA
DEC
BPL noCcwRollover       
SEA 03                  ; if (--tetriminoRotation == 0) {
noCcwRollover:          ;   tetriminoRotation = 3;
STA                     ; }

JSR updateRandomType0;  ; updateRandomType0();

JSR testTetrimino       ; if (testTetrimino()) {
BEQ endCcw              ;   goto endCcw;
                        ; }
SMN originalValue
LDA
SMN tetriminoRotation
STA                     ; tetriminoRotation = originalValue;

endCcw:
SMN BUTTON_CCW
LDA
SMN lastCcw
STA                     ; lastCcw = BUTTON_CCW;  // ---------------------------------------------------------------------


; Handle clockwise rotation --------------------------------------------------------------------------------------------
SMN BUTTON_CW
LDA                     ; if (BUTTON_CW == RELEASED_VALUE) {
BEQ endCw               ;   goto endCw;
                        ; }
SMN lastCw
LDA                     ; if (lastCw != RELEASED_VALUE) {
BNE endCw               ;   goto endCw;
                        ; }
SMN tetriminoRotation
LDA
SMN originalValue
STA                     ; originalValue = tetriminoRotation;

SMN tetriminoRotation
LDA
DEC
BPL noCwRollover       
SEA 03                  ; if (--tetriminoRotation == 0) {
noCwRollover:           ;   tetriminoRotation = 3;
STA                     ; }

JSR updateRandomType1;  ; updateRandomType1();

JSR testTetrimino       ; if (testTetrimino()) {
BEQ endCw               ;   goto endCw;
                        ; }
SMN originalValue
LDA
SMN tetriminoRotation
STA                     ; tetriminoRotation = originalValue;

endCw:
SMN BUTTON_CW
LDA
SMN lastCw
STA                     ; lastCw = BUTTON_CW;  // -----------------------------------------------------------------------


; Handle drop ----------------------------------------------------------------------------------------------------------
SMN fallTimer
LDA
INC
STA                     ; ++fallTimer;

SMN autorepeatY
LDA                     ; if (autorepeatY > 0) {
BPL autorepeating       ;   goto autorepeating;
                        ; } else if (autorepeatY == 0) {
BEQ playing             ;   goto playing;
                        ; }

                        ; game just started
                        ; initial Tetrimino hanging at spawn point

SMN lastDown
LDA                     ; if (lastDown != releasedButton) {
BNE incrementAutorepeatY;   goto incrementAutorepeatY;
                        ; }
SMN BUTTON_DOWN
LDA                     ; if (BUTTON_DOWN == releasedButton) {
BEQ incrementAutorepeatY;   goto incrementAutorepeatY;
                        ; }

                        ; player just pressed down ending startup delay

SMN autorepeatY
SEA 00
STA                     ; autorpeatY = 0;

playing:
SMN BUTTON_LEFT
LDA                     ; if (BUTTON_LEFT != releasedButton) {
BNE lookupDropSpeed     ;   goto lookupDropSpeed;
                        ; }
SMN BUTTON_RIGHT
LDA                     ; if (BUTTON_RIGHT != releasedButton) {
BNE lookupDropSpeed     ;   goto lookupDropSpeed;
                        ; }

                        ; left/right not pressed

SMN lastDown
LDA                     ; if (lastDown != releasedButton) {
BNE lookupDropSpeed     ;   goto lookupDropSpeed;
                        ; }
SMN BUTTON_DOWN
LDA                     ; if (BUTTON_DOWN == releasedButton) {
BEQ lookupDropSpeed     ;   goto lookupDropSpeed;
                        ; }

                        ; player exclusively just presssed down

SMN autorepeatY
SEA 01
STA                     ; autorepeatY = 1;

JMP lookupDropSpeed     ; goto lookupDropSpeed;

autorepeating:
SMN BUTTON_LEFT
LDA                     ; if (BUTTON_LEFT != releasedButton) {
BNE downReleased        ;   goto downReleased;
                        ; }
SMN BUTTON_RIGHT
LDA                     ; if (BUTTON_RIGHT != releasedButton) {
BNE downReleased        ;   goto downReleased;
                        ; }
SMN BUTTON_DOWN
LDA                     ; if (BUTTON_DOWN != releasedButton) {
BNE downPressed         ;   goto downPressed;
                        ; }
downReleased:
SEA 00
SMN autorepeatY         
STA                     ; autorepeatY = 0;
SMN holdDownPoints      
STA                     ; holdDownPoints = 0;

JMP lookupDropSpeed     ; goto lookupDropSpeed;

downPressed:
SMN autorepeatY
LDA
INC
STA                     
SEB 03
SUB                     ; if (++autorepeatY < 3) {
BMI lookupDropSpeed     ;   goto lookupDropSpeed;
                        ; }
SEA 01
SMN autorepeatY         
STA                     ; autorepeatY = 1;

SMN holdDownPoints
LDA
INC
STA                     ; ++holdDownPoints;

drop:
SEA 00
SMN fallTimer
STA                     ; fallTimer = 0;

SMN tetriminoY
LDA
SMN originalValue
STA                     ; originalValue = tetriminoY;

SMN tetriminoY
LDA
INC
STA                     ; ++tetriminoY

JSR testTetrimino       ; if (testTetrimino()) {
BEQ endDrop             ;   goto endDrop;
                        ; }

                        ; the piece is locked

SMN originalValue
LDA
SMN tetriminoY
STA                     ; tetriminoY = originalValue;

; TODO PIECE LOCKED <------------------------------------------ !!!!!!!!!!!!!!!!!!!!!

JMP endDrop             ; goto endDrop;

lookupDropSpeed:
SMN level
LDA
SEB 1D
SUB                     ; if (level < 29) {
BMI tableLookup         ;   goto tableLookup;
                        ; }
SEA 01                  ; A = 1;
JMP noTableLookup       ; goto noTableLookup;

tableLookup:
SMN level
LDA
SMN framesPerDrop
TNB
ADD
TAN
LDA                     ; A = framesPerDrop[level];

noTableLookup:
SMN dropSpeed
STA                     ; dropSpeed = A;

SMN fallTimer
LDB
SUB
BEQ drop                ; if (fallTimer >= dropSpeed) {
BMI drop                ;   goto drop;
                        ; }

JMP endDrop             ; goto endDrop;

incrementAutorepeatY:
SMN autorepeatY
LDA
INC
STA                     ; ++autorepeatY;

endDrop:
SMN BUTTON_DOWN
LDA
SMN lastDown
STA                     ; lastDown = BUTTON_DOWN;  // ------------------------------------------------------------------


SEA SOLID
SMN drawTile+1
STA                     ; [drawTile+1] = SOLID;
JSR drawTetrimino       ; drawTetrimino();

JMP mainLoop            ; goto mainLoop; // ----------------------------------------------------------------------------


testTetrimino: ; -------------------------------------------------------------------------------------------------------
; tetriminoType     - type
; tetriminoRotation - rotation
; tetriminoX        - x
; tetriminoY        - y

; z: 0 = valid position, 1 = invalid position

SMN tetriminoType
LDA
LSH
LSH
LSH
LSH
LSH
TAB
SMN tetriminoRotation
LDA
LSH
LSH
LSH
ADD
SMN tetriminoRow
STA                     ; tetriminoRow = (tetriminoType << 5) + (tetriminoRotation << 3);

SMN i
SEA 03
STA                     ; i = 3;

testLoop:

LSH
SMN tetriminoRow
LDB
ADD
SMN tableIndex          
STA                     ; tableIndex = tetriminoRow + (i << 1);

SMN tetriminos
TAN
LDA
SMN tetriminoX
LDB
ADD
SMN blockX              
STA                     ; blockX = tetriminos[tableIndex] + tetriminoX;

SMN tableIndex
LDA
INC
SMN tetriminos
TAN
LDA
SMN tetriminoY
LDB                     ; A = tetriminos[tableIndex + 1] + tetriminoY;
ADD                     ; if (A < 0) {
BMI continueTestLoop    ;   goto continueTestLoop;                 
SMN blockY              ; }
STA                     ; blockY = A;

SMN playfieldRowsHigh
TNB
ADD
TAN
LDA
SMN addressHigh         ; addressHigh = playfieldRowsHigh[blockY];
STA

SMN blockY
LDA
SMN playfieldRowsLow
TNB
ADD
TAN
LDA                    
SMN blockX
LDB
ADD                     ; A = playfieldRowsLow[blockY] + blockX;

SMN addressHigh
LDB
TBM
TAN
LDA                     ; if (*((addressHigh << 8) | A) != EMPTY) {
BNE endTestLoop         ;   goto endTestLoop;
                        ; }                      
continueTestLoop:
SMN i
LDA                     ; if (i == 0) {
BEQ endTestLoop         ;   goto endTestLoop;
                        ; } 
DEC                     
STA                     ; --i;

JMP testLoop            ; goto testLoop;

endTestLoop:
RTS                     ; return; // -----------------------------------------------------------------------------------



drawTetrimino: ; -------------------------------------------------------------------------------------------------------
; tetriminoType     - type
; tetriminoRotation - rotation
; tetriminoX        - x
; tetriminoY        - y
; drawTile+1        - tile

SMN tetriminoType
LDA
LSH
LSH
LSH
LSH
LSH
TAB
SMN tetriminoRotation
LDA
LSH
LSH
LSH
ADD
SMN tetriminoRow
STA                     ; tetriminoRow = (tetriminoType << 5) + (tetriminoRotation << 3);

SMN i
SEA 03
STA                     ; i = 3;

drawLoop:

LSH
SMN tetriminoRow
LDB
ADD
SMN tableIndex          
STA                     ; tableIndex = tetriminoRow + (i << 1);

SMN tetriminos
TAN
LDA
SMN tetriminoX
LDB
ADD
SMN blockX              
STA                     ; blockX = tetriminos[tableIndex] + tetriminoX;

SMN tableIndex
LDA
INC
SMN tetriminos
TAN
LDA
SMN tetriminoY
LDB                     ; A = tetriminos[tableIndex + 1] + tetriminoY;
ADD                     ; if (A < 0) {    
BMI continueDrawLoop    ;   goto continueDrawLoop;
SMN blockY              ; }
STA                     ; blockY = A;

SMN playfieldRowsHigh
TNB
ADD
TAN
LDA
SMN addressHigh         ; addressHigh = playfieldRowsHigh[blockY];
STA

SMN blockY
LDA
SMN playfieldRowsLow
TNB
ADD
TAN
LDA                    
SMN blockX
LDB
ADD                     ; A = playfieldRowsLow[blockY] + blockX;

SMN addressHigh
LDB
TBM
TAN
drawTile:  
SEA 00
STA                     ; *((addressHigh << 8) | A) = [drawTile+1];

continueDrawLoop:
SMN i
LDA                     ; if (i == 0) {
BEQ endDrawLoop         ;   goto endDrawLoop;
                        ; } 
DEC                     
STA                     ; --i;

JMP drawLoop            ; goto drawLoop

endDrawLoop:
RTS                     ; return; // -----------------------------------------------------------------------------------


updateRandomType0: ; ---------------------------------------------------------------------------------------------------

SMN randomType0
LDA                     ; if (randomType0 == 0) {
BEQ randomType0Zero     ;   goto randomType0Zero;
                        ; }
DEC                     ; --randomType0;
STA
RTS                     ; return;

randomType0Zero:
SEA 06
STA                     ; randomType0Zero = 6;

RTS                     ; return; // -----------------------------------------------------------------------------------


updateRandomType1: ; ---------------------------------------------------------------------------------------------------

SMN randomType1
LDA                     ; if (randomType1 == 0) {
BEQ randomType1Zero     ;   goto randomType1Zero;
                        ; }
DEC                     ; --randomType1;
STA
RTS                     ; return;

randomType1Zero:
SEA 06
STA                     ; randomType1Zero = 6;

RTS                     ; return; // -----------------------------------------------------------------------------------


segment FC00 ; vram
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 03 03 03 03 03 03 03 03 03 03 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
define FALL_SPEED 01

define STATE_GAME_OVER   00
define STATE_PLAYING     01
define STATE_CLEAR_LINES 02

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
; B0 B1 B2 B3
  FF 00 01 0B ; 00 Td
  F5 FF 00 0B ; 01 Tl
  FF 00 01 F5 ; 02 Tu
  F5 00 01 0B ; 03 Tr

  FF 00 01 0C ; 04 Jd
  F5 00 0A 0B ; 05 Jl
  F4 FF 00 01 ; 06 Ju
  F5 F6 00 0B ; 07 Jr

  FF 00 0B 0C ; 08 Zh
  F6 00 01 0B ; 09 Zv
  FF 00 0B 0C ; 0A Zh
  F6 00 01 0B ; 0B Zv

  FF 00 0A 0B ; 0C O
  FF 00 0A 0B ; 0D O
  FF 00 0A 0B ; 0E O
  FF 00 0A 0B ; 0F O

  00 01 0A 0B ; 10 Sh
  F5 00 01 0C ; 11 Sv
  00 01 0A 0B ; 12 Sh
  F5 00 01 0C ; 13 Sv

  FF 00 01 0A ; 14 Ld
  F4 F5 00 0B ; 15 Ll
  F6 FF 00 01 ; 16 Lu
  F5 00 0B 0C ; 17 Lr

  FE FF 00 01 ; 18 Ih
  EA F5 00 0B ; 19 Iv
  FE FF 00 01 ; 1A Ih
  EA F5 00 0B ; 1B Iv

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

nextType:            00 ; 00--06 (T, J, Z, O, S, L, I)

seedHigh:            89
seedLow:             88
nextBit:             00

i:                   00
origin:              00
tetriminosIndex:     00

fallTimer:           00
state:               00
minY:                00

main: ; ----------------------------------------------------------------------------------------------------------------

mainLoop:

SMN drawFrame
SEA 01
STA                     ; render frame

JSR updateNext          ; updateNext();

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
BMI skipNextUpdate      ;   goto skipNextUpdate;
                        ; }
JSR updateNext          ; updateNext();

skipNextUpdate:
SMN state
LDB
SEA STATE_PLAYING
SUB                     ; if (state == STATE_PLAYING) {
BEQ playing             ;   goto playing;
                        ; }
SEA STATE_CLEAR_LINES
SUB                     ; if (state == STATE_CLEAR_LINES) {
BEQ clearLines          ;   goto clearLines;
                        ; }
SMN startButton
LDA                     ; if (startButton == 0) {
BEQ mainLoop;           ;   goto mainLoop;
                        ; }

SMN minY
SEA 16
STA                     ; minY = 22;

SEA F1                  ; A = 0xF1;
SEB 00                  
SMN playfield
clearLoop:
TAN
STB                     ; playfield[A] = 0;
DEC                     ; if (--A != 0) {
BNE clearLoop           ;   goto clearLoop;
                        ; }
STB                     ; playfield[0] = 0

SMN 00F1                ; MN = 0x00F1;
SEB FF                  
edgeLoop:
STB                     ; *MN = 0xFF;
TNA
SEB 0B
SUB
TAN                     ; MN -= 11;
SEB FF
SUB                     ; if (*MN != -1) {
BNE edgeLoop            ;   goto edgeLoop;
                        ; }
spawn:
SMN state
SEA STATE_PLAYING
STA                     ; state = STATE_PLAYING;
SMN tetriminoRotation
SEA 00
STA                     ; tetriminoRotation = 0;
SMN tetriminoX
SEA 05
STA                     ; tetriminoX = 5;
SMN tetriminoY
SEA 02
STA                     ; tetriminoY = 2;
SMN fallTimer
SEA FALL_SPEED
STA                     ; fallTimer = FALL_SPEED;

SMN tetriminoType
LDA
SMN nextType
LDB
SUB
BNE uniqueType          ; if (tetriminoType == nextType) {
JSR updateNext          ;   updateNext(); 
LDB                     ; }
uniqueType:
SMN tetriminoType       
STB                     ; tetriminoType = nextType; 

SMN drawOrTest
SEA 23
STA                     ; drawOrTest = 23;
JSR drawOrTestTetrimino ; if (drawOrTestTetrimino()) {
BEQ keepPosition        ;   goto playing;
                        ; }
SMN state
SEA STATE_GAME_OVER
STA                     ; state = STATE_GAME_OVER;

JMP mainLoop            ; goto mainLoop;

playing:
SMN drawOrTest
SEA 22
STA                     ; drawOrTest = 22;
SMN drawBlock+1
SEA 00
STA                     ; *(drawBlock+1) = 0;
JSR drawOrTestTetrimino ; drawOrTestTetrimino();

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
SMN tetriminoX
LDA
DEC
STA                     ; --tetriminoX;
JSR updateNext          ; updateNext();
JMP testCcwRotateButton ; goto testCcwRotateButton;

testRightButton:
SMN rightButton
LDA                     ; if (rightButton == 0) {
BEQ testCcwRotateButton ;   goto testCcwRotateButton;
                        ; }
SMN tetriminoX
LDA
INC
STA                     ; ++tetriminoX;
JSR updateNext          ; updateNext();

testCcwRotateButton:
SEB 03                  ; B = 3;
SMN ccwRotateButton
LDA                     ; if (ccwRotateButton == 0) {
BEQ testCwRotateButton  ;   goto testCwRotateButton;
                        ; }
SMN tetriminoRotation
LDA
DEC
AND
STA                     ; tetriminoRotation = (tetriminoRotation - 1) & 3;

JSR updateNext          ; updateNext();
JMP validatePosition    ; goto validatePosition;

testCwRotateButton:
SMN cwRotateButton
LDA                     ; if (cwRotateButton == 0) {
BEQ validatePosition    ;   goto validatePosition;
                        ; }
SMN tetriminoRotation
LDA
INC
AND
STA                     ; tetriminoRotation = (tetriminoRotation + 1) & 3;

JSR updateNext          ; updateNext();

validatePosition: 
SMN drawOrTest
SEA 23
STA                     ; drawOrTest = 23;
JSR drawOrTestTetrimino ; if (drawOrTestTetrimino()) {
BEQ keepPosition        ;   goto keepPosition;
                        ; }
SMN lastRotation
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
SEA 00
STA                     ; fallTimer = 0;                  

updateFallTimer:
LDA                     ; if (fallTimer != 0) {
BNE decFallTimer        ;   goto decFallTimer;
SEA FALL_SPEED          ; } 
STA                     ; fallTimer = FALL_SPEED;

SMN tetriminoY
LDA
INC
STA                     ; ++tetriminoY;
JSR drawOrTestTetrimino ; if (drawOrTestTetrimino()) {
BEQ endFall             ;   goto endFall;
                        ; }
SMN tetriminoY
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
SMN state
SEA STATE_CLEAR_LINES
STA                     ; state = STATE_CLEAR_LINES;

decFallTimer:
SMN fallTimer
LDA
DEC
STA

endFall:
SMN drawOrTest
SEA 22
STA                     ; drawOrTest = 22;
SMN tetriminoType
LDA
INC
SMN drawBlock+1
STA                     ; *(drawBlock+1) = tetriminoType + 1;
JSR drawOrTestTetrimino ; drawOrTestTetrimino();

JMP mainLoop

clearLines:
SMN i
SEA 03
STA                     ; i = 3;

SMN origin
LDA
SMN tetriminoX
LDB
SUB
SEB 0B
ADD
SMN origin
STA                     ; origin = 11 * tetriminoY + 11;

clearLinesLoop:
SMN minY
LDA
LSH
LSH
LSH
LDB
ADD
ADD
ADD
SEB 16
SUB
BNE notLine0
SEA 0B
notLine0:
DEC
SMN minN+1
STA                     ; *(minN+1) = max(11, 11 * minY - 22) - 1;

SMN origin
LDA
SMN playfield
TAN                     ; MN = playfield + origin;

SEB 0A                  ; B = 10;
scanLine:
LDA                     ; if (*MN == 0) {
BEQ continueClearLines  ;   goto continueClearLines;
                        ; }
TNA
INC
TAN                     ; ++N;

TBA
DEC
TAB                     ; if (--B >= 0) {
BPL scanLine            ;   goto scanLine;
                        ; }
copyLines:
SEB 0B
TNA
SUB
TAN                     ; N -= 11;
LDA                     
TAM                     ; M = *MN;
TNA
ADD
TAN                     ; N += 11;
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
SEB 00
clearTopLine:
STB                     ; *MN = 0;

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
SEB 0B
SUB
STA                     ; origin -= 11;

continueClear:
SMN i
LDA
DEC
STA                     ; if (--i > 0) {
BPL clearLinesLoop      ;   goto clearLinesLoop;
                        ; } else {
JMP spawn               ;   goto spawn;
                        ; }

drawOrTestTetrimino: ; -------------------------------------------------------------------------------------------------
; drawOrTest        - 22 = draw, 23 = test
; drawBlock+1       - block to draw
;
; tetriminoType     - type
; tetriminoRotation - rotation
; tetriminoX        - x
; tetriminoY        - y
;
; z: 0 = valid position, 1 = invalid position

SMN i
SEA 03
STA                     ; i = 3;

SMN tetriminoY
LDA
LSH
LSH
LSH
LDB
ADD
ADD
ADD
SMN tetriminoX
LDB
ADD
SMN origin
STA                     ; origin = 11 * tetriminoY + tetriminoX;

SMN tetriminoType
LDA
LSH
LSH
LSH
LSH
TAB
SMN tetriminoRotation
LDA
LSH
LSH
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
BNE drawBlock           ; *** self-modifying code [BNE = draw, BEQ = test] ***

LDA                     ; if (playfield[tetriminos[tetriminosIndex] + origin] != 0) {
BNE endDrawLoop         ;   goto endDrawLoop;
                        ; } else {
JMP incDrawLoop         ;   goto incDrawLoop;
                        ; }

drawBlock:
SEA 00
STA                     ; playfield[tetriminos[tetriminosIndex] + origin] = [drawBlock+1];

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
 
RTS                     ; return; // -----------------------------------------------------------------------------------


updateNext: ; ----------------------------------------------------------------------------------------------------------

SMN nextType
LDA
DEC
BPL endUpdateNext
SEA 06
endUpdateNext:
STA                     ; nextType = (nextType == 0) ? 6 : (nextType - 1);

RTS                     ; return; // -----------------------------------------------------------------------------------

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
  FF FF FF FF FF FF FF FF FF FF FF  ; 22

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
rotateButton:        00 ; 00 = released; otherwise, pressed 

tetriminoType:       00 ; 00--06 (T, J, Z, O, S, L, I)
tetriminoRotation:   00 ; 00--03
tetriminoX:          05 ; 00--09
tetriminoY:          02 ; 02--15

lastRotation:        00 ; 00--03
lastX:               05 ; 00--09

nextType:            00 ; 00--06 (T, J, Z, O, S, L, I)

i:                   00
origin:              00
tetriminosIndex:     00

main: ; ----------------------------------------------------------------------------------------------------------------

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
JMP testRotationButton  ; goto testRotationButton;

testRightButton:
SMN rightButton
LDA                     ; if (rightButton == 0) {
BEQ testRotationButton  ;   goto testRotationButton;
                        ; }
SMN tetriminoX
LDA
INC
STA                     ; ++tetriminoX;

testRotationButton:
SMN rotationButton
LDA                     ; if (rotationButton == 0) {
BEQ validatePosition    ;   goto validatePosition;
                        ; }
SMN tetriminoRotation
LDA
DEC
BPL positiveRotation
SEA 03
positiveRotation:
STA                     ; tetriminoRotation = (tetriminoRotation == 0) ? 3 : tetriminoRotation - 1;

validatePosition: 


SMN drawOrTest
SEA 22
STA                     ; drawOrTest = 22;
SMN tetriminoType
LDA
INC
SMN drawBlock+1
STA                     ; *(drawBlock+1) = tetriminoType + 1;
JSR drawOrTestTetrimino ; drawOrTestTetrimino();

SMN drawFrame
SEA 01
STA                     ; render frame

JMP main

drawOrTestTetrimino: ; -------------------------------------------------------------------------------------------------
; drawOrTest - 22 = draw, 23 = test
; drawBlock+1 - block to draw

SMN i
SEA 03
STA                     ; i = 3;

SMN tetriminoY
LDA
LSH
LSH
LSH
TAB
LDA
LSH
ADD
TAB
LDA
ADD                     
TAB
SMN tetriminoX
LDA
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
LDA
DEC                     ; if (--i < 0) {
BMI endDrawLoop;        ;   goto endDrawLoop;
STA                     ; }

SMN tetriminosIndex
LDA                     
INC                     ; A = tetriminosIndex + 1;
STA                     ; tetriminosIndex = A;

JMP drawLoop            ; goto drawLoop;

endDrawLoop:
 
RTS                     ; return; // -----------------------------------------------------------------------------------

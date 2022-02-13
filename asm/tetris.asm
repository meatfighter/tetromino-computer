define render            FB00
define renderValue       FF
define empty             00
define solid             01
define releasedValue     00
define pressedValue      FF
define leftButton        FB25
define rightButton       FB27
define downButton        FB28
define ccwButton         FB5A
define cwButton          FB58
define longRepeatX       10
define shortRepeatX      0A

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

autorepeatX:       00
autorepeatY:       00 

tetriminoType:     00 ; 00--06 (T, J, Z, O, S, L, I)
tetriminoRotation: 01 ; 00--03
tetriminoX:        05 ; 00--09
tetriminoY:        05 ; 00--13

lastLeft:          00 ; prior value of left button
lastRight:         00 ; prior value of right button
lastDown:          00 ; prior value of down button
lastCcw:           00 ; prior value of counterclockwise rotation button
lastCw:            00 ; prior value of clockwise rotation button

tetriminoRow:      00 ; (type << 5) + (rotation << 3)
i:                 00
blockX:            00
blockY:            00
tableIndex:        00
addressHigh:       00
originalValue:     00

main: ; ----------------------------------------------------------------------------------------------------------------

SEA 01
SMN drawTile+1
STA
JSR drawTetrimino

mainLoop:

SEA empty
SMN drawTile+1
STA                     ; [drawTile+1] = empty;
JSR drawTetrimino       ; drawTetrimino();

; Handle counterclockwise rotation -------------------------------------------------------------------------------------
SMN ccwButton
LDA                     ; if (ccwButton == releasedValue) {
BEQ endCcw              ;   goto endCcw;
                        ; }
SMN lastCcw
LDA                     ; if (lastCcw != releasedValue) {
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

JSR testTetrimino       ; if (testTetrimino()) {
BEQ endCcw              ;   goto endCcw;
                        ; }
SMN originalValue
LDA
SMN tetriminoRotation
STA                     ; tetriminoRotation = originalValue;

endCcw:
SMN ccwButton
LDA
SMN lastCcw
STA                     ; lastCcw = ccwButton;  // ---------------------------------------------------------------------


; Handle clockwise rotation --------------------------------------------------------------------------------------------
SMN cwButton
LDA                     ; if (cwButton == releasedValue) {
BEQ endCw               ;   goto endCw;
                        ; }
SMN lastCw
LDA                     ; if (lastCw != releasedValue) {
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

JSR testTetrimino       ; if (testTetrimino()) {
BEQ endCw               ;   goto endCw;
                        ; }
SMN originalValue
LDA
SMN tetriminoRotation
STA                     ; tetriminoRotation = originalValue;

endCw:
SMN cwButton
LDA
SMN lastCw
STA                     ; lastCw = cwButton;  // -----------------------------------------------------------------------


; Handle shifting ------------------------------------------------------------------------------------------------------
SMN tetriminoX
LDA
SMN originalValue
STA                     ; originalValue = tetriminoX;

SMN downButton
LDA                     ; if (downButton != releasedValue) { 
BNE endShift            ;   goto endShift;
                        ; }

SMN leftButton
LDA                     ; if (leftButton == releasedValue) {
BEQ notPressingLeft     ;   goto notPressingLeft;
                        ; }
SMN lastLeft
LDA                     ; if (lastLeft == releasedValue) {
BEQ resetAutorepeatX    ;   goto resetAutorepeatX;
                        ; }

JMP incAutorepeatX      ; goto incAutorepeatX;

notPressingLeft:
SMN rightButton
LDA                     ; if (rightButton == releasedValue) {
BEQ endShift            ;   goto endShift;
                        ; }

SMN lastRight
LDA                     ; if (lastRight == releasedValue) {
BEQ resetAutorepeatX    ;   goto resetAutorepeatX;
                        ; }
incAutorepeatX:
SMN autorepeatX
LDA
INC
STA                     
SEB longRepeatX
SUB                     ; if (++autorepeatX != 16) {
BNE endShift            ;   goto endShift;
                        ; }
SEA shortRepeatX
STA                     ; autorepeatX = 10;

JMP buttonHeldDown      ; goto buttonHeldDown;

resetAutorepeatX:
SMN autorepeatX
SEA 00
STA                     ; autorepeatX = 0;

buttonHeldDown:
SMN rightButton
LDA                     ; if (rightButton == releasedValue) {
BEQ notPressingRight    ;   goto notPressingRight;
                        ; }
SMN tetriminoX
LDA
INC                    
STA                     ; ++tetriminoX;

JSR testTetrimino       ; if (!testTetrimino()) {
BNE restoreX            ;   goto restoreX;
                        ; }

JMP endShift            ; goto endShift;

notPressingRight:
SMN leftButton
LDA                     ; if (leftButton == releasedValue) {
BEQ endShift            ;   goto endShift;
                        ; }
SMN tetriminoX
LDA
DEC                    
STA                     ; --tetriminoX;

JSR testTetrimino       ; if (!testTetrimino()) {
BNE restoreX            ;   goto restoreX;
                        ; }

JMP endShift            ; goto endShift;

restoreX:
SMN originalValue
LDA
SMN tetriminoX
STA                     ; tetriminoX = originalValue;

endShift:
SMN leftButton
LDA
SMN lastLeft            ; lastLeft = leftButton;
STA
SMN rightButton
LDA
SMN lastRight           
STA                     ; lastRight = rightButton; // ------------------------------------------------------------------


; Handle drop ----------------------------------------------------------------------------------------------------------
SMN autorepeatY
LDA                     ; if (autorepeatY > 0) {
BPL autorepeating       ;   goto autorepeating;
                        ; } else if (autorepeatY == 0) {
BEQ playing             ;   goto playing;
                        ; }



SEA solid
SMN drawTile+1
STA                     ; [drawTile+1] = solid;
JSR drawTetrimino       ; drawTetrimino();

SMN render              ; Render frame
SEA renderValue
STA

JMP mainLoop ; ---------------------------------------------------------------------------------------------------------


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
LDA                     ; if (*((addressHigh << 8) | A) != empty) {
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
RTS ; ------------------------------------------------------------------------------------------------------------------



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
RTS ; ------------------------------------------------------------------------------------------------------------------








segment FC00 ; vram
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 00 00 00 00 00 00 00 00 00 00 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02 02
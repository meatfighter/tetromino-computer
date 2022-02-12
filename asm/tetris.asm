define render            FB00
define renderValue       FF
define vramHigh          FC
define playfield         CB
define solid             01

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
;                   0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19
playfieldRowsHigh: FC FC FD FD FD FD FD FD FD FD FE FE FE FE FE FE FE FE FF FF
playfieldRowsLow:  CC EC 0C 2C 4C 6C 8C AC CC EC 0C 2C 4C 6C 8C AC CC EC 0C 2C

tetriminoType:     00 ; 00--06 (T, J, Z, O, S, L, I)
tetriminoRotation: 00 ; 00--03
tetriminoX:        00 ; 00--09
tetriminoY:        00 ; 00--13

i:                 00
blockX:            00
blockY:            00
rowOffset:         00
tableIndex:        00
addressHigh:       00

main:

; Draw tetrimino

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
SMN rowOffset
STA                     ; rowOffset = (tetriminoType << 5) + (tetriminoRotation << 3); 

SMN i
SEA 03
STA                     ; i = 3;

drawLoop:

LSH
SMN rowOffset
LDB
ADD
SMN tableIndex          
STA                     ; tableIndex = rowOffset + (i << 1);

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
LDB
ADD                     
SMN blockY              
STA                     ; blockY = tetriminos[tableIndex + 1] + tetriminoY;

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
SEA solid
STA                     ; *((addressHigh << 8) | A) = solid;

SMN i
LDA
CLB                     
SUB                     ; if (i == 0) {
BEQ endDrawLoop         ;   goto endDrawLoop;
                        ; }
DEC                     
STA                     ; --i;
JMP drawLoop            ; goto drawLoop;

endDrawLoop:

SMN render              ; Render frame
SEA renderValue
STA

JMP main



segment FC00 ; screen
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
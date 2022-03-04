segment 0100
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

tetriminoType:     00 ; 00--06 (T, J, Z, O, S, L, I)
tetriminoRotation: 00 ; 00--03
tetriminoX:        00 ; 00--09
tetriminoY:        00 ; 00--13

tetriminoNextType: 00 ; 00--06 (T, J, Z, O, S, L, I)

main:

PRINT tetriminoType

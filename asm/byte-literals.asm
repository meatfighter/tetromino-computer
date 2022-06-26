define X 3     ; #define X = 0x03;

SEA D5         ; A = 0xD5;

i: 00          ; i = 0;

SMN i
LDA            ; A = i;

values:        ; values[] = { 0xFE, 0xA0, 0x91, 0x5B, 0xD5, 0x3C, 0x7F, 0x88 };
  FE A0 91 5B 
  D5 3C 7F 88

SMN values
SEA X
TNB
ADD


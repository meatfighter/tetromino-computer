segment 0100

table:                  ; table[] = { 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80 }; 
01 02 04 08 
10 20 40 80

index: 03               ; index = 0x03;

SMN index               ; MN = &index;
LDA                     ; A = *MN;
SMN table               ; MN = &table;
TNB                     ; B = N;
ADD                     ; A += B;
TAN                     ; N = A;
LDA                     ; A = *MN;
v: 00                   ; v = 0;

SMN v                   ; MN = &v;
LDA                     ; A = *MN;
INC                     ; ++A;
STA                     ; *MN = A;
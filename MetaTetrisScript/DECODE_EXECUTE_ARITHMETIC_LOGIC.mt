SWAP 14
SWAP 16
SWAP 15
SWAP 14
SWAP 19
SWAP 18
SWAP 17
SWAP 16
SWAP 20
SWAP 19
SWAP 18
SWAP 17
CLEAR 14                ; d = 0;      
ADD_C 12                ; s1 = (i is ADD);
SWAP 13
OR_AB_FB 13             ; d |= s1;
COPY_B_A 15             ; s0 = A;
SWAP 15
ADD_AB_FB 16            ; s0 += B;
SWAP 15
C_COPY_A_B 14           ; if (s1) A = s0;
SWAP 13
AND_C 12                ; s1 = (i is AND);
SWAP 13
OR_AB_FB 13             ; d |= s1;
COPY_B_A 15             ; s0 = A;
SWAP 15
AND_AB_FB 16            ; s0 &= B;
SWAP 15
C_COPY_A_B 14           ; if (s1) A = s0;
SWAP 13
DEC_C 12                ; s1 = (i is DEC);
SWAP 13
OR_AB_FB 13             ; d |= s1;
COPY_B_A 15             ; s0 = A;
DEC 15                  ; --s0;
C_COPY_A_B 14           ; if (s1) A = s0;
SWAP 13
INC_C 12                ; s1 = (i is INC);
SWAP 13
OR_AB_FB 13             ; d |= s1;
COPY_B_A 15             ; s0 = A;
INC 15                  ; ++s0;
C_COPY_A_B 14           ; if (s1) A = s0;
SWAP 13
LS2_C 12                ; s1 = (i is LS2);
SWAP 13
OR_AB_FB 13             ; d |= s1;
COPY_B_A 15             ; s0 = A;
LS2 15                  ; s0 <<= 2;
C_COPY_A_B 14           ; if (s1) A = s0;
SWAP 13
LS3_C 12                ; s1 = (i is LS3);
SWAP 13
OR_AB_FB 13             ; d |= s1;
COPY_B_A 15             ; s0 = A;
LS3 15                  ; s0 <<= 3;
C_COPY_A_B 14           ; if (s1) A = s0;
SWAP 13
LS4_C 12                ; s1 = (i is LS4);
SWAP 13
OR_AB_FB 13             ; d |= s1;
COPY_B_A 15             ; s0 = A;
LS4 15                  ; s0 <<= 4;
C_COPY_A_B 14           ; if (s1) A = s0;
SWAP 13
OR_C 12                 ; s1 = (i is OR);
SWAP 13
OR_AB_FB 13             ; d |= s1;
COPY_B_A 15             ; s0 = A;
SWAP 15
OR_AB_FB 16             ; s0 |= B;
SWAP 15
C_COPY_A_B 14           ; if (s1) A = s0;
SWAP 13
RS1_C 12                ; s1 = (i is RS1);
SWAP 13
OR_AB_FB 13             ; d |= s1;
COPY_B_A 15             ; s0 = A;
RS1 15                  ; s0 >>= 1;
C_COPY_A_B 14           ; if (s1) A = s0;
SWAP 13
RS5_C 12                ; s1 = (i is RS5);
SWAP 13
OR_AB_FB 13             ; d |= s1;
COPY_B_A 15             ; s0 = A;
RS5 15                  ; s0 >>= 5;
C_COPY_A_B 14           ; if (s1) A = s0;
SWAP 13
SUB_C 12                ; s1 = (i is SUB);
SWAP 13
OR_AB_FB 13             ; d |= s1;
COPY_B_A 15             ; s0 = A;
SWAP 15
SUB_AB_FB 16            ; s0 -= B;
SWAP 15
C_COPY_A_B 14           ; if (s1) A = s0;
SWAP 13
XOR_C 12                ; s1 = (i is XOR);
SWAP 13
OR_AB_FB 13             ; d |= s1;
COPY_B_A 15             ; s0 = A;
SWAP 15
XOR_AB_FB 16            ; s0 ^= B;
SWAP 15
C_COPY_A_B 14           ; if (s1) A = s0;
SWAP 13
CLEAR 15                ; s0 = 0; // required by C_MINUS
C_MINUS 15              ; s0 = (A < 0);
SWAP 19
SWAP 18
SWAP 17
SWAP 16
C_COPY_A_B 14           ; if (d) n = s0;
SWAP 16
SWAP 17
SWAP 18
SWAP 19
C_ZERO 15               ; s0 = (A == 0);
SWAP 20
SWAP 19
SWAP 18
SWAP 17
SWAP 16
C_COPY_A_B 14           ; if (d) z = s0;
SWAP 16
SWAP 17
SWAP 18
SWAP 19
SWAP 20
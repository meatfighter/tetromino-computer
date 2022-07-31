INC_16 13
SWAP 23
SWAP 22
SWAP 21
SWAP 20
SWAP 19
SWAP 18
SWAP 17
SWAP 16
SWAP 15
SWAP 14
SWAP 13
SWAP 12
SWAP 11
SWAP 10
SWAP 9
SWAP 8
SWAP 7
SWAP 6
SWAP 5
SWAP 4
SWAP 3
DEC_16 13
SWAP 2
SWAP 3
SWAP 4
SWAP 5
SWAP 6
SWAP 7
SWAP 8
SWAP 9
SWAP 10
SWAP 11
SWAP 12
SWAP 13
SWAP 14
SWAP 15
SWAP 16
SWAP 17
SWAP 18
SWAP 19
SWAP 20
SWAP 21
SWAP 22
SWAP 12
SWAP 11
SWAP 10
SWAP 11
CMP_C 9                  ; s1 = (P1 == a1);
SWAP 11
SWAP 10
SWAP 11
SWAP 12                  ; order restored
SWAP 13
SWAP 12
SWAP 11
CMP_AND_C 10             ; s1 = (P1 == a1) & (P0 == a0);
SWAP 11
SWAP 12
SWAP 13                  ; order restored
SWAP 10
SWAP 9
SWAP 8
SWAP 7
SWAP 6
SWAP 5
SWAP 4
SWAP 0
SWAP 1
COPY_A_B_C 2             ; if (s1) i = I;
SWAP 1
SWAP 0
SWAP 4
SWAP 1
SWAP 2
COPY_A_B_C 3             ; if (s1) j = J;
SWAP 2
SWAP 1
SWAP 2
SWAP 3
SWAP 5
COPY_A_B_C 4             ; if (s1) k = K;
SWAP 3
SWAP 2
SWAP 6
SWAP 7
SWAP 8
SWAP 9
SWAP 10                  ; order restored
SWAP 14
SWAP 12
SWAP 13
CMP_C 12                 ; s0 = (M == a1);
SWAP 14
SWAP 15
CMP_AND_C 14             ; s0 = (M == a1) & (N == a0);
SWAP 15
SWAP 14
SWAP 13
SWAP 12
SWAP 14                  ; order restored
SWAP 0
SWAP 1
SWAP 2
SWAP 3
SWAP 4
COPY_B_A 11              ; s1 = s0;
SWAP 10
SWAP 9
AND_AB_AF 8              ; s1 &= r;
SWAP 8
SWAP 7
COPY_A_B_C 5             ; if (s1) m = I;
SWAP 7
SWAP 8
SWAP 9
SWAP 10
COPY_B_A 11              ; s1 = s0;
SWAP 10
SWAP 9
SWAP 8
AND_AB_AF 7              ; s1 &= w;
SWAP 7
COPY_B_A_C 5             ; if (s1) I = m;
SWAP 7
SWAP 8
SWAP 9
SWAP 10
SWAP 4
SWAP 3
SWAP 2
SWAP 1
SWAP 0                   ; order restored
SWAP 6
SWAP 7
SWAP 8
SWAP 9
SWAP 10
MINUS_C 11               ; s0 = (m < 0);
SWAP 12
SWAP 13
SWAP 14
SWAP 15
SWAP 16
SWAP 7
SWAP 8
SWAP 9
SWAP 10
SWAP 11
SWAP 12
SWAP 13
SWAP 14
SWAP 15
C_COPY_A_B 16            ; if (r) n = s0;
SWAP 10
SWAP 11
SWAP 12
SWAP 13
SWAP 14
SWAP 15
ZERO_C 16                ; s0 = (m == 0);
SWAP 17
SWAP 15
SWAP 16
C_COPY_A_B 17            ; if (r) z = s0;
SWAP 17
SWAP 14
SWAP 15
SWAP 16
SWAP 17
C_AND_A_NOT_B 16         ; s0 = r & !d;
SWAP 14
SWAP 15
SWAP 16
SWAP 17
SWAP 18
SWAP 15
SWAP 16
SWAP 17
C_COPY_A_B 18            ; if (s0) A = m;
SWAP 17
AND_A_B_C 15             ; s0 = r & d;
SWAP 19
SWAP 17
SWAP 18
C_COPY_A_B 19            ; if (s0) B = m;
SWAP 19
SWAP 18
SWAP 17
SWAP 16
SWAP 15
SWAP 14
SWAP 13
SWAP 12
SWAP 11
SWAP 10
SWAP 9
SWAP 8
SWAP 7
SWAP 6
SWAP 15
SWAP 14
SWAP 13
SWAP 12
SWAP 11
SWAP 10
SWAP 9
SWAP 8
SWAP 19
SWAP 18
SWAP 17
SWAP 16
SWAP 15
SWAP 14
SWAP 13
SWAP 12
SWAP 17                  ; order restored
SWAP 12
SWAP 13
SWAP 14
SWAP 11
SWAP 12
SWAP 3
SWAP 4
SWAP 5
SWAP 6
SWAP 7
SWAP 8
SWAP 9
SWAP 10
SWAP 11
INC_16 8                 ; ++P;
SEX_C 12                 ; s1 = (i == SEx);
SWAP 12
SWAP 11
SWAP 10
INC_16_C 8               ; if (s1) ++P;
SWAP 10
SWAP 11
SWAP 12
THREE_C 12               ; s1 = (i == [3 byte instruction]);
SWAP 12
SWAP 11
SWAP 10
INC_16_C 8               ; if (s1) ++P;
INC_16_C 8               ; if (s1) ++P;
SWAP 10
SWAP 11
SWAP 12
TMX_C 12                 ; s1 = (i == TMx);
C_COPY_A_B 13            ; if (s1) s0 = M;
SWAP 15
SWAP 13
SWAP 12
TNX_C 13                 ; s1 = (i == TNx);
C_COPY_A_B 14            ; if (s1) s0 = N;
SWAP 16
SWAP 17
SWAP 18
SWAP 19
SWAP 14
SWAP 15
SWAP 16
SWAP 17
SWAP 13
SWAP 14
SWAP 15
SWAP 16
TAX_C 17                 ; s1 = (i == TAx);
C_COPY_A_B 18            ; if (s1) s0 = A;
SWAP 20
SWAP 18
SWAP 17
TBX_C 18                 ; s1 = (i == TBx);
C_COPY_A_B 19            ; if (s1) s0 = B;
TXB_C 18                 ; s1 = (i == TxB);
C_COPY_B_A 19            ; if (s1) B = s0;
SWAP 17
SWAP 18
SWAP 20
TXA_C 17                 ; s1 = (i == TxA);
C_COPY_B_A 18            ; if (s1) A = s0;        
SWAP 16
SWAP 15
SWAP 14
SWAP 13
SWAP 17
SWAP 16
SWAP 15
SWAP 14
SWAP 19
SWAP 18
SWAP 17
SWAP 16
TXN_C 13                 ; s1 = (i == TxN);
C_COPY_B_A 14            ; if (s1) N = s0;
SWAP 12
SWAP 13
SWAP 15
TXM_C 12                 ; s1 = (i == TxM);
C_COPY_B_A 13            ; if (s1) M = s0;        
SWAP 14
SWAP 16
SWAP 15
SWAP 14
SWAP 19
SWAP 18
SWAP 17
SWAP 16
SWAP 20
SWAP 19                  ; 0  0  0  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  2  2  2  2   2
SWAP 18                  ; 0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5  6  7  8  9  0  1  2  3   4
SWAP 17                  ; I  J  K [j  k  m  w  r  P1 P0 a1 a0 i  s1 d  s0 A  B  M  N  n  z  R1 R0] Q
CLEAR 14                 ; d = 0;      
ADD_C 12                 ; s1 = (i == ADD);
SWAP 13
OR_AB_FB 13              ; d |= s1;
COPY_B_A 15              ; s0 = A;
SWAP 15
ADD_AB_FB 16             ; s0 += B;
SWAP 15
C_COPY_A_B 14            ; if (s1) A = s0;
SWAP 13
AND_C 12                 ; s1 = (i == AND);
SWAP 13
OR_AB_FB 13              ; d |= s1;
COPY_B_A 15              ; s0 = A;
SWAP 15
AND_AB_FB 16             ; s0 &= B;
SWAP 15
C_COPY_A_B 14            ; if (s1) A = s0;
SWAP 13
DEC_C 12                 ; s1 = (i == DEC);
SWAP 13
OR_AB_FB 13              ; d |= s1;
COPY_B_A 15              ; s0 = A;
DEC 15                   ; --s0;
C_COPY_A_B 14            ; if (s1) A = s0;
SWAP 13
INC_C 12                 ; s1 = (i == INC);
SWAP 13
OR_AB_FB 13              ; d |= s1;
COPY_B_A 15              ; s0 = A;
INC 15                   ; ++s0;
C_COPY_A_B 14            ; if (s1) A = s0;
SWAP 13
LS2_C 12                 ; s1 = (i == LS2);
SWAP 13
OR_AB_FB 13              ; d |= s1;
COPY_B_A 15              ; s0 = A;
LS2 15                   ; s0 <<= 2;
C_COPY_A_B 14            ; if (s1) A = s0;
SWAP 13
LS3_C 12                 ; s1 = (i == LS3);
SWAP 13
OR_AB_FB 13              ; d |= s1;
COPY_B_A 15              ; s0 = A;
LS3 15                   ; s0 <<= 3;
C_COPY_A_B 14            ; if (s1) A = s0;
SWAP 13
LS4_C 12                 ; s1 = (i == LS4);
SWAP 13
OR_AB_FB 13              ; d |= s1;
COPY_B_A 15              ; s0 = A;
LS4 15                   ; s0 <<= 4;
C_COPY_A_B 14            ; if (s1) A = s0;
SWAP 13
OR_C 12                  ; s1 = (i == OR);
SWAP 13
OR_AB_FB 13              ; d |= s1;
COPY_B_A 15              ; s0 = A;
SWAP 15
OR_AB_FB 16              ; s0 |= B;
SWAP 15
C_COPY_A_B 14            ; if (s1) A = s0;
SWAP 13
RS1_C 12                 ; s1 = (i == RS1);
SWAP 13
OR_AB_FB 13              ; d |= s1;
COPY_B_A 15              ; s0 = A;
RS1 15                   ; s0 >>= 1;
C_COPY_A_B 14            ; if (s1) A = s0;
SWAP 13
RS5_C 12                 ; s1 = (i == RS5);
SWAP 13
OR_AB_FB 13              ; d |= s1;
COPY_B_A 15              ; s0 = A;
RS5 15                   ; s0 >>= 5;
C_COPY_A_B 14            ; if (s1) A = s0;
SWAP 13
SUB_C 12                 ; s1 = (i == SUB);
SWAP 13
OR_AB_FB 13              ; d |= s1;
COPY_B_A 15              ; s0 = A;
SWAP 15
SUB_AB_FB 16             ; s0 -= B;
SWAP 15
C_COPY_A_B 14            ; if (s1) A = s0;
SWAP 13
XOR_C 12                 ; s1 = (i == XOR);
SWAP 13
OR_AB_FB 13              ; d |= s1;
COPY_B_A 15              ; s0 = A;
SWAP 15
XOR_AB_FB 16             ; s0 ^= B;
SWAP 15
C_COPY_A_B 14            ; if (s1) A = s0;
SWAP 13
CLEAR 15                 ; so = 0; since it is not a flag
C_MINUS 15               ; so = (A < 0);
SWAP 19
SWAP 18
SWAP 17
SWAP 16
C_COPY_A_B 14            ; if (d) n = s0;
SWAP 16
SWAP 17
SWAP 18
SWAP 19
C_ZERO 15                ; so = (A == 0);
SWAP 20
SWAP 19
SWAP 18
SWAP 17
SWAP 16
C_COPY_A_B 14            ; if (d) z = s0;
SWAP 16
SWAP 17
SWAP 18
SWAP 19
SWAP 20
SEA_C 12                 ; s1 = (i == SEA);
SWAP 15
SWAP 14
SWAP 3
SWAP 4
SWAP 5
SWAP 6
SWAP 7
SWAP 8
SWAP 9
SWAP 10
SWAP 11
SWAP 12
C_COPY_A_B 12            ; if (s1) A = j;
SWAP 16
SWAP 15
SWAP 14
SEB_C 11                 ; s1 = (i == SEB);
C_COPY_A_B 12            ; if (s1) B = j;
SWAP 17
SWAP 16
SWAP 15
SWAP 14
SMN_C 11                 ; s1 = (i == SMN);
C_COPY_A_B 12            ; if (s1) M = j;
SWAP 3
SWAP 4
SWAP 5
SWAP 6
SWAP 7
SWAP 8
SWAP 9
SWAP 10
SWAP 11
SWAP 18
SWAP 17
SWAP 16
SWAP 15
SWAP 14
SWAP 13
C_COPY_A_B 11            ; if (s1) N = k;        
SWAP 7
SWAP 8
SWAP 9
SWAP 10
SWAP 11
JMP_C 9                  ; s1 = (i == JMP);
C_COPY_A_B 10            ; if (s1) P0 = k;
SWAP 13
SWAP 12
SWAP 11
SWAP 6
SWAP 7
SWAP 8
SWAP 9
SWAP 10
C_COPY_A_B 9             ; if (s1) P1 = j;
SWAP 20
SWAP 19
SWAP 18
SWAP 17
SWAP 16
SWAP 15
SWAP 14
SWAP 13
SWAP 12
SWAP 11
SWAP 10
BEQ_C 8                  ; s1 = (i == BEQ);
AND_AB_FB 9              ; s1 &= z;
SWAP 10
SWAP 11
C_COPY_A_B 9             ; if (s1) P1 = j;
SWAP 9
SWAP 10
SWAP 11
C_COPY_A_B 12            ; if (s1) P0 = k;
SWAP 11
SWAP 10
SWAP 9
SWAP 11
SWAP 10
BNE_C 8                  ; s1 = (i == BNE);
AND_NOT_AB_FB 9          ; s1 &= !z;
SWAP 10
SWAP 11
C_COPY_A_B 9             ; if (s1) P1 = j;
SWAP 9
SWAP 10
SWAP 11
C_COPY_A_B 12            ; if (s1) P0 = k;
SWAP 11
SWAP 10
SWAP 9
SWAP 12
SWAP 13
SWAP 14
SWAP 15
SWAP 16
SWAP 17
SWAP 18
SWAP 19
SWAP 20
SWAP 19
SWAP 18
SWAP 17
SWAP 16
SWAP 15
SWAP 14
SWAP 13
SWAP 12
SWAP 11
SWAP 10
BMI_C 8                  ; s1 = (i == BMI);
AND_AB_FB 9              ; s1 &= n;
SWAP 10
SWAP 11
C_COPY_A_B 9             ; if (s1) P1 = j;
SWAP 9
SWAP 10
SWAP 11
C_COPY_A_B 12            ; if (s1) P0 = k;
SWAP 11
SWAP 10
SWAP 9
SWAP 11
SWAP 10
BPL_C 8                  ; s1 = (i == BPL);
AND_NOT_AB_FB 9          ; s1 &= !n;
SWAP 10
SWAP 11
C_COPY_A_B 9             ; if (s1) P1 = j;
SWAP 9
SWAP 10
SWAP 11
C_COPY_A_B 12            ; if (s1) P0 = k;
SWAP 11
SWAP 10
SWAP 9
SWAP 12
SWAP 13
SWAP 14
SWAP 15
SWAP 16
SWAP 17
SWAP 18
SWAP 19
SWAP 21
SWAP 20
SWAP 19
SWAP 18
SWAP 17
SWAP 16
SWAP 15
SWAP 14
SWAP 13
SWAP 12
SWAP 22
SWAP 21
SWAP 20
SWAP 19
SWAP 18
SWAP 17
SWAP 16
SWAP 15
JSR_C 8                  ; s1 = (i == JSR);       
SWAP 9
SWAP 10
SWAP 11
SWAP 12
C_COPY_A_B 13            ; if (s1) R0 = P0;
SWAP 12
C_COPY_A_B 12            ; if (s1) P0 = k;
SWAP 11
SWAP 10
C_COPY_A_B 10            ; if (s1) R1 = P1;
SWAP 9
C_COPY_A_B 9             ; if (s1) P1 = j;
RTS_C 8                  ; s1 = (i == RTS); 
SWAP 9
C_COPY_B_A 10            ; if (s1) P1 = R1;
SWAP 10
SWAP 11
SWAP 12
C_COPY_B_A 13            ; if (s1) P0 = R0;
SWAP 8
SWAP 7
SWAP 6
SWAP 5
SWAP 4
SWAP 3
SWAP 11
SWAP 10
SWAP 9
SWAP 8
SWAP 7
SWAP 6
SWAP 5
SWAP 4
SWAP 12
SWAP 13
SWAP 14
SWAP 15
SWAP 16
SWAP 17
SWAP 18
SWAP 19
SWAP 20
SWAP 21
SWAP 22
SWAP 14
SWAP 15
SWAP 16
SWAP 17
SWAP 18
SWAP 19
SWAP 20
SWAP 21
SWAP 22
SWAP 10
SWAP 9
SWAP 8
SWAP 12
SWAP 11
SWAP 10
SWAP 9
SWAP 14
SWAP 15
SWAP 14
SWAP 5
SWAP 6
SWAP 7
SWAP 8
SWAP 9
SWAP 10
SWAP 11
SWAP 12
STB_C 11                 ; s1 = (i == STB);
C_COPY_B_A 12            ; if (s1) m = B;
SWAP 14
SWAP 15
SWAP 16
SWAP 17
SWAP 18
SWAP 19
SWAP 20
SWAP 15
SWAP 14
STA_C 11                 ; s1 = (i == STA);
C_COPY_B_A 12            ; if (s1) m = A;
SWAP 14
SWAP 15
SWAP 16
SWAP 17
SWAP 18
SWAP 19
SWAP 15
SWAP 14
SWAP 13
SWAP 12
SWAP 13
SWAP 12
SWAP 11
SWAP 10
SWAP 9
SWAP 8
SWAP 7
SWAP 6
SWAP 5
LDB_C 12                 ; d = (i == STA);
SWAP 13
SWAP 14
SWAP 15
SWAP 16
SWAP 15
SWAP 14
SWAP 11
SWAP 10
SWAP 9
SWAP 8
SWAP 7
LDX_C 7                  ; r = (i == LDx);
SWAP 6
STX_C 6                  ; w = (i == STx);
SWAP 5
SWAP 4
SWAP 3
SWAP 12
SWAP 11
SWAP 13
SWAP 12                  ; order restored
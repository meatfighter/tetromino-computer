TMX_C 12                ; s1 = (i matches TM*);
C_COPY_A_B 13           ; if (s1) s0 = M;
SWAP 15
SWAP 13
SWAP 12
TNX_C 13                ; s1 = (i matches TN*);
C_COPY_A_B 14           ; if (s1) s0 = N;
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
TAX_C 17                ; s1 = (i matches TA*);
C_COPY_A_B 18           ; if (s1) s0 = A;
SWAP 20
SWAP 18
SWAP 17
TBX_C 18                ; s1 = (i matches TB*);
C_COPY_A_B 19           ; if (s1) s0 = B;
TXB_C 18                ; s1 = (i matches T*B);
C_COPY_B_A 19           ; if (s1) B = s0;
SWAP 17
SWAP 18
SWAP 20
TXA_C 17                ; s1 = (i matches T*A);
C_COPY_B_A 18           ; if (s1) A = s0;        
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
TXN_C 13                ; s1 = (i matches T*N);
C_COPY_B_A 14           ; if (s1) N = s0;
SWAP 12
SWAP 13
SWAP 15
TXM_C 12                ; s1 = (i matches T*M);
C_COPY_B_A 13           ; if (s1) M = s0;
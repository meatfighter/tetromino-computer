SEA_C 12                ; s1 = (i is SEA);
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
C_COPY_A_B 12           ; if (s1) A = j;
SWAP 16
SWAP 15
SWAP 14
SEB_C 11                ; s1 = (i is SEB);
C_COPY_A_B 12           ; if (s1) B = j;
SWAP 17
SWAP 16
SWAP 15
SWAP 14
SMN_C 11                ; s1 = (i is SMN);
C_COPY_A_B 12           ; if (s1) M = j;
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
C_COPY_A_B 11           ; if (s1) N = k;
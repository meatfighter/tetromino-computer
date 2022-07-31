SWAP 14
SWAP 12
SWAP 13
CMP_C 12                ; s0 = (M == a1);
SWAP 14
SWAP 15
CMP_AND_C 14            ; s0 &= (N == a0);
SWAP 15
SWAP 14
SWAP 13
SWAP 12
SWAP 14
SWAP 0
SWAP 1
SWAP 2
SWAP 3
SWAP 4
COPY_B_A 11             ; s1 = s0;
SWAP 10
SWAP 9
AND_AB_AF 8             ; s1 &= r;
SWAP 8
SWAP 7
COPY_A_B_C 5            ; if (s1) m = I;
SWAP 7
SWAP 8
SWAP 9
SWAP 10
COPY_B_A 11             ; s1 = s0;
SWAP 10
SWAP 9
SWAP 8
AND_AB_AF 7             ; s1 &= w;
SWAP 7
COPY_B_A_C 5            ; if (s1) I = m;
SWAP 7
SWAP 8
SWAP 9
SWAP 10
SWAP 4
SWAP 3
SWAP 2
SWAP 1
SWAP 0
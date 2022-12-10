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
INC_16 8                ; ++P;
SEX_C 12                ; s1 = (i is a 2-byte instruction);
SWAP 12
SWAP 11
SWAP 10
INC_16_C 8              ; if (s1) ++P;
SWAP 10
SWAP 11
SWAP 12
THREE_C 12              ; s1 = (i is a 3-byte instruction);
SWAP 12
SWAP 11
SWAP 10
INC_16_C 8              ; if (s1) ++P;
INC_16_C 8              ; if (s1) ++P;
SWAP 10
SWAP 11
SWAP 12
seedHigh: 89
seedLow:  88
bit: 00

main:


random:            
SEB 02
SMN seedLow
LDA
AND
SMN bit
STA
SMN seedHigh
LDA
AND
SMN bit
LDB
XOR
LSH
LSH
LSH
LSH
LSH
LSH
STA                ; bit = ((seedHigh & 0x02) ^ (seedLow & 0x02)) << 6;

SMN seedHigh
LDA
LSH
LSH
LSH
LSH
LSH
LSH
LSH
TAB
SMN seedLow
LDA
RSH
OR
STA                ; seedLow = (seedHigh << 7) | (seedLow >> 1);

SMN bit
LDB
SMN seedHigh
LDA
RSH
OR
STA                ; seedHigh = bit | (seedHigh >> 1);




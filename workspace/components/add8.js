a = (a7 ? 0x0080 : 0) | (a6 ? 0x0040 : 0) | (a5 ? 0x0020 : 0) | (a4 ? 0x0010 : 0) | (a3 ? 0x0008 : 0) | (a2 ? 0x0004 : 0) | (a1 ? 0x0002 : 0) | (a0 ? 0x0001 : 0);
b = (b7 ? 0x0080 : 0) | (b6 ? 0x0040 : 0) | (b5 ? 0x0020 : 0) | (b4 ? 0x0010 : 0) | (b3 ? 0x0008 : 0) | (b2 ? 0x0004 : 0) | (b1 ? 0x0002 : 0) | (b0 ? 0x0001 : 0);
s = a + b + (carryIn ? 1 : 0);
carryOut = (s & 0x0100) != 0;
s7 = (s & 0x0080) != 0;
s6 = (s & 0x0040) != 0;
s5 = (s & 0x0020) != 0;
s4 = (s & 0x0010) != 0;
s3 = (s & 0x0008) != 0;
s2 = (s & 0x0004) != 0;
s1 = (s & 0x0002) != 0;
s0 = (s & 0x0001) != 0;
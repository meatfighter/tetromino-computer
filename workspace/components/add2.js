a = (a1 ? 0x0002 : 0) | (a0 ? 0x0001 : 0);
b = (b1 ? 0x0002 : 0) | (b0 ? 0x0001 : 0);
s = a + b + (carryIn ? 1 : 0);
carryOut = (s & 0x0004) != 0;
s1 = (s & 0x0002) != 0;
s0 = (s & 0x0001) != 0;
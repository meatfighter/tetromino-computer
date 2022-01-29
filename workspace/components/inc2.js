s = enabled ? 1 : 0;
s += a1 ? 2 : 0;
s += a0 ? 1 : 0;
carryOut = (s & 0x0004) != 0;
s1 = (s & 0x0002) != 0;
s0 = (s & 0x0001) != 0;
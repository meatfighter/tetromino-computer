s = enabled ? 1 : 0;
s += a ? 1 : 0;
carryOut = (s & 0x0002) != 0;
sum = (s & 0x0001) != 0;
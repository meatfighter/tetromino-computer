a = enabled ? -1 : 0;
a += a3 ? 0x0008 : 0;
a += a2 ? 0x0004 : 0;
a += a1 ? 0x0002 : 0;
a += a0 ? 0x0001 : 0;

borrow = (a & 0x0010) != 0;
d3 = (a & 0x0008) != 0;
d2 = (a & 0x0004) != 0;
d1 = (a & 0x0002) != 0;
d0 = (a & 0x0001) != 0;
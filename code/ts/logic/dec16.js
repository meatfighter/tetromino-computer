a = enabled ? -1 : 0;
a += a15 ? 0x8000 : 0;
a += a14 ? 0x4000 : 0;
a += a13 ? 0x2000 : 0;
a += a12 ? 0x1000 : 0;
a += a11 ? 0x0800 : 0;
a += a10 ? 0x0400 : 0;
a += a9 ? 0x0200 : 0;
a += a8 ? 0x0100 : 0;
a += a7 ? 0x0080 : 0;
a += a6 ? 0x0040 : 0;
a += a5 ? 0x0020 : 0;
a += a4 ? 0x0010 : 0;
a += a3 ? 0x0008 : 0;
a += a2 ? 0x0004 : 0;
a += a1 ? 0x0002 : 0;
a += a0 ? 0x0001 : 0;

borrow = (a & 0x10000) != 0;
d15 = (a & 0x8000) != 0;
d14 = (a & 0x4000) != 0;
d13 = (a & 0x2000) != 0;
d12 = (a & 0x1000) != 0;
d11 = (a & 0x0800) != 0;
d10 = (a & 0x0400) != 0;
d9 = (a & 0x0200) != 0;
d8 = (a & 0x0100) != 0;
d7 = (a & 0x0080) != 0;
d6 = (a & 0x0040) != 0;
d5 = (a & 0x0020) != 0;
d4 = (a & 0x0010) != 0;
d3 = (a & 0x0008) != 0;
d2 = (a & 0x0004) != 0;
d1 = (a & 0x0002) != 0;
d0 = (a & 0x0001) != 0;
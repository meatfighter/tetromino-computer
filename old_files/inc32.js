s = 0;
s += a31 ? 0x80000000 : 0;
s += a30 ? 0x40000000 : 0;
s += a29 ? 0x20000000 : 0;
s += a28 ? 0x10000000 : 0;
s += a27 ? 0x08000000 : 0;
s += a26 ? 0x04000000 : 0;
s += a25 ? 0x02000000 : 0;
s += a24 ? 0x01000000 : 0;
s += a23 ? 0x00800000 : 0;
s += a22 ? 0x00400000 : 0;
s += a21 ? 0x00200000 : 0;
s += a20 ? 0x00100000 : 0;
s += a19 ? 0x00080000 : 0;
s += a18 ? 0x00040000 : 0;
s += a17 ? 0x00020000 : 0;
s += a16 ? 0x00010000 : 0;
s += a15 ? 0x00008000 : 0;
s += a14 ? 0x00004000 : 0;
s += a13 ? 0x00002000 : 0;
s += a12 ? 0x00001000 : 0;
s += a11 ? 0x00000800 : 0;
s += a10 ? 0x00000400 : 0;
s += a9 ? 0x00000200 : 0;
s += a8 ? 0x00000100 : 0;
s += a7 ? 0x00000080 : 0;
s += a6 ? 0x00000040 : 0;
s += a5 ? 0x00000020 : 0;
s += a4 ? 0x00000010 : 0;
s += a3 ? 0x00000008 : 0;
s += a2 ? 0x00000004 : 0;
s += a1 ? 0x00000002 : 0;
s += a0 ? 0x00000001 : 0;

carryOut = enabled && (s == 0xFFFFFFFF);
s += enabled ? 1 : 0;

s31 = (s & 0x80000000) != 0;
s30 = (s & 0x40000000) != 0;
s29 = (s & 0x20000000) != 0;
s28 = (s & 0x10000000) != 0;
s27 = (s & 0x08000000) != 0;
s26 = (s & 0x04000000) != 0;
s25 = (s & 0x02000000) != 0;
s24 = (s & 0x01000000) != 0;
s23 = (s & 0x00800000) != 0;
s22 = (s & 0x00400000) != 0;
s21 = (s & 0x00200000) != 0;
s20 = (s & 0x00100000) != 0;
s19 = (s & 0x00080000) != 0;
s18 = (s & 0x00040000) != 0;
s17 = (s & 0x00020000) != 0;
s16 = (s & 0x00010000) != 0;
s15 = (s & 0x00008000) != 0;
s14 = (s & 0x00004000) != 0;
s13 = (s & 0x00002000) != 0;
s12 = (s & 0x00001000) != 0;
s11 = (s & 0x00000800) != 0;
s10 = (s & 0x00000400) != 0;
s9 = (s & 0x00000200) != 0;
s8 = (s & 0x00000100) != 0;
s7 = (s & 0x00000080) != 0;
s6 = (s & 0x00000040) != 0;
s5 = (s & 0x00000020) != 0;
s4 = (s & 0x00000010) != 0;
s3 = (s & 0x00000008) != 0;
s2 = (s & 0x00000004) != 0;
s1 = (s & 0x00000002) != 0;
s0 = (s & 0x00000001) != 0;
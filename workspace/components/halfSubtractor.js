r = y ? -1 : 0; 
r += x ? 0x0001 : 0;

b = (r & 0x0002) != 0;
d = (r & 0x0001) != 0;
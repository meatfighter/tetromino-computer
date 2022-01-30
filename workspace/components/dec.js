r = enabled ? -1 : 0; 
r += a ? 0x0001 : 0;

borrow = (r & 0x0002) != 0;
difference = (r & 0x0001) != 0;
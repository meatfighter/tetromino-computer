sum = (ai ? 1 : 0) + (bi ? 1 : 0) + (ci ? 1 : 0);
co = ((sum >> 1) & 1) != 0;
ao = ai;
s = (sum & 1) != 0;
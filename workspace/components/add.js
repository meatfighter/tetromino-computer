aXorB = (a != b);
sum = (carryIn != aXorB);
carryOut = (a && b) || (carryIn && aXorB);
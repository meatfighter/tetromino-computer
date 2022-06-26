SEA 09                  ; A = 9;

label:

DEC                     ; --A;

                        ; if (A == 0) {
BEQ done                ;   goto done;
                        ; }

JMP label               ; goto label;

done:
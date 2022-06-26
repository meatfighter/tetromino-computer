SEA 09                  ; A = 9;

label:

DEC                     ; --A;

                        ; if (A != 0) {
BNE label               ;   goto label;
                        ; }
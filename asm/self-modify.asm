SEA FF                  ; A = 0xFF;
SMN target+1            ; MN = target + 1;
STA                     ; *MN = A;

target:
SEB 00                  ; B = 0x00; // unless replaced with 0xFF
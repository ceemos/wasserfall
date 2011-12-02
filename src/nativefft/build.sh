#!/bin/bash
gcc -c -fPIC -std=gnu99 -I /opt/java/include/ -I /opt/java/include/linux/ nativefft.c -o ../../nativefft.o
cd ../..
gcc -shared -Wl,-soname,libnativefft.so,-lfftw3,-lm -o libnativefft.so  nativefft.o
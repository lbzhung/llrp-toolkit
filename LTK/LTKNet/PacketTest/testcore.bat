
copy ..\..\Tests\dx101\dx101_a.bin refa.bin
..\LLRP2XML\bin\Debug\llrp2xml.exe refa.bin > tmpa.xml
..\XML2LLRP\bin\Debug\xml2llrp.exe tmpa.xml > tmpa.bin
fc /b tmpa.bin refa.bin


copy ..\..\Tests\dx101\dx101_c.bin refc.bin
..\LLRP2XML\bin\Debug\llrp2xml.exe refc.bin > tmpc.xml
..\XML2LLRP\bin\Debug\xml2llrp.exe tmpc.xml > tmpc.bin
fc /b tmpc.bin refc.bin

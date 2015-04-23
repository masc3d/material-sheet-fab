@echo off

REM Create 32bit regfile
C:\Windows\Microsoft.NET\Framework\v4.0.30319\RegAsm.exe /verbose /regfile:LeoBridge-x86.reg LeoBridge.dll

REM Create 64bit regfile
C:\Windows\Microsoft.NET\Framework64\v4.0.30319\RegAsm.exe /verbose /regfile:LeoBridge-amd64.reg LeoBridge.dll

pause
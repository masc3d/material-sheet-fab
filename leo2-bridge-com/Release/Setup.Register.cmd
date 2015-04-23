@echo off

REM Register for 32bit consumers
C:\Windows\Microsoft.NET\Framework\v4.0.30319\RegAsm.exe /verbose /codebase /tlb:LeoBridge-x86.tlb LeoBridge.dll

REM Register for 64bit consumers
C:\Windows\Microsoft.NET\Framework64\v4.0.30319\RegAsm.exe /verbose /codebase /tlb:LeoBridge-amd64.tlb LeoBridge.dll
pause
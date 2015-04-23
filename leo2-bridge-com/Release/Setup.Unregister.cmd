@echo off

REM Unregister for 32bit consumers
C:\Windows\Microsoft.NET\Framework\v4.0.30319\RegAsm.exe /verbose /codebase /unregister /tlb:LeoBridge-x86.tlb LeoBridge.dll

REM Unregister for 64bit consumers
C:\Windows\Microsoft.NET\Framework64\v4.0.30319\RegAsm.exe /verbose /codebase /unregister /tlb:LeoBridge-amd64.tlb LeoBridge.dll

pause
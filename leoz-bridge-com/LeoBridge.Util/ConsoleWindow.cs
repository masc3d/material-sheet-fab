using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;

namespace LeoBridge.Util
{
    public class ConsoleWindow : IDisposable
    {
        [DllImport("kernel32.dll", SetLastError = true)]
        static extern bool AllocConsole();

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern bool FreeConsole();

        [DllImport("kernel32", SetLastError = true)]
        static extern bool AttachConsole(int dwProcessId);

        [DllImport("user32.dll")]
        static extern IntPtr GetForegroundWindow();

        [DllImport("user32.dll", SetLastError = true)]
        static extern uint GetWindowThreadProcessId(IntPtr hWnd, out int lpdwProcessId);

        public ConsoleWindow()
        {
            IntPtr ptr = GetForegroundWindow();

            int u;
            GetWindowThreadProcessId(ptr, out u);

            Process process = Process.GetProcessById(u);

            if (process.ProcessName == "cmd")    //Is the uppermost window a cmd process?
            {
                // Attach to existing console (running within cmd.exe)
                AttachConsole(process.Id);
            }
            else
            {                
                // Create new console
                AllocConsole();
            }
        }       

        public void Dispose()
        {
            FreeConsole();   
        }
    }
}

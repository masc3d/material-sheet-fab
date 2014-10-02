using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

using LeoBridge;
using System.Runtime.InteropServices;

namespace LeoBridge.TestWindow
{
    class Program
    {
        static LeoBridge _leoBridge;
        static FormMain _formMain;

        [DllImport("User32.dll")]
        public static extern Int32 SetForegroundWindow(int hWnd);  

        static void Main(string[] args)
        {
            using(_leoBridge = new LeoBridge())
            {
                _leoBridge.OnMessage += _leoBridge_OnMessage;                    
                _leoBridge.Start();

                _formMain = new FormMain();
                Application.Run(_formMain);
            }
        }

        static void _leoBridge_OnMessage(string message)
        {
            _formMain.uxMessage.Text = message;
            SetForegroundWindow(_formMain.Handle.ToInt32());
            //_formMain.Activate();
            //_formMain.BringToFront();
        }
    }
}

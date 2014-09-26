using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Runtime.InteropServices;
using System.ServiceModel;
using System.ServiceModel.Description;
using LeoBridge.Service;
using System.Windows.Forms;

namespace LeoBridge
{
    public static class ControlExtensions
    {
        /// <summary>
        /// Generic extension method to support threadsafe windows forms access
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="control"></param>
        /// <param name="action"></param>
        public static void Invoke<T>(this T control, Action<T> action) where T : Control
        {
            if (control.InvokeRequired)
            {
                control.Invoke(new Action<T, Action<T>>(Invoke), new object[] { control, action });
            }
            else
            {
                action(control);
            }
        }

        /// <summary>
        /// Generic extension method to support threadsafe windows forms access
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="control"></param>
        /// <param name="action"></param>
        public static void BeginInvoke<T>(this T control, Action<T> action) where T : Control
        {
            if (control.InvokeRequired)
            {
                control.BeginInvoke(new Action<T, Action<T>>(BeginInvoke), new object[] { control, action });
            }
            else
            {
                action(control);
            }
        }
    }

    public delegate void OnMessageDelegate(String message);

    /// <summary>
    /// Interface for MessageService events
    /// </summary>
    /// <author>masc</author>
    [Guid("709C2294-A2E0-4CD0-9969-6F2CC1B71625")]
    [ComVisible(true)]
    [InterfaceType(ComInterfaceType.InterfaceIsIDispatch)]
    public interface ILeoBridgeEvents
    {
        [DispId(1)]
        void OnMessage(String message);        
    }

    /// <summary>
    /// Message service provider
    /// </summary>
    /// <author>masc</author>
    [Guid("EF6CD085-7175-4975-B1BB-2E7DE0A3774D")]
    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]    
    [ComSourceInterfaces(typeof(ILeoBridgeEvents))]
    public class LeoBridge : ILeoBridge
    {
        System.Windows.Forms.Form _dispatchWindow;

        /// <summary>
        /// Message event
        /// </summary>
        public event OnMessageDelegate OnMessage;

        public LeoBridge()
        {
            _dispatchWindow = new Form();
            _dispatchWindow.Name = "LeoBridge.Dispatch";
            _dispatchWindow.AutoScaleMode = AutoScaleMode.None;
            _dispatchWindow.WindowState = FormWindowState.Minimized;
            _dispatchWindow.FormBorderStyle = FormBorderStyle.None;
            _dispatchWindow.DesktopBounds = new System.Drawing.Rectangle(0, 0, 0, 0);            
            _dispatchWindow.Show();
            _dispatchWindow.Hide();            
        }

        /// <summary>
        /// Event test method
        /// </summary>
        /// <remarks>May be removed at some point</remarks>
        /// <param name="testMessage"></param>
        public void TestEvent(String testMessage)
        {
            if (OnMessage != null)
                OnMessage(testMessage);
        }

        /// <summary>
        /// Start services
        /// </summary>
        public void Start()
        {           
            MessageServiceHost.Instance.OnMessage += MessageServiceHost_OnMessage;
            MessageServiceHost.Instance.Open();            
        }

        public void Stop()
        {
            MessageServiceHost.Instance.Close();
            MessageServiceHost.Instance.OnMessage -= MessageServiceHost_OnMessage;
        }

        public void Dispose()
        {
            this.Stop();
        }

        void MessageServiceHost_OnMessage(string message)
        {
            _dispatchWindow.Invoke(f => {
                OnMessageDelegate d = this.OnMessage;
                if (d != null)
                    d(message);
            });
        }
    }
}

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
using System.Threading;

namespace LeoBridge
{
    public delegate void OnMessageDelegate(String message);

    /// <summary>
    /// Interface for LEO bridge (COM) events
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
    /// LEO bridge main (COM) component
    /// </summary>
    /// <author>masc</author>
    [Guid("EF6CD085-7175-4975-B1BB-2E7DE0A3774D")]
    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]    
    [ComSourceInterfaces(typeof(ILeoBridgeEvents))]
    public class LeoBridge : ILeoBridge
    {
        System.Windows.Forms.Form _dispatchWindow;
        ChannelFactory<IMessageService> _messageServiceChannelFactory;

        /// <summary>
        /// Message event
        /// </summary>
        public event OnMessageDelegate OnMessage;

        public LeoBridge()
        {
            // Create hidden dispatcher window for routing threaded ws callbacks back to COM/UI thread
            _dispatchWindow = new Form();
            _dispatchWindow.Name = "LeoBridge.Dispatch";
            _dispatchWindow.AutoScaleMode = AutoScaleMode.None;
            _dispatchWindow.WindowState = FormWindowState.Minimized;
            _dispatchWindow.FormBorderStyle = FormBorderStyle.None;
            _dispatchWindow.DesktopBounds = new System.Drawing.Rectangle(0, 0, 0, 0);            
            _dispatchWindow.Show();
            _dispatchWindow.Hide();
 
            // Create client message service channel factory
            WebHttpBinding myBinding = new WebHttpBinding();
            myBinding.OpenTimeout = TimeSpan.FromMilliseconds(250);
            myBinding.ReceiveTimeout = TimeSpan.FromMilliseconds(250);            
            EndpointAddress myEndpoint = new EndpointAddress("http://localhost:37420");            
            _messageServiceChannelFactory = new ChannelFactory<IMessageService>(myBinding, myEndpoint);
            _messageServiceChannelFactory.Endpoint.EndpointBehaviors.Add(new WebHttpBehavior());
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

        public void SendMessage(string message)
        {
            using (IClientChannel channel = (IClientChannel)_messageServiceChannelFactory.CreateChannel())
            {
                IMessageService s = (IMessageService)channel;
                s.SendMessage(message);
            }
        }
    }
}

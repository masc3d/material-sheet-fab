using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.ServiceModel;
using System.ServiceModel.Description;
using LeoBridge.Service;
using System.Windows.Forms;
using System.Threading;
using System.Runtime.Serialization;
using System.ComponentModel;

namespace LeoBridge
{
    /// <summary>
    /// Message service provider interface
    /// </summary>
    /// <author>masc</author>
    [ComVisible(true)]
    public interface IMessageQueue : IDisposable
    {
        /// <summary>
        /// Start listener
        /// </summary>
        void Start();
        /// <summary>
        /// Stop listener
        /// </summary>
        void Stop();
        ///// <summary>
        ///// Send message
        ///// </summary>
        ///// <param name="message"></param>
        void SendMessage(Message message);
        /// <summary>
        /// Send single value message
        /// </summary>
        /// <param name="message"></param>
        void SendValue(Object message);
    }

    /// <summary>
    /// Interface for LEO bridge (COM) events
    /// </summary>
    /// <author>masc</author>
    [ComVisible(true)]
    [InterfaceType(ComInterfaceType.InterfaceIsIDispatch)]
    public interface IMessageQueueEvents
    {
        [DispId(1)]
        void OnMessage(Message message);
    }

    public delegate void OnMessageDelegate(Message message);

    /// <summary>
    /// LEO bridge main (COM) component
    /// </summary>
    /// <author>masc</author>
    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [ComSourceInterfaces(typeof(IMessageQueueEvents))]
    public class MessageQueue : IMessageQueue
    {
        System.Windows.Forms.Form _dispatchWindow;
        ChannelFactory<IMessageService> _messageServiceChannelFactory;

        /// <summary>
        /// Message event
        /// </summary>
        public event OnMessageDelegate OnMessage;

        public MessageQueue()
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
            //_messageServiceChannelFactory.Endpoint.EndpointBehaviors.Add(new WebHttpBehavior());
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

        void MessageServiceHost_OnMessage(Message message)
        {
            ThreadPool.QueueUserWorkItem(s =>
            {
                _dispatchWindow.Invoke(f =>
                {
                    OnMessageDelegate d = this.OnMessage;
                    if (d != null)
                        d(message);
                });
            });
        }

        /// <summary>
        /// Send a single value to remote
        /// </summary>
        /// <param name="value"></param>
        public void SendValue(object value)
        {
            this.SendMessage(new Message(value)); ;
        }

        /// <summary>
        /// Send message to remote
        /// </summary>
        /// <param name="message"></param>
        public void SendMessage(Message message)
        {            
            using (IClientChannel channel = (IClientChannel)_messageServiceChannelFactory.CreateChannel())
            {
                IMessageService s = (IMessageService)channel;
                s.SendMessage((Message)message);
            }
        }
    }
}

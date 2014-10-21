using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.ServiceModel;
using System.ServiceModel.Description;
using LeoBridge.Service;
using System.Windows.Forms;
using System.Threading;
using System.Runtime.Serialization;

namespace LeoBridge
{
    public delegate void OnMessageDelegate(IMessage message);

    /// <summary>
    /// LeoBridge message
    /// </summary>
    [Guid("4EA174D1-6115-4002-AC5D-A02CBC49B1FE")]
    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [Serializable]
    public class Message : IMessage, ISerializable
    {
        private static string DEFAULT_KEY = "_";

        /// <summary>
        /// Message attributes
        /// </summary>
        public Dictionary<string, object> Attributes { get; private set; }

        public Message() 
        {
            this.Attributes = new Dictionary<string, object>();
        }

        public Message(object value)
            : this()
        {
            this.Attributes.Add(DEFAULT_KEY, value);
        }

        public Message(Dictionary<String, Object> attributes)
        {
            this.Attributes = attributes;     
        }

        public object Get(string key)
        {
            return this.Attributes[key];
        }

        public object GetValue()
        {
            return this.Attributes[DEFAULT_KEY];
        }

        public void Put(string key, object value)
        {
            this.Attributes.Add(key, value);            
        }

        #region WCF data contract de/serialization
        /// <summary>
        /// c'tor for data contract/WCF deserialization
        /// </summary>
        /// <param name="info"></param>
        /// <param name="context"></param>
        protected Message(SerializationInfo info, StreamingContext context)
            : this()
        {
            foreach (var entry in info)
            {
                if (entry.Value is string)
                {
                    DateTime dt;
                    if (DateTime.TryParse((String)entry.Value, out dt))
                    {
                        this.Attributes.Add(entry.Name, dt);
                        continue;
                    }
                }

                this.Attributes.Add(entry.Name, entry.Value);
            }
        }

        void ISerializable.GetObjectData(SerializationInfo info, StreamingContext context)
        {
            foreach (String key in this.Attributes.Keys)
            {
                object value = this.Attributes[key];
                if (value is DateTime)
                {                    
                    info.AddValue(key.ToString(), ((DateTime)value).ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ssZ"));
                }
                else
                {
                    info.AddValue(key.ToString(), this.Attributes[key]);
                }
            }            
        }
        #endregion

        public override string ToString()
        {
            string s = "";
            foreach (KeyValuePair<string, object> kvp in this.Attributes)
            {
                if (s.Length > 0)
                    s += ", ";
                s += string.Format("{0}:{1}", kvp.Key, kvp.Value);
            }
            return s;
        }
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

        void MessageServiceHost_OnMessage(IMessage message)
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
        public void SendMessage(IMessage message)
        {            
            using (IClientChannel channel = (IClientChannel)_messageServiceChannelFactory.CreateChannel())
            {
                IMessageService s = (IMessageService)channel;
                s.SendMessage((Message)message);
            }
        }
    }
}

using System;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Description;
using System.ServiceModel.Web;

namespace LeoBridge
{
    [ServiceContract]
    interface IMessageService
    {
        [OperationContract]
        [WebInvoke(RequestFormat = WebMessageFormat.Json, ResponseFormat = WebMessageFormat.Json, UriTemplate = "/send", Method = "POST")]
        void SendMessage(Message message);
    }

    #region Entities
    /// <summary>
    /// LeoBridge message interface
    /// </summary>
    [ComVisible(true)]
    public interface IMessage
    {
        /// <summary>
        /// Get main message attribute
        /// </summary>
        /// <returns></returns>
        object GetValue();
        /// <summary>
        /// Get message attribute by key
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        object Get(string key);
        /// <summary>
        /// Add attribute to message
        /// </summary>
        /// <param name="key"></param>
        /// <param name="value"></param>
        void Put(string key, object value);
        string ToString();
    }

    /// <summary>
    /// LeoBridge message
    /// </summary>
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
            object value = null;
            this.Attributes.TryGetValue(key, out value);
            return value;
        }

        public object GetValue()
        {
            return this.Get(DEFAULT_KEY);
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
    #endregion

    #region Host
    /// <summary>
    /// Message service host
    /// Public methods are thread-safe.
    /// </summary>
    public class MessageServiceHost : ServiceHost
    {
        /// <summary>
        /// Message service host implementation
        /// </summary>
        class MessageService : IMessageService
        {
            public void SendMessage(Message message)
            {
                ((MessageServiceHost)OperationContext.Current.Host).OnMessageReceived(message);
            }
        }

        #region Singleton
        private static readonly MessageServiceHost _instance = new MessageServiceHost();

        /// <summary>
        /// Singleton accessor
        /// </summary>
        public static MessageServiceHost Instance
        {
            get
            {
                return _instance;
            }
        }
        #endregion

        /// <summary>
        /// Message event
        /// </summary>
        public event OnMessageDelegate OnMessage;

        public MessageServiceHost()
            : base(typeof(MessageService), new Uri("http://localhost:37421/"))
        {
            // Configure endpoints. JSON requires HTTP binding and behaviour
            ServiceEndpoint ep = this.AddServiceEndpoint(typeof(IMessageService), new WebHttpBinding(), "");
            //ep.EndpointBehaviors.Add(new WebHttpBehavior());

            // Enable metadata publishing
            ServiceMetadataBehavior smb = new ServiceMetadataBehavior();
            smb.HttpGetEnabled = true;
            smb.MetadataExporter.PolicyVersion = PolicyVersion.Policy15;
            this.Description.Behaviors.Add(smb);
        }

        /// <summary>
        /// Callback for message service implementation
        /// </summary>
        /// <param name="message"></param>
        void OnMessageReceived(Message message)
        {
            OnMessageDelegate d = this.OnMessage;
            if (d != null)
                d(message);
        }
    }
    #endregion
}

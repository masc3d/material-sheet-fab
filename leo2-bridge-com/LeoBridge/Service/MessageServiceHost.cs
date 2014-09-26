using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Description;
using System.Text;
using System.Threading.Tasks;

namespace LeoBridge.Service
{
    /// <summary>
    /// Message service host
    /// Public methods are thread-safe.
    /// </summary>
    public class MessageServiceHost : ServiceHost, IMessageServiceListener
    {
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

        public event OnMessageDelegate OnMessage;

        public MessageServiceHost()
            : base(typeof(MessageService), new Uri("http://localhost:37421/"))
        {
            // Configure endpoints. JSON requires HTTP binding and behaviour
            ServiceEndpoint ep = this.AddServiceEndpoint(typeof(IMessageService), new WebHttpBinding(), "");
            ep.EndpointBehaviors.Add(new WebHttpBehavior());

            // Enable metadata publishing
            ServiceMetadataBehavior smb = new ServiceMetadataBehavior();
            smb.HttpGetEnabled = true;
            smb.MetadataExporter.PolicyVersion = PolicyVersion.Policy15;
            this.Description.Behaviors.Add(smb);            
        }

        void IMessageServiceListener.OnMessageReceived(string message)
        {
            OnMessageDelegate d = this.OnMessage;
            if (d != null)
                d(message);            
        }
    }
}

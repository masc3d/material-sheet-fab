using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.Text;
using System.Threading.Tasks;

namespace LeoBridge
{
    /// <summary>
    /// Webservice client proxy with support for automatically recreating channels on fault
    /// </summary>
    /// <typeparam name="TChannel">Type of channel/service</typeparam>
    public abstract class ServiceClientProxy<TChannel>
    {
        IClientChannel _serviceClient;

        public ServiceClientProxy(ChannelFactory<TChannel> channelFactory)
        {
            this.ChannelFactory = channelFactory;
        }

        /// <summary>
        /// The channel factory for this type of service
        /// </summary>
        private ChannelFactory<TChannel> ChannelFactory { get; set; }

        protected TChannel ServiceClient
        {
            get
            {
                if (_serviceClient == null)
                {
                    _serviceClient = (IClientChannel)this.ChannelFactory.CreateChannel();
                    _serviceClient.Faulted += (sender, e) =>
                    {
                        _serviceClient.Dispose();
                        _serviceClient = null;
                    };
                }
                return (TChannel)_serviceClient;
            }
        }
    }
}

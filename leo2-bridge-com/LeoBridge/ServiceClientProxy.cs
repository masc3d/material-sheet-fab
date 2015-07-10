using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Runtime.Serialization.Json;
using System.ServiceModel.Web;

using LeoBridge.Service;

namespace LeoBridge
{
    /// <summary>
    /// Webservice client proxy for deocrating WCF interfaces so they become usable via COM interop.
    /// Also supports caching and automatically recreating channel on fault.
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

        /// <summary>
        /// Wraps call to service with appropriate JSON exception handling
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="a"></param>
        /// <returns></returns>
        protected T Call<T>(Func<T> a) {
            try
            {
                return a();
            } catch(Exception e) {
                WebException we = (e.InnerException != null) ? e.InnerException as WebException : null;
                if (we != null)
                {
                    // Convert json response and http response code to WebFaultException
                    DataContractJsonSerializer jsonSerializer = new DataContractJsonSerializer(typeof(Error));
                    Error err = (Error)jsonSerializer.ReadObject(we.Response.GetResponseStream());
                    HttpWebResponse hwr = (HttpWebResponse)we.Response;
                    throw new WebFaultException<Error>(err, hwr.StatusCode);                    
                }
                throw e;
            }
        }
    }
}

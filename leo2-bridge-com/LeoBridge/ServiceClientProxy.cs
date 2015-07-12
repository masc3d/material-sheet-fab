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
        /// Wraps call to service with appropriate JSON exception handling.
        /// 
        /// If the result type parameter implements IErrorContainer, the error result JSON will be unwrapped
        /// from the WebException and injected into the result instance.
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="a"></param>
        /// <returns></returns>
        protected T Call<T>(Func<T> a) where T : new()
        {
            try
            {
                return a();
            }
            catch (Exception e)
            {
                T result = new T();
                IErrorContainer ec = result as IErrorContainer;
                // Check if we can embed error inside result, simply throw otherwise
                if (ec == null)
                    throw e;

                Action<Exception> embedException = (ex) =>
                {
                    ec.Error = new Error();
                    ec.Error.HttpStatus = -1;
                    ec.Error.Status = -1;
                    ec.Error.Message = ex.Message;
                };

                // Extract error information from response
                WebException we = (e.InnerException != null) ? e.InnerException as WebException : null;
                if (we != null && we.Response != null)
                {
                    // Convert json response and http response code to WebFaultException
                    DataContractJsonSerializer jsonSerializer = new DataContractJsonSerializer(typeof(Error));
                    HttpWebResponse hwr = (HttpWebResponse)we.Response;
                    ec.Error = (Error)jsonSerializer.ReadObject(we.Response.GetResponseStream());
                    ec.Error.HttpStatus = (int)hwr.StatusCode;
                    return result;
                }
                else throw e;
            }
        }
    }
}

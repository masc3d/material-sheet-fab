using LeoBridge.Service;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.ServiceModel;
using System.ServiceModel.Description;
using System.ServiceModel.Dispatcher;
using System.ServiceModel.Web;

namespace LeoBridge
{
    [ComVisible(true)]
    public interface IServiceClientFactory
    {
        String BaseUri { get; set; }
        IRoutingService RoutingService { get; }
    }

    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    public class ServiceClientFactory : IServiceClientFactory
    {
        String _baseUri;
        WebHttpBinding _httpBinding;
        EndpointAddress _endpointAddress;

        // Service proxies
        Lazy<IRoutingService> _routingService;

        public ServiceClientFactory()
        {
            _httpBinding = new WebHttpBinding();
            _httpBinding.OpenTimeout = TimeSpan.FromMilliseconds(3000);
            _httpBinding.CloseTimeout = TimeSpan.FromMilliseconds(3000);
            _httpBinding.ReceiveTimeout = TimeSpan.FromMilliseconds(6000);

            // Create service proxies
            _routingService = new Lazy<IRoutingService>(() => new RoutingServiceProxy(this.CreateChannelFactory<IRoutingService>()));
        }

        public IRoutingService RoutingService { get { return _routingService.Value; } }

        /// <summary>
        /// Create channel factory for WCF service
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <returns></returns>
        private ChannelFactory<T> CreateChannelFactory<T>()
        {
            ChannelFactory<T> factory = new ChannelFactory<T>(_httpBinding, this.EndpointAdress);
            factory.Endpoint.Behaviors.Add(new WebHttpBehavior());
            return factory;
        }

        /// <summary>
        /// Webservice endpoint address
        /// </summary>
        private EndpointAddress EndpointAdress
        {
            get
            {
                if (_endpointAddress == null)
                    // Create referring endpoint and message channel factory
                    _endpointAddress = new EndpointAddress(this.BaseUri);
                return _endpointAddress;
            }
        }

        /// <summary>
        /// Webservices base uri
        /// </summary>
        public string BaseUri
        {
            get { return _baseUri; }
            set
            {
                _baseUri = value;
                _endpointAddress = null;
            }
        }
    }
}

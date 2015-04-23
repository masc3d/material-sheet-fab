using LeoBridge.Service;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.InteropServices;
using System.ServiceModel;
using System.ServiceModel.Description;
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
        IRoutingService _routingService;

        private ChannelFactory<T> CreateChannelFactory<T>()
        {
            ChannelFactory<T> factory = new ChannelFactory<T>(_httpBinding, this.EndpointAdress);
            factory.Endpoint.EndpointBehaviors.Add(new WebHttpBehavior());
            return factory;
        }

        public IRoutingService RoutingService
        {
            get
            {
                if (_routingService == null)
                    _routingService = new RoutingServiceProxy(this.CreateChannelFactory<IRoutingService>());
                return _routingService;
            }
        }

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

        public ServiceClientFactory()//String baseUri)
        {
            //_baseUri = baseUri;
            // Create http binding
            _httpBinding = new WebHttpBinding();
            _httpBinding.OpenTimeout = TimeSpan.FromMilliseconds(1000);
            _httpBinding.ReceiveTimeout = TimeSpan.FromMilliseconds(1000);           
        }
    }
}

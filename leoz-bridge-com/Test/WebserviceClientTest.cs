using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using LeoBridge;

namespace LeoBridgeTest
{
    public abstract class WebserviceClientTest
    {
        public WebserviceClientTest()
        {
            this.ClientFactory= new ServiceClientFactory();
            //this.ClientFactory.BaseUri = "http://leo2ws:8080/leo2";
            //this.ClientFactory.BaseUri = "http://217.7.24.80/leo2";
//            this.ClientFactory.BaseUri = "http://217.7.24.80";
           // this.ClientFactory.BaseUri = "http://leoservice.derkurier.de:80";
            //this.ClientFactory.BaseUri = "http://217.7.24.81";
            this.ClientFactory.BaseUri = "http://leoz.derkurier.de";
                                          

        }

        protected ServiceClientFactory ClientFactory
        {
            get;
            private set;
        }
    }
}

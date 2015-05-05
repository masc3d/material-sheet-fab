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
            this.ClientFactory.BaseUri = "http://leo2ws:8080/leo2";
        }

        protected ServiceClientFactory ClientFactory
        {
            get;
            private set;
        }
    }
}

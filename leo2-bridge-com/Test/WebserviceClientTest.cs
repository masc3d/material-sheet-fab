﻿using System;
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
            this.ClientFactory.BaseUri = "http://192.168.0.215:8080/";
                                          

        }

        protected ServiceClientFactory ClientFactory
        {
            get;
            private set;
        }
    }
}

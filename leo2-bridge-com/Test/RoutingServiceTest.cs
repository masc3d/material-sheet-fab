using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using LeoBridge;
using LeoBridge.Service;

namespace LeoBridgeTest
{
    [TestClass]
    public class RoutingServiceTest : WebserviceClientTest
    {

        [TestMethod]
        public void TestRequest()
        {
            LeoBridge.Service.participant sender;
            LeoBridge.Service.participant consignee;

            sender = new LeoBridge.Service.participant();
            consignee = new LeoBridge.Service.participant();
            //sender={"","","",""};

            //sender=new pariticipant();
            //sender.setcountry("DE");

            // Test repeated invocations
            for (int i = 0; i < 5; i++)
            {
                //RoutingResult result = this.ClientFactory.RoutingService.find("2014-11-05", "DE", "64850", "s");
                RoutingRequestResult result = this.ClientFactory.RoutingService.routingRequest("05.11.2014","06.11.2014",sender,consignee,0,0);

                Console.WriteLine(String.Format("{0}: {1}", i, result.ToString()));
            }
        }

        
        //[TestMethod]
        //public void TestFind()
        //{
        //    // Test repeated invocations
        //    for (int i = 0; i < 5; i++)
        //    {
        //        //RoutingResult result = this.ClientFactory.RoutingService.find("2014-11-05", "DE", "64850", "s");
        //        RoutingResult result = this.ClientFactory.RoutingService.find("a05.11.2014", "DE", "64850", "s");
                
        //        Console.WriteLine(String.Format("{0}: {1}", i, result.ToString()));
        //    }
        //}

        //[TestMethod]
        //public void TestFindVia()
        //{
           
        //    // Test repeated invocations
        //    for (int i = 0; i < 5; i++)
        //    {
        //        RoutingViaResult r = this.ClientFactory.RoutingService.findVia("2015-02-01", "source", "destination");
        //        Console.WriteLine(String.Format("{0}: {1}", i, r.ToString()));
        //    }
        //}
    }
}

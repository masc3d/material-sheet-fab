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
            
            LeoBridge.Service.RoutingRequest r = new RoutingRequest();
            r.sendDate = "2015-06-01";
            r.deliverDate = "2015-06-02";
            consignee.country = "DE";
            consignee.zip = "80331";
            consignee.timeFrom="10:00";
            consignee.timeTo="12:00";
            r.consignee = consignee;
            sender.country="DE";
            sender.zip = "80331";
            sender.timeFrom = "10:00";
            sender.timeTo = "12:00";
            r.sender = sender;
            r.weight = 0;
            r.services = 0;
            for (int i = 0; i < 5; i++)
            {
                RoutingRequestResult result = this.ClientFactory.RoutingService.request(r);
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

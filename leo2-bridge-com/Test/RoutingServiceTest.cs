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
            LeoBridge.Service.RoutingRequestParticipant sender;
            LeoBridge.Service.RoutingRequestParticipant consignee;

            sender = new LeoBridge.Service.RoutingRequestParticipant();
            consignee = new LeoBridge.Service.RoutingRequestParticipant();
            
            LeoBridge.Service.RoutingRequest r = new RoutingRequest();
            r.SendDate = "2015-06-01";
            r.DeliveryDate = "2015-06-02";
            consignee.Country = "DE";
            consignee.Zip = "80331";
            consignee.TimeFrom="10:00";
            consignee.TimeTo="12:00";
            r.Consignee = consignee;
            sender.Country="DE";
            sender.Zip = "80331";
            sender.TimeFrom = "10:00";
            sender.TimeTo = "12:00";
            r.Sender = sender;
            r.Weight = 0;
            r.Services = 0;
            for (int i = 0; i < 5; i++)
            {
                Routing result = this.ClientFactory.RoutingService.Request(r);
                Console.WriteLine(String.Format("{0}: {1}", i, result.ToString()));
            }
        }
        [TestMethod]
        public void TestRequestPartial()
        {
            LeoBridge.Service.RoutingRequestParticipant sender;
            LeoBridge.Service.RoutingRequestParticipant consignee;

            sender = new LeoBridge.Service.RoutingRequestParticipant();
            consignee = new LeoBridge.Service.RoutingRequestParticipant();

            LeoBridge.Service.RoutingRequest r = new RoutingRequest();
            //r.sendDate = "2015-06-01";
            //r.deliveryDate = "2015-06-02";
            consignee.Country = "DE";
            consignee.Zip = "80331";
            consignee.TimeFrom = "10:00";
            consignee.TimeTo = "12:00";
            r.Consignee = consignee;
            sender.Country = "DE";
            sender.Zip = "80331";
            sender.TimeFrom = "10:00";
            sender.TimeTo = "12:00";
            r.Sender = sender;
            //r.weight = 0;
            //r.services = 0;
            for (int i = 0; i < 5; i++)
            {
                Routing result = this.ClientFactory.RoutingService.Request(r);
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

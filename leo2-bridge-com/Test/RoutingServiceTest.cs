using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.ServiceModel.Web;

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
            try
            {
                LeoBridge.Service.RoutingRequestParticipant sender;
                LeoBridge.Service.RoutingRequestParticipant consignee;

                sender = new LeoBridge.Service.RoutingRequestParticipant();
                consignee = new LeoBridge.Service.RoutingRequestParticipant();

                LeoBridge.Service.RoutingRequest r = new RoutingRequest();

                r.SendDate = "2015-06-05";
                r.DeliveryDate = "2015-06-02";
                consignee.Country = "DE";
                consignee.Zip = "";
                consignee.TimeFrom = "10:00";
                consignee.TimeTo = "12:00";
                consignee.StationByRequest = "0";
                r.Consignee = consignee;
                sender.Country = "DE";
                sender.Zip = "80331";
                sender.TimeFrom = "10:00";
                sender.TimeTo = "12:00";
                sender.StationByRequest = "0";
                r.Sender = sender;
                r.Weight = 5;
                r.Services = 0;
                for (int i = 0; i < 1; i++)
                {
                    Routing result = this.ClientFactory.RoutingService.Request(r);
                    Console.WriteLine(String.Format("{0}: {1}", i, result.ToString()));
                }
            }
            catch (WebFaultException<Error> e)
            {
                Console.WriteLine(e);
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
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
            for (int i = 0; i < 1; i++)
            {
                Routing result = this.ClientFactory.RoutingService.Request(r);
                Console.WriteLine(String.Format("{0}: {1}", i, result.ToString()));
            }
        }
    }
}

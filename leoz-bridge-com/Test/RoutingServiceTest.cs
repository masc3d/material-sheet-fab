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
using System.Threading;

namespace LeoBridgeTest
{
    [TestClass]
    public class RoutingServiceTest : WebserviceClientTest
    {  
        [TestMethod]
        public void TestRequestErrorSenddate()
        {
            LeoBridge.Service.RoutingRequestParticipant sender;
            LeoBridge.Service.RoutingRequestParticipant consignee;

            sender = new LeoBridge.Service.RoutingRequestParticipant();
            consignee = new LeoBridge.Service.RoutingRequestParticipant();

            LeoBridge.Service.RoutingRequest r = new RoutingRequest();

//            r.SendDate = "2015-06-01";
            consignee.Country = "DE";
            consignee.Zip = "64850";
            consignee.TimeFrom = "10:00";
            consignee.TimeTo = "12:00";
            r.Consignee = consignee;
            sender.Country = "DE";
            sender.Zip = "36286";
            sender.TimeFrom = "09:00";
            sender.TimeTo = "12:00";
            r.Sender = sender;
            r.Weight = 0;
            r.Services = 0;

            for (int i = 0; i < 1; i++)
            {
                Routing result = this.ClientFactory.RoutingService.Request(r);

                Console.WriteLine("ErrorCode:" + String.Format(result.Error.Code.ToString()));
            }
        }

        [TestMethod]
        public void TestRequest()
        {
            LeoBridge.Service.RoutingRequestParticipant sender;
            LeoBridge.Service.RoutingRequestParticipant consignee;

            sender = new LeoBridge.Service.RoutingRequestParticipant();
            consignee = new LeoBridge.Service.RoutingRequestParticipant();

            LeoBridge.Service.RoutingRequest r = new RoutingRequest();

            r.SendDate = "2015-06-01";
            consignee.Country = "DE";
            consignee.Zip = "64850";
            consignee.TimeFrom = "10:00";
            consignee.TimeTo = "12:00";
            r.Consignee = consignee;
            sender.Country = "DE";
            sender.Zip = "36286";
            sender.TimeFrom = "09:00";
            sender.TimeTo = "12:00";
            r.Sender = sender;
            r.Weight = 0;
            r.Services = 0;

            for (int i = 0; i < 1; i++)
            {
                Routing result = this.ClientFactory.RoutingService.Request(r);
                Console.WriteLine(String.Format("{0}: {1}", i, result.ToString()));
            }
        }


        [TestMethod]
        public void TestRequestDesired()
        {
            LeoBridge.Service.RoutingRequestParticipant sender;
            LeoBridge.Service.RoutingRequestParticipant consignee;

            sender = new LeoBridge.Service.RoutingRequestParticipant();
            consignee = new LeoBridge.Service.RoutingRequestParticipant();

            LeoBridge.Service.RoutingRequest r = new RoutingRequest();

            r.SendDate = "2015-06-01";
            r.DesiredDeliveryDate = "2015-06-02";
            consignee.Country = "DE";
            consignee.Zip = "64850";
            consignee.TimeFrom = "10:00";
            consignee.TimeTo = "12:00";
            consignee.DesiredStation = "020";
            r.Consignee = consignee;
            sender.Country = "DE";
            sender.Zip = "36286";
            sender.TimeFrom = "09:00";
            sender.TimeTo = "12:00";
            sender.DesiredStation = "020";
            r.Sender = sender;
            r.Weight = 0;
            r.Services = 0;

            for (int i = 0; i < 1; i++)
            {
                Routing result = this.ClientFactory.RoutingService.Request(r);
                Console.WriteLine(String.Format("{0}: {1}", i, result.ToString()));
            }
        }


        [TestMethod]
        public void TestRequestThreaded()
        {
            List<Thread> threads = new List<Thread>();
            for (int i = 0; i < 100; i++)
            {
                ThreadStart ts = () =>
                {
                    try
                    {
                        this.TestRequest();
                    }
                    catch (Exception ex)
                    {
                        Assert.Fail(ex.ToString());
                    }
                };

                Thread t = new Thread(ts);
                threads.Add(t);
                t.Start();
            }
            threads.ForEach((t) => t.Join());
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

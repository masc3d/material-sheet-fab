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
        public void TestFind()
        {
            // Test repeated invocations
            for (int i = 0; i < 5; i++)
            {
                RoutingResult result = this.ClientFactory.RoutingService.find("2015-02-01", "germany", "63589", "stuff");
                Console.WriteLine(String.Format("{0}: {1}", i, result.ToString()));
            }
        }

        [TestMethod]
        public void TestFindVia()
        {
           
            // Test repeated invocations
            for (int i = 0; i < 5; i++)
            {
                RoutingViaResult r = this.ClientFactory.RoutingService.findVia("2015-02-01", "source", "destination");
                Console.WriteLine(String.Format("{0}: {1}", i, r.ToString()));
            }
        }
    }
}

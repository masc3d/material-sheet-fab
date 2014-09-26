using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;

using LeoBridge;

namespace LeoBridgeTest
{
    [TestClass]
    public class MessagingTest
    {
        [TestMethod]
        public void TestMessaging()
        {
            using (LeoBridge.LeoBridge m = new LeoBridge.LeoBridge())
            {
                m.Start();
                System.Windows.Forms.MessageBox.Show("Running");
            }            
        }
    }
}

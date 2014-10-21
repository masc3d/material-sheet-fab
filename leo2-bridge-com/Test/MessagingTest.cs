using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;

using LeoBridge;

namespace LeoBridge.Test
{
    [TestClass]
    public class MessagingTest
    {
        [TestMethod]
        public void TestService()
        {
            using (LeoBridge m = new LeoBridge())
            {
                m.Start();
                System.Windows.Forms.MessageBox.Show("Running");
            }            
        }

        [TestMethod]
        public void TestSend()
        {
            using (LeoBridge lb = new LeoBridge())
            {
                Message msg = new Message();
                msg.Add("", "Test");
                lb.SendMessage(msg);
            }
        }
    }
}

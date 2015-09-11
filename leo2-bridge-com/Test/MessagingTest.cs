using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;

using LeoBridge;
using LeoBridge.Service;

namespace LeoBridge.Test
{
    [TestClass]
    public class MessagingTest
    {
        [TestMethod]
        public void TestService()
        {
            using (MessageQueue m = new MessageQueue())
            {
                m.Start();
                m.OnMessage += m_OnMessage;
                System.Windows.Forms.MessageBox.Show("Running");
            }            
        }

        void m_OnMessage(Message message)
        {
            System.Windows.Forms.MessageBox.Show(message.ToString());
        }

    }
}

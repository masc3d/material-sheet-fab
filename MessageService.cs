using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Runtime.InteropServices;

namespace LeoBridge
{
    [Guid("709C2294-A2E0-4CD0-9969-6F2CC1B71625")]
    [ComVisible(true)]
    [InterfaceType(ComInterfaceType.InterfaceIsIDispatch)]
    public interface IMessageServiceEvents
    {
        [DispId(1)]
        void OnMessage(String message);
    }

    [Guid("EF6CD085-7175-4975-B1BB-2E7DE0A3774D")]
    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]    
    [ComSourceInterfaces(typeof(IMessageServiceEvents))]
    public class MessageService : IMessageService
    {
        public delegate void OnMessageDelegate(String message);
        public event OnMessageDelegate OnMessage;

        public void TestEvent(String testMessage)
        {
            if (OnMessage != null)
            {
                OnMessage(testMessage);
            }
        }
    }
}

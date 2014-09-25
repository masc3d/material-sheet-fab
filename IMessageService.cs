using System;
using System.Runtime.InteropServices;

namespace LeoBridge
{
    [Guid("067B41A1-F76D-4520-A873-232560AE0DA8")]    
    [ComVisible(true)]
    public interface IMessageService
    {
        void TestEvent(String testMessage);
    }
}

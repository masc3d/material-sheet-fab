using System;
using System.Runtime.InteropServices;

namespace LeoBridge
{
    /// <summary>
    /// Message service provider interface
    /// </summary>
    /// <author>masc</author>
    [Guid("067B41A1-F76D-4520-A873-232560AE0DA8")]    
    [ComVisible(true)]
    public interface ILeoBridge : IDisposable
    {
        void TestEvent(String testMessage);
        /// <summary>
        /// Start listener
        /// </summary>
        void Start();
        /// <summary>
        /// Stop listener
        /// </summary>
        void Stop();
        /// <summary>
        /// Send message
        /// </summary>
        /// <param name="message"></param>
        void SendMessage(String message);
    }
}

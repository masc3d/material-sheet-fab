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
        /// <summary>
        /// Start listener
        /// </summary>
        void Start();
        /// <summary>
        /// Stop listener
        /// </summary>
        void Stop();
        ///// <summary>
        ///// Send message
        ///// </summary>
        ///// <param name="message"></param>
        void SendMessage(IMessage message);          
        /// <summary>
        /// Send single value message
        /// </summary>
        /// <param name="message"></param>
        void SendValue(Object message);
    }

    /// <summary>
    /// Interface for LEO bridge (COM) events
    /// </summary>
    /// <author>masc</author>
    [Guid("709C2294-A2E0-4CD0-9969-6F2CC1B71625")]
    [ComVisible(true)]
    [InterfaceType(ComInterfaceType.InterfaceIsIDispatch)]
    public interface ILeoBridgeEvents
    {
        [DispId(1)]
        void OnMessage(IMessage message);
    }

    /// <summary>
    /// LeoBridge message
    /// </summary>
    [Guid("4E33B608-CFB8-4247-9385-7E5E795D9DF6")]
    [ComVisible(true)]
    public interface IMessage
    {
        /// <summary>
        /// Get main message attribute
        /// </summary>
        /// <returns></returns>
        object GetValue();
        /// <summary>
        /// Get message attribute by key
        /// </summary>
        /// <param name="key"></param>
        /// <returns></returns>
        object Get(string key);
        /// <summary>
        /// Add attribute to message
        /// </summary>
        /// <param name="key"></param>
        /// <param name="value"></param>
        void Put(string key, object value);
        string ToString();
    }
}

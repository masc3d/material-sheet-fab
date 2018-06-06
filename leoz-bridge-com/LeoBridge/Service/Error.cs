using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Runtime.InteropServices;
using System.Text;

namespace LeoBridge.Service
{
    [ComVisible(true)]
    public interface IError
    {
        int Code { get; set; }
        String Message { get; set; }
    }

    /// <summary>
    /// REST error container
    /// </summary>
    [ComVisible(true)]    
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class Error : IError
    {
        public Error() { }

        [DataMember(Name = "code")]
        public int Code { get; set; }
        [DataMember(Name = "message")]
        public string Message { get; set; }
    }

    /// <summary>
    /// Interface which can be optionally used on REST results for embedded error information    
    /// </summary>
    [Serializable]
    [DataContract]
    public abstract class ErrorContainer
    {
        public Error Error { get; set; }
    }
}

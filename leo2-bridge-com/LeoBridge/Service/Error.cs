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
        int HttpStatus { get; set; }
        int Status { get; set; }
        int ErrorCode { get; set; }
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

        [DataMember(Name = "httpStatus")]
        public int HttpStatus { get; set; }
        [DataMember(Name = "status")]
        public int Status { get; set; }
        [DataMember(Name = "message")]
        public String Message { get; set; }
        [DataMember(Name = "code")]
        public int ErrorCode { get; set; }
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

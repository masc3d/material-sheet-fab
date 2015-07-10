using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Text;

namespace LeoBridge.Service
{
    [DataContract]
    public class Error
    {
        [DataMember(Name = "status")]
        public int Status { get; set; }
        [DataMember(Name = "message")]
        public String Message { get; set; }
    }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
using System.Threading.Tasks;

namespace LeoBridge
{
    [ServiceContract]
    interface IMessageService
    {
        [OperationContract]
        [WebGet(ResponseFormat=WebMessageFormat.Json, UriTemplate="/send?m={message}")]        
        void Send(String message);
    }
}

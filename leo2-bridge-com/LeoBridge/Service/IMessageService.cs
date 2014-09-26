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
        [WebGet(RequestFormat=WebMessageFormat.Json, ResponseFormat=WebMessageFormat.Json, UriTemplate="/send?m={message}")]        
        bool SendMessage(String message);
    }
}

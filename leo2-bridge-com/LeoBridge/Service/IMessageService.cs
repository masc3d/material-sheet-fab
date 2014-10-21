using System.ServiceModel;
using System.ServiceModel.Web;

namespace LeoBridge
{
    [ServiceContract]
    interface IMessageService
    {
        [OperationContract]        
        [WebInvoke(RequestFormat=WebMessageFormat.Json, ResponseFormat=WebMessageFormat.Json, UriTemplate="/send", Method="POST")]
        void SendMessage(Message message);
    }
}

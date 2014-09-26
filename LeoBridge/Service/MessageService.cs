using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.Text;
using System.Threading.Tasks;

using LeoBridge.Service;

namespace LeoBridge
{
    /// <summary>
    /// Message service listener
    /// </summary>
    public interface IMessageServiceListener
    {
       void OnMessageReceived(String message);
    }

    /// <summary>
    /// Message service
    /// </summary>
    class MessageService : IMessageService
    {
        public void Send(string message)
        {
            ((IMessageServiceListener)OperationContext.Current.Host).OnMessageReceived(message);
        }
    }
}

using System.ServiceModel;


namespace LeoBridge
{
    /// <summary>
    /// Message service listener
    /// </summary>
    public interface IMessageServiceListener
    {
       void OnMessageReceived(IMessage message);
    }

    /// <summary>
    /// Message service
    /// </summary>
    class MessageService : IMessageService
    {
        public void SendMessage(Message message)
        {
            ((IMessageServiceListener)OperationContext.Current.Host).OnMessageReceived(message);            
        }
    }
}

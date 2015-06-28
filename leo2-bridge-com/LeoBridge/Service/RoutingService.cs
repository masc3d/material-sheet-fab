using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
using System.Threading.Tasks;
using System.Runtime.Serialization;
using System.Runtime.InteropServices;

namespace LeoBridge.Service
{


    [ServiceContract]
    [ComVisible(true)]
    public interface IRoutingService
    {
        [OperationContract]
        [WebInvoke(
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,

            UriTemplate = "/rs/api/v1/routing/request",

            Method = "POST"

            )]
        RoutingRequestResult request(RoutingRequest r);

    }


    #region Entities
    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class RoutingRequest
    {
        [DataMember(Name = "sendDate", IsRequired = true)]
        public String sendDate { get; set; }
        [DataMember]
        public String deliverDate { get; set; }
        [DataMember]
        public participant sender {get;set;}
        [DataMember]
        public participant consignee { get; set; }
        [DataMember]
        public int services { get; set; }
        [DataMember]
        public double weight {get;set;}

    }

    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class RoutingRequestResult
    {
        [DataMember(Name = "sendDate", IsRequired = true)]
        public String sendDate;
        [DataMember(Name = "deliveryDate", IsRequired = false)]
        public String deliveryDate;
        [DataMember(Name = "sender", IsRequired = false)]
        public RoutingParticipant sender;
        [DataMember(Name = "consignee", IsRequired = false)]
        public RoutingParticipant consignee;
        [DataMember(Name = "viaHubs", IsRequired = false)]
        public String viaHubs;
        [DataMember(Name = "labelContent", IsRequired = false)]
        public String labelContent;
        [DataMember(Name = "message", IsRequired = false)]
        public String message;
    }

    [ComVisible(true)]
    [DataContract]
    public struct participant
    {
        public string country { get; set; }
        public string zip { get; set; }
        public string timefrom { get; set; }
        public string timeto { get; set; }
    }

    [ComVisible(true)]
    [DataContract]
    public struct RoutingParticipant
    {

        [DataMember(Name = "station", IsRequired = false)]
        string station;
        [DataMember(Name = "country", IsRequired = false)]
        string country;
        [DataMember(Name = "zipCode", IsRequired = false)]
        string zipCode;
        [DataMember(Name = "zone", IsRequired = false)]
        string zone;
        [DataMember(Name = "sector", IsRequired = false)]
        string sector;
        [DataMember(Name = "dayType", IsRequired = false)]
        string dayType;
        [DataMember(Name = "island", IsRequired = false)]
        Boolean island;
        [DataMember(Name = "term", IsRequired = false)]
        int term;
        [DataMember(Name = "earliestTimeOfDelivery", IsRequired = false)]
        string earliestTimeOfDelivery;
        [DataMember(Name = "saturdayDeliveryUntil", IsRequired = false)]
        string saturdayDeliveryUntil;
        [DataMember(Name = "sundayDeliveryUntil", IsRequired = false)]
        string sundayDeliveryUntil;
        [DataMember(Name = "pickupUntil", IsRequired = false)]
        string pickupUntil;
        [DataMember(Name = "partnerManager", IsRequired = false)]
        string partnerManager;
    }

    #endregion

    #region Proxy
    public class RoutingServiceProxy : ServiceClientProxy<IRoutingService>, IRoutingService
    {
        public RoutingServiceProxy(ChannelFactory<IRoutingService> factory)
            : base(factory) { }
        public RoutingRequestResult request(RoutingRequest r)
        {
            //try
            //{
            //    DateTime dDat = DateTime.Parse(senddate);
            //    senddate = dDat.ToString("yyyy-MM-dd");
            //}
            //catch {
            //    senddate = " ";
            //}
            //try
            //{
            //    DateTime dDat = DateTime.Parse(deliverydate);
            //    deliverydate = dDat.ToString("yyyy-MM-dd");
            //}
            //catch
            //{
            //    deliverydate = " ";
            //}
            try
            {
                return this.ServiceClient.request(r);
            }
            catch ( Exception e)
            {
                Exception ex = e;
                throw;
            }

        }

    }
    #endregion
}

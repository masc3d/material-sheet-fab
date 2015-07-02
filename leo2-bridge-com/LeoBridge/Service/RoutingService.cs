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
        RoutingRequestResult Request(RoutingRequest r);
    }

    #region RoutingRequest
    [ComVisible(true)]
    public interface IRoutingRequest
    {
        String SendDate { get; set; }
        String DeliveryDate { get; set; }
        Participant Sender { get; set; }
        Participant Consignee { get; set; }
        int Services { get; set; }
        double Weight { get; set; }
    }

    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class RoutingRequest : IRoutingRequest
    {
        public RoutingRequest() { }

        [DataMember(Name = "sendDate", IsRequired = true)]
        public String SendDate { get; set; }
        [DataMember]
        public String DeliveryDate { get; set; }
        [DataMember]
        public Participant Sender { get; set; }
        [DataMember]
        public Participant Consignee { get; set; }
        [DataMember]
        public int Services { get; set; }
        [DataMember]
        public double Weight { get; set; }
    }
    #endregion

    #region RoutingRequestResult
    [ComVisible(true)]
    public interface IRoutingRequestResult
    {
        String SendDate { get; set; }
        String DeliveryDate { get; set; }
        RoutingParticipant Sender { get; set; }
        RoutingParticipant Consignee { get; set; }
        String[] ViaHubs { get; set; }
        String LabelContent { get; set; }
        String Message { get; set; }
    }

    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class RoutingRequestResult : IRoutingRequestResult
    {
        public RoutingRequestResult() { }

        [DataMember(Name = "sendDate", IsRequired = true)]
        public String SendDate { get; set; }

        [DataMember(Name = "deliveryDate", IsRequired = false)]
        public String DeliveryDate { get; set; }

        [DataMember(Name = "sender", IsRequired = false)]
        public RoutingParticipant Sender { get; set; }

        [DataMember(Name = "consignee", IsRequired = false)]
        public RoutingParticipant Consignee { get; set; }

        [DataMember(Name = "viaHubs", IsRequired = false)]
        public String[] ViaHubs { get; set; }

        [DataMember(Name = "labelContent", IsRequired = false)]
        public String LabelContent { get; set; }

        [DataMember(Name = "message", IsRequired = false)]
        public String Message { get; set; }

    }
    #endregion

    #region Participant
    [ComVisible(true)]
    public interface IParticipant
    {
        string Country { get; set; }
        string Zip { get; set; }
        string TimeFrom { get; set; }
        string TimeTo { get; set; }
    }

    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class Participant : IParticipant
    {
        public Participant() { }

        [DataMember(IsRequired = false)]
        public string Country { get; set; }
        [DataMember(IsRequired = false)]
        public string Zip { get; set; }
        [DataMember(IsRequired = false)]
        public string TimeFrom { get; set; }
        [DataMember(IsRequired = false)]
        public string TimeTo { get; set; }
    }
    #endregion

    #region RoutingParticiapnt
    [ComVisible(true)]
    public interface IRoutingParticipant
    {
        string Station { get; set; }
        string Country { get; set; }
        string ZipCode { get; set; }
        string Zone { get; set; }
        string Sector { get; set; }
        string DayType { get; set; }
        Boolean Island { get; set; }
        int Term { get; set; }
        string EarliestTimeOfDelivery { get; set; }
        string SaturdayDeliveryUntil { get; set; }
        string SundayDeliveryUntil { get; set; }
        string PickupUntil { get; set; }
        string PartnerManager { get; set; }
    }

    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class RoutingParticipant : IRoutingParticipant
    {
        public RoutingParticipant() { }

        [DataMember(IsRequired = false)]
        public string Station { get; set; }
        [DataMember(IsRequired = false)]
        public string Country { get; set; }
        [DataMember(IsRequired = false)]
        public string ZipCode { get; set; }
        [DataMember(IsRequired = false)]
        public string Zone { get; set; }
        [DataMember(IsRequired = false)]
        public string Sector { get; set; }
        [DataMember(IsRequired = false)]
        public string DayType { get; set; }
        [DataMember(IsRequired = false)]
        public Boolean Island { get; set; }
        [DataMember(IsRequired = false)]
        public int Term { get; set; }
        [DataMember(IsRequired = false)]
        public string EarliestTimeOfDelivery { get; set; }
        [DataMember(IsRequired = false)]
        public string SaturdayDeliveryUntil { get; set; }
        [DataMember(IsRequired = false)]
        public string SundayDeliveryUntil { get; set; }
        [DataMember(IsRequired = false)]
        public string PickupUntil { get; set; }
        [DataMember(IsRequired = false)]
        public string PartnerManager { get; set; }
    }
    #endregion

    #region Proxy
    public class RoutingServiceProxy : ServiceClientProxy<IRoutingService>, IRoutingService
    {
        public RoutingServiceProxy(ChannelFactory<IRoutingService> factory)
            : base(factory) { }

        public RoutingRequestResult Request(RoutingRequest r)
        {
            return this.ServiceClient.Request(r);
        }
    }
    #endregion
}

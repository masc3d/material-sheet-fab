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
        Routing Request(RoutingRequest r);
    }

    #region RoutingRequest

    #region RoutingRequestParticipant
    [ComVisible(true)]
    public interface IRoutingRequestParticipant
    {
        string Country { get; set; }
        string Zip { get; set; }
        string TimeFrom { get; set; }
        string TimeTo { get; set; }
        string StationByRequest { get; set; }
    }

    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class RoutingRequestParticipant : IRoutingRequestParticipant
    {
        public RoutingRequestParticipant() { }

        [DataMember(Name = "country", IsRequired = false, EmitDefaultValue = false)]
        public string Country { get; set; }
        [DataMember(Name = "zip", IsRequired = false, EmitDefaultValue = false)]
        public string Zip { get; set; }
        [DataMember(Name = "timeFrom", IsRequired = false, EmitDefaultValue = false)]
        public string TimeFrom { get; set; }
        [DataMember(Name = "timeTo", IsRequired = false, EmitDefaultValue = false)]
        public string TimeTo { get; set; }
        [DataMember(Name = "stationByRequest", IsRequired = false, EmitDefaultValue = false)]
        public string StationByRequest { get; set; }
    }
    #endregion

    [ComVisible(true)]
    public interface IRoutingRequest
    {
        String SendDate { get; set; }
        String SetDeliveryDate { get; set; }
        RoutingRequestParticipant Sender { get; set; }
        RoutingRequestParticipant Consignee { get; set; }
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
        [DataMember(Name = "setDeliveryDate", EmitDefaultValue = false)]
        public String SetDeliveryDate { get; set; }
        [DataMember(Name = "sender", EmitDefaultValue = false)]
        public RoutingRequestParticipant Sender { get; set; }
        [DataMember(Name = "consignee", EmitDefaultValue = false)]
        public RoutingRequestParticipant Consignee { get; set; }
        [DataMember(Name = "services", EmitDefaultValue = false)]
        public int Services { get; set; }
        [DataMember(Name = "weight", EmitDefaultValue = false)]
        public double Weight { get; set; }
    }
    #endregion

    #region Routing

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

        [DataMember(Name = "station", IsRequired = false, EmitDefaultValue = false)]
        public string Station { get; set; }
        [DataMember(Name = "country", IsRequired = false, EmitDefaultValue = false)]
        public string Country { get; set; }
        [DataMember(Name = "zipCode", IsRequired = false, EmitDefaultValue = false)]
        public string ZipCode { get; set; }
        [DataMember(Name = "zone", IsRequired = false, EmitDefaultValue = false)]
        public string Zone { get; set; }
        [DataMember(Name = "sector", IsRequired = false, EmitDefaultValue = false)]
        public string Sector { get; set; }
        [DataMember(Name = "dayType", IsRequired = false, EmitDefaultValue = false)]
        public string DayType { get; set; }
        [DataMember(Name = "island", IsRequired = false, EmitDefaultValue = false)]
        public Boolean Island { get; set; }
        [DataMember(Name = "term", IsRequired = false, EmitDefaultValue = false)]
        public int Term { get; set; }
        [DataMember(Name = "earliestTimeOfDelivery", IsRequired = false, EmitDefaultValue = false)]
        public string EarliestTimeOfDelivery { get; set; }
        [DataMember(Name = "saturdayDeliveryUntil", IsRequired = false, EmitDefaultValue = false)]
        public string SaturdayDeliveryUntil { get; set; }
        [DataMember(Name = "sundayDeliveryUntil", IsRequired = false, EmitDefaultValue = false)]
        public string SundayDeliveryUntil { get; set; }
        [DataMember(Name = "pickupUntil", IsRequired = false, EmitDefaultValue = false)]
        public string PickupUntil { get; set; }
        [DataMember(Name = "partnerManager", IsRequired = false, EmitDefaultValue = false)]
        public string PartnerManager { get; set; }
    }
    #endregion

    [ComVisible(true)]
    public interface IRouting
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
    public class Routing : IRouting
    {
        public Routing() { }

        [DataMember(Name = "sendDate", IsRequired = true, EmitDefaultValue = false)]
        public String SendDate { get; set; }

        [DataMember(Name = "deliveryDate", IsRequired = false, EmitDefaultValue = false)]
        public String DeliveryDate { get; set; }

        [DataMember(Name = "sender", IsRequired = false, EmitDefaultValue = false)]
        public RoutingParticipant Sender { get; set; }

        [DataMember(Name = "consignee", IsRequired = false, EmitDefaultValue = false)]
        public RoutingParticipant Consignee { get; set; }

        [DataMember(Name = "viaHubs", IsRequired = false, EmitDefaultValue = false)]
        public String[] ViaHubs { get; set; }

        [DataMember(Name = "labelContent", IsRequired = false, EmitDefaultValue = false)]
        public String LabelContent { get; set; }

        [DataMember(Name = "message", IsRequired = false, EmitDefaultValue = false)]
        public String Message { get; set; }

    }
    #endregion

    #region Proxy
    public class RoutingServiceProxy : ServiceClientProxy<IRoutingService>, IRoutingService
    {
        public RoutingServiceProxy(ChannelFactory<IRoutingService> factory)
            : base(factory) { }

        public Routing Request(RoutingRequest r)
        {
            Routing ret;
            try
            {
                ret = this.ServiceClient.Request(r);
            }
            catch (Exception e)
            {
                Console.WriteLine(String.Format("{0}", e));
                ret = new Routing();
                ret.Message = "NOK";
                //throw;
            }
            return ret;
        }
    }
    #endregion
}

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
        string DesiredStation { get; set; }
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
        [DataMember(Name = "desiredStation", IsRequired = false, EmitDefaultValue = false)]
        public string DesiredStation { get; set; }
    }
    #endregion

    [ComVisible(true)]
    public interface IRoutingRequest
    {
        String SendDate { get; set; }
        String DesiredDeliveryDate { get; set; }
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
        [DataMember(Name = "desiredDeliveryDate", EmitDefaultValue=false)]
        public String DesiredDeliveryDate { get; set; }
        [DataMember(Name = "sender")]
        public RoutingRequestParticipant Sender { get; set; }
        [DataMember(Name = "consignee")]
        public RoutingRequestParticipant Consignee { get; set; }
        [DataMember(Name = "services")]
        public int Services { get; set; }
        [DataMember(Name = "weight")]
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
        string ToString();
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
        [DataMember(Name = "island", IsRequired = false)]
        public Boolean Island { get; set; }
        [DataMember(Name = "term", IsRequired = false)]
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

        public override string ToString()
        {
            return String.Format("Station [{0}] Country [{1}] ZipCode [{2}] Zone [{3}] Sector [{4}] Day type [{5}] Island [{6}] Term [{7}]",
                this.Station,
                this.Country,
                this.ZipCode,
                this.Zone,
                this.Sector,
                this.DayType,
                this.Island,
                this.Term);
        }
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

        Error Error { get; set; }
        String ToString();
    }

    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class Routing : ErrorContainer, IRouting
    {
        public Routing() { }

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

        public override string ToString()
        {
            return String.Format("Send date [{0}] Delivery date [{1}] Sender [{2}] Consignee [{3}] ViaHubs [{4}] Label content [{5}] Message [{6}]",
                this.SendDate,
                this.DeliveryDate,
                this.Sender,
                this.Consignee,
                this.ViaHubs,
                this.LabelContent,
                this.Message);
        }
    }
    #endregion

    #region Proxy
    /// <summary>
    /// COM proxy class for routing service
    /// </summary>
    public class RoutingServiceProxy : ServiceClientProxy<IRoutingService>, IRoutingService
    {
        public RoutingServiceProxy(ChannelFactory<IRoutingService> factory)
            : base(factory) { }

        public Routing Request(RoutingRequest r)
        {
            return this.Call<Routing>(() => this.ServiceClient.Request(r) );
        }
    }
    #endregion
}

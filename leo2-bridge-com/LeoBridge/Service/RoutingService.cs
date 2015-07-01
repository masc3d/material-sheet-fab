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
            ResponseFormat =  WebMessageFormat.Json,
            UriTemplate = "/rs/api/v1/routing/request",
            Method = "POST"
            )]
        RoutingRequestResult request(RoutingRequest r);
    }


    #region Entities
    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class RoutingRequest : IRoutingrequest
    {
        public RoutingRequest() { }

        [DataMember(Name = "sendDate", IsRequired = true)]
        public String sendDate { get; set; }
        [DataMember]
        public String deliveryDate { get; set; }
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
    public interface IRoutingrequest
    {
        String sendDate { get; set; }
        String deliveryDate { get; set; }
        participant sender { get; set; }
        participant consignee { get; set; }
        int services { get; set; }
        double weight { get; set; }
    }

    //[ComVisible(true)]
    //[ClassInterface(ClassInterfaceType.None)]
    //[DataContract]
    //public class RoutingRequestResult2
    //{
    //    [DataMember(Name = "sendDate", IsRequired = true)]
    //    public String sendDate;
    //    [DataMember(Name = "deliveryDate", IsRequired = false)]
    //    public String deliveryDate;
    //    [DataMember(Name = "labelContent", IsRequired = false)]
    //    public String labelContent;
    //    [DataMember(Name = "message", IsRequired = false)]
    //    public String message;

    //    public override string ToString()
    //    {
    //        return "egal";
    //    }
    //}





    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class RoutingRequestResult : IRoutingRequestResult
    {
        public RoutingRequestResult() { }

        [DataMember(Name = "sendDate", IsRequired = true)]
        public String sendDate { get; set; }

        [DataMember(Name = "deliveryDate", IsRequired = false)]
        public String deliveryDate { get; set; }

        [DataMember(Name = "sender", IsRequired = false)]
        public RoutingParticipant sender { get; set; }

        [DataMember(Name = "consignee", IsRequired = false)]
        public RoutingParticipant consignee { get; set; }

        [DataMember(Name = "viaHubs", IsRequired = false)]
        public String[] viaHubs { get; set; }

        [DataMember(Name = "labelContent", IsRequired = false)]
        public String labelContent { get; set; }

        [DataMember(Name = "message", IsRequired = false)]
        public String message { get; set; }

    }





    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class participant : IParticipant
    {
        public participant() { }
        [DataMember(IsRequired = false)]
        public string country { get; set; }
        [DataMember(IsRequired = false)]
        public string zip { get; set; }
        [DataMember(IsRequired = false)]
        public string timeFrom { get; set; }
        [DataMember(IsRequired = false)]
        public string timeTo { get; set; }

    }

    [ComVisible(true)]
    public interface IParticipant
        {
            string country { get; set; }
            string zip { get; set; }
            string timeFrom { get; set; }
            string timeTo { get; set; }
    }

    [ComVisible(true)]
    public interface IRoutingRequestResult
    {
        String sendDate { get; set; }
        String deliveryDate { get; set; }
        RoutingParticipant sender { get; set; }
        RoutingParticipant consignee { get; set; }
        String[] viaHubs { get; set; }
        String labelContent { get; set; }
        String message { get; set; }

    }

    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class RoutingParticipant : IRoutingParticipant
    {
        public RoutingParticipant() { }

        [DataMember(IsRequired = false)]
        public string station { get; set; }
        [DataMember(IsRequired = false)]
        public string country { get; set; }
        [DataMember(IsRequired = false)]
        public string zipCode { get; set; }
        [DataMember(IsRequired = false)]
        public string zone { get; set; }
        [DataMember(IsRequired = false)]
        public string sector { get; set; }
        [DataMember(IsRequired = false)]
        public string dayType { get; set; }
        [DataMember(IsRequired = false)]
        public Boolean island { get; set; }
        [DataMember(IsRequired = false)]
        public int term { get; set; }
        [DataMember(IsRequired = false)]
        public string earliestTimeOfDelivery { get; set; }
        [DataMember(IsRequired = false)]
        public string saturdayDeliveryUntil { get; set; }
        [DataMember(IsRequired = false)]
        public string sundayDeliveryUntil { get; set; }
        [DataMember(IsRequired = false)]
        public string pickupUntil { get; set; }
        [DataMember(IsRequired = false)]
        public string partnerManager { get; set; }
    }

    [ComVisible(true)]
    public interface IRoutingParticipant
     {
         string station { get; set; }
         string country { get; set; }
         string zipCode { get; set; }
         string zone { get; set; }
         string sector { get; set; }
         string dayType { get; set; }
         Boolean island { get; set; }
         int term { get; set; }
         string earliestTimeOfDelivery { get; set; }
         string saturdayDeliveryUntil { get; set; }
         string sundayDeliveryUntil { get; set; }
         string pickupUntil { get; set; }
         string partnerManager { get; set; }
         }


    #endregion

    #region Proxy
    public class RoutingServiceProxy : ServiceClientProxy<IRoutingService>, IRoutingService
    {
        public RoutingServiceProxy(ChannelFactory<IRoutingService> factory)
            : base(factory) { }
        public RoutingRequestResult request(RoutingRequest r)
        {
            try
            {
                DateTime dDat = DateTime.Parse(r.sendDate);
                r.sendDate = dDat.ToString("yyyy-MM-dd");
            }
            catch
            {
                r.sendDate = System.DateTime.Now.ToString("yyyy-MM-dd");
            }
            try
            {
                DateTime dDat = DateTime.Parse(r.deliveryDate);
                r.deliveryDate = dDat.ToString("yyyy-MM-dd");
            }
            catch
            {
                r.deliveryDate = System.DateTime.Now.ToString("yyyy-MM-dd");
            }
            try
            {
                return this.ServiceClient.request(r);
            }
            catch ( Exception e)
            {
                Console.WriteLine("EX [{0}]", e.Message);
                
                Exception ex = e;

              throw;
            }

        }

    }
    #endregion
}

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
        //[OperationContract]
        //[WebInvoke(
        //    RequestFormat = WebMessageFormat.Json,
        //    ResponseFormat = WebMessageFormat.Json,
        //    UriTemplate = "/rs/api/v1/routing/find?senddate={date}&country={country}&zip={zip}&product={product}",
        //    Method = "GET")]
        //RoutingResult find(String date, String country, String zip, String product);

        //[OperationContract]
        //[WebInvoke(
        //    RequestFormat = WebMessageFormat.Json,
        //    ResponseFormat = WebMessageFormat.Json,
        //    UriTemplate = "/rs/api/v1/routing/find/via?senddate={date}&source_sector={sourceSector}&destination_sector={destinationSector}",
        //    Method = "GET")]
        //RoutingViaResult findVia(String date, String sourceSector, String destinationSector);

//{
//  "sendDate": "2015-06-01",
//  "deliverDate": "2015-06-02",
//  "sender": {
//    "country": "DE",
//    "zip": "36286",
//    "timeFrom": "09:00",
//    "timeTo": "12:00"
//  },
//  "consignee": {
//    "country": "DE",
//    "zip": "36286",
//    "timeFrom": "09:00",
//    "timeTo": "12:00"
//  },
//  "services": 0,
//  "weight": 0
//}

    //     [WebInvoke(UriTemplate = "/UplaodData", 
    //              Method = "POST",
    //               RequestFormat = WebMessageFormat.Json,
    //               ResponseFormat = WebMessageFormat.Json)]
    //    Student GetUpdatedStudent(Student s);
    //}
    //[DataContract]
    //public class Student
    //{
    //    [DataMember]
    //    public string RollNumber { get; set; }
    //    [DataMember] 
    //    public string Name { get; set; }
    //}

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

    //[ComVisible(true)]
    //[ClassInterface(ClassInterfaceType.None)]
    //[DataContract]
    //public class RoutingResult
    //{
    //    [DataMember(Name = "zipCode", IsRequired = true)]
    //    public String zipCode;
    //    [DataMember(Name = "routing", IsRequired = true)]
    //    public String Routing;
    //    [DataMember(Name = "term", IsRequired = false)]
    //    public int Term;
    //    [DataMember(Name = "zone", IsRequired = true)]
    //    public String Zone;
    //    [DataMember(Name = "sector", IsRequired = false)]
    //    public String Sector;
    //    [DataMember(Name = "dayType", IsRequired = true)]
    //    public String dayType;
    //    [DataMember(Name = "island", IsRequired = true)]
    //    public Boolean Island;
    //    [DataMember(Name = "earliestTimeOfDelivery", IsRequired = true)]
    //    public String earliestTimeOfDelivery;
    //    [DataMember(Name = "earliestTimeOfDelivery2", IsRequired = false)]
    //    public String earliestTimeOfDelivery2;
    //    [DataMember(Name = "delieveryDay", IsRequired = true)]
    //    public String delieveryDay;
    //    [DataMember(Name = "saterdayDeliveryUntil", IsRequired = true)]
    //    public String saterdayDeliveryUntil;
    //    [DataMember(Name = "sundayDeliveryUntil", IsRequired = true)]
    //    public String sundayDeliveryUntil;
    //    [DataMember(Name = "pickupUntil", IsRequired = true)]
    //    public String pickupUntil;
    //    [DataMember(Name = "partnerManager", IsRequired = false)]
    //    public String partnerManager;

    //    public override string ToString()
    //    {
    //        return String.Format("zipCode [{0}] Routing Station [{1}] Term in Days [{2}] Zone [{3}] Sector [{4}] dayType [{5}] Island [{6}] earliest Time of Delivery [{7}] earliest Time of Delivery optonal [{8}] delievery Day [{9}] saterday Delivery until [{10}]  sunday Delivery until [{11}] pickupUntil [{12}] partnerManager [{13}] ",
    //            this.zipCode,
    //            this.Routing,
    //            this.Term,
    //            this.Zone,
    //            this.Sector,
    //            this.dayType,
    //            this.Island,
    //            this.earliestTimeOfDelivery,
    //            this.earliestTimeOfDelivery2,
    //            this.delieveryDay,
    //            this.saterdayDeliveryUntil,
    //            this.sundayDeliveryUntil,
    //            this.pickupUntil,
    //            this.partnerManager
                
    //            );
    //    }

    //}

    //[ComVisible(true)]
    //[ClassInterface(ClassInterfaceType.None)]
    //[DataContract]
    //public class RoutingViaResult
    //{
    //    [DataMember(Name = "sectors", IsRequired = true)]
    //    public String[] Sectors { get; set; }
    //    [DataMember(Name = "labelContent", IsRequired = true)]
    //    public String LabelContent { get; set; }

    //    public override string ToString()
    //    {
    //        return String.Format("Sectors [{0}] Label content [{1}]",
    //            String.Join(",", this.Sectors).TrimEnd(','),
    //            this.LabelContent);
    //    }
    //}

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
        //RoutingRequestResult
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
                return this.ServiceClient.request(r);// request(r);

            }
            catch ( Exception e)
            {
                Exception ex = e;
                throw;
            }

        }

        //public RoutingResult find(string date, string country, string zip, string product)
        //{
        //    try
        //    {
        //        DateTime dDat = DateTime.Parse(date);
        //        date = dDat.ToString("yyyy-MM-dd");
        //    }
        //    catch {
        //        date = " ";
        //    }

        //    return this.ServiceClient.find(date, country, zip, product);
        //}

//        public RoutingViaResult findVia(String date, String source_sector, String destination_sector)
//        {
//            return this.ServiceClient.findVia(date, source_sector, destination_sector);
//        }
    }
    #endregion
}

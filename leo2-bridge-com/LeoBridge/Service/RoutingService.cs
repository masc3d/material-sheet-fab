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
            UriTemplate = "/v1/routing/find?date={date}&country={country}&zip={zip}&product={product}",
            Method = "GET")]
        RoutingResult find(String date, String country, String zip, String product);

        [OperationContract]
        [WebInvoke(
            RequestFormat = WebMessageFormat.Json,
            ResponseFormat = WebMessageFormat.Json,
            UriTemplate = "/v1/routing/find/via?date={date}&source_sector={sourceSector}&destination_sector={destinationSector}",
            Method = "GET")]
        RoutingViaResult findVia(String date, String sourceSector, String destinationSector);
    }

    #region Entities
    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class RoutingResult
    {
        [DataMember(Name = "sector", IsRequired = true)]
        public String Sector;
        [DataMember(Name = "zone", IsRequired = true)]
        public String Zone;
        [DataMember(Name = "earliestDelivery", IsRequired = true)]
        public String EarliestDelivery;
        [DataMember(Name = "routing", IsRequired = true)]
        public int Routing;
        [DataMember(Name = "holiday", IsRequired = true)]
        public int Holiday;
        [DataMember(Name = "island", IsRequired = true)]
        public Boolean Island;

        public override string ToString()
        {
            return String.Format("Sector [{0}] Zone [{1}] Earliest delivery [{2}] Routing [{3}] Holiday [{4}] Island [{5}]",
                this.Sector,
                this.Zone,
                this.EarliestDelivery,
                this.Routing,
                this.Holiday,
                this.Island);
        }
    }

    [ComVisible(true)]
    [ClassInterface(ClassInterfaceType.None)]
    [DataContract]
    public class RoutingViaResult
    {
        [DataMember(Name = "sectors", IsRequired = true)]
        public String[] Sectors { get; set; }
        [DataMember(Name = "labelContent", IsRequired = true)]
        public String LabelContent { get; set; }

        public override string ToString()
        {
            return String.Format("Sectors [{0}] Label content [{1}]",
                String.Join(",", this.Sectors).TrimEnd(','),
                this.LabelContent);
        }
    }
    #endregion

    #region Proxy
    public class RoutingServiceProxy : ServiceClientProxy<IRoutingService>, IRoutingService
    {
        public RoutingServiceProxy(ChannelFactory<IRoutingService> factory)
            : base(factory) { }

        public RoutingResult find(string date, string country, string zip, string product)
        {
            return this.ServiceClient.find(date, country, zip, product);
        }

        public RoutingViaResult findVia(String date, String source_sector, String destination_sector)
        {
            return this.ServiceClient.findVia(date, source_sector, destination_sector);
        }
    }
    #endregion
}

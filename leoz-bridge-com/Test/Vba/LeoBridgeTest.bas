Attribute VB_Name = "LeoBridgeTest"
Sub RoutingRequestTest()
    Dim scf As New LeoBridge.ServiceClientFactory
    
    scf.BaseUri = "http://localhost:8080/"

    Dim rq As New LeoBridge.routingRequest

    Dim c As New LeoBridge.RoutingRequestParticipant
    Dim s As New LeoBridge.RoutingRequestParticipant

    ' Consignee
    c.Country = "DE"
    c.Zip = ""
    c.DesiredStation = "0"
    c.TimeFrom = "10:00"
    c.TimeTo = "12:00"
    ' Sender
    s.Country = "DE"
    s.Zip = "80331"
    s.DesiredStation = "0"
    s.TimeFrom = "10:00"
    s.TimeTo = "12:00"
    ' Request
    rq.SendDate = "2015-06-05"
    rq.DesiredDeliveryDate = "2015-06-02"
    rq.Weight = 2
    rq.Services = 0
    Set rq.consignee = c
    Set rq.sender = s

    ' Perform
    Dim result As LeoBridge.Routing
    Set result = scf.RoutingService.request(rq)
    If result.Error Is Nothing Then
        MsgBox ("No Error")
    End If
    
    MsgBox (result)
    Exit Sub
   
End Sub

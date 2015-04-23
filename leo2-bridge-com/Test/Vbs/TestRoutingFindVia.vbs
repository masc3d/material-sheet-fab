Dim oServiceClientFactory
Set oServiceClientFactory = WScript.CreateObject("LeoBridge.ServiceClientFactory")
oServiceClientFactory.BaseUri = "http://192.168.0.215:8080/leo2"

Set oRoutingService = oServiceClientFactory.RoutingService
Set oRoutingViaResult = oRoutingService.findVia("2015-02-01", "source", "destination")

MsgBox(oRoutingViaResult.ToString())

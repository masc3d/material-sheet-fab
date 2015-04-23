Dim oLeoBridge
Set oLeoBridge = WScript.CreateObject("LeoBridge.MessageQueue", "LeoBridge_")

oLeoBridge.Start
MsgBox("LeoBridge service running")

Function LeoBridge_OnMessage(oMessage)
	MsgBox("Event message [" + oMessage.ToString()) + "]"	
End Function



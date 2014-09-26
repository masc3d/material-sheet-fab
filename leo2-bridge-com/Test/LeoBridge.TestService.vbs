Dim oLeoBridge
Set oLeoBridge = WScript.CreateObject("LeoBridge.LeoBridge", "LeoBridge_")

oLeoBridge.Start
MsgBox("LeoBridge service running")

Function LeoBridge_OnMessage(sMessage)
	MsgBox("Event message [" + sMessage) + "]"
End Function



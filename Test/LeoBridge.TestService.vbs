Dim oLeoBridge
Set oLeoBridge = WScript.CreateObject("LeoBridge.LeoBridge", "LeoBridge_")

oLeoBridge.Start
MsgBox("Running")

Function LeoBridge_OnMessage(sMessage)
	MsgBox("Event message [" + sMessage) + "]"
End Function



Dim oLeoBridge
Set oLeoBridge = WScript.CreateObject("LeoBridge.Messaging", "LeoBridge_")

oLeoBridge.TestEvent "Test"

Function LeoBridge_OnMessage(sMessage)
	MsgBox("Event message [" + sMessage) + "]"
End Function



Dim oLeoBridge
Set oLeoBridge = WScript.CreateObject("LeoBridge.MessageQueue", "LeoBridge_")

Dim oMessage
Set oMessage = WScript.CreateObject("LeoBridge.Message")

oMessage.Put "view", "depot"
oMessage.Put "id", 800

oLeoBridge.SendMessage(oMessage)



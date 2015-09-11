Dim oLeoBridge
Set oLeoBridge = WScript.CreateObject("LeoBridge.MessageQueue", "LeoBridge_")

Dim oMessage
Set oMessage = WScript.CreateObject("LeoBridge.Message")

oMessage.Put "Text", "yo"
oMessage.Put "Date", Now()
oMessage.Put "Int", 34
oMessage.Put "Float", 32.4

MsgBox("going to send")
oLeoBridge.SendMessage(oMessage)

oLeoBridge.SendValue "Success"
MsgBox("sent1")
oLeoBridge.SendValue "Success2"
MsgBox("sent2")
oLeoBridge.SendValue "Success3"
MsgBox("sent3")


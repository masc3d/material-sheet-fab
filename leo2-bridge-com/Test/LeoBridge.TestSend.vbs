Dim oLeoBridge
Set oLeoBridge = WScript.CreateObject("LeoBridge.LeoBridge", "LeoBridge_")

oLeoBridge.SendMessage "Success"
MsgBox("sent1")
oLeoBridge.SendMessage "Success2"
MsgBox("sent2")
oLeoBridge.SendMessage "Success3"
MsgBox("sent3")



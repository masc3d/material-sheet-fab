masc20150415. 

COM interop
===========

* C# classes must have a parameterless constructor, 
otherwise class will not be exported as COM object (without notice)
* C# classes can be exported without implementing a ComVisible(true) attributed interface, 
but ONLY if they don't implement other interfaces (!)
* If C# classes implement interfaces, there MUST be a COM interface for all exported COM methods and properties 
and it has to be the first in the list to be implemented (!!) 
(eg "class Message : ISerializable, IMessage" will NOT work while "class Mesasge : IMessage, ISerializable" will)
* GUID attributes should NOT be set explicitly, even though nearly ALL examples found on the web will say otherwise.
Putting static GUIDs on classes closes the door to automatic versioning. 
When omitting the GUID attribute all ComVisible(true) attributed classes will have an automatically generated GUID which 
is dependent on the assembly version, so multiple versions of an assembly can be registered and used at the same time.

Deployment
==========

* The recommended and supported way is to use an install shield/vs setup which will register the .dll without codebase (static path) 
entries and put the assembly (dll) into the .NET global assembly cache
* Alternatively assemblies can be manually registered using regasm with the /codebase switch, which will add static paths to the registry.
In this manual deployment scenario it's vital that each version assembly/dll has a unique path,
meaning either version number in filename or even better: 
a unique folder for each assembly version, also containing copies of the register/unregister scripts for ease of maintenance


masc20150415. 

COM interop
===========

* C# classes and interfaces can be exposed using the attribute [ComVisible(true)]
* C# classes must have a parameterless constructor, 
  otherwise they will not be exposed as COM objects (without notice)
* C# classes may have to implement a referring (ComVisible) interface containing the methods/properties to expose
	* C# classes can be exposed without implementing a CON interface but _only_ if they don't implement other interfaces (!)
	* If C# classes implement other non-COM interfaces, the COM interface is mandatory
	  This interface has to be the first in line for classes to implement (!!) 
	  (eg "class Message : ISerializable, IMessage" will NOT work while "class Mesasge : IMessage, ISerializable" will)
* GUID attributes should _not_ be set explicitly, even though nearly _all_ examples found on the web will say otherwise.
  Putting static GUIDs on classes closes the door to automatic versioning. 
  When omitting the GUID attribute, all exposed classes will have an automatically generated GUID dependent on the assembly version
  so multiple versions of the assembly can be registered and used simultaneously

Deployment
==========

* The recommended and supported way is to use an install shield/vs setup which will register the .dll without codebase (static path) 
  entries and put the assembly (dll) into the .NET global assembly cache
* Alternatively assemblies can be manually registered using regasm with the /codebase switch, which will add static paths to the registry.
  In this manual deployment scenario it's vital that each version assembly/dll has a unique path,
  meaning either version number in filename or even better: 
  a unique folder for each assembly version, also containing copies of the register/unregister scripts for ease of maintenance


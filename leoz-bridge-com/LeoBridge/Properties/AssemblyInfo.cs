using System.Reflection;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
using System.ComponentModel;

// General Information about an assembly is controlled through the following 
// set of attributes. Change these attribute values to modify the information
// associated with an assembly.
[assembly: AssemblyTitle("LeoBridge")]
[assembly: AssemblyDescription("Leo Bridge " + AssemblyConstants.Version)]
[assembly: AssemblyConfiguration("desc3")]
[assembly: AssemblyCompany("")]
[assembly: AssemblyProduct("LeoBridge")]
[assembly: AssemblyCopyright("Copyright ©  2015")]
[assembly: AssemblyTrademark("")]
[assembly: AssemblyCulture("")]

// Setting ComVisible to false makes the types in this assembly not visible 
// to COM components.  If you need to access a type in this assembly from 
// COM, set the ComVisible attribute to true on that type.
[assembly: ComVisible(false)]
[assembly: Description("Hello")]

// The following GUID is for the ID of the typelib if this project is exposed to COM
// masc20150715. This GUID is unique for all versions of this component
// and should not be updated. OLE View displays different versions correctly,
// however VBA applications may not offer anything but the latest version
// in their references dialog. Still it's preferrabe to use just one GUID,
// for the sake of cleaner registry, less maintenance (no need to generate new GUIDs)
[assembly: Guid("1ABBAA03-EF6B-47F5-AFC2-D83684B2D88B")]

// Version information for an assembly consists of the following four values:
//
//      Major Version
//      Minor Version 
//      Build Number
//      Revision
//
// You can specify all the values or you can default the Build and Revision Numbers 
// by using the '*' as shown below:
// [assembly: AssemblyVersion("1.0.*")]
[assembly: AssemblyVersion(AssemblyConstants.Version + ".*")]
[assembly: AssemblyFileVersion(AssemblyConstants.Version + ".0.0")]

static internal class AssemblyConstants {
    internal const string Version = "1.2";
}

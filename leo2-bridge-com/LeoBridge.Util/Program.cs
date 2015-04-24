using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;
using System.IO;

using CLAP;
using System.Runtime.InteropServices;
using System.Security.Policy;

namespace LeoBridge.Util
{
    public class Program
    {
        [Empty, Help]
        public static void Help(string help)
        {
            // this is an empty handler that prints
            // the automatic help string to the console.
            Console.WriteLine(help);
        }

        [Verb]
        public static void Package(
            [Aliases("a")]
            [Description("Assembly to package")]
            [Required]
            String assemblyFilename,
            [Aliases("d")]
            [Description("Destination path")]
            [Required]
            String destinationPath,
            [Aliases("s")]
            [Description("Scripts path")]
            [Required]
            String scriptsPath)
        {
            Console.WriteLine("Packaging Assembly [{0}]", assemblyFilename);

            if (!File.Exists(assemblyFilename))
                throw new FileNotFoundException(assemblyFilename);

            if (!Directory.Exists(destinationPath))
                throw new DirectoryNotFoundException(destinationPath);

            Assembly a = Assembly.LoadFile(assemblyFilename);
            Version assemblyVersion = a.GetName().Version;

            Console.WriteLine("Assembly Version [{0}]", assemblyVersion);

            string releaseDir = Path.Combine(destinationPath, String.Format("Release-{0}", assemblyVersion.ToString(2)));

            string releaseFilename = Path.Combine(releaseDir, Path.GetFileName(assemblyFilename));

            Console.WriteLine("Release directory [{0}]", releaseDir);

            if (Directory.Exists(releaseDir))
                Directory.Delete(releaseDir, true);

            Directory.CreateDirectory(releaseDir);

            File.Copy(assemblyFilename, releaseFilename);

            if (Directory.Exists(scriptsPath))
            {
                foreach (String file in Directory.GetFiles(scriptsPath)) 
                {
                    File.Copy(file, Path.Combine(releaseDir, Path.GetFileName(file)));
                }
            }
        }

        [STAThread()]
        public static int Main(string[] args)
        {
            int result = 0;
            // using (new ConsoleWindow())
            {
                try
                {
                    Console.WriteLine("LeoBridge Util v{0}", Assembly.GetExecutingAssembly().GetName().Version);
                    Parser.Run<Program>(args);                
                    //App a = new App();            
                    //return a.Run(new MainWindow());
                }
                catch (Exception ex)
                {                    
                    Console.WriteLine(ex.Message);
                    result = 1;
                }
            }
            return result;
        }
    }
}

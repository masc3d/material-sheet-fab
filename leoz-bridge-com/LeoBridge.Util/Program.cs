using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Reflection;
using System.IO;
using System.Runtime.InteropServices;
using System.Security.Policy;

using Microsoft.Build.Utilities;

using Wsh = IWshRuntimeLibrary;
using CLAP;

namespace LeoBridge.Util
{
    public class Program
    {
        private static bool _pauseOnTermination = false;
        private static bool _pauseOnError = false;

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
                    Console.WriteLine(ex);
                    result = 1;

                    if (!_pauseOnTermination && _pauseOnError)
                        Console.ReadLine();
                }

                if (_pauseOnTermination)
                    Console.ReadLine();
            }
            return result;
        }

        /// <summary>
        /// Directory name of installation dir, used by install/uninstall commands
        /// </summary>
        private const String PATH_INSTALLED = "Installed";

        #region Package
        /// <summary>
        /// Package component
        /// </summary>
        /// <param name="assemblyFilename"></param>
        /// <param name="destinationPath"></param>
        [Verb]
        public static void Package(
            [Aliases("a")]
            [Description("Assembly to package")]
            [Required]
            String assemblyFilename,
            [Aliases("d")]
            [Description("Destination path")]
            [Required]
            String destinationPath)
        {
            Console.WriteLine("Packaging Assembly [{0}]", assemblyFilename);

            if (!File.Exists(assemblyFilename))
                throw new FileNotFoundException(assemblyFilename);

            if (!Directory.Exists(destinationPath))
                throw new DirectoryNotFoundException(destinationPath);

            // Load assembly
            Assembly a = Assembly.LoadFile(assemblyFilename);
            Version assemblyVersion = a.GetName().Version;
            Console.WriteLine("Assembly Version [{0}]", assemblyVersion);

            // Prepare release directory
            string releaseDir = Path.Combine(destinationPath, String.Format("Release-{0}", assemblyVersion.ToString(2)));
            Console.WriteLine("Release directory [{0}]", releaseDir);
            if (!Directory.Exists(releaseDir))
                Directory.CreateDirectory(releaseDir);

            // Copy assembly
            string releaseFilename = Path.Combine(releaseDir, Path.GetFileName(assemblyFilename));
            File.Copy(assemblyFilename, releaseFilename, true);

            // Copy util
            String utilFilename = new Uri(Assembly.GetExecutingAssembly().GetName().CodeBase).LocalPath;
            String utilPath = Path.GetDirectoryName(utilFilename);

            String releaseSetupDir = Path.Combine(releaseDir, "Setup");
            if (!Directory.Exists(releaseSetupDir))
                Directory.CreateDirectory(releaseSetupDir);

            String releaseSetupUtilFilename = Path.Combine(releaseSetupDir, Path.GetFileName(utilFilename));

            foreach (String filename in Directory.GetFiles(utilPath))
                if ((filename.EndsWith(".exe") || filename.EndsWith(".dll")) && !filename.EndsWith(".vshost.exe"))
                    File.Copy(filename, Path.Combine(releaseSetupDir, Path.GetFileName(filename)), true);

            // Remove existing shortcuts
            foreach (String filename in Directory.GetFiles(releaseDir, "*.lnk"))
                File.Delete(filename);

            // Create shortcuts
            Wsh.WshShell wShell = new Wsh.WshShell();
            Wsh.IWshShortcut shortcut = wShell.CreateShortcut(Path.Combine(releaseDir, "Setup-Install.lnk"));
            shortcut.TargetPath = releaseSetupUtilFilename;
            shortcut.Arguments = String.Format("install -a:\"{0}\" -pause-on-error", releaseFilename);
            shortcut.WorkingDirectory = releaseDir;
            shortcut.Save();

            shortcut = wShell.CreateShortcut(Path.Combine(releaseDir, "Setup-Uninstall.lnk"));
            shortcut.TargetPath = releaseSetupUtilFilename;
            shortcut.Arguments = String.Format("uninstall -a:\"{0}\" -pause-on-error", releaseFilename);
            shortcut.WorkingDirectory = releaseDir;
            shortcut.Save();
        }
        #endregion

        #region Install
        /// <summary>
        /// Install component
        /// </summary>
        /// <param name="assemblyFilename"></param>
        [Verb]
        public static void Install(
            [Aliases("a")]
            [Description("Assembly to install")]
            [Required]
            String assemblyFilename)
        {
            assemblyFilename = Path.GetFullPath(assemblyFilename);
            Console.WriteLine("Installing [{0}]", assemblyFilename);

            if (!File.Exists(assemblyFilename))
                throw new FileNotFoundException(assemblyFilename);

            Assembly a = Assembly.LoadFile(assemblyFilename);

            String installDirectory = Path.Combine(Path.GetDirectoryName(assemblyFilename), PATH_INSTALLED);
            String installFile = Path.Combine(installDirectory, Path.GetFileName(assemblyFilename));

            // Uninstall existing assembly
            if (File.Exists(installFile))
                Uninstall(assemblyFilename);

            Boolean installDirectoryExisted = Directory.Exists(installDirectory);
            try
            {
                if (!installDirectoryExisted)
                    Directory.CreateDirectory(installDirectory);

                File.Copy(assemblyFilename, installFile, false);

                Console.WriteLine("Registering win32 [{0}]", installFile);
                Execute(GetRegAsmPath(DotNetFrameworkArchitecture.Bitness32),
                     String.Format("/verbose /codebase /tlb:{0}-x86.tlb {1}", Path.GetFileNameWithoutExtension(installFile), Path.GetFileName(installFile)),
                     installDirectory);

                Execute(GetRegAsmPath(DotNetFrameworkArchitecture.Bitness32),
                 String.Format("/verbose /codebase /regfile:{0}-x86.reg {1}", Path.GetFileNameWithoutExtension(installFile), Path.GetFileName(installFile)),
                 installDirectory);

                if (Environment.Is64BitOperatingSystem)
                {
                    Console.WriteLine("Registering win64 [{0}]", installFile);
                    Execute(GetRegAsmPath(DotNetFrameworkArchitecture.Bitness64),
                         String.Format("/verbose /codebase /tlb:{0}-x64.tlb {1}", Path.GetFileNameWithoutExtension(installFile), Path.GetFileName(installFile)),
                         installDirectory);

                    Execute(GetRegAsmPath(DotNetFrameworkArchitecture.Bitness64),
                     String.Format("/verbose /codebase /regfile:{0}-x64.reg {1}", Path.GetFileNameWithoutExtension(installFile), Path.GetFileName(installFile)),
                     installDirectory);
                }
                Console.WriteLine("Installed successfully");
            }
            catch (Exception ex)
            {
                if (!installDirectoryExisted && Directory.Exists(installDirectory))
                    Directory.Delete(installDirectory, true);
                throw ex;
            }
        }
        #endregion

        #region Uninstall
        /// <summary>
        /// Uninstall component
        /// </summary>
        /// <param name="assemblyFilename"></param>
        [Verb]
        public static void Uninstall(
            [Aliases("a")]
            [Description("Assembly to uninstall")]
            [Required]
            String assemblyFilename)
        {
            assemblyFilename = Path.GetFullPath(assemblyFilename);
            Console.WriteLine("Uninstalling assembly [{0}]", assemblyFilename);

            if (!File.Exists(assemblyFilename))
                throw new FileNotFoundException(assemblyFilename);

            Assembly a = Assembly.LoadFile(assemblyFilename);

            String installDirectory = Path.Combine(Path.GetDirectoryName(assemblyFilename), PATH_INSTALLED);
            String installFile = Path.Combine(installDirectory, Path.GetFileName(assemblyFilename));

            if (File.Exists(installFile))
            {
                Console.WriteLine("Unregistering win32 [{0}]", installFile);
                Execute(GetRegAsmPath(DotNetFrameworkArchitecture.Bitness32),
                     String.Format("/verbose /codebase /unregister /tlb:{0}-x86.tlb {1}", Path.GetFileNameWithoutExtension(installFile), Path.GetFileName(installFile)),
                     installDirectory);

                if (Environment.Is64BitOperatingSystem)
                {
                    Console.WriteLine("Unregistering win64 [{0}]", installFile);
                    Execute(GetRegAsmPath(DotNetFrameworkArchitecture.Bitness64),
                    String.Format("/verbose /codebase /unregister /tlb:{0}-x64.tlb {1}", Path.GetFileNameWithoutExtension(installFile), Path.GetFileName(installFile)),
                    installDirectory);
                }
            }

            // Perform usage check on all files, eg. the tlb may still be in use
            foreach (String filename in Directory.GetFiles(installDirectory))
            {
                Console.WriteLine("Check File [{0}]", filename);
                if (IsFileLocked(filename))
                    throw new InvalidOperationException(String.Format("File [{0}] is still in use", filename));
            }

            if (Directory.Exists(installDirectory))
                Directory.Delete(installDirectory, true);

            Console.WriteLine("Uninstalled successfully");
        }
        #endregion

        /// <summary>
        /// Command line help
        /// </summary>
        /// <param name="help"></param>
        [Empty, Help]
        public static void Help(string help)
        {
            Console.WriteLine(help);
        }

        /// <summary>
        /// Command line pause switch
        /// </summary>
        [Global(Description = "Pause/wait for enter on termination")]
        public static void Pause()
        {
            _pauseOnTermination = true;
        }

        /// <summary>
        /// Command line pause on error switch
        /// </summary>
        [Global("pause-on-error", Description = "Pause/wait for enter on termination")]
        public static void PauseOnError()
        {
            _pauseOnError = true;
        }


        /// <summary>
        /// Execute command, redirecting standard output and throwing exception when exit code is non-zero
        /// </summary>
        /// <param name="executable">Path to executable</param>
        /// <param name="arguments">Arguments to pass</param>
        /// <param name="workingDirectory">Working directory</param>
        private static void Execute(String executable, String arguments, String workingDirectory)
        {
            ProcessStartInfo psi = new ProcessStartInfo(executable, arguments);

            psi.WorkingDirectory = workingDirectory;
            psi.UseShellExecute = false;
            psi.RedirectStandardOutput = true;

            Process p = Process.Start(psi);
            p.WaitForExit();
            if (p.ExitCode != 0)
                throw new InvalidOperationException(
                    String.Format("Process [{0} {1}] failed with error code {2}", executable, arguments, p.ExitCode));
        }

        /// <summary>
        /// Determine regasm path for specific architecture
        /// </summary>
        /// <param name="arch"></param>
        /// <returns></returns>
        private static String GetRegAsmPath(DotNetFrameworkArchitecture arch)
        {
            return Path.Combine(
                ToolLocationHelper.GetPathToDotNetFramework(TargetDotNetFrameworkVersion.Version40, arch),
                "regasm.exe");
        }

        /// <summary>
        /// Check if file is locked/in use
        /// </summary>
        /// <param name="fileName"></param>
        /// <returns></returns>
        private static bool IsFileLocked(String fileName)
        {
            FileStream stream = null;

            try
            {
                stream = new FileInfo(fileName).Open(FileMode.Open, FileAccess.ReadWrite, FileShare.None);
            }
            catch (IOException)
            {
                return true;
            }
            finally
            {
                if (stream != null)
                    stream.Close();
            }

            return false;
        }
    }
}

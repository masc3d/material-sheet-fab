using System;
using System.Windows.Forms;

namespace LeoBridge
{
    /// <summary>
    /// Windows forms control extensions
    /// </summary>
    /// <author>masc</author>
    public static class ControlExtensions
    {
        /// <summary>
        /// Generic extension method to support threadsafe windows forms access
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="control"></param>
        /// <param name="action"></param>
        public static void Invoke<T>(this T control, Action<T> action) where T : Control
        {
            if (control.InvokeRequired)
            {
                control.Invoke(new Action<T, Action<T>>(Invoke), new object[] { control, action });
            }
            else
            {
                action(control);
            }
        }

        /// <summary>
        /// Generic extension method to support threadsafe windows forms access
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="control"></param>
        /// <param name="action"></param>
        public static void BeginInvoke<T>(this T control, Action<T> action) where T : Control
        {
            if (control.InvokeRequired)
            {
                control.BeginInvoke(new Action<T, Action<T>>(BeginInvoke), new object[] { control, action });
            }
            else
            {
                action(control);
            }
        }
    }
}

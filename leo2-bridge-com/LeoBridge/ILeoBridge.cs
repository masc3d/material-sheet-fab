﻿using System;
using System.Runtime.InteropServices;

namespace LeoBridge
{
    /// <summary>
    /// Message service provider interface
    /// </summary>
    /// <author>masc</author>
    [Guid("067B41A1-F76D-4520-A873-232560AE0DA8")]    
    [ComVisible(true)]
    public interface ILeoBridge : IDisposable
    {
        void TestEvent(String testMessage);
        void Start();
        void Stop();
    }
}

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.deku.leo2.messaging.activemq;

import org.apache.activemq.command.BrokerInfo;
import org.apache.activemq.transport.TransportServerSupport;
import org.apache.activemq.transport.util.TextWireFormat;
import org.apache.activemq.transport.xstream.XStreamWireFormat;
import org.apache.activemq.util.ServiceStopper;

import javax.servlet.ServletContext;
import java.net.InetSocketAddress;
import java.net.URI;

/**
 * Empty Transport server for embedding into a servlet engine. All the
 * connections... are managed by the external engine, The httpTunnelServlet is
 * the link with AMQ
 *
 * @version $Revision$
 */
public class HttpExternalTransportServer extends TransportServerSupport {
    private TextWireFormat mWireFormat;
    ServletContext mContext;

    public HttpExternalTransportServer(URI uri, ServletContext context) {
        super(uri);
        mContext = context;
    }

    public void setBrokerInfo(BrokerInfo brokerInfo) {
    }

    /** Wire format **/
    public TextWireFormat getWireFormat() {
        if (mWireFormat == null) {
            mWireFormat = new XStreamWireFormat();
        }
        return mWireFormat;
    }

    protected void doStart() throws Exception {
        // only set the required attribute for HttpTunnelServlet usage
        mContext.setAttribute("acceptListener", getAcceptListener());
        mContext.setAttribute("wireFormat", getWireFormat());
    }

    protected void doStop(ServiceStopper stopper) throws Exception {

    }

    public InetSocketAddress getSocketAddress() {
        return null;
    }

    @Override
    public boolean isSslServer() {
        return false;
    }

}
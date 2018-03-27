/**
 * Identifyservice25Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.atypon.ringgoldClient.api;

public interface Identifyservice25Service extends javax.xml.rpc.Service {
    public java.lang.String getIdentifyPortAddress();

    public com.atypon.ringgoldClient.api.IdentifyPortType getIdentifyPort() throws javax.xml.rpc.ServiceException;

    public com.atypon.ringgoldClient.api.IdentifyPortType getIdentifyPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}

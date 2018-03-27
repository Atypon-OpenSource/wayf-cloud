/**
 * Identifyservice25ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.atypon.ringgoldClient.api;

public class Identifyservice25ServiceLocator extends org.apache.axis.client.Service implements com.atypon.ringgoldClient.api.Identifyservice25Service {

    public Identifyservice25ServiceLocator() {
    }


    public Identifyservice25ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public Identifyservice25ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for IdentifyPort
    private java.lang.String IdentifyPort_address = "https://idproduction.ringgold.com/identifyservice25.php";

    public java.lang.String getIdentifyPortAddress() {
        return IdentifyPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String IdentifyPortWSDDServiceName = "IdentifyPort";

    public java.lang.String getIdentifyPortWSDDServiceName() {
        return IdentifyPortWSDDServiceName;
    }

    public void setIdentifyPortWSDDServiceName(java.lang.String name) {
        IdentifyPortWSDDServiceName = name;
    }

    public com.atypon.ringgoldClient.api.IdentifyPortType getIdentifyPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(IdentifyPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getIdentifyPort(endpoint);
    }

    public com.atypon.ringgoldClient.api.IdentifyPortType getIdentifyPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.atypon.ringgoldClient.api.IdentifyBindingStub _stub = new com.atypon.ringgoldClient.api.IdentifyBindingStub(portAddress, this);
            _stub.setPortName(getIdentifyPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setIdentifyPortEndpointAddress(java.lang.String address) {
        IdentifyPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.atypon.ringgoldClient.api.IdentifyPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.atypon.ringgoldClient.api.IdentifyBindingStub _stub = new com.atypon.ringgoldClient.api.IdentifyBindingStub(new java.net.URL(IdentifyPort_address), this);
                _stub.setPortName(getIdentifyPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("IdentifyPort".equals(inputPortName)) {
            return getIdentifyPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.wsdl", "identifyservice25Service");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.wsdl", "IdentifyPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("IdentifyPort".equals(portName)) {
            setIdentifyPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}

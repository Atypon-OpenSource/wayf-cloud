/**
 * IdentifyBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.atypon.ringgoldClient.api;

public class IdentifyBindingStub extends org.apache.axis.client.Stub implements com.atypon.ringgoldClient.api.IdentifyPortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[4];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getInstitution");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "getInstitutionRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">getInstitutionRequest"), com.atypon.ringgoldClient.api.GetInstitutionRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">getInstitutionResponse"));
        oper.setReturnClass(com.atypon.ringgoldClient.api.GetInstitutionResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "getInstitutionResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("findInstitutions");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "findInstitutionsRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">findInstitutionsRequest"), com.atypon.ringgoldClient.api.FindInstitutionsRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">findInstitutionsResponse"));
        oper.setReturnClass(com.atypon.ringgoldClient.api.FindInstitutionsResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "findInstitutionsResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("findInstitutionsByKeywords");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "findInstitutionsByKeywordsRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">findInstitutionsByKeywordsRequest"), com.atypon.ringgoldClient.api.FindInstitutionsByKeywordsRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">findInstitutionsByKeywordsResponse"));
        oper.setReturnClass(com.atypon.ringgoldClient.api.FindInstitutionsByKeywordsResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "findInstitutionsByKeywordsResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getInstitutionFamily");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "getInstitutionFamilyRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">getInstitutionFamilyRequest"), com.atypon.ringgoldClient.api.GetInstitutionFamilyRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">getInstitutionFamilyResponse"));
        oper.setReturnClass(com.atypon.ringgoldClient.api.GetInstitutionFamilyResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "getInstitutionFamilyResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[3] = oper;

    }

    public IdentifyBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public IdentifyBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public IdentifyBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">altNames");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.AltNameType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "altName");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">classifications");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.ClassificationType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "classification");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">family");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.FamilyType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "familyMember");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">findInstitutionsByKeywordsRequest");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.FindInstitutionsByKeywordsRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">findInstitutionsByKeywordsResponse");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.FindInstitutionsByKeywordsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">findInstitutionsRequest");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.FindInstitutionsRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">findInstitutionsResponse");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.FindInstitutionsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">getInstitutionFamilyRequest");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.GetInstitutionFamilyRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">getInstitutionFamilyResponse");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.GetInstitutionFamilyResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">getInstitutionRequest");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.GetInstitutionRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">getInstitutionResponse");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.GetInstitutionResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">institutions");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.InstitutionType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "institution");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">parents");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.FamilyType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "familyMember");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">urls");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.UrlType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "urlEntry");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "altNameType");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.AltNameType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "classificationType");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.ClassificationType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "familyType");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.FamilyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "institutionType");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.InstitutionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "ofrOptions");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.OfrOptions.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "placeType");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.PlaceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "urlType");
            cachedSerQNames.add(qName);
            cls = com.atypon.ringgoldClient.api.UrlType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public com.atypon.ringgoldClient.api.GetInstitutionResponse getInstitution(com.atypon.ringgoldClient.api.GetInstitutionRequest getInstitutionRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://idproduction.ringgold.com/identifyservice25.wsdl/getInstitution");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.wsdl", "getInstitution"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {getInstitutionRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.atypon.ringgoldClient.api.GetInstitutionResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.atypon.ringgoldClient.api.GetInstitutionResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.atypon.ringgoldClient.api.GetInstitutionResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.atypon.ringgoldClient.api.FindInstitutionsResponse findInstitutions(com.atypon.ringgoldClient.api.FindInstitutionsRequest findInstitutionsRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://idproduction.ringgold.com/identifyservice25.wsdl/findInstitutions");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.wsdl", "findInstitutions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {findInstitutionsRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.atypon.ringgoldClient.api.FindInstitutionsResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.atypon.ringgoldClient.api.FindInstitutionsResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.atypon.ringgoldClient.api.FindInstitutionsResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.atypon.ringgoldClient.api.FindInstitutionsByKeywordsResponse findInstitutionsByKeywords(com.atypon.ringgoldClient.api.FindInstitutionsByKeywordsRequest findInstitutionsByKeywordsRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://idproduction.ringgold.com/identifyservice25.wsdl/findInstitutionsByKeywords");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.wsdl", "findInstitutionsByKeywords"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {findInstitutionsByKeywordsRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.atypon.ringgoldClient.api.FindInstitutionsByKeywordsResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.atypon.ringgoldClient.api.FindInstitutionsByKeywordsResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.atypon.ringgoldClient.api.FindInstitutionsByKeywordsResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.atypon.ringgoldClient.api.GetInstitutionFamilyResponse getInstitutionFamily(com.atypon.ringgoldClient.api.GetInstitutionFamilyRequest getInstitutionFamilyRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://idproduction.ringgold.com/identifyservice25.wsdl/getInstitutionFamily");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.wsdl", "getInstitutionFamily"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {getInstitutionFamilyRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.atypon.ringgoldClient.api.GetInstitutionFamilyResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.atypon.ringgoldClient.api.GetInstitutionFamilyResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.atypon.ringgoldClient.api.GetInstitutionFamilyResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}

/**
 * GetInstitutionResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.atypon.ringgoldClient.api;

public class GetInstitutionResponse  implements java.io.Serializable {
    private java.lang.String licenseKey;

    private java.lang.String responseStatus;

    private java.lang.String errorMessage;

    private int identifier;

    private com.atypon.ringgoldClient.api.InstitutionType institution;

    public GetInstitutionResponse() {
    }

    public GetInstitutionResponse(
           java.lang.String licenseKey,
           java.lang.String responseStatus,
           java.lang.String errorMessage,
           int identifier,
           com.atypon.ringgoldClient.api.InstitutionType institution) {
           this.licenseKey = licenseKey;
           this.responseStatus = responseStatus;
           this.errorMessage = errorMessage;
           this.identifier = identifier;
           this.institution = institution;
    }


    /**
     * Gets the licenseKey value for this GetInstitutionResponse.
     * 
     * @return licenseKey
     */
    public java.lang.String getLicenseKey() {
        return licenseKey;
    }


    /**
     * Sets the licenseKey value for this GetInstitutionResponse.
     * 
     * @param licenseKey
     */
    public void setLicenseKey(java.lang.String licenseKey) {
        this.licenseKey = licenseKey;
    }


    /**
     * Gets the responseStatus value for this GetInstitutionResponse.
     * 
     * @return responseStatus
     */
    public java.lang.String getResponseStatus() {
        return responseStatus;
    }


    /**
     * Sets the responseStatus value for this GetInstitutionResponse.
     * 
     * @param responseStatus
     */
    public void setResponseStatus(java.lang.String responseStatus) {
        this.responseStatus = responseStatus;
    }


    /**
     * Gets the errorMessage value for this GetInstitutionResponse.
     * 
     * @return errorMessage
     */
    public java.lang.String getErrorMessage() {
        return errorMessage;
    }


    /**
     * Sets the errorMessage value for this GetInstitutionResponse.
     * 
     * @param errorMessage
     */
    public void setErrorMessage(java.lang.String errorMessage) {
        this.errorMessage = errorMessage;
    }


    /**
     * Gets the identifier value for this GetInstitutionResponse.
     * 
     * @return identifier
     */
    public int getIdentifier() {
        return identifier;
    }


    /**
     * Sets the identifier value for this GetInstitutionResponse.
     * 
     * @param identifier
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }


    /**
     * Gets the institution value for this GetInstitutionResponse.
     * 
     * @return institution
     */
    public com.atypon.ringgoldClient.api.InstitutionType getInstitution() {
        return institution;
    }


    /**
     * Sets the institution value for this GetInstitutionResponse.
     * 
     * @param institution
     */
    public void setInstitution(com.atypon.ringgoldClient.api.InstitutionType institution) {
        this.institution = institution;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GetInstitutionResponse)) return false;
        GetInstitutionResponse other = (GetInstitutionResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.licenseKey==null && other.getLicenseKey()==null) || 
             (this.licenseKey!=null &&
              this.licenseKey.equals(other.getLicenseKey()))) &&
            ((this.responseStatus==null && other.getResponseStatus()==null) || 
             (this.responseStatus!=null &&
              this.responseStatus.equals(other.getResponseStatus()))) &&
            ((this.errorMessage==null && other.getErrorMessage()==null) || 
             (this.errorMessage!=null &&
              this.errorMessage.equals(other.getErrorMessage()))) &&
            this.identifier == other.getIdentifier() &&
            ((this.institution==null && other.getInstitution()==null) || 
             (this.institution!=null &&
              this.institution.equals(other.getInstitution())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getLicenseKey() != null) {
            _hashCode += getLicenseKey().hashCode();
        }
        if (getResponseStatus() != null) {
            _hashCode += getResponseStatus().hashCode();
        }
        if (getErrorMessage() != null) {
            _hashCode += getErrorMessage().hashCode();
        }
        _hashCode += getIdentifier();
        if (getInstitution() != null) {
            _hashCode += getInstitution().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GetInstitutionResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">getInstitutionResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("licenseKey");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "licenseKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("responseStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "responseStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "errorMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identifier");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "identifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("institution");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "institution"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "institutionType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

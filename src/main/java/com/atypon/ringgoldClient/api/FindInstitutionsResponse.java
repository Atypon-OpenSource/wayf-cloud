/**
 * FindInstitutionsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.atypon.ringgoldClient.api;

public class FindInstitutionsResponse  implements java.io.Serializable {
    private java.lang.String licenseKey;

    private java.lang.String searchName;

    private java.lang.String city;

    private java.lang.String state;

    private java.lang.String postCode;

    private java.lang.String country;

    private java.lang.String responseStatus;

    private java.lang.String errorMessage;

    private int resultCount;

    private int totalCount;

    private com.atypon.ringgoldClient.api.InstitutionType[] institutions;

    private int startItem;

    private int pageFactor;

    private com.atypon.ringgoldClient.api.OfrOptions ofrOption;

    public FindInstitutionsResponse() {
    }

    public FindInstitutionsResponse(
           java.lang.String licenseKey,
           java.lang.String searchName,
           java.lang.String city,
           java.lang.String state,
           java.lang.String postCode,
           java.lang.String country,
           java.lang.String responseStatus,
           java.lang.String errorMessage,
           int resultCount,
           int totalCount,
           com.atypon.ringgoldClient.api.InstitutionType[] institutions,
           int startItem,
           int pageFactor,
           com.atypon.ringgoldClient.api.OfrOptions ofrOption) {
           this.licenseKey = licenseKey;
           this.searchName = searchName;
           this.city = city;
           this.state = state;
           this.postCode = postCode;
           this.country = country;
           this.responseStatus = responseStatus;
           this.errorMessage = errorMessage;
           this.resultCount = resultCount;
           this.totalCount = totalCount;
           this.institutions = institutions;
           this.startItem = startItem;
           this.pageFactor = pageFactor;
           this.ofrOption = ofrOption;
    }


    /**
     * Gets the licenseKey value for this FindInstitutionsResponse.
     * 
     * @return licenseKey
     */
    public java.lang.String getLicenseKey() {
        return licenseKey;
    }


    /**
     * Sets the licenseKey value for this FindInstitutionsResponse.
     * 
     * @param licenseKey
     */
    public void setLicenseKey(java.lang.String licenseKey) {
        this.licenseKey = licenseKey;
    }


    /**
     * Gets the searchName value for this FindInstitutionsResponse.
     * 
     * @return searchName
     */
    public java.lang.String getSearchName() {
        return searchName;
    }


    /**
     * Sets the searchName value for this FindInstitutionsResponse.
     * 
     * @param searchName
     */
    public void setSearchName(java.lang.String searchName) {
        this.searchName = searchName;
    }


    /**
     * Gets the city value for this FindInstitutionsResponse.
     * 
     * @return city
     */
    public java.lang.String getCity() {
        return city;
    }


    /**
     * Sets the city value for this FindInstitutionsResponse.
     * 
     * @param city
     */
    public void setCity(java.lang.String city) {
        this.city = city;
    }


    /**
     * Gets the state value for this FindInstitutionsResponse.
     * 
     * @return state
     */
    public java.lang.String getState() {
        return state;
    }


    /**
     * Sets the state value for this FindInstitutionsResponse.
     * 
     * @param state
     */
    public void setState(java.lang.String state) {
        this.state = state;
    }


    /**
     * Gets the postCode value for this FindInstitutionsResponse.
     * 
     * @return postCode
     */
    public java.lang.String getPostCode() {
        return postCode;
    }


    /**
     * Sets the postCode value for this FindInstitutionsResponse.
     * 
     * @param postCode
     */
    public void setPostCode(java.lang.String postCode) {
        this.postCode = postCode;
    }


    /**
     * Gets the country value for this FindInstitutionsResponse.
     * 
     * @return country
     */
    public java.lang.String getCountry() {
        return country;
    }


    /**
     * Sets the country value for this FindInstitutionsResponse.
     * 
     * @param country
     */
    public void setCountry(java.lang.String country) {
        this.country = country;
    }


    /**
     * Gets the responseStatus value for this FindInstitutionsResponse.
     * 
     * @return responseStatus
     */
    public java.lang.String getResponseStatus() {
        return responseStatus;
    }


    /**
     * Sets the responseStatus value for this FindInstitutionsResponse.
     * 
     * @param responseStatus
     */
    public void setResponseStatus(java.lang.String responseStatus) {
        this.responseStatus = responseStatus;
    }


    /**
     * Gets the errorMessage value for this FindInstitutionsResponse.
     * 
     * @return errorMessage
     */
    public java.lang.String getErrorMessage() {
        return errorMessage;
    }


    /**
     * Sets the errorMessage value for this FindInstitutionsResponse.
     * 
     * @param errorMessage
     */
    public void setErrorMessage(java.lang.String errorMessage) {
        this.errorMessage = errorMessage;
    }


    /**
     * Gets the resultCount value for this FindInstitutionsResponse.
     * 
     * @return resultCount
     */
    public int getResultCount() {
        return resultCount;
    }


    /**
     * Sets the resultCount value for this FindInstitutionsResponse.
     * 
     * @param resultCount
     */
    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }


    /**
     * Gets the totalCount value for this FindInstitutionsResponse.
     * 
     * @return totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }


    /**
     * Sets the totalCount value for this FindInstitutionsResponse.
     * 
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }


    /**
     * Gets the institutions value for this FindInstitutionsResponse.
     * 
     * @return institutions
     */
    public com.atypon.ringgoldClient.api.InstitutionType[] getInstitutions() {
        return institutions;
    }


    /**
     * Sets the institutions value for this FindInstitutionsResponse.
     * 
     * @param institutions
     */
    public void setInstitutions(com.atypon.ringgoldClient.api.InstitutionType[] institutions) {
        this.institutions = institutions;
    }


    /**
     * Gets the startItem value for this FindInstitutionsResponse.
     * 
     * @return startItem
     */
    public int getStartItem() {
        return startItem;
    }


    /**
     * Sets the startItem value for this FindInstitutionsResponse.
     * 
     * @param startItem
     */
    public void setStartItem(int startItem) {
        this.startItem = startItem;
    }


    /**
     * Gets the pageFactor value for this FindInstitutionsResponse.
     * 
     * @return pageFactor
     */
    public int getPageFactor() {
        return pageFactor;
    }


    /**
     * Sets the pageFactor value for this FindInstitutionsResponse.
     * 
     * @param pageFactor
     */
    public void setPageFactor(int pageFactor) {
        this.pageFactor = pageFactor;
    }


    /**
     * Gets the ofrOption value for this FindInstitutionsResponse.
     * 
     * @return ofrOption
     */
    public com.atypon.ringgoldClient.api.OfrOptions getOfrOption() {
        return ofrOption;
    }


    /**
     * Sets the ofrOption value for this FindInstitutionsResponse.
     * 
     * @param ofrOption
     */
    public void setOfrOption(com.atypon.ringgoldClient.api.OfrOptions ofrOption) {
        this.ofrOption = ofrOption;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FindInstitutionsResponse)) return false;
        FindInstitutionsResponse other = (FindInstitutionsResponse) obj;
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
            ((this.searchName==null && other.getSearchName()==null) || 
             (this.searchName!=null &&
              this.searchName.equals(other.getSearchName()))) &&
            ((this.city==null && other.getCity()==null) || 
             (this.city!=null &&
              this.city.equals(other.getCity()))) &&
            ((this.state==null && other.getState()==null) || 
             (this.state!=null &&
              this.state.equals(other.getState()))) &&
            ((this.postCode==null && other.getPostCode()==null) || 
             (this.postCode!=null &&
              this.postCode.equals(other.getPostCode()))) &&
            ((this.country==null && other.getCountry()==null) || 
             (this.country!=null &&
              this.country.equals(other.getCountry()))) &&
            ((this.responseStatus==null && other.getResponseStatus()==null) || 
             (this.responseStatus!=null &&
              this.responseStatus.equals(other.getResponseStatus()))) &&
            ((this.errorMessage==null && other.getErrorMessage()==null) || 
             (this.errorMessage!=null &&
              this.errorMessage.equals(other.getErrorMessage()))) &&
            this.resultCount == other.getResultCount() &&
            this.totalCount == other.getTotalCount() &&
            ((this.institutions==null && other.getInstitutions()==null) || 
             (this.institutions!=null &&
              java.util.Arrays.equals(this.institutions, other.getInstitutions()))) &&
            this.startItem == other.getStartItem() &&
            this.pageFactor == other.getPageFactor() &&
            ((this.ofrOption==null && other.getOfrOption()==null) || 
             (this.ofrOption!=null &&
              this.ofrOption.equals(other.getOfrOption())));
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
        if (getSearchName() != null) {
            _hashCode += getSearchName().hashCode();
        }
        if (getCity() != null) {
            _hashCode += getCity().hashCode();
        }
        if (getState() != null) {
            _hashCode += getState().hashCode();
        }
        if (getPostCode() != null) {
            _hashCode += getPostCode().hashCode();
        }
        if (getCountry() != null) {
            _hashCode += getCountry().hashCode();
        }
        if (getResponseStatus() != null) {
            _hashCode += getResponseStatus().hashCode();
        }
        if (getErrorMessage() != null) {
            _hashCode += getErrorMessage().hashCode();
        }
        _hashCode += getResultCount();
        _hashCode += getTotalCount();
        if (getInstitutions() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInstitutions());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInstitutions(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getStartItem();
        _hashCode += getPageFactor();
        if (getOfrOption() != null) {
            _hashCode += getOfrOption().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FindInstitutionsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">findInstitutionsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("licenseKey");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "licenseKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchName");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "searchName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("city");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "city"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("state");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "state"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("postCode");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "postCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("country");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "country"));
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
        elemField.setFieldName("resultCount");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "resultCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalCount");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "totalCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("institutions");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "institutions"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">institutions"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startItem");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "startItem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pageFactor");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "pageFactor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ofrOption");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "ofrOption"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "ofrOptions"));
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

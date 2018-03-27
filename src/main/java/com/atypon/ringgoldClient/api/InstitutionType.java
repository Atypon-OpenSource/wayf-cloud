/**
 * InstitutionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.atypon.ringgoldClient.api;

public class InstitutionType  implements java.io.Serializable {
    private int identifier;

    private java.lang.String name;

    private java.lang.String city;

    private java.lang.String state;

    private java.lang.String postCode;

    private java.lang.String country;

    private java.lang.String rgTier;

    private java.lang.String rgType;

    private java.lang.String timestamp;

    private com.atypon.ringgoldClient.api.FamilyType[] parents;

    private int beds;

    private int doctors;

    private int hospitals;

    private int libraries;

    private int schools;

    private int size;

    private int staff;

    private java.lang.String extidATHENS;

    private java.lang.String extidIPEDS;

    private java.lang.String extidISNI;

    private java.lang.String extidNACS;

    private java.lang.String extidNCES;

    private java.lang.String extidOFR;

    private java.math.BigDecimal matchScore;

    private com.atypon.ringgoldClient.api.AltNameType[] altNames;

    private com.atypon.ringgoldClient.api.UrlType[] urls;

    private com.atypon.ringgoldClient.api.ClassificationType[] classifications;

    private com.atypon.ringgoldClient.api.PlaceType place;

    public InstitutionType() {
    }

    public InstitutionType(
           int identifier,
           java.lang.String name,
           java.lang.String city,
           java.lang.String state,
           java.lang.String postCode,
           java.lang.String country,
           java.lang.String rgTier,
           java.lang.String rgType,
           java.lang.String timestamp,
           com.atypon.ringgoldClient.api.FamilyType[] parents,
           int beds,
           int doctors,
           int hospitals,
           int libraries,
           int schools,
           int size,
           int staff,
           java.lang.String extidATHENS,
           java.lang.String extidIPEDS,
           java.lang.String extidISNI,
           java.lang.String extidNACS,
           java.lang.String extidNCES,
           java.lang.String extidOFR,
           java.math.BigDecimal matchScore,
           com.atypon.ringgoldClient.api.AltNameType[] altNames,
           com.atypon.ringgoldClient.api.UrlType[] urls,
           com.atypon.ringgoldClient.api.ClassificationType[] classifications,
           com.atypon.ringgoldClient.api.PlaceType place) {
           this.identifier = identifier;
           this.name = name;
           this.city = city;
           this.state = state;
           this.postCode = postCode;
           this.country = country;
           this.rgTier = rgTier;
           this.rgType = rgType;
           this.timestamp = timestamp;
           this.parents = parents;
           this.beds = beds;
           this.doctors = doctors;
           this.hospitals = hospitals;
           this.libraries = libraries;
           this.schools = schools;
           this.size = size;
           this.staff = staff;
           this.extidATHENS = extidATHENS;
           this.extidIPEDS = extidIPEDS;
           this.extidISNI = extidISNI;
           this.extidNACS = extidNACS;
           this.extidNCES = extidNCES;
           this.extidOFR = extidOFR;
           this.matchScore = matchScore;
           this.altNames = altNames;
           this.urls = urls;
           this.classifications = classifications;
           this.place = place;
    }


    /**
     * Gets the identifier value for this InstitutionType.
     * 
     * @return identifier
     */
    public int getIdentifier() {
        return identifier;
    }


    /**
     * Sets the identifier value for this InstitutionType.
     * 
     * @param identifier
     */
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }


    /**
     * Gets the name value for this InstitutionType.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this InstitutionType.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the city value for this InstitutionType.
     * 
     * @return city
     */
    public java.lang.String getCity() {
        return city;
    }


    /**
     * Sets the city value for this InstitutionType.
     * 
     * @param city
     */
    public void setCity(java.lang.String city) {
        this.city = city;
    }


    /**
     * Gets the state value for this InstitutionType.
     * 
     * @return state
     */
    public java.lang.String getState() {
        return state;
    }


    /**
     * Sets the state value for this InstitutionType.
     * 
     * @param state
     */
    public void setState(java.lang.String state) {
        this.state = state;
    }


    /**
     * Gets the postCode value for this InstitutionType.
     * 
     * @return postCode
     */
    public java.lang.String getPostCode() {
        return postCode;
    }


    /**
     * Sets the postCode value for this InstitutionType.
     * 
     * @param postCode
     */
    public void setPostCode(java.lang.String postCode) {
        this.postCode = postCode;
    }


    /**
     * Gets the country value for this InstitutionType.
     * 
     * @return country
     */
    public java.lang.String getCountry() {
        return country;
    }


    /**
     * Sets the country value for this InstitutionType.
     * 
     * @param country
     */
    public void setCountry(java.lang.String country) {
        this.country = country;
    }


    /**
     * Gets the rgTier value for this InstitutionType.
     * 
     * @return rgTier
     */
    public java.lang.String getRgTier() {
        return rgTier;
    }


    /**
     * Sets the rgTier value for this InstitutionType.
     * 
     * @param rgTier
     */
    public void setRgTier(java.lang.String rgTier) {
        this.rgTier = rgTier;
    }


    /**
     * Gets the rgType value for this InstitutionType.
     * 
     * @return rgType
     */
    public java.lang.String getRgType() {
        return rgType;
    }


    /**
     * Sets the rgType value for this InstitutionType.
     * 
     * @param rgType
     */
    public void setRgType(java.lang.String rgType) {
        this.rgType = rgType;
    }


    /**
     * Gets the timestamp value for this InstitutionType.
     * 
     * @return timestamp
     */
    public java.lang.String getTimestamp() {
        return timestamp;
    }


    /**
     * Sets the timestamp value for this InstitutionType.
     * 
     * @param timestamp
     */
    public void setTimestamp(java.lang.String timestamp) {
        this.timestamp = timestamp;
    }


    /**
     * Gets the parents value for this InstitutionType.
     * 
     * @return parents
     */
    public com.atypon.ringgoldClient.api.FamilyType[] getParents() {
        return parents;
    }


    /**
     * Sets the parents value for this InstitutionType.
     * 
     * @param parents
     */
    public void setParents(com.atypon.ringgoldClient.api.FamilyType[] parents) {
        this.parents = parents;
    }


    /**
     * Gets the beds value for this InstitutionType.
     * 
     * @return beds
     */
    public int getBeds() {
        return beds;
    }


    /**
     * Sets the beds value for this InstitutionType.
     * 
     * @param beds
     */
    public void setBeds(int beds) {
        this.beds = beds;
    }


    /**
     * Gets the doctors value for this InstitutionType.
     * 
     * @return doctors
     */
    public int getDoctors() {
        return doctors;
    }


    /**
     * Sets the doctors value for this InstitutionType.
     * 
     * @param doctors
     */
    public void setDoctors(int doctors) {
        this.doctors = doctors;
    }


    /**
     * Gets the hospitals value for this InstitutionType.
     * 
     * @return hospitals
     */
    public int getHospitals() {
        return hospitals;
    }


    /**
     * Sets the hospitals value for this InstitutionType.
     * 
     * @param hospitals
     */
    public void setHospitals(int hospitals) {
        this.hospitals = hospitals;
    }


    /**
     * Gets the libraries value for this InstitutionType.
     * 
     * @return libraries
     */
    public int getLibraries() {
        return libraries;
    }


    /**
     * Sets the libraries value for this InstitutionType.
     * 
     * @param libraries
     */
    public void setLibraries(int libraries) {
        this.libraries = libraries;
    }


    /**
     * Gets the schools value for this InstitutionType.
     * 
     * @return schools
     */
    public int getSchools() {
        return schools;
    }


    /**
     * Sets the schools value for this InstitutionType.
     * 
     * @param schools
     */
    public void setSchools(int schools) {
        this.schools = schools;
    }


    /**
     * Gets the size value for this InstitutionType.
     * 
     * @return size
     */
    public int getSize() {
        return size;
    }


    /**
     * Sets the size value for this InstitutionType.
     * 
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
    }


    /**
     * Gets the staff value for this InstitutionType.
     * 
     * @return staff
     */
    public int getStaff() {
        return staff;
    }


    /**
     * Sets the staff value for this InstitutionType.
     * 
     * @param staff
     */
    public void setStaff(int staff) {
        this.staff = staff;
    }


    /**
     * Gets the extidATHENS value for this InstitutionType.
     * 
     * @return extidATHENS
     */
    public java.lang.String getExtidATHENS() {
        return extidATHENS;
    }


    /**
     * Sets the extidATHENS value for this InstitutionType.
     * 
     * @param extidATHENS
     */
    public void setExtidATHENS(java.lang.String extidATHENS) {
        this.extidATHENS = extidATHENS;
    }


    /**
     * Gets the extidIPEDS value for this InstitutionType.
     * 
     * @return extidIPEDS
     */
    public java.lang.String getExtidIPEDS() {
        return extidIPEDS;
    }


    /**
     * Sets the extidIPEDS value for this InstitutionType.
     * 
     * @param extidIPEDS
     */
    public void setExtidIPEDS(java.lang.String extidIPEDS) {
        this.extidIPEDS = extidIPEDS;
    }


    /**
     * Gets the extidISNI value for this InstitutionType.
     * 
     * @return extidISNI
     */
    public java.lang.String getExtidISNI() {
        return extidISNI;
    }


    /**
     * Sets the extidISNI value for this InstitutionType.
     * 
     * @param extidISNI
     */
    public void setExtidISNI(java.lang.String extidISNI) {
        this.extidISNI = extidISNI;
    }


    /**
     * Gets the extidNACS value for this InstitutionType.
     * 
     * @return extidNACS
     */
    public java.lang.String getExtidNACS() {
        return extidNACS;
    }


    /**
     * Sets the extidNACS value for this InstitutionType.
     * 
     * @param extidNACS
     */
    public void setExtidNACS(java.lang.String extidNACS) {
        this.extidNACS = extidNACS;
    }


    /**
     * Gets the extidNCES value for this InstitutionType.
     * 
     * @return extidNCES
     */
    public java.lang.String getExtidNCES() {
        return extidNCES;
    }


    /**
     * Sets the extidNCES value for this InstitutionType.
     * 
     * @param extidNCES
     */
    public void setExtidNCES(java.lang.String extidNCES) {
        this.extidNCES = extidNCES;
    }


    /**
     * Gets the extidOFR value for this InstitutionType.
     * 
     * @return extidOFR
     */
    public java.lang.String getExtidOFR() {
        return extidOFR;
    }


    /**
     * Sets the extidOFR value for this InstitutionType.
     * 
     * @param extidOFR
     */
    public void setExtidOFR(java.lang.String extidOFR) {
        this.extidOFR = extidOFR;
    }


    /**
     * Gets the matchScore value for this InstitutionType.
     * 
     * @return matchScore
     */
    public java.math.BigDecimal getMatchScore() {
        return matchScore;
    }


    /**
     * Sets the matchScore value for this InstitutionType.
     * 
     * @param matchScore
     */
    public void setMatchScore(java.math.BigDecimal matchScore) {
        this.matchScore = matchScore;
    }


    /**
     * Gets the altNames value for this InstitutionType.
     * 
     * @return altNames
     */
    public com.atypon.ringgoldClient.api.AltNameType[] getAltNames() {
        return altNames;
    }


    /**
     * Sets the altNames value for this InstitutionType.
     * 
     * @param altNames
     */
    public void setAltNames(com.atypon.ringgoldClient.api.AltNameType[] altNames) {
        this.altNames = altNames;
    }


    /**
     * Gets the urls value for this InstitutionType.
     * 
     * @return urls
     */
    public com.atypon.ringgoldClient.api.UrlType[] getUrls() {
        return urls;
    }


    /**
     * Sets the urls value for this InstitutionType.
     * 
     * @param urls
     */
    public void setUrls(com.atypon.ringgoldClient.api.UrlType[] urls) {
        this.urls = urls;
    }


    /**
     * Gets the classifications value for this InstitutionType.
     * 
     * @return classifications
     */
    public com.atypon.ringgoldClient.api.ClassificationType[] getClassifications() {
        return classifications;
    }


    /**
     * Sets the classifications value for this InstitutionType.
     * 
     * @param classifications
     */
    public void setClassifications(com.atypon.ringgoldClient.api.ClassificationType[] classifications) {
        this.classifications = classifications;
    }


    /**
     * Gets the place value for this InstitutionType.
     * 
     * @return place
     */
    public com.atypon.ringgoldClient.api.PlaceType getPlace() {
        return place;
    }


    /**
     * Sets the place value for this InstitutionType.
     * 
     * @param place
     */
    public void setPlace(com.atypon.ringgoldClient.api.PlaceType place) {
        this.place = place;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InstitutionType)) return false;
        InstitutionType other = (InstitutionType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.identifier == other.getIdentifier() &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
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
            ((this.rgTier==null && other.getRgTier()==null) || 
             (this.rgTier!=null &&
              this.rgTier.equals(other.getRgTier()))) &&
            ((this.rgType==null && other.getRgType()==null) || 
             (this.rgType!=null &&
              this.rgType.equals(other.getRgType()))) &&
            ((this.timestamp==null && other.getTimestamp()==null) || 
             (this.timestamp!=null &&
              this.timestamp.equals(other.getTimestamp()))) &&
            ((this.parents==null && other.getParents()==null) || 
             (this.parents!=null &&
              java.util.Arrays.equals(this.parents, other.getParents()))) &&
            this.beds == other.getBeds() &&
            this.doctors == other.getDoctors() &&
            this.hospitals == other.getHospitals() &&
            this.libraries == other.getLibraries() &&
            this.schools == other.getSchools() &&
            this.size == other.getSize() &&
            this.staff == other.getStaff() &&
            ((this.extidATHENS==null && other.getExtidATHENS()==null) || 
             (this.extidATHENS!=null &&
              this.extidATHENS.equals(other.getExtidATHENS()))) &&
            ((this.extidIPEDS==null && other.getExtidIPEDS()==null) || 
             (this.extidIPEDS!=null &&
              this.extidIPEDS.equals(other.getExtidIPEDS()))) &&
            ((this.extidISNI==null && other.getExtidISNI()==null) || 
             (this.extidISNI!=null &&
              this.extidISNI.equals(other.getExtidISNI()))) &&
            ((this.extidNACS==null && other.getExtidNACS()==null) || 
             (this.extidNACS!=null &&
              this.extidNACS.equals(other.getExtidNACS()))) &&
            ((this.extidNCES==null && other.getExtidNCES()==null) || 
             (this.extidNCES!=null &&
              this.extidNCES.equals(other.getExtidNCES()))) &&
            ((this.extidOFR==null && other.getExtidOFR()==null) || 
             (this.extidOFR!=null &&
              this.extidOFR.equals(other.getExtidOFR()))) &&
            ((this.matchScore==null && other.getMatchScore()==null) || 
             (this.matchScore!=null &&
              this.matchScore.equals(other.getMatchScore()))) &&
            ((this.altNames==null && other.getAltNames()==null) || 
             (this.altNames!=null &&
              java.util.Arrays.equals(this.altNames, other.getAltNames()))) &&
            ((this.urls==null && other.getUrls()==null) || 
             (this.urls!=null &&
              java.util.Arrays.equals(this.urls, other.getUrls()))) &&
            ((this.classifications==null && other.getClassifications()==null) || 
             (this.classifications!=null &&
              java.util.Arrays.equals(this.classifications, other.getClassifications()))) &&
            ((this.place==null && other.getPlace()==null) || 
             (this.place!=null &&
              this.place.equals(other.getPlace())));
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
        _hashCode += getIdentifier();
        if (getName() != null) {
            _hashCode += getName().hashCode();
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
        if (getRgTier() != null) {
            _hashCode += getRgTier().hashCode();
        }
        if (getRgType() != null) {
            _hashCode += getRgType().hashCode();
        }
        if (getTimestamp() != null) {
            _hashCode += getTimestamp().hashCode();
        }
        if (getParents() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParents());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParents(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getBeds();
        _hashCode += getDoctors();
        _hashCode += getHospitals();
        _hashCode += getLibraries();
        _hashCode += getSchools();
        _hashCode += getSize();
        _hashCode += getStaff();
        if (getExtidATHENS() != null) {
            _hashCode += getExtidATHENS().hashCode();
        }
        if (getExtidIPEDS() != null) {
            _hashCode += getExtidIPEDS().hashCode();
        }
        if (getExtidISNI() != null) {
            _hashCode += getExtidISNI().hashCode();
        }
        if (getExtidNACS() != null) {
            _hashCode += getExtidNACS().hashCode();
        }
        if (getExtidNCES() != null) {
            _hashCode += getExtidNCES().hashCode();
        }
        if (getExtidOFR() != null) {
            _hashCode += getExtidOFR().hashCode();
        }
        if (getMatchScore() != null) {
            _hashCode += getMatchScore().hashCode();
        }
        if (getAltNames() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAltNames());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAltNames(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getUrls() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getUrls());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getUrls(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getClassifications() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getClassifications());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getClassifications(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPlace() != null) {
            _hashCode += getPlace().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InstitutionType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "institutionType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("identifier");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "identifier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "name"));
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
        elemField.setFieldName("rgTier");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "rgTier"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rgType");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "rgType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timestamp");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "timestamp"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parents");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "parents"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">parents"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("beds");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "beds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("doctors");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "doctors"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hospitals");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "hospitals"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("libraries");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "libraries"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("schools");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "schools"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("size");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "size"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("staff");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "staff"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extidATHENS");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "extidATHENS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extidIPEDS");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "extidIPEDS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extidISNI");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "extidISNI"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extidNACS");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "extidNACS"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extidNCES");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "extidNCES"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("extidOFR");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "extidOFR"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("matchScore");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "matchScore"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("altNames");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "altNames"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">altNames"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("urls");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "urls"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">urls"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("classifications");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "classifications"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", ">classifications"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("place");
        elemField.setXmlName(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "place"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://idproduction.ringgold.com/identifyservice25.xsd", "placeType"));
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

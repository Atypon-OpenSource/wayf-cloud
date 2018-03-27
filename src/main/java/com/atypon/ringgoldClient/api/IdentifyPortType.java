/**
 * IdentifyPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.atypon.ringgoldClient.api;

public interface IdentifyPortType extends java.rmi.Remote {
    public com.atypon.ringgoldClient.api.GetInstitutionResponse getInstitution(com.atypon.ringgoldClient.api.GetInstitutionRequest getInstitutionRequest) throws java.rmi.RemoteException;
    public com.atypon.ringgoldClient.api.FindInstitutionsResponse findInstitutions(com.atypon.ringgoldClient.api.FindInstitutionsRequest findInstitutionsRequest) throws java.rmi.RemoteException;
    public com.atypon.ringgoldClient.api.FindInstitutionsByKeywordsResponse findInstitutionsByKeywords(com.atypon.ringgoldClient.api.FindInstitutionsByKeywordsRequest findInstitutionsByKeywordsRequest) throws java.rmi.RemoteException;
    public com.atypon.ringgoldClient.api.GetInstitutionFamilyResponse getInstitutionFamily(com.atypon.ringgoldClient.api.GetInstitutionFamilyRequest getInstitutionFamilyRequest) throws java.rmi.RemoteException;
}

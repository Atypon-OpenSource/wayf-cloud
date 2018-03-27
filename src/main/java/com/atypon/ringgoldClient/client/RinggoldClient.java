package com.atypon.ringgoldClient.client;

import com.atypon.ringgoldClient.api.*;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;

public class RinggoldClient {

    private static IdentifyPortType port;

    public static InstitutionType getIntsitution(String licenseId, int institutionId) throws Exception {
        InstitutionType institutionType = null;
        try {
            GetInstitutionResponse response = getPort().getInstitution(new GetInstitutionRequest(licenseId, institutionId));
            institutionType = response.getInstitution();
        } catch (RemoteException e) {
            throw new Exception("can't get Institution response for ID : " + institutionId, e);
        }
        return institutionType;
    }

    public static IdentifyPortType getPort() {

        if (port == null) {
            try {
                Identifyservice25ServiceLocator locator = new Identifyservice25ServiceLocator();
                port = locator.getIdentifyPort();
            } catch (ServiceException e) {
                throw new RuntimeException("Can't create IdentifyBindingStub object.", e);

            }
        }
        return port;
    }
}

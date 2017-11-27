package com.atypon.wayf.facade.impl;

import com.atypon.wayf.dao.IdPExternalIdDao;
import com.atypon.wayf.data.ServiceException;
import com.atypon.wayf.data.identity.IdentityProviderQuery;
import com.atypon.wayf.data.identity.external.IdPExternalId;
import com.atypon.wayf.data.identity.external.IdpExternalIdQuery;
import com.atypon.wayf.facade.IdentityProviderFacade;
import com.atypon.wayf.facade.IdpExternalIdFacade;
import com.atypon.wayf.reactivex.FacadePolicies;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import io.reactivex.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.http.HttpStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static com.atypon.wayf.reactivex.FacadePolicies.singleOrException;


public class IdpExternalIdFacadeImpl implements IdpExternalIdFacade {

    private static final Set<String> STATIC_ALLOWED_PROVIDERS;
    private static Logger LOG = LoggerFactory.getLogger(IdpExternalIdFacadeImpl.class);

    static {
        STATIC_ALLOWED_PROVIDERS = new HashSet<>();
        STATIC_ALLOWED_PROVIDERS.add("ISNI");
        STATIC_ALLOWED_PROVIDERS.add("REDLINK");
        STATIC_ALLOWED_PROVIDERS.add("RINGGOLD");
    }


    @Inject
    private IdentityProviderFacade identityProviderFacade;

    @Inject
    private IdPExternalIdDao idPExternalIdDao;

    public IdpExternalIdFacadeImpl() {

    }


    @Override
    public Single<IdPExternalId> create(IdPExternalId externalId) {
        LOG.debug("Creating External Id [{}]", externalId);

        validate(externalId);

        return idPExternalIdDao.create(externalId);
    }

    @Override
    public Observable<IdPExternalId> filter(IdpExternalIdQuery query) {
        LOG.debug("Filtering for IDP external ids with criteria [{}]", query);
        return idPExternalIdDao.filter(query);
    }

    @Override
    public Single<IdPExternalId> getOrCreateExternalId(IdPExternalId externalId) {

        Observable<IdPExternalId> externalIdObservable =
                filter(new IdpExternalIdQuery().setIdpId(externalId.getIdentityProvider().getId()).setProvider(externalId.getProvider()));

        externalIdObservable = externalIdObservable.isEmpty().blockingGet() ? Observable.just(create(externalId).blockingGet()) : externalIdObservable;


        return singleOrException(externalIdObservable, HttpStatus.SC_BAD_REQUEST,
                "Could not determine a unique External Id from input");
    }




    private void validate(IdPExternalId idPExternalId) {
        if (!STATIC_ALLOWED_PROVIDERS.contains(idPExternalId.getProvider().toUpperCase())) {
            throw new ServiceException(HttpStatus.SC_BAD_REQUEST, "Not valid provider");
        }
    }

}

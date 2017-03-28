package com.atypon.wayf.verticle;

import com.atypon.wayf.data.Institution;
import com.atypon.wayf.facade.InstitutionFacade;
import com.atypon.wayf.facade.impl.InstitutionFacadeImpl;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class InstitutionVerticle implements RoutingProvider {
    private static final Logger LOG = LoggerFactory.getLogger(InstitutionVerticle.class);

    private static final String INSTITUTION_BASE_URL = "/1/institution";
    private static final String INSTITUTION_ID_PARAM_NAME = "id";
    private static final String INSTITUTION_ID_PARAM = ":" + INSTITUTION_ID_PARAM_NAME;

    private static final String CREATE_INSTITUTION = INSTITUTION_BASE_URL;
    private static final String READ_INSTITUTION = INSTITUTION_BASE_URL + "/" +  INSTITUTION_ID_PARAM;
    private static final String UPDATE_INSTITUTION = INSTITUTION_BASE_URL + "/" +  INSTITUTION_ID_PARAM;
    private static final String DELETE_INSTITUTION = INSTITUTION_BASE_URL + "/" +  INSTITUTION_ID_PARAM;

    private InstitutionFacade institutionFacade;

    public InstitutionVerticle() {
        institutionFacade = new InstitutionFacadeImpl();
    }

    public void addRoutings(Router router) {
        router.route(INSTITUTION_BASE_URL + "*").handler(BodyHandler.create());
        router.post(CREATE_INSTITUTION).handler(this::createInstitution);
        router.get(READ_INSTITUTION).handler(this::readInstitution);
        router.put(UPDATE_INSTITUTION).handler(this::updateInstitution);
        router.delete(DELETE_INSTITUTION).handler(this::deleteInstitution);
    }

    public void createInstitution(RoutingContext routingContext) {
            LOG.debug("Received create institution request");

            Single.just(routingContext)
                    .flatMap((rc) -> BaseVerticle.readRequestBody(rc, Institution.class))
                    .flatMap((requestInstitution) -> institutionFacade.create(requestInstitution))
                    .subscribeOn(Schedulers.io()) // Write HTTP response on IO thread
                    .subscribe(
                            (createdInstitution) -> BaseVerticle.buildSuccess(routingContext, createdInstitution),
                            (e) -> routingContext.fail(e)
                    );
    }

    public void readInstitution(RoutingContext routingContext) {
        LOG.debug("Received read institution request");

        Single.just(routingContext)
                .flatMap((rc) -> BaseVerticle.readPathArugment(rc, INSTITUTION_ID_PARAM_NAME))
                .flatMap((institutionId) -> institutionFacade.read(institutionId))
                .subscribeOn(Schedulers.io()) // Write HTTP response on IO thread
                .subscribe(
                        (readInstitution) -> BaseVerticle.buildSuccess(routingContext, readInstitution),
                        (e) -> routingContext.fail(e)
                );
    }

    public void updateInstitution(RoutingContext routingContext) {
        LOG.debug("Received update institution request");

        Single.just(routingContext)
                .flatMap((rc) -> BaseVerticle.readRequestBody(rc, Institution.class))
                .flatMap((requestInstitution) -> institutionFacade.update(requestInstitution))
                .subscribeOn(Schedulers.io()) // Write HTTP response on IO thread
                .subscribe(
                        (updatedInstitution) -> BaseVerticle.buildSuccess(routingContext, updatedInstitution),
                        (e) -> routingContext.fail(e)
                );
    }

    public void deleteInstitution(RoutingContext routingContext) {
        LOG.debug("Received delete institution request");

        Single.just(routingContext)
                .flatMap((rc) -> BaseVerticle.readPathArugment(rc, INSTITUTION_ID_PARAM_NAME))
                .flatMapCompletable((institutionId) -> institutionFacade.delete(institutionId))
                .subscribeOn(Schedulers.io()) // Write HTTP response on IO thread
                .subscribe(
                        () -> BaseVerticle.buildSuccess(routingContext, null),
                        (e) -> routingContext.fail(e)
                );
    }
}

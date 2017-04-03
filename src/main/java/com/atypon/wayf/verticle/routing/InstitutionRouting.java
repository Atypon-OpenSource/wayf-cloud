package com.atypon.wayf.verticle.routing;

import com.atypon.wayf.data.Institution;
import com.atypon.wayf.facade.InstitutionFacade;
import com.atypon.wayf.facade.impl.InstitutionFacadeImpl;
import com.atypon.wayf.verticle.RequestReader;
import com.atypon.wayf.verticle.ResponseWriter;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstitutionRouting implements RoutingProvider {
    private static final Logger LOG = LoggerFactory.getLogger(InstitutionRouting.class);

    private static final String INSTITUTION_BASE_URL = "/1/institution";
    private static final String INSTITUTION_ID_PARAM_NAME = "id";
    private static final String INSTITUTION_ID_PARAM = ":" + INSTITUTION_ID_PARAM_NAME;

    private static final String CREATE_INSTITUTION = INSTITUTION_BASE_URL;
    private static final String READ_INSTITUTION = INSTITUTION_BASE_URL + "/" +  INSTITUTION_ID_PARAM;
    private static final String UPDATE_INSTITUTION = INSTITUTION_BASE_URL + "/" +  INSTITUTION_ID_PARAM;
    private static final String DELETE_INSTITUTION = INSTITUTION_BASE_URL + "/" +  INSTITUTION_ID_PARAM;

    private InstitutionFacade institutionFacade;

    public InstitutionRouting() {
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
                    .flatMap((rc) -> RequestReader.readRequestBody(rc, Institution.class))
                    .flatMap((requestInstitution) -> institutionFacade.create(requestInstitution))
                    .subscribeOn(Schedulers.io()) // Write HTTP response on IO thread
                    .subscribe(
                            (createdInstitution) -> ResponseWriter.buildSuccess(routingContext, createdInstitution),
                            (e) -> routingContext.fail(e)
                    );
    }

    public void readInstitution(RoutingContext routingContext) {
        LOG.debug("Received read institution request");

        Single.just(routingContext)
                .flatMap((rc) -> RequestReader.readPathArgument(rc, INSTITUTION_ID_PARAM_NAME))
                .flatMap((institutionId) -> institutionFacade.read(institutionId))
                .subscribeOn(Schedulers.io()) // Write HTTP response on IO thread
                .subscribe(
                        (readInstitution) -> ResponseWriter.buildSuccess(routingContext, readInstitution),
                        (e) -> routingContext.fail(e)
                );
    }

    public void updateInstitution(RoutingContext routingContext) {
        LOG.debug("Received update institution request");

        Single.just(routingContext)
                .flatMap((rc) -> RequestReader.readRequestBody(rc, Institution.class))
                .flatMap((requestInstitution) -> institutionFacade.update(requestInstitution))
                .subscribeOn(Schedulers.io()) // Write HTTP response on IO thread
                .subscribe(
                        (updatedInstitution) -> ResponseWriter.buildSuccess(routingContext, updatedInstitution),
                        (e) -> routingContext.fail(e)
                );
    }

    public void deleteInstitution(RoutingContext routingContext) {
        LOG.debug("Received delete institution request");

        Single.just(routingContext)
                .flatMap((rc) -> RequestReader.readPathArgument(rc, INSTITUTION_ID_PARAM_NAME))
                .flatMapCompletable((institutionId) -> institutionFacade.delete(institutionId))
                .subscribeOn(Schedulers.io()) // Write HTTP response on IO thread
                .subscribe(
                        () -> ResponseWriter.buildSuccess(routingContext, null),
                        (e) -> routingContext.fail(e)
                );
    }
}

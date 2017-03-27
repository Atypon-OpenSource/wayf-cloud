package com.atypon.wayf.facade;

import com.atypon.wayf.data.Institution;
import io.reactivex.Completable;
import io.reactivex.Observable;

import java.util.Optional;

/**
 * Created by mmason on 3/22/17.
 */
public interface InstitutionFacade {
    public Observable<Institution> create(Institution institution);
    public Observable<Institution> read(String id);
    public Observable<Institution> update(Institution institution);
    public Observable<Optional<Void>> delete(String id);
}

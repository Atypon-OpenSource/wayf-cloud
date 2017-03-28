package com.atypon.wayf.facade;

import com.atypon.wayf.data.Institution;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import java.util.Optional;

public interface InstitutionFacade {
    public Single<Institution> create(Institution institution);
    public Single<Institution> read(String id);
    public Single<Institution> update(Institution institution);
    public Completable delete(String id);
}

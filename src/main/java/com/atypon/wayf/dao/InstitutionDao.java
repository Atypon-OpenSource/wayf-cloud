package com.atypon.wayf.dao;

import com.atypon.wayf.data.Institution;


public interface InstitutionDao {
    public Institution create(Institution institution);
    public Institution read(String id);
    public Institution update(Institution institution);
    public void delete(String id);
}

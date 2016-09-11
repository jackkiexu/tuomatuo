package com.lami.dcm.lbdatasource.service;

import com.lami.dcm.lbdatasource.dao.AddressDao;
import com.lami.dcm.lbdatasource.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xjk on 9/10/16.
 */
@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    public void save(Address address){
        addressDao.save(address);
    }

    public Address findById(int id){
        return addressDao.findByID(id);
    }

}

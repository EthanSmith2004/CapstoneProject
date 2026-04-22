package com.jel.spys.service;

import com.jel.spys.entity.CampusEntity;
import com.jel.spys.repository.CampusRepository;
import org.springframework.stereotype.Service;

@Service
public class CampusManagementService extends GenericManagementService<CampusEntity, Long> {
    public CampusManagementService(CampusRepository repository) {
        super(repository, "Campus");
    }
}

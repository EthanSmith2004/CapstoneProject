package com.jel.spys.service;

import com.jel.spys.entity.ResidenceEntity;
import com.jel.spys.repository.ResidenceRepository;
import org.springframework.stereotype.Service;

@Service
public class ResidenceManagementService extends GenericManagementService<ResidenceEntity, Long> {
    public ResidenceManagementService(ResidenceRepository repository) {
        super(repository, "Residence");
    }
}

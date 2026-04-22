package com.jel.spys.service;

import com.jel.spys.entity.AllergyEntity;
import com.jel.spys.repository.AllergyRepository;
import org.springframework.stereotype.Service;

@Service
public class AllergyManagementService extends GenericManagementService<AllergyEntity, Long> {
    public AllergyManagementService(AllergyRepository repository) {
        super(repository, "Allergy");
    }
}

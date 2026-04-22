package com.jel.spys.service;

import com.jel.spys.model.SelectDTO;
import com.jel.spys.repository.AllergyRepository;
import com.jel.spys.repository.CampusRepository;
import com.jel.spys.repository.ResidenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListService {

    private final AllergyRepository allergyRepository;
    private final CampusRepository campusRepository;
    private final ResidenceRepository residenceRepository;

    public List<SelectDTO> getAllergyNames() {
        return allergyRepository.findAll().stream().map(v -> new SelectDTO(v.getId(), v.getAllergy())).collect(Collectors.toList());
    }

    public List<SelectDTO> getCampusNames() {
        return campusRepository.findAll().stream().map(v -> new SelectDTO(v.getId(), v.getCampus())).collect(Collectors.toList());
    }

    public List<SelectDTO> getResidenceNames() {
        return residenceRepository.findAll().stream().map(v -> new SelectDTO(v.getId(), v.getResidence())).collect(Collectors.toList());
    }
}

package com.jel.spys.service;

import com.jel.spys.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class GenericManagementService<T, ID> {

    private final JpaRepository<T, ID> repository;
    private final String resourceName;

    public GenericManagementService(JpaRepository<T, ID> repository, String resourceName) {
        this.repository = repository;
        this.resourceName = resourceName;
    }

    public T create(T entity) {
        return repository.save(entity);
    }

    public T findById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(resourceName + " not found with id " + id));
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public T update(ID id, T entityDetails) {
        @SuppressWarnings("unused")
        T entity = findById(id);
        return repository.save(entityDetails);
    }

    public void delete(ID id) {
        T entity = findById(id);
        repository.delete(entity);
    }
}

package com.booking.system.service;

import com.booking.system.domain.Resource;
import com.booking.system.dto.ResourceRequest;
import com.booking.system.dto.ResourceResponse;
import com.booking.system.exception.ResourceNotFoundException;
import com.booking.system.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<ResourceResponse> getAllResources() {
        return resourceRepository.findAll().stream()
                .map(ResourceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ResourceResponse getResourceById(Long id) {
        return resourceRepository.findById(id)
                .map(ResourceResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Resource target metadata map node not indexed: " + id));
    }

    @Transactional
    public ResourceResponse createResource(ResourceRequest request) {
        Resource resource = new Resource();
        mapRequestToEntity(request, resource);
        return ResourceResponse.fromEntity(resourceRepository.save(resource));
    }

    @Transactional
    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource targeted for update operation not found: " + id));
        mapRequestToEntity(request, resource);
        return ResourceResponse.fromEntity(resourceRepository.save(resource));
    }

    @Transactional
    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete unindexed asset structural coordinates: " + id);
        }
        resourceRepository.deleteById(id);
    }

    private void mapRequestToEntity(ResourceRequest request, Resource resource) {
        resource.setName(request.name());
        resource.setType(request.type());
        resource.setPricePerHour(request.pricePerHour());
        resource.setAvailable(request.available());
    }
}
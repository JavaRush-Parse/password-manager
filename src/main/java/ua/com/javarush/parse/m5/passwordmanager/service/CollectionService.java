package ua.com.javarush.parse.m5.passwordmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.repository.CollectionRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;

    public List<Collection> findAll() {
        return collectionRepository.findAll();
    }

    public Optional<Collection> findById(Long id) {
        return collectionRepository.findById(id);
    }

    public Collection save(Collection collection) {
        return collectionRepository.save(collection);
    }

    public void deleteById(Long id) {
        collectionRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return collectionRepository.existsByNameIgnoreCase(name);
    }

    public Optional<Collection> findByName(String name) {
        return collectionRepository.findByNameIgnoreCase(name);
    }
}

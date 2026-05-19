package ru.alarkhipov.crm.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.alarkhipov.crm.entities.SellerEntity;
import ru.alarkhipov.crm.records.Seller;
import ru.alarkhipov.crm.repos.SellerRepository;

import java.util.List;

@Slf4j
@Service
public class SellerService {
    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public List<Seller> getAllSellers() {
        List<SellerEntity> allSellers = sellerRepository.findAll();
        log.info("List of all sellers was sent");
        return allSellers.stream().map(this::getSellerByEntity).toList();
    }

    public Seller getSellerById(Long id) {
        SellerEntity sellerEntity = sellerRepository.findById(id).orElseThrow(() -> {
            log.error("Seller not found by id {}", id);
            return new EntityNotFoundException("Seller not found by id: " + id);
        });
        return getSellerByEntity(sellerEntity);
    }

    public Seller createSeller(Seller sellerToCreate){
        SellerEntity sellerToSave = new SellerEntity(
                null,
                sellerToCreate.name(),
                sellerToCreate.contactInfo(),
                sellerToCreate.registrationDate()

        );
        log.info("Created new seller: name='{}', contact='{}'",
                sellerToSave.getName(), sellerToSave.getContactInfo());
        SellerEntity savedSeller = sellerRepository.save(sellerToSave);
        return getSellerByEntity(savedSeller);
    }

    public Seller updateSellerInfo(Long id, Seller sellerToUpdate){
        SellerEntity sellerEntity = sellerRepository.findById(id).orElseThrow(() -> {
            log.error("Seller not found by id {}", id);
            return new EntityNotFoundException("Seller not found by id: " + id);
        });
        SellerEntity sellerToSave = new SellerEntity(
                sellerEntity.getId(),
                sellerToUpdate.name(),
                sellerToUpdate.contactInfo(),
                sellerToUpdate.registrationDate()
        );
        log.info("Updating seller id {}. New name: '{}'", id, sellerToSave.getName());
        SellerEntity savedSeller = sellerRepository.save(sellerToSave);
        return getSellerByEntity(savedSeller);
    }

    public void deleteSeller(Long id){
        if (!sellerRepository.existsById(id)) {
            log.error("Seller not found by id {}", id);
            throw new EntityNotFoundException("Seller not found by id: " + id);
        }
        log.info("Deleting seller by id {}", id);
        sellerRepository.deleteById(id);
    }

    private Seller getSellerByEntity(SellerEntity seller){
        return new Seller(
                seller.getId(),
                seller.getName(),
                seller.getContactInfo(),
                seller.getRegistrationDate()
        );
    }
}

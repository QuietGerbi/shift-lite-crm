package ru.alarkhipov.crm.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.alarkhipov.crm.entities.SellerEntity;
import ru.alarkhipov.crm.records.Seller;
import ru.alarkhipov.crm.repos.SellerRepository;
import ru.alarkhipov.crm.repos.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SellerServiceTest {
    @Autowired
    private SellerService sellerService;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    @Test
    void getAllSellersSuccess() {
        sellerRepository.save(new SellerEntity(null, "Алексей", "alex@kakoitomail.ru", LocalDateTime.now()));
        sellerRepository.save(new SellerEntity(null, "Борис", "boris@kakoitomail.ru", LocalDateTime.now()));

        List<Seller> result = sellerService.getAllSellers();

        assertEquals(2, result.size());
    }

    @Test
    void getSellerByIdSuccess() {
        SellerEntity entity = sellerRepository.save(new SellerEntity(null, "Владимир", "vlad@kakoitomail.ru", LocalDateTime.now()));

        Seller result = sellerService.getSellerById(entity.getId());

        assertNotNull(result);
        assertEquals("Владимир", result.name());
        assertEquals("vlad@kakoitomail.ru", result.contactInfo());
    }

    @Test
    void getSellerByIdThrowsException() {
        assertThrows(EntityNotFoundException.class, () -> sellerService.getSellerById(999L));
    }

    @Test
    void createSellerSuccess() {
        LocalDateTime regDate = LocalDateTime.now();
        Seller dto = new Seller(null, "Дмитрий", "dima@kakoitomail.ru", regDate);

        Seller result = sellerService.createSeller(dto);

        assertNotNull(result.id());
        assertEquals("Дмитрий", result.name());
        assertTrue(sellerRepository.existsById(result.id()));
    }

    @Test
    void updateSellerInfoSuccess() {
        SellerEntity entity = sellerRepository.save(new SellerEntity(null, "Игорь Старый", "old@kakoitomail.ru", LocalDateTime.now()));
        Seller updateDto = new Seller(null, "Игорь Хитрый", "new@kakoitomail.ru", entity.getRegistrationDate());

        Seller result = sellerService.updateSellerInfo(entity.getId(), updateDto);

        assertEquals("Игорь Хитрый", result.name());
        assertEquals("new@kakoitomail.ru", result.contactInfo());

        SellerEntity updatedEntity = sellerRepository.findById(entity.getId()).orElseThrow();
        assertEquals("Игорь Хитрый", updatedEntity.getName());
    }

    @Test
    void updateSellerInfoThrowsException() {
        Seller updateDto = new Seller(null, "NoName", "none", LocalDateTime.now());
        assertThrows(EntityNotFoundException.class, () -> sellerService.updateSellerInfo(999L, updateDto));
    }

    @Test
    void deleteSellerSuccess() {
        SellerEntity entity = sellerRepository.save(new SellerEntity(null, "Deletion candidate",
                "delete@kakoitomail.ru", LocalDateTime.now()));

        assertDoesNotThrow(() -> sellerService.deleteSeller(entity.getId()));
        assertFalse(sellerRepository.existsById(entity.getId()));
    }

    @Test
    void deleteSellerThrowsException() {
        assertThrows(EntityNotFoundException.class, () -> sellerService.deleteSeller(999L));
    }
}
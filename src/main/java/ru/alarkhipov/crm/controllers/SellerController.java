package ru.alarkhipov.crm.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alarkhipov.crm.records.Transaction;
import ru.alarkhipov.crm.services.SellerService;
import ru.alarkhipov.crm.records.Seller;
import ru.alarkhipov.crm.services.TransactionService;

import java.util.List;

// эндпоинты для раздела продавцов
@RestController
@RequestMapping("/sellers")
public class SellerController {
    private final SellerService sellerService;
    private final TransactionService transactionService;

    public SellerController(SellerService sellerService,
                            TransactionService transactionService){
        this.sellerService = sellerService;
        this.transactionService = transactionService;
    }

    @GetMapping()
    public ResponseEntity<List<Seller>> getAllSellers(){
        return ResponseEntity.ok(sellerService.getAllSellers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Seller> getSellerById(@PathVariable("id") Long id){
        return ResponseEntity.ok(sellerService.getSellerById(id));
    }

    @PostMapping
    public ResponseEntity<Seller> createSeller(@RequestBody @Valid Seller sellerToCreate){
        return ResponseEntity.ok(sellerService.createSeller(sellerToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Seller> updateSeller(@PathVariable("id") Long id,
                                               @RequestBody @Valid Seller sellerToUpdate){
        return ResponseEntity.ok(sellerService.updateSellerInfo(id, sellerToUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable("id") Long id){
        sellerService.deleteSeller(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactionsBySellerId(@PathVariable("id") Long id){
        return ResponseEntity.ok(transactionService.getAllTransactionsBySellerId(id));
    }
}

package com.ltv.saas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class Routes {

    @Autowired
    private SellerService sellerService;
    
    @Autowired
    private SaleRepository saleRepository;

    @GetMapping("/")
    public String home() {
        return "Sales Insight Backend is up and running!";
    }

    @GetMapping("/health")
    public String health() {
        return "Server status: OK";
    }

    @GetMapping("/seller/{id}/summary")
    public ResponseEntity<?> getSellerSummary(@PathVariable Long id) {
        try {
            // Check if seller exists (check if they have any sales)
            Integer salesCount = saleRepository.countBySellerId(id);
            if (salesCount == null || salesCount == 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Seller not found");
                error.put("message", "Seller with ID " + id + " does not exist");
                return ResponseEntity.status(404).body(error);
            }

            // Get total sales this week
            int totalSales = sellerService.getTotalSalesThisWeek(id);
            
            // Get total revenue this week
            double totalRevenue = sellerService.getTotalRevenueThisWeek(id);
            
            // Get return rate this week
            double returnRate = sellerService.getReturnRateThisWeek(id);

            // Create response object
            Map<String, Object> response = new HashMap<>();
            response.put("totalSalesThisWeek", totalSales);
            response.put("totalRevenueThisWeek", totalRevenue);
            response.put("returnRate", returnRate);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Return 500 error for any exception
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
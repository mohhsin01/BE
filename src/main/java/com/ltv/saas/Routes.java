package com.ltv.saas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
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
            
            Integer salesCount = saleRepository.countBySellerId(id);
            if (salesCount == null || salesCount == 0) {
                Map<String, String> error = new LinkedHashMap<>();
                error.put("error", "Seller not found");
                error.put("message", "Seller with ID " + id + " does not exist");
                return ResponseEntity.status(404).body(error);
            }

            SellerService.SellerSummaryData summary = sellerService.getSellerSummaryOptimized(id);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("totalSalesThisWeek", summary.totalSales);
            response.put("totalRevenueThisWeek", summary.totalRevenue);
            response.put("returnRate", summary.returnRate);
            response.put("alerts", summary.alerts);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Internal server error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}

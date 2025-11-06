package com.ltv.saas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SellerService {
    
    @Autowired
    private SaleRepository saleRepository;
    
    public int getTotalSalesThisWeek(Long sellerId) {
       
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        
        
        String startDate = startOfWeek.toString();
        String endDate = endOfWeek.toString();
        
        
        List<Sale> sales = saleRepository.findSalesBySellerAndDateRange(
            sellerId, 
            startDate, 
            endDate
        );
        
        
        return sales.size();
    }
    
    public double getTotalRevenueThisWeek(Long sellerId) {
        
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        
        
        String startDate = startOfWeek.toString();
        String endDate = endOfWeek.toString();
        
        
        List<Sale> sales = saleRepository.findSalesBySellerAndDateRange(
            sellerId, 
            startDate, 
            endDate
        );
       
        return sales.stream()
                .mapToDouble(sale -> sale.getPrice() * sale.getQuantity())
                .sum();
    }
    
    public double getReturnRateThisWeek(Long sellerId) {
     
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        
       
        String startDate = startOfWeek.toString();
        String endDate = endOfWeek.toString();
        
       
        Integer returnsCount = saleRepository.countReturnsBySellerAndDateRange(
            sellerId, 
            startDate, 
            endDate
        );
        
       
        int salesCount = getTotalSalesThisWeek(sellerId);
           
        if (salesCount == 0) {
            return 0.0;
        }
        
        return ((double) returnsCount / salesCount) * 100.0;
    }
    
    public int getTotalSalesLastWeek(Long sellerId) {

        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = today.with(DayOfWeek.MONDAY);
        
        LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
        LocalDate endOfLastWeek = startOfLastWeek.plusDays(6); 
        
    
        String startDate = startOfLastWeek.toString();
        String endDate = endOfLastWeek.toString();
        

        List<Sale> sales = saleRepository.findSalesBySellerAndDateRange(
            sellerId, 
            startDate, 
            endDate
        );
        
        return sales.size();
    }
    
    public List<String> getAlerts(Long sellerId) {
        List<String> alerts = new ArrayList<>();
        
        int salesThisWeek = getTotalSalesThisWeek(sellerId);
        int salesLastWeek = getTotalSalesLastWeek(sellerId);
        
        if (salesLastWeek > 0) {
           
            double percentageChange = ((double)(salesThisWeek - salesLastWeek) / salesLastWeek) * 100.0;
            
            if (percentageChange < -30.0) {
                alerts.add("Sales dropped by more than 30% vs last week");
            }
        }
        
        double returnRate = getReturnRateThisWeek(sellerId);
        if (returnRate > 10.0) {
            alerts.add("Return rate above 10%");
        }
        
        return alerts;
    }
    

    @Cacheable(value = "sellerSummary", key = "#sellerId")
    public SellerSummaryData getSellerSummaryOptimized(Long sellerId) {
       
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);
        
       
        String startDate = startOfWeek.toString();
        String endDate = endOfWeek.toString();
        

        List<Sale> allSalesThisWeek = saleRepository.findAllSalesBySellerAndDateRange(
            sellerId, 
            startDate, 
            endDate
        );
        

        List<Sale> salesThisWeek = allSalesThisWeek.stream()
            .filter(sale -> sale.getReturned() == 0)
            .toList();
        

        int totalSales = salesThisWeek.size();
        
  
        double totalRevenue = salesThisWeek.stream()
            .mapToDouble(sale -> sale.getPrice() * sale.getQuantity())
            .sum();
        
     
        int returnsCount = (int) allSalesThisWeek.stream()
            .filter(sale -> sale.getReturned() == 1)
            .count();
        

        double returnRate = (totalSales == 0) ? 0.0 : ((double) returnsCount / totalSales) * 100.0;
        

        int salesLastWeek = getTotalSalesLastWeek(sellerId);
        
  
        List<String> alerts = new ArrayList<>();
        if (salesLastWeek > 0) {
            double percentageChange = ((double)(totalSales - salesLastWeek) / salesLastWeek) * 100.0;
            if (percentageChange < -30.0) {
                alerts.add("Sales dropped by more than 30% vs last week");
            }
        }
        if (returnRate > 10.0) {
            alerts.add("Return rate above 10%");
        }
        
        return new SellerSummaryData(totalSales, totalRevenue, returnRate, alerts);
    }
    

    public static class SellerSummaryData {
        public final int totalSales;
        public final double totalRevenue;
        public final double returnRate;
        public final List<String> alerts;
        
        public SellerSummaryData(int totalSales, double totalRevenue, double returnRate, List<String> alerts) {
            this.totalSales = totalSales;
            this.totalRevenue = totalRevenue;
            this.returnRate = returnRate;
            this.alerts = alerts;
        }
    }
}

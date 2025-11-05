package com.ltv.saas;

import javax.persistence.*;

@Entity
@Table(name = "sales")
public class Sale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "seller_id")
    private Long sellerId;
    
    private String date;  // SQLite stores dates as TEXT
    
    private Integer quantity;
    
    private Double price;
    
    private Integer returned;  // 0 or 1 (0 = not returned, 1 = returned)
    
    // Default constructor (required by JPA)
    public Sale() {
    }
    
    // Constructor with all fields
    public Sale(Long sellerId, String date, Integer quantity, Double price, Integer returned) {
        this.sellerId = sellerId;
        this.date = date;
        this.quantity = quantity;
        this.price = price;
        this.returned = returned;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Integer getReturned() {
        return returned;
    }
    
    public void setReturned(Integer returned) {
        this.returned = returned;
    }
}
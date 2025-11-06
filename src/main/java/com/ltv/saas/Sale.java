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
    
    private String date; 
    
    private Integer quantity;
    
    private Double price;
    
    private Integer returned;

    public Sale() {
    }
    
    public Sale(Long sellerId, String date, Integer quantity, Double price, Integer returned) {
        this.sellerId = sellerId;
        this.date = date;
        this.quantity = quantity;
        this.price = price;
        this.returned = returned;
    }
    
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

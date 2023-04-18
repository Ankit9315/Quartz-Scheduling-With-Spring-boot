package com.example.QuartzScheduling.Dto;

import java.time.LocalDateTime;

public class ProductDto {

    private String name;
	
	private long quantity;
	
	private long price;
	
	private Long makeVisibleAt;
	
    @Override
	public String toString() {
		return "ProductDto [name=" + name + ", quantity=" + quantity + ", price=" + price + ", makeVisibleAt="
				+ makeVisibleAt + ", created=" + created + ", modified=" + modified + ", status=" + status + "]";
	}

	private LocalDateTime created;
    
    private LocalDateTime modified;
    
    private String status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public Long getMakeVisibleAt() {
		return makeVisibleAt;
	}

	public void setMakeVisibleAt(Long makeVisibleAt) {
		this.makeVisibleAt = makeVisibleAt;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getModified() {
		return modified;
	}

	public void setModified(LocalDateTime modified) {
		this.modified = modified;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
    
}

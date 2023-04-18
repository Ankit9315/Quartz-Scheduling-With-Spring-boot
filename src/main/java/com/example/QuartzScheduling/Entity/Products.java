package com.example.QuartzScheduling.Entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
public class Products {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name ="Name")
	private String name;
	
	@Column(name ="Quantity")
	private long quantity;
	
	@Column(name ="Price")
	private long price;
	
	@Column(name ="Visible")
	private Boolean visible = false;
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public long getVisibleAt() {
		return visibleAt;
	}

	public void setVisibleAt(long visibleAt) {
		this.visibleAt = visibleAt;
	}

	public LocalDateTime getCreatedOn() {
		return createdon;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdon = createdOn;
	}

	public LocalDateTime getModifiedOn() {
		return modifiedon;
	}

	public void setModifiedOn(LocalDateTime modifiedOn) {
		this.modifiedon = modifiedOn;
	}

	@Column(name="Visible_At")
	private long visibleAt;
	
	@CreatedDate
	@Column(name ="CreatedOn")
	LocalDateTime createdon;
	
	@LastModifiedDate
	@Column(name ="ModifiedOn")
	LocalDateTime modifiedon;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

	@Override
	public String toString() {
		return "Products [id=" + id + ", name=" + name + ", quantity=" + quantity + ", price=" + price + ", visibleAt="
				+ visibleAt + ", createdOn=" + createdon + ", modifiedOn=" + modifiedon + "]";
	}

	
	
}

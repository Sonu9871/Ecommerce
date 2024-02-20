package com.sonu.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sonu.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String>{

}

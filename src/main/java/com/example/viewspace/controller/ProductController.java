package com.example.viewspace.controller;


import java.io.IOException;

import org.apache.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.viewspace.entity.Product;
import com.example.viewspace.service.ProductService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/viewspace/product")
@AllArgsConstructor
public class ProductController 
{
	private ProductService productservice;

	//@PreAuthorize("hasRole('ROLE_ADMIN')")
	 @PostMapping(consumes = {"multipart/form-data"})
	public ResponseEntity<Product> addProduct(@RequestPart("productData") Product product, @RequestPart("file") MultipartFile file) 
	 {
	        Product savedProduct = productservice.addProduct(product, file);
	        return ResponseEntity.ok(savedProduct);
	   
     }
	
	//@PreAuthorize("hasAnyRole('USER')")
	 @GetMapping("/{id}")
	 public ResponseEntity<Product> getProduct(@PathVariable Long id)
	 {
		 return ResponseEntity.ok(productservice.findProductById(id));
	 }
	 
	 @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	 @GetMapping
	 public ResponseEntity<Page<Product>> getAllProduct(@RequestParam(value="page",defaultValue = "0") int page,@RequestParam(value="size",defaultValue = "10") int size,@RequestParam(value="sortBy",defaultValue="id")String sortBy,@RequestParam(value="sortDir",defaultValue="asc") String sortDir)
	 {
		 return ResponseEntity.ok(productservice.getAllProducts(page,size,sortBy,sortDir));
	 }
	 
	 //@PreAuthorize("hasRole('ADMIN')")
	    @DeleteMapping("/{id}")
	    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) 
	    {
	        if (productservice.deleteProductById(id)) {
	            return ResponseEntity.noContent().build();
	        }
	        return ResponseEntity.notFound().build();
	    }
    
	 //@PreAuthorize("hasRole('ADMIN')")
	 @PutMapping(value="/{id}",consumes = {"multipart/form-data"})
     public ResponseEntity<Product>  updateProduct(@PathVariable Long id,@RequestPart("productData") Product product , @RequestPart("file") MultipartFile file)
     {
		 Product pr = productservice.updateProduct(id, product, file);
		 return ResponseEntity.ok(pr);
     }
	 
	 
}

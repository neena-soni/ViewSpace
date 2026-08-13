package com.example.viewspace.service;





import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.viewspace.entity.Product;
import com.example.viewspace.repository.ProductRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService 
{
	
	private ProductRepository productrepo;
	private Cloudinary cloudinary;
	
	public  Product addProduct(Product product,MultipartFile file)
	{
		Map<?,?> uploadResult = null;
		try
		{
			uploadResult = cloudinary.uploader().upload(file.getBytes(),ObjectUtils.asMap("resource_type","auto"));     //"folder", "ecommerce_products"
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
		String url = uploadResult.get("secure_url").toString();
		String publicId = uploadResult.get("public_id").toString();
		
		
		Product pr = new Product();
		pr.setTitle(product.getTitle());
		pr.setPrice(product.getPrice());
		pr.setStock(product.getStock());
		pr.setCategory(product.getCategory());
		pr.setImageurl(url);
		pr.setPublicid(publicId);
		return productrepo.save(pr);
	}

    public Product findProductById(Long id)
    {
    	return productrepo.findById(id).orElseThrow(()->new RuntimeException("Product not found with id: "+ id));
    }
    
    
    public Page<Product> getAllProducts(int page, int size,String sortBy,String direction)
    {
    	
    	Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())?Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
    	Pageable pageable = PageRequest.of(page, size,sort);
    	return productrepo.findAll(pageable);
    }
    
    
    
    public boolean deleteProductById(Long id) {
        return productrepo.findById(id).map(product->{
        	if(product.getPublicid() != null)
        	{
        		try {
        			cloudinary.uploader().destroy(product.getPublicid(),ObjectUtils.asMap("invalidate",true));
        		}catch(IOException e)
        		{
        			e.printStackTrace();
        		}
        	}
        	productrepo.deleteById(id);
        	return true;
        }).orElse(false);
    }
    
    public Product updateProduct(Long id,Product product,MultipartFile file)
    {
    	 if (product == null) {
    	        throw new IllegalArgumentException("Request payload product data cannot be null");
    	    }
    	
    	Product pr = productrepo.findById(id).orElseThrow(()-> new RuntimeException("Product not found with id: "+ id));
    	
    	pr.setTitle(product.getTitle());
    	pr.setPrice(product.getPrice());
    	pr.setStock(product.getStock());
    	pr.setCategory(product.getCategory());
    	
    	if(file != null)
    	{
    		try
    		{
    			if(pr.getPublicid() != null)
    			{
    				cloudinary.uploader().destroy(pr.getPublicid(),ObjectUtils.asMap("invalidate",true));
    			}
    			Map<?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(),ObjectUtils.asMap("resource_type","auto"));
    			String url = uploadResult.get("secure_url").toString();
    			String publicId = uploadResult.get("public_id").toString();
    			pr.setImageurl(url);
    			pr.setPublicid(publicId);
    			
    		}catch(IOException e)
    		{
    			e.printStackTrace();
    		}
    		
    	}
    	return productrepo.save(pr);
    }
    
}















 
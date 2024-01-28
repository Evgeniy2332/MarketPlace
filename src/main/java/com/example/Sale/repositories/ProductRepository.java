package com.example.Sale.repositories;

import com.example.Sale.models.Product;
import com.example.Sale.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findByTitle(String title);
    List<Product> findByTitleIgnoreCase(String title);
    List<Product> findAllByUser(User user);

    List<Product> findByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndPriceBetweenAndCityContainingIgnoreCaseAndUser_NameContainingIgnoreCase(
           String title, String description, int minPrice, int maxPrice, String city, String author);



}

package kr.ac.hansung.cse.service;

import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.model.Product;
import kr.ac.hansung.cse.repository.CategoryRepository;
import kr.ac.hansung.cse.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Category resolveCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return null;
        }
        return categoryRepository.findByName(categoryName).orElse(null);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> search(String keyword, String category) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasCategory = category != null && !category.isBlank();

        if (hasKeyword && hasCategory) {
            return productRepository.findByNameContainingAndCategoryName(keyword, category);
        } else if (hasKeyword) {
            return productRepository.findByNameContaining(keyword);
        } else if (hasCategory) {
            return productRepository.findByCategoryName(category);
        } else {
            return productRepository.findAll();
        }
    }

    @Transactional
    public Product createProduct(Product product) {
        if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("상품 가격은 0 이상이어야 합니다.");
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Product product) {
        if (product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("상품 가격은 0 이상이어야 합니다.");
        }
        return productRepository.update(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.delete(id);
    }
}
package kr.ac.hansung.cse.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.cse.exception.ProductNotFoundException;
import kr.ac.hansung.cse.model.Product;
import kr.ac.hansung.cse.model.ProductForm;
import kr.ac.hansung.cse.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            Model model) {

        List<Product> products;

        if ((keyword == null || keyword.isEmpty()) &&
            (category == null || category.isEmpty())) {
            products = productService.getAllProducts();
        } else {
            products = productService.search(keyword, category);
        }

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);

        return "productList";
    }

    @GetMapping("/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        model.addAttribute("product", product);
        return "productView";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("productForm", new ProductForm());
        return "productForm";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productForm") ProductForm productForm,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "productForm";
        }

        Product product = productForm.toEntity();
        product.setCategory(productService.resolveCategory(productForm.getCategory()));
        Product savedProduct = productService.createProduct(product);

        redirectAttributes.addFlashAttribute("successMessage",
                "'" + savedProduct.getName() + "' 상품이 성공적으로 등록되었습니다.");

        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        model.addAttribute("productForm", ProductForm.from(product));
        return "productEditForm";
    }

    @PostMapping("/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("productForm") ProductForm productForm,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "productEditForm";
        }

        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(productForm.getName());
        product.setCategory(productService.resolveCategory(productForm.getCategory()));
        product.setPrice(productForm.getPrice());
        product.setDescription(productForm.getDescription());

        productService.updateProduct(product);

        redirectAttributes.addFlashAttribute("successMessage",
                "'" + product.getName() + "' 상품 정보가 수정되었습니다.");
        return "redirect:/products/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {

        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        String productName = product.getName();
        productService.deleteProduct(id);

        redirectAttributes.addFlashAttribute("successMessage",
                "'" + productName + "' 상품이 삭제되었습니다.");
        return "redirect:/products";
    }
}
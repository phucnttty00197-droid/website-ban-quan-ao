package com.example.demo.Controller.admin;

import com.example.demo.Model.product.Categories;
import com.example.demo.Service.product.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CategoryAController {
    private final CategoryService categoryService;

    @GetMapping("/admin/category/index")
    public String index(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        Categories category = new Categories();
        category.setId(buildNextCategoryId());
        model.addAttribute("category", category);
        return "admin/category";
    }
    @PostMapping("/admin/category/create")
    public String create(@RequestParam("id") String id,
                         @RequestParam("name") String name) {
        if (id == null || id.isBlank()) {
            id = buildNextCategoryId();
        }
        Categories category = new Categories();
        category.setId(id);
        category.setName(name);
        categoryService.create(category);
        return "redirect:/admin/category/index";
    }

    @GetMapping("/admin/category/edit/{id}")
    public String edit(@PathVariable("id") String id, Model model) {
        Optional<Categories> category = categoryService.findById(id);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("category", category.orElseGet(Categories::new));
        return "admin/category";
    }

    @PostMapping("/admin/category/update")
    public String update(@RequestParam("id") String id,
                         @RequestParam("name") String name) {
        Categories category = new Categories();
        category.setId(id);
        category.setName(name);
        categoryService.update(category);
        return "redirect:/admin/category/index";
    }
    @GetMapping("/admin/category/delete/{id}")
    public String delete(@PathVariable("id") String id) {
        categoryService.deleteById(id);
        return "redirect:/admin/category/index";
    }
    private String buildNextCategoryId() {
        int max = 0;
        for (Categories category : categoryService.findAll()) {
            String currentId = category.getId();
            if (currentId == null || !currentId.startsWith("CAT")) {
                continue;
            }
            String numberPart = currentId.substring(3);
            if (!numberPart.matches("\\d+")) {
                continue;
            }
            int value = Integer.parseInt(numberPart);
            if (value > max) {
                max = value;
            }
        }
        return String.format("CAT%02d", max + 1);
    }
}

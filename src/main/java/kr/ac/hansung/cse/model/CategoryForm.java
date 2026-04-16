package kr.ac.hansung.cse.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryForm {

    @NotBlank(message = "카테고리 이름은 필수입니다.")
    @Size(max = 50, message = "카테고리 이름은 50자 이하여야 합니다.")
    private String name;

    public CategoryForm() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
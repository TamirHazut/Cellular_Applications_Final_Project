package com.example.lets_plan.data;

import java.util.Objects;

public class Category implements Comparable<Category> {
    private String name;
    private int count;

    public Category(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void addCount(int numberToAdd) {
        if (numberToAdd > 0) {
            this.count += numberToAdd;
        }
    }

    public void substractCount(int numberToSubstract) {
        if (numberToSubstract > 0) {
            this.count -= numberToSubstract;
        }
    }

    @Override
    public int compareTo(Category other) {
        return this.getName().compareTo(other.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return this.name + " (" + this.count + ")";
    }
}

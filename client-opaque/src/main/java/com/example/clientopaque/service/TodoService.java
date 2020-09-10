package com.example.clientjwt.service;

import com.example.clientjwt.service.dto.Todo;

import java.util.List;

public interface TodoService {

    public List<Todo> findAll();

    public void save(Todo todo);

    public void updateDoneById(Integer id);

    public void deleteById(Integer id);
}

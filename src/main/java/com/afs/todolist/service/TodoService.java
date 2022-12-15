package com.afs.todolist.service;

import com.afs.todolist.entity.Todo;
import com.afs.todolist.exception.InvalidIdException;
import com.afs.todolist.exception.TodoNotFoundException;
import com.afs.todolist.repository.TodoRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    public Todo create(Todo todo) {
        return todoRepository.save(todo);
    }

    private void validateObjectId(String id){
        if(!ObjectId.isValid(id)){
            throw new InvalidIdException(id);
        }
    }

    public Todo update(String id, Todo updatedTodo) {
        validateObjectId(id);
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        if (updatedTodo.getDone() != null) {
            existingTodo.setDone(updatedTodo.getDone());
        }
        if (updatedTodo.getText() != null) {
            existingTodo.setText(updatedTodo.getText());
        }
        return todoRepository.save(existingTodo);

    }

    public Todo findById(String id) {
        validateObjectId(id);
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

}

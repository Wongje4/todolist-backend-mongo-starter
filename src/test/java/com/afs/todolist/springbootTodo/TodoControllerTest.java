package com.afs.todolist.springbootTodo;

import com.afs.todolist.entity.Todo;
import com.afs.todolist.repository.TodoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTest {

    @Autowired
    MockMvc client;

    @Autowired
    TodoRepository todoRepository;

    @BeforeEach
    void cleanRepository() {
        todoRepository.deleteAll();
    }

    @Test
    void should_get_all_todos_when_perform_get_given_todos() throws Exception {
        //given
        String todoId = new ObjectId().toString();
        todoRepository.save(new Todo(todoId, "testing text1", true));

        //when & then
        client.perform(MockMvcRequestBuilders.get("/todos"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text").value("testing text1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].done").value(true));
    }

    @Test
    void should_return_updated_todo_done_when_perform_put_given_todo_id_and_done() throws Exception {
        //given
        String todoId = new ObjectId().toString();
        todoRepository.save(new Todo(todoId, "testing text1", true));
        Todo updateTodo = (new Todo(todoId, null, false));

        String updateTodoJson = new ObjectMapper().writeValueAsString(updateTodo);

        //when
        client.perform(MockMvcRequestBuilders.put("/todos/{id}", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTodoJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("text").value("testing text1"))
                .andExpect(MockMvcResultMatchers.jsonPath("done").value(false));

        // then
        final Todo updatedTodo = todoRepository.findAll().get(0);
        assertThat(updatedTodo.getText(), equalTo("testing text1"));
        assertThat(updatedTodo.getDone(), equalTo(false));
    }

    @Test
    void should_return_updated_todo_text_when_perform_put_given_todo_id_and_text() throws Exception {
        //given
        String todoId = new ObjectId().toString();
        todoRepository.save(new Todo(todoId, "testing text1", true));
        Todo updateTodo = (new Todo(todoId, "updated_text", true));

        String updateTodoJson = new ObjectMapper().writeValueAsString(updateTodo);

        //when
        client.perform(MockMvcRequestBuilders.put("/todos/{id}", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateTodoJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("text").value("updated_text"))
                .andExpect(MockMvcResultMatchers.jsonPath("done").value(true));

        // then
        final Todo updatedTodo = todoRepository.findAll().get(0);
        assertThat(updatedTodo.getText(), equalTo("updated_text"));
        assertThat(updatedTodo.getDone(), equalTo(true));
    }

    @Test
    void should_create_new_todo_when_perform_post_given_new_todo() throws Exception {
        //given
        String todoId = new ObjectId().toString();
        Todo newTodo = new Todo(todoId, "newtext", false);
        String newTodoJson = new ObjectMapper().writeValueAsString(newTodo);

        //when
        client.perform(MockMvcRequestBuilders.post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newTodoJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("text").value("newtext"))
                .andExpect(MockMvcResultMatchers.jsonPath("done").value(false));

        // then
        final List<Todo> todos = todoRepository.findAll();
        assertThat(todos.get(0).getText(), equalTo("newtext"));
        assertThat(todos.get(0).getDone(), equalTo(false));

    }

}

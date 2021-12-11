package arp.controller;

import arp.dto.GridInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GridControllerTest {
    public static final String BASE_DIR = "./src/test/resources/";

    @Autowired
    private MockMvc mvc;


    @Test
    public void exampleTest() throws Exception {
        File file = new File(BASE_DIR + "validate_grid_in.json");
        GridInput gridInput = new ObjectMapper().readValue(file, GridInput.class);
        this.mvc.perform(post("/validateGrid", gridInput)).andExpect(status().isOk())
                .andExpect(content().string("Hello World"));
    }

}
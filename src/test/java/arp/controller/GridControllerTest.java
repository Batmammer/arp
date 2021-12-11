package arp.controller;

import arp.dto.GridInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@JsonTest
class GridControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester jacksonTester;

    @Test
    public void exampleTest() throws Exception {
        GridInput gridInput = (GridInput)jacksonTester.readObject("validate_grid_in.json");
        this.mvc.perform(get("/validateGrid", gridInput)).andExpect(status().isOk())
                .andExpect(content().string("Hello World"));
    }

}
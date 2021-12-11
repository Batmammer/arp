package arp.controller;

import arp.dto.GridInput;
import arp.dto.GridResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

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
    public void validateGridTest()  {
        String baseFileName = "validate_grid";
        String jsonInput = readJsonFromFiles(baseFileName + "_in.json", GridInput.class);
        String jsonOutput = readJsonFromFiles(baseFileName + "_expected.json", GridResult.class);

        postEndpoint("/validateGrid", jsonInput, jsonOutput);
    }

    private void postEndpoint(String urlTemplate, String jsonInput, String jsonExpected) {
        try {
            this.mvc.perform(post(urlTemplate)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonInput)
            ).andExpect(status().isOk())
                    .andExpect(content().string(jsonExpected));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private <T> String readJsonFromFiles(String fileName, Class<T> jsonClassType) {
        try {
            File file = new File(BASE_DIR + fileName);
            ObjectMapper objectMapper = new ObjectMapper();
            T gridInput = objectMapper.readValue(file, jsonClassType);
            String jsonInput = objectMapper.writeValueAsString(gridInput);
            return jsonInput;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
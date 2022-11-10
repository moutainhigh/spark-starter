package sample.doc.restdoc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.spark.starter.common.constant.App;
import info.spark.starter.test.SparkTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import sample.doc.restdoc.entity.Category;
import sample.doc.restdoc.entity.Pet;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.22 17:35
 * @since 1.0.0
 */
@SparkTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/asciidoc/snippets")
class SampleRestdocApplicationTest {

    static {
        System.setProperty(App.START_TYPE, App.START_JUNIT);
    }

    /** Mvc */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Create springfox swagger json *
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @Test
    public void createSpringfoxSwaggerJson() throws Exception {
        String outputDir = "target/swagger";
        MvcResult result = this.mockMvc.perform(get("/v2/api-docs").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = result.getResponse();
        String swaggerJson = response.getContentAsString();
        Files.createDirectories(Paths.get(outputDir));
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir, "swagger.json"), StandardCharsets.UTF_8)) {
            writer.write(swaggerJson);
        }
    }

    /**
     * Should return default message *
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
            .andExpect(status().isOk())
            .andDo(document("home"));
    }

    /**
     * Add a new pet to the store *
     *
     * @throws Exception exception
     * @since 1.0.0
     */
    @Test
    public void addANewPetToTheStore() throws Exception {
        this.mockMvc.perform(post("/pets/")
                            .content(this.createPet())
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("access_token", "access_token")
                            .header("user_uuid", "user_uuid"))
            .andExpect(status().isOk())
            .andDo(document("addPetUsingPOST",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                headerWithName("access_token").description("Basic auth credentials"),
                                headerWithName("user_uuid").description("User Uuid Key")
                                          ),
                            requestBody(

                                       ),
                            responseFields(
                                fieldWithPath("code").description("状态码").type(JsonFieldType.NUMBER),
                                fieldWithPath("message").description("返回消息"),
                                fieldWithPath("type").description(""),
                                fieldWithPath("data").description("承载数据"),
                                fieldWithPath("success").description("成功状态"),
                                fieldWithPath("traceId").description("追溯标识")
                                          )));
    }

    /**
     * Create pet string
     *
     * @return the string
     * @throws JsonProcessingException json processing exception
     * @since 1.0.0
     */
    private String createPet() throws JsonProcessingException {
        Pet pet = new Pet();
        pet.setId(1L);
        pet.setName("英短");
        Category category = new Category(1L, "猫");
        pet.setCategory(category);
        return new ObjectMapper().writeValueAsString(pet);
    }


}

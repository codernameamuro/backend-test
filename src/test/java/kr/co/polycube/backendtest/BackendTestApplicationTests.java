package kr.co.polycube.backendtest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.polycube.backendtest.Domain.user.Dto.postUserDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BackendTestApplicationTests {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    void GenerateUser_And_checkPostForEntityUser() throws Exception {
        String url = "http://localhost:8080/users";

        for (int i = 0; i < 10; i++) {
            postUserDto postUserDto = new postUserDto();
            postUserDto.setName("user" + i);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, postUserDto, Map.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                String responseBody = response.getBody().toString();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                assertNotNull(jsonNode.get("id"), "Response body does not contain 'id'");
                assertNotNull(jsonNode.get("result"), "Response body does not contain 'result'");
            }

        }
    }

    @Test
    @Order(2)
    void GenerateLotto_And_checkPostForEntityLotto() throws Exception {
        String url = "http://localhost:8080/lottos";

        for (int i = 0; i < 1000; i++) {
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {

                String responseBody = response.getBody();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                assertNotNull(jsonNode.get("result"), "Response body does not contain 'result'");
                assertNotNull(jsonNode.get("round"), "Response body does not contain 'round'");
                assertNotNull(jsonNode.get("numbers"), "Response body does not contain 'numbers'");
                if (jsonNode.get("numbers").size() != 6) {
                    fail("Response body does not contain 6 numbers");
                }
                assertNotNull(jsonNode.get("bonusNumber"), "Response body does not contain 'bonusNumber'");

                System.out.println("Request " + (i+1) + " failed with status code: " + response.getStatusCode());
            }
        }

        System.out.println("Completed 500000 requests.");
    }


    @Test
    @Order(3)
    public void check_getForEntity_User() throws Exception {
        String url = "http://localhost:8080/users/1";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Request failed with status code: " + response.getStatusCode());

        String responseBody = response.getBody();
        assertNotNull(responseBody, "Response body is null");

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        assertTrue(jsonNode.has("id"), "Response body does not contain 'id'");
        assertTrue(jsonNode.has("name"), "Response body does not contain 'name'");
    }


    @Test
    @Order(4)
    public void check_putForEntity_User() throws Exception {
        String url = "http://localhost:8080/users/1";
        postUserDto postUserDto = new postUserDto();
        postUserDto.setName("eunseong");

        restTemplate.put(url, postUserDto);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Request failed with status code: " + response.getStatusCode());

        String responseBody = response.getBody();
        assertNotNull(responseBody, "Response body is null");

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        assertTrue(jsonNode.has("id"), "Response body does not contain 'id'");
        assertTrue(jsonNode.has("name"), "Response body does not contain 'name'");

        assertEquals("eunseong", jsonNode.get("name").asText(), "Name is not updated");

        ResponseEntity<String> response2 = restTemplate.getForEntity(url, String.class);
        String responseBody2 = response2.getBody();
        JsonNode jsonNode2 = objectMapper.readTree(responseBody2);
        assertEquals("eunseong", jsonNode2.get("name").asText(), "Name is not updated");
    }

    @Test
    @Order(5)
    public void check_InValidUrl() throws Exception {
        String url = "http://localhost:8080/users/1?name=test!!";

        try {
            restTemplate.getForEntity(url, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode(), "Request failed with status code: " + e.getStatusCode());
        }
    }

    @Test
    @Order(6)
    public void check_NotExistUrl() throws Exception {
        String url = "http://localhost:8080/notexistUrl";

        try {
            restTemplate.getForEntity(url, String.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode(), "Request failed with status code: " + e.getStatusCode());
            String responseBody = e.getResponseBodyAsString();
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(responseBody);
            } catch (Exception ex) {
                fail("Response body is not a valid JSON");
            }
            assertTrue(jsonNode.has("reason"), "Response body does not contain 'reason'");
            assertEquals("API endpoint not found", jsonNode.get("reason").asText(), "Reason is not 'API endpoint not found'");
        }
    }


}

package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerIntegrationTest {

    static MockWebServer mockWebServer;

    static {
        try {
            mockWebServer = new MockWebServer();
            mockWebServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("google.books.base-url",
                () -> mockWebServer.url("/").toString());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @AfterAll
    static void shutdown() throws Exception {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void cleanDb() {
        bookRepository.deleteAll();
    }


    @Test
    void shouldAddBookFromGoogle() throws Exception {

        String mockResponse = """
        {
          "id": "test123",
          "volumeInfo": {
            "title": "Effective Java",
            "authors": ["Joshua Bloch"],
            "pageCount": 412
          }
        }
        """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(mockResponse)
                        .addHeader("Content-Type", "application/json")
        );

        mockMvc.perform(post("/books/test123"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("test123"))
                .andExpect(jsonPath("$.title").value("Effective Java"))
                .andExpect(jsonPath("$.author").value("Joshua Bloch"))
                .andExpect(jsonPath("$.pageCount").value(412));

        assertEquals(1, bookRepository.count());
    }


	@Test
	void shouldReturnBadRequestForInvalidGoogleId() throws Exception {

		mockWebServer.enqueue(new MockResponse().setResponseCode(404));

		mockMvc.perform(post("/books/invalid-id")).andExpect(status().isBadRequest());

		assertEquals(0, bookRepository.count());
	}

	@Test
	void shouldReturnAllBooks() throws Exception {

		bookRepository.save(new Book("1", "Test Book", "Author", 200));

		mockMvc.perform(get("/books")).andExpect(status().isOk()).andExpect(jsonPath("$[0].title").value("Test Book"));
	}
}

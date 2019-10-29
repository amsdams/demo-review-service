package com.amsdams.demoreviewservice;



import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ReviewControllerTest {

    @MockBean
    private ReviewService service;

    @Autowired
    private MockMvc mockMvc;

    /**
     * Create a DateFormat that we can use to compare SpringMVC returned dates to expected values.
     */
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @BeforeAll
    static void beforeAll() {
        // Spring's dates are configured to GMT, so adjust our timezone accordingly
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @Test
    @DisplayName("GET /review/reviewId - Found")
    void testGetReviewByIdFound() throws Exception {
        // Setup our mocked service
		Review mockReview = new Review("reviewId", 1, 1);
        Date now = new Date();
        mockReview.getEntries().add(new ReviewEntry("test-user", now, "Great product"));
        doReturn(Optional.of(mockReview)).when(service).findById("reviewId");

        // Execute the GET request
        mockMvc.perform(get("/review/{id}", "reviewId"))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/review/reviewId"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is("reviewId")))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.entries.length()", is(1)))
                .andExpect(jsonPath("$.entries[0].username", is("test-user")))
                .andExpect(jsonPath("$.entries[0].review", is("Great product")))
                .andExpect(jsonPath("$.entries[0].date", is(df.format(now))));
    }
    @Test
    @DisplayName("GET /reviews - Found")
    void testGetReviewsFound() throws Exception {
        // Setup our mocked service

        List<Review> mockReviews = new ArrayList<>();
        
        mockReviews.add(new Review("reviewId", 1, 1));
        mockReviews.add(new Review("reviewId", 2, 2));
        
        doReturn(mockReviews).when(service).findAll();

        // Execute the GET request
        mockMvc.perform(get("/reviews"))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the headers

                // Validate the returned fields
                .andExpect(jsonPath("$[0].id", is("reviewId")))
                .andExpect(jsonPath("$[0].productId", is(1)))
                .andExpect(jsonPath("$[0].version", is(1)))
                .andExpect(jsonPath("$[0].entries.length()", is(0)))
                .andExpect(jsonPath("$[1].id", is("reviewId")))
                .andExpect(jsonPath("$[1].productId", is(2)))
                .andExpect(jsonPath("$[1].version", is(2)))

                .andExpect(jsonPath("$[1].entries.length()", is(0)));
                
    }
    
    @Test
    @DisplayName("GET /reviews/?productId=1 - Found")
    void testGetReviewsByProductIdFound() throws Exception {
        // Setup our mocked service
    	int productId = 0;
		Review mockReview = new Review("reviewId", productId, 1);
        
        doReturn(Optional.of(mockReview)).when(service).findByProductId(productId);

        // Execute the GET request
        mockMvc.perform(get("/reviews/?productId={productId}", productId))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the headers

                // Validate the returned fields
                .andExpect(jsonPath("$[0].id", is("reviewId")))
                .andExpect(jsonPath("$[0].productId", is(0)))
                .andExpect(jsonPath("$[0].version", is(1)))
                .andExpect(jsonPath("$[0].entries.length()", is(0)))
                ;
                
    }
    
    @Test
    @DisplayName("GET /review/reviewId - Not Found")
    void testGetReviewByIdNotFound() throws Exception {
        // Setup our mocked service
        doReturn(Optional.empty()).when(service).findById("reviewId");

        // Execute the GET request
        mockMvc.perform(get("/review/{id}", "reviewId"))

                // Validate that we get a 404 Not Found response
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DEL /review/reviewId - Found")
    void testDeleteReviewOk() throws Exception {
        // Setup our mocked service
    	Review mockReview = new Review("reviewId", 1, 1);
        Date now = new Date();
        mockReview.getEntries().add(new ReviewEntry("test-user", now, "Great product"));
        
    	doReturn(Optional.of(mockReview)).when(service).findById("reviewId");
    	
    	
        //doReturn().when(service).delete("reviewId");

        // Execute the GET request
        mockMvc.perform(delete("/review/{id}", "reviewId"))

                // Validate that we get a 200 OK response
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("DEL /review/reviewId - Not Found")
    void testDeleteReviewNotFound() throws Exception {
        // Setup our mocked service
    	/*Review mockReview = new Review("reviewId", 1, 1);
        Date now = new Date();
        mockReview.getEntries().add(new ReviewEntry("test-user", now, "Great product"));
        
    	doReturn(Optional.of(mockReview)).when(service).findById("reviewId");
    	*/
    	
        //doReturn().when(service).delete("reviewId");

        // Execute the GET request
        mockMvc.perform(delete("/review/{id}", "reviewId"))

                // Validate that we get a 404 Not Found response
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("POST /review - Success")
    void testCreateReview() throws Exception {
        // Setup mocked service
        Date now = new Date();
        Review postReview = new Review(1);
        postReview.getEntries().add(new ReviewEntry("test-user", now, "Great product"));

        Review mockReview = new Review("reviewId", 1, 1);
        mockReview.getEntries().add(new ReviewEntry("test-user", now, "Great product"));

        doReturn(mockReview).when(service).save(any());

        mockMvc.perform(post("/review")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(mockReview)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/review/reviewId"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is("reviewId")))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.entries.length()", is(1)))
                .andExpect(jsonPath("$.entries[0].username", is("test-user")))
                .andExpect(jsonPath("$.entries[0].review", is("Great product")))
                .andExpect(jsonPath("$.entries[0].date", is(df.format(now))));
    }

    @Test
    @DisplayName("POST /review/{productId}/entry - Created")
    void testAddEntryToReview() throws Exception {
        // Setup mocked service
        Date now = new Date();
        ReviewEntry reviewEntry = new ReviewEntry("test-user", now, "Great product");
        Review mockReview = new Review("1", 1, 1);
        Review returnedReview = new Review("1", 1, 2);
        returnedReview.getEntries().add(reviewEntry);

        // Handle lookup
        doReturn(Optional.of(mockReview)).when(service).findByProductId(1);

        // Handle save
        doReturn(returnedReview).when(service).save(any());

        mockMvc.perform(post("/review/{productId}/entry", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(reviewEntry)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, "\"2\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/review/1"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.entries.length()", is(1)))
                .andExpect(jsonPath("$.entries[0].username", is("test-user")))
                .andExpect(jsonPath("$.entries[0].review", is("Great product")));
    }


    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
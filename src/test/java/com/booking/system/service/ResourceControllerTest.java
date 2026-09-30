package com.booking.system.service;



import com.booking.system.controller.ResourceController;
import com.booking.system.domain.ResourceType;
import com.booking.system.dto.ResourceRequest;
import com.booking.system.dto.ResourceResponse;
import com.booking.system.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResourceController.class)
@Import(TestSecurityConfig.class)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ResourceService resourceService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("Should retrieve all resources")
    void getAllResources_Success() throws Exception {

        ResourceResponse response = new ResourceResponse(1L, "Conference Room", ResourceType.ROOM, new BigDecimal("50.00"), true);
        when(resourceService.getAllResources()).thenReturn(Collections.singletonList(response));

        
        mockMvc.perform(get("/api/v1/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Conference Room"))
                .andExpect(jsonPath("$[0].type").value("ROOM"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    @DisplayName("Should retrieve resource by ID")
    void getResourceById_Success() throws Exception {
        
        ResourceResponse response = new ResourceResponse(1L, "Conference Room", ResourceType.ROOM, new BigDecimal("50.00"), true);
        when(resourceService.getResourceById(1L)).thenReturn(response);

        
        mockMvc.perform(get("/api/v1/resources/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Conference Room"))
                .andExpect(jsonPath("$.type").value("ROOM"));
    }

    @Test
    @DisplayName("Should create a resource successfully")
    void createResource_Success() throws Exception {
        

        ResourceRequest request = new ResourceRequest("Conference Room", ResourceType.ROOM, new BigDecimal("50.00"), true);

        ResourceResponse response = new ResourceResponse(1L, "Conference Room", ResourceType.ROOM, new BigDecimal("50.00"), true);

        when(resourceService.createResource(any(ResourceRequest.class))).thenReturn(response);

        
        mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Conference Room"));
    }

    @Test
    @DisplayName("Should update resource details successfully")
    void updateResource_Success() throws Exception {
        

        ResourceRequest request = new ResourceRequest("Updated Room", ResourceType.ROOM, new BigDecimal("60.00"), true);


        ResourceResponse response = new ResourceResponse(1L, "Updated Room", ResourceType.ROOM, new BigDecimal("60.00"), true);

        when(resourceService.updateResource(eq(1L), any(ResourceRequest.class))).thenReturn(response);

        
        mockMvc.perform(put("/api/v1/resources/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Room"))
                .andExpect(jsonPath("$.pricePerHour").value(60.00));
    }

    @Test
    @DisplayName("Should delete resource and return 204 No Content status")
    void deleteResource_Success() throws Exception {
        
        doNothing().when(resourceService).deleteResource(1L);

        
        mockMvc.perform(delete("/api/v1/resources/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when resource price is below minimum allowed value")
    void createResource_InvalidPrice_ReturnsBadRequest() throws Exception {
        ResourceRequest invalidRequest = new ResourceRequest("Test Room", ResourceType.ROOM, new BigDecimal("0.00"), true);

        
        mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}

package com.booking.system.service;



import com.booking.system.controller.ReservationController;
import com.booking.system.domain.ReservationStatus;
import com.booking.system.domain.ResourceType;
import com.booking.system.domain.Role;
import com.booking.system.domain.User;
import com.booking.system.dto.ReservationRequest;
import com.booking.system.dto.ReservationResponse;
import com.booking.system.dto.ResourceResponse;
import com.booking.system.dto.StatusUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@Import(TestSecurityConfig.class)




class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    private User createMockUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@booking.local");
        user.setRole(Role.ROLE_USER);
        return user;
    }


    private ReservationResponse createMockReservationResponse(Long reservationId, ReservationStatus status) {
        ResourceResponse resourceMock = new ResourceResponse(
                1L,
                "Conference Room",
                ResourceType.ROOM,
                new BigDecimal("50.00"),
                true
        );

        return new ReservationResponse(
                reservationId,
                1L, // userId
                "user@booking.local", // userEmail
                resourceMock, // ResourceResponse sub-record
                LocalDateTime.now().plusDays(1), // startTime
                LocalDateTime.now().plusDays(2), // endTime
                new BigDecimal("150.00"), // totalPrice
                status // status enum
        );
    }

    @Test
    @DisplayName("POST /api/v1/reservations - Success Scenario")
    void createReservation_Success() throws Exception {
        
        ReservationRequest request = new ReservationRequest(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        ReservationResponse response = createMockReservationResponse(1L, ReservationStatus.PENDING);
        User mockUser = createMockUser();

        when(reservationService.createReservation(any(ReservationRequest.class), eq("user@booking.local")))
                .thenReturn(response);

        
        mockMvc.perform(post("/api/v1/reservations")
                        .with(user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userEmail").value("user@booking.local"))
                .andExpect(jsonPath("$.resource.name").value("Conference Room"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/v1/reservations - Success Scenario")
    void getReservations_Success() throws Exception {
        
        ReservationResponse response = createMockReservationResponse(1L, ReservationStatus.CONFIRMED);
        Page<ReservationResponse> pageResponse = new PageImpl<>(Collections.singletonList(response));
        User mockUser = createMockUser();

        when(reservationService.getReservations(any(), any(), any(), any(User.class), any(Pageable.class)))
                .thenReturn(pageResponse);

        
        mockMvc.perform(get("/api/v1/reservations")
                        .with(user(mockUser))
                        .param("status", "CONFIRMED")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].resource.type").value("ROOM"))
                .andExpect(jsonPath("$.content[0].status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("GET /api/v1/reservations/{id} - Success Scenario")
    void getReservationById_Success() throws Exception {
        
        ReservationResponse response = createMockReservationResponse(1L, ReservationStatus.CONFIRMED);
        User mockUser = createMockUser();

        when(reservationService.getReservationById(eq(1L), any(User.class))).thenReturn(response);

        
        mockMvc.perform(get("/api/v1/reservations/{id}", 1L)
                        .with(user(mockUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalPrice").value(150.00));
    }

    @Test
    @DisplayName("PUT /api/v1/reservations/{id}/status - Success Scenario")
    void updateReservationStatus_Success() throws Exception {
        
        StatusUpdateRequest request = new StatusUpdateRequest(ReservationStatus.CONFIRMED);
        ReservationResponse response = createMockReservationResponse(1L, ReservationStatus.CONFIRMED);
        User mockUser = createMockUser();

        when(reservationService.updateReservationStatus(eq(1L), eq(ReservationStatus.CONFIRMED), any(User.class)))
                .thenReturn(response);

        
        mockMvc.perform(put("/api/v1/reservations/{id}/status", 1L)
                        .with(user(mockUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
}

package com.booking.system.service;

import com.booking.system.domain.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; 
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.booking.system.domain.Role;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerSecurityReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    @WithMockUser(username = "user@booking.com", roles = "USER")
    void createResource_AsUserRole_ShouldReturnForbiddenHttpStatus() throws Exception {
        User mockUser = new User();
        mockUser.setEmail("user@booking.local");
        mockUser.setRole(Role.ROLE_USER);

        mockMvc.perform(post("/api/v1/resources")
                        .with(user(mockUser)) 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Conference Auditorium X\",\"type\":\"ROOM\",\"pricePerHour\":50.00,\"available\":true}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@booking.com", roles = "ADMIN")
    void getReservations_WithAuthorizedContext_ShouldReturnSuccess() throws Exception {
         
        User mockAdmin = new User();
        mockAdmin.setEmail("admin@booking.local");
        mockAdmin.setRole(Role.ROLE_ADMIN);

        when(reservationService.getReservations(any(), any(), any(), any(User.class), any()))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/reservations")
                        .with(user(mockAdmin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

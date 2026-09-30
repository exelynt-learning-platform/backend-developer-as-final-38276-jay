package com.booking.system.service;

import com.booking.system.domain.Resource;
import com.booking.system.domain.User;
import com.booking.system.dto.ReservationRequest;
import com.booking.system.exception.BadRequestException;
import com.booking.system.repository.ReservationRepository;
import com.booking.system.repository.ResourceRepository;
import com.booking.system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceReservationControllerTest {

    @Mock private ResourceRepository resourceRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void createReservation_WithChronologicallyInvertedDates_ShouldThrowBadRequestException() {
        
        String testEmail = "user@booking.com";
        ReservationRequest invalidRequest = new ReservationRequest(
                1L,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(2)
        );


        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(new User()));


        Resource mockResource = new Resource();
        mockResource.setId(1L);
        mockResource.setAvailable(true);
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(mockResource));

        assertThrows(BadRequestException.class, () ->
                reservationService.createReservation(invalidRequest, testEmail)
        );
    }

    @Test
    void createReservation_WhenResourceIsUnavailable_ShouldThrowBadRequestException() {
        
        String testEmail = "user@booking.com";
        ReservationRequest request = new ReservationRequest(
                1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2)
        );

        Resource lockedResource = new Resource();
        lockedResource.setId(1L);
        lockedResource.setAvailable(false);


        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(new User()));
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(lockedResource));

        assertThrows(BadRequestException.class, () ->
                reservationService.createReservation(request, testEmail)
        );
    }
}

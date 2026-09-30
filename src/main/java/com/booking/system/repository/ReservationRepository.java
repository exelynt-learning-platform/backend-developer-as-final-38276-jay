package com.booking.system.repository;

import com.booking.system.domain.Reservation;
import com.booking.system.domain.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {


    @Query("SELECT r FROM Reservation r " +
            "WHERE (:status IS NULL OR r.status = :status) " +
            "AND (:email IS NULL OR r.user.email = :email) " +
            "AND (:minPrice IS NULL OR r.totalPrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR r.totalPrice <= :maxPrice)")
    Page<Reservation> findReservationsWithFilters(
            @Param("status") ReservationStatus status,
            @Param("email") String email,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
}
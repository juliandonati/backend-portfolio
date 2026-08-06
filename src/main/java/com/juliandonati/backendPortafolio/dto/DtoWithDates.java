package com.juliandonati.backendPortafolio.dto;

import java.time.LocalDate;

public interface DtoWithDates {
    LocalDate getStartDate();
    LocalDate getEndDate();
}

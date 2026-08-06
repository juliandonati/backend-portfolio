package com.juliandonati.backendPortafolio.validation;

import com.juliandonati.backendPortafolio.dto.DtoWithDates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<AfterStartDate, DtoWithDates> {
    @Override
    public boolean isValid(DtoWithDates dtoWithDates, ConstraintValidatorContext constraintValidatorContext) {
        if(dtoWithDates.getStartDate() == null || dtoWithDates.getEndDate() == null)
            return true;

        boolean isValid = dtoWithDates.getEndDate().isAfter(dtoWithDates.getStartDate());

        if(!isValid){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                    constraintValidatorContext.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
        }


        return isValid;
    }
}

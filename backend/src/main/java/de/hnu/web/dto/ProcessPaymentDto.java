package de.hnu.web.dto;

import de.hnu.domain.enums.PaymentMethod;

public class ProcessPaymentDto {
    public Integer bookingId;
    public PaymentMethod paymentMethod;
    public String cardNumber;
    public String cardExpiry;
    public String cardCvv;
    public String cardName;
    public String paypalEmail;
}

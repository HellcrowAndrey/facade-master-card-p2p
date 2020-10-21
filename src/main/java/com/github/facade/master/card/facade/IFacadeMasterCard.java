package com.github.facade.master.card.facade;

import com.github.facade.master.card.payload.MastercardPaymentTransfer;

import java.util.Optional;

public interface IFacadeMasterCard {

    Optional<String> send(MastercardPaymentTransfer paymentTransfer);

}

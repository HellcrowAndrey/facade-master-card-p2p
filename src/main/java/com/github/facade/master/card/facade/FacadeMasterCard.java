package com.github.facade.master.card.facade;

import com.github.facade.master.card.payload.MastercardPaymentTransfer;
import com.mastercard.api.core.ApiConfig;
import com.mastercard.api.core.exception.ApiException;
import com.mastercard.api.core.model.RequestMap;
import com.mastercard.api.core.security.oauth.OAuthAuthentication;
import com.mastercard.api.p2p.PaymentTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

public class FacadeMasterCard implements IFacadeMasterCard {

    private static final Logger log = LoggerFactory.getLogger(FacadeMasterCard.class);

    private final String consumerKey;

    private final String keyAlias;

    private final String keyPassword;

    private final String privateKey;

    private final boolean isDebug;

    private final boolean isSandbox;

    public FacadeMasterCard(String consumerKey, String keyAlias, String keyPassword, String privateKey, boolean isDebug, boolean isSandbox) {
        this.consumerKey = consumerKey;
        this.keyAlias = keyAlias;
        this.keyPassword = keyPassword;
        this.privateKey = privateKey;
        this.isDebug = isDebug;
        this.isSandbox = isSandbox;
    }

    public void init() {
        try {
            InputStream is = new FileInputStream(this.privateKey);
            ApiConfig.setAuthentication(new OAuthAuthentication(this.consumerKey, is, this.keyAlias, this.keyPassword));
            ApiConfig.setDebug(this.isDebug);
            ApiConfig.setSandbox(this.isSandbox);
        } catch (FileNotFoundException e) {
            log.error("Enter: {}", e.getMessage());
        }
    }

    @Override
    public Optional<String> send(MastercardPaymentTransfer paymentTransfer) {
        try {
            String reference = UUID.randomUUID().toString();
            RequestMap map = new RequestMap();
            map.set("partnerId", paymentTransfer.getPartnerId());
            map.set("payment_transfer.transfer_reference", reference);
            map.set("payment_transfer.funding_source", paymentTransfer.getFundingSource());
            map.set("payment_transfer.payment_type", paymentTransfer.getPaymentType());
            map.set("payment_transfer.amount", paymentTransfer.getAmount());
            map.set("payment_transfer.currency", paymentTransfer.getCurrency());
            map.set("payment_transfer.sender_account_uri", paymentTransfer.getSenderAccountUri());
            map.set("payment_transfer.sender.first_name", paymentTransfer.getSenderFirstName());
            map.set("payment_transfer.sender.last_name", paymentTransfer.getSenderLastName());
            map.set("payment_transfer.sender.address.line1", paymentTransfer.getSenderAddressLine1());
            map.set("payment_transfer.sender.address.city", paymentTransfer.getSenderCity());
            map.set("payment_transfer.sender.address.postal_code", paymentTransfer.getSenderPostalCode());
            map.set("payment_transfer.sender.address.country_subdivision", paymentTransfer.getSenderCountrySubdivision());
            map.set("payment_transfer.sender.address.country", paymentTransfer.getSenderCountry());
            map.set("payment_transfer.recipient_account_uri", paymentTransfer.getRecipientAccountUri());
            map.set("payment_transfer.recipient.first_name", paymentTransfer.getRecipientFirstName());
            map.set("payment_transfer.recipient.last_name", paymentTransfer.getRecipientLastName());
            map.set("payment_transfer.recipient.address.line1", paymentTransfer.getRecipientAddressLine1());
            map.set("payment_transfer.recipient.address.city", paymentTransfer.getRecipientCity());
            map.set("payment_transfer.recipient.address.postal_code", paymentTransfer.getRecipientPostalCode());
            if (paymentTransfer.getRecipientNameOnAccount() != null) {
                map.set("payment_transfer.recipient.name_on_account", paymentTransfer.getRecipientNameOnAccount());
            }
            PaymentTransfer.create(map);
            return Optional.ofNullable(reference);
        } catch (ApiException e) {
            log.error("Enter: {}", e.getMessage());
        }
        return Optional.empty();
    }

}

package de.hhu.propra.sharingplatform.model.contracts;

import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.payments.Payment;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;

public class SellContract extends Contract {

    private SellContract() {
    }

    public SellContract(Item item, String fromPropaiId, String toPropayId){
        super.item = item;
        //TODO: item sale price..
        int amount = item.getPrice();
        super.payment = new Payment(0, fromPropaiId, toPropayId);
    }

}

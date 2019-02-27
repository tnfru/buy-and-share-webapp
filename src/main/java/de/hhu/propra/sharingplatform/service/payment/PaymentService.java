package de.hhu.propra.sharingplatform.service.payment;

import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import org.springframework.stereotype.Service;


@Service
public class PaymentService implements IPaymentService {

    private final IPaymentApi apiService;

    public PaymentService(IPaymentApi apiService) {
        this.apiService = apiService;
    }

    @Override
    public void createPayment(Contract contract) {
        contract.prepare(apiService);
    }


    @Override
    public void transferPayment(BorrowContract contract) {
        contract.returnItem();
        contract.pay(apiService);
    }

    @Override
    public void freeBailReservation(BorrowContract contract) {
        contract.freeBail(apiService);
    }

    @Override
    public void freeChargeReservation(BorrowContract contract) {
        contract.freeCharge(apiService);
    }

    @Override
    public void punishBailReservation(BorrowContract contract) {
        contract.punishBail(apiService);
    }
}

package de.hhu.propra.sharingplatform.service.payment;

import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import org.springframework.stereotype.Service;


@Deprecated
@Service
public class PaymentService implements IPaymentService {

    private final PaymentRepo paymentRepo;
    private final IPaymentApi apiService;

    public PaymentService(PaymentRepo paymentRepo, IPaymentApi apiService) {
        this.paymentRepo = paymentRepo;
        this.apiService = apiService;
    }

    @Override
    public void createPayment(Contract contract) {
        contract.prepare(apiService);
    }

    @Override
    public boolean recipientSolvent(Contract contract) {
        //TODO
        return true;
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

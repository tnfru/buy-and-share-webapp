package de.hhu.propra.sharingplatform.service.payment;

import de.hhu.propra.sharingplatform.dao.PaymentRepo;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.model.payments.Payment;
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
    public Payment createPayment(Contract contract) {
        contract.prepare(apiService);
    }

    @Override
    public boolean recipientSolvent(Contract contract) {
        //TODO
        return true;
    }

    @Override
    public void transferPayment(Contract contract) {
        ((BorrowContract)contract).returnItem();
        contract.pay(apiService);
    }

    @Override
    public void freeBailReservation(Contract contract) {
        ((BorrowContract) contract).freeBail(apiService);
    }

    @Override
    public void punishBailReservation(Contract contract) {
        ((BorrowContract) contract).punishBail(apiService);
    }
}

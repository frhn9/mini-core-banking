package org.fd.mcb.modules.master.adapter.query.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.adapter.query.PaymentTypeQueryAdapter;
import org.fd.mcb.modules.master.model.entity.PaymentType;
import org.fd.mcb.modules.master.model.repository.PaymentTypeRepository;
import org.fd.mcb.shared.exception.PaymentTypeNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentTypeQueryAdapterImpl implements PaymentTypeQueryAdapter {

    private final PaymentTypeRepository paymentTypeRepository;

    @Override
    public PaymentType findByName(String name) {
        return paymentTypeRepository.findByName(name)
                .orElseThrow(PaymentTypeNotFoundException::new);
    }

}

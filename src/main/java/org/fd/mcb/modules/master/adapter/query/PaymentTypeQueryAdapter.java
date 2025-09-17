package org.fd.mcb.modules.master.adapter.query;

import org.fd.mcb.modules.master.model.entity.PaymentType;

public interface PaymentTypeQueryAdapter {

    PaymentType findByName(String name);

}

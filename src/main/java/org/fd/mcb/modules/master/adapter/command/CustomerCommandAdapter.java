package org.fd.mcb.modules.master.adapter.command;

import org.fd.mcb.modules.master.model.entity.Customer;

public interface CustomerCommandAdapter {
  Customer save(Customer customer);
}

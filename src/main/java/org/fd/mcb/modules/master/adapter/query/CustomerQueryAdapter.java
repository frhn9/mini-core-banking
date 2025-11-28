package org.fd.mcb.modules.master.adapter.query;

public interface CustomerQueryAdapter {
  void validateCustomerUniqueness(String cin, String email, String nationalId);
}

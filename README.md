# 🚧 FEATURES 🚧

Progress :

- [ ] **Customer Registration**
  - This feature allows new customers to be onboarded into the banking system. The process typically involves capturing personal information (name, address, national ID), performing Know Your Customer (KYC) checks to verify their identity, and creating a unique Customer Identification Number (CIN). Upon successful registration, a customer profile is created, and they can then open bank accounts.

- [ ] **Authentication with JWT and 2FA**
  - This feature provides secure access for customers.
    - **JWT (JSON Web Token)** is used to manage login sessions. After a customer logs in with their credentials, a JWT is generated and sent to the client. This token is then included in subsequent requests to prove that the customer is authenticated.
    - **2FA (Two-Factor Authentication)** adds an extra layer of security by requiring a second form of verification in addition to a password, such as a one-time code sent to their phone or email.

- [ ] **Staff Authentication**
  - This provides internal access to the banking system for employees. It typically uses a role-based access control (RBAC) system, where each staff member has a specific role (e.g., Teller, Admin, Auditor). This role determines what actions they can perform and what data they can access, ensuring that employees only have access to the information necessary for their job.

- [✅] **Deposits**
  - This feature allows customers to add funds to their bank accounts. This can be done through various channels, such as in-person at a branch with a teller, at an ATM, or via an electronic transfer from another bank. Each deposit results in a credit transaction to the customer's account.

- [✅] **Withdrawals**
  - This feature allows customers to take funds out of their bank accounts. Similar to deposits, withdrawals can be made at a branch, an ATM, or by making a payment or transfer to another account. Each withdrawal results in a debit transaction from the customer's account.

- [✅] **Transfer (Auth Payment, Capture, Settlement)**
  - This feature handles the process of moving funds from one account to another, and is broken down into three key steps:
    - **Authorization (Auth Payment):** When a transfer is initiated, the system first checks if the sender has sufficient funds and places a temporary hold on the transfer amount. This ensures the funds are available and reserved for the transaction.
    - **Capture:** Once the transfer is confirmed (e.g., after a 2FA verification), the funds are "captured" and debited from the sender's account.
    - **Settlement:** The final step is to credit the funds to the recipient's account, completing the transfer.

- [ ] **Notifications**
  - This feature keeps customers and staff informed about important events. Notifications can be sent for a variety of reasons, such as confirming a transaction, alerting to a low account balance, or notifying about a login from a new device. These are typically delivered via email, SMS, or push notifications to a mobile app.

- [ ] **Adding journal entries manually**
  - This is an administrative feature for bank staff. A journal entry is the fundamental record of a financial transaction, consisting of debits and credits. This feature allows authorized staff (like accountants or auditors) to manually create journal entries to correct errors, record offline transactions, or make other financial adjustments that are not handled by the automated system.

- [ ] **Auditing/Reporting**
  - This feature provides a comprehensive record of all activities within the system. Every significant action, from customer transactions to changes made by staff, is logged for security and compliance purposes. This allows the bank to generate reports for internal analysis, track down issues, and provide necessary documentation to external auditors or regulatory bodies.

- [ ] **Reconciliation scheduler at EoD**
  - This is an automated process that runs at the End of Day (EoD). It compares all the transactions recorded in the system throughout the day against a master ledger to ensure that all entries are consistent and that the books are balanced. This is a critical process for maintaining financial integrity and accuracy.

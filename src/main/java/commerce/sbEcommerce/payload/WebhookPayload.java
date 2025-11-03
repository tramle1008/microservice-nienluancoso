package commerce.sbEcommerce.payload;

public class WebhookPayload {
    public Long id;
    public String gateway;
    public String transactionDate;
    public String accountNumber;
    public String code;
    public String content;
    public String transferType;
    public Long transferAmount;
    public Long accumulated;
    public String subAccount;
    public String referenceCode;
    public String description;
}

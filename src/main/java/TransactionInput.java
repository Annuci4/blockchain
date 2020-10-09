public class TransactionInput {
    public String transactionOutputId;
    public TransactionOutput UnspentCoin;

    public TransactionInput(String transactionOutputId)
    {
        this.transactionOutputId = transactionOutputId;
    }
}
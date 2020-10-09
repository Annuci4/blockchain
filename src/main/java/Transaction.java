import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction{

    public PublicKey sender;
    public PublicKey recipient;
    public String signature;
    public float value;
    public long timeStamp;
    public String transactionId;
    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence=0;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs){
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public boolean processTransaction(){

        for(TransactionInput i : inputs) {
            i.UnspentCoin = MainChain.UnspentCoins.get(i.transactionOutputId);
        }

        float leftOver = getInputsValue() - value;
        transactionId = calculateTxHash();
        outputs.add(new TransactionOutput( this.recipient, value,transactionId));
        outputs.add(new TransactionOutput( this.sender, leftOver,transactionId));

        for(TransactionOutput o : outputs) {
            MainChain.UnspentCoins.put(o.id, o);
        }

        for(TransactionInput i : inputs) {
            if(i.UnspentCoin == null) continue;
            MainChain.UnspentCoins.remove(i.UnspentCoin.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UnspentCoin == null) {
                continue; //if Transaction can't be found skip it
            }
            total += i.UnspentCoin.value;
        }
        return total;
    }

    public float getOutputsValue() {
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }

    public String calculateTxHash(){
        sequence++;
        String calculatesTxHash = StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        value +
                        timeStamp +
                        sequence
        );
        return calculatesTxHash;
    }

    public void generateSignature(PrivateKey privateKey){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient) + Float.toString(value)	;
        signature = StringUtil.applySha256(privateKey+data);
    }
}


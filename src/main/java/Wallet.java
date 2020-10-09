import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;

    public HashMap<String,TransactionOutput> UnspentCoins = new HashMap<>();

    public Wallet() throws NoSuchAlgorithmException {
        generateKeyPar();
    }

    public void generateKeyPar() throws NoSuchAlgorithmException {

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair pair= keyPairGen.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
        System.out.println("Keys generated!");

    }

    public  float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : MainChain.UnspentCoins.entrySet()) {
            TransactionOutput UnspentCoin = item.getValue();
            if (UnspentCoin.isMine(publicKey)) {
                UnspentCoins.put(UnspentCoin.id, UnspentCoin);
                total += UnspentCoin.value;
            }
        }
        return total;
    }

    public Transaction sendCoins(PublicKey _recipient, float value ) {

        if(getBalance() < value) {
            System.out.println("Not Enough coins!");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: MainChain.UnspentCoins.entrySet()){
            TransactionOutput UnspentCoin = item.getValue();
            total += UnspentCoin.value;
            inputs.add(new TransactionInput(UnspentCoin.id));
            if(total > value)
                break;

        }

        Transaction newTransaction = new Transaction(publicKey, _recipient , value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs){
            UnspentCoins.remove(input.transactionOutputId);
        }

        return newTransaction;
    }

}

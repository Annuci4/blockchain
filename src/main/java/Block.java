import java.util.ArrayList;
import java.util.Date;

public class Block {

    public String hash;
    public String previousBlockHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    private long timeStamp;
    private int nonce;
    public int difficulty = 5;

    public Block( String previousHash ) {
        this.previousBlockHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateBlockHash();
    }

    public String calculateBlockHash(){
        String calculatedhash = StringUtil.applySha256(
                previousBlockHash +
                        timeStamp +
                        nonce +
                        merkleRoot
        );
        return calculatedhash;
    }

    public void mineBlock() {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = "00000";
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateBlockHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    public boolean addTransaction(Transaction transaction) {
        if(transaction == null)
            return false;
        if((!previousBlockHash.equals("0"))) {
            if((!transaction.processTransaction())) {
                System.out.println("Transaction failed to process!");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

}

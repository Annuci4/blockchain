import com.google.gson.GsonBuilder;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class MainChain {
    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static HashMap<String, TransactionOutput> UnspentCoins = new HashMap<>();

    public static Wallet walletA;
    public static Wallet walletB;
    public static Wallet walletC;
    public static Transaction genesisTransaction;

    public static void main(String[] args) throws NoSuchAlgorithmException{

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();
        walletC = new Wallet();


        Wallet coinbase = new Wallet();

        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 1000f, null);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.transactionId = "0";
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId));
        UnspentCoins.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println("WalletC's balance is: " + walletC.getBalance());

        Block block1 = new Block(genesis.hash);

        System.out.println("\nWalletA is Attempting to send funds (800) to WalletB...");
        block1.addTransaction(walletA.sendCoins(walletB.publicKey, 800f));
        System.out.println("WalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        System.out.println("\nWalletB is Attempting to send funds (4000) to WalletC...");
        block1.addTransaction(walletB.sendCoins(walletC.publicKey, 4000f));
        System.out.println("WalletB's balance is: " + walletB.getBalance());
        System.out.println("WalletC's balance is: " + walletC.getBalance());

        addBlock(block1);
        Block block2 = new Block(block1.hash);
        addBlock(block2);

        System.out.println("\nBlockchain is Valid: " + isChainValid());

       String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println(blockchainJson);

    }
    public static Boolean isChainValid() {
        int difficulty=5;
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateBlockHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.previousBlockHash)) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //check if id is solved
            if (!currentBlock.hash.substring(0, 5).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock();
        blockchain.add(newBlock);
    }
}


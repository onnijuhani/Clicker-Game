package model.characters.player.clicker;

import model.Settings;
import model.characters.Person;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.characters.payments.PaymentTracker;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.stateSystem.MessageTracker;
import model.time.Time;

import java.util.HashMap;
import java.util.Map;

public class Clicker implements PaymentTracker {
    private static Clicker instance;
    private final Map<Resource, ClickerTools> ownedClickerTools;
    private final MessageTracker messageTracker;
    private int totalClicks = 0;
    private final Wallet wallet;
    private final WorkWallet workWallet;
    private final Person person;
    private boolean autoClickerOwned = false;
    private int autoClickerLevel = 4; // smaller number is better. 1 is the best. Functionality in Time.java
    private boolean showClickerSalaryInPayments = true;

    private Clicker(Person person) {
        this.person = person;
        this.messageTracker = person.getMessageTracker();
        this.wallet = person.getWallet();
        this.workWallet = person.getWorkWallet();
        this.ownedClickerTools = new HashMap<>();
        this.ownedClickerTools.put(Resource.Food, new FoodClicker(Settings.getInt("foodClicker")));
    }
    public static void initializeClicker(Person person) {
        if (instance == null) {
            instance = new Clicker(person);
        }
    }

    @Override
    public void updatePaymentManager(PaymentManager calendar) {
        double taxRate = person.getRole().getAuthority().getTaxForm().getTaxRate() / 100;
        person.getPaymentManager().addPayment(PaymentManager.PaymentType.INCOME, Payment.EXPECTED_CLICKER_INCOME ,person.getWorkWallet().getBalance().multiply(taxRate), 27);

    }



    public static Clicker getInstance() {
        return instance;
    }

    public void addClickerTool(Resource type, ClickerTools tool) {
        ownedClickerTools.put(type, tool);
    }
    public void generateResources() {
        if(Time.gameOver){
            return;
        }
        TransferPackage resourcesGenerated = generate();
        workWallet.addResources(resourcesGenerated);
        totalClicks++;
        String message = "Clicker generated "+ clickerTransferMessage(resourcesGenerated);
        messageTracker.addMessage(MessageTracker.Message("Clicker", message));
        if(showClickerSalaryInPayments) {
            updatePaymentManager(person.getPaymentManager());
        }
    }
    private TransferPackage generate() {
        int totalFood = 0;
        int totalAlloy = 0;
        int totalGold = 0;

        for (Map.Entry<Resource, ClickerTools> entry : ownedClickerTools.entrySet()) {
            double amount = entry.getValue().getValue();
            switch (entry.getKey()) {
                case Food:
                    totalFood += (int) amount;
                    break;
                case Alloy:
                    totalAlloy += (int) amount;
                    break;
                case Gold:
                    totalGold += (int) amount;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported resource type: " + entry.getKey());
            }
        }
        return new TransferPackage(totalFood, totalAlloy, totalGold);
    }
    private String clickerTransferMessage(TransferPackage resourcesGenerated) {
        return "Generated " + resourcesGenerated.food() + "F, " +
                resourcesGenerated.alloy() + "A, " +
                resourcesGenerated.gold() + "G!";
    }
    public ClickerTools getClickerTool(Resource resourceType) {
        return ownedClickerTools.get(resourceType);
    }
    public int getTotalClicks() {
        return totalClicks;
    }


    public boolean isAlloyClickerOwned() {
        return ownedClickerTools.containsKey(Resource.Alloy);
    }

    public boolean isGoldClickerOwned() {
        return ownedClickerTools.containsKey(Resource.Gold);
    }

    public void setShowClickerSalaryInPayments(boolean showClickerSalaryInPayments) {

        if(!showClickerSalaryInPayments){
            person.getPaymentManager().removePayment(PaymentManager.PaymentType.INCOME, Payment.EXPECTED_CLICKER_INCOME);
        }

        this.showClickerSalaryInPayments = showClickerSalaryInPayments;
    }

    public boolean isAutoClickerOwned() {
        return autoClickerOwned;
    }
    public void setAutoClickerOwned(boolean autoClickerOwned) {
        this.autoClickerOwned = autoClickerOwned;
    }
    public int getAutoClickerLevel() {
        return autoClickerLevel;
    }

    public void decreaseAutoClickerLevel() {
        if(autoClickerLevel == 1){
            return;
        }
        autoClickerLevel--;
    }


}





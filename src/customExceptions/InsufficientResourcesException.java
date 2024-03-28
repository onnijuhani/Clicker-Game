package customExceptions;

import model.resourceManagement.TransferPackage;

public class InsufficientResourcesException extends Exception {
    private final TransferPackage cost;

    public InsufficientResourcesException(String message, TransferPackage cost) {
        super(message);
        this.cost = cost;
    }

    public TransferPackage getCost() {
        return cost;
    }
}

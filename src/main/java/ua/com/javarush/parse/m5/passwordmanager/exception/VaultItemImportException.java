package ua.com.javarush.parse.m5.passwordmanager.exception;

import java.util.List;

public class VaultItemImportException extends RuntimeException {
    private final List<String> errors;

    public VaultItemImportException(List<String> errors) {
        super("Vault item import failed with " + errors.size() + " errors.");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}

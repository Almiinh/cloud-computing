package fr.emse.etu.cloud.worker.app;

public record Sale(String store, String product, int quantity, float price, float profit) {
}

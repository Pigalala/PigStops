package me.pigalala.pigstops.annotations;

public class PitVariantHandler {

    public PitVariantHandler(Class<?> pitVariant) {
        if(pitVariant.isAnnotationPresent(PitVariant.class)) {
            PitVariant pitVariantValues = pitVariant.getAnnotation(PitVariant.class);
            itemsToClick = pitVariantValues.itemsToClick();
            inventorySize = pitVariantValues.inventorySize();
        }
    }

    public int itemsToClick;
    public int inventorySize;
}

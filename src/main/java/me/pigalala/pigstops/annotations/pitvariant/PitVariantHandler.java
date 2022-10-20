package me.pigalala.pigstops.annotations.pitvariant;

public class PitVariantHandler {

    private final Class<PitVariant> variantAnno = PitVariant.class;

    public PitVariantHandler(Class<?> pitVariant) {
        if(pitVariant.isAnnotationPresent(variantAnno)) {
            PitVariant pitVariantValues = pitVariant.getAnnotation(variantAnno);

            itemsToClick = pitVariantValues.itemsToClick();
            inventorySize = pitVariantValues.inventorySize();
        }
    }

    public int itemsToClick;
    public int inventorySize;
}

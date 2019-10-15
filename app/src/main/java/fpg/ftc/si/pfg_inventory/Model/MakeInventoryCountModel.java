package fpg.ftc.si.pfg_inventory.Model;

import fpg.ftc.si.pfg_inventory.MakeInventory;

/**
 * Created by AndroidDev on 2015/1/9.
 */
public class MakeInventoryCountModel {
    public String ProductCode;
    public String ClassLevel;
    public String MakeInventory;
    public String Count;

    public String getMakeInventory() {
        return MakeInventory;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public String getClassLevel() {
        return ClassLevel;
    }

    public String getCount() {
        return Count;
    }

    public MakeInventoryCountModel(String productCode, String classLevel, String makeInventory ,String count) {
        ProductCode = productCode;
        ClassLevel = classLevel;
        MakeInventory=makeInventory;
        Count = count;
    }
}

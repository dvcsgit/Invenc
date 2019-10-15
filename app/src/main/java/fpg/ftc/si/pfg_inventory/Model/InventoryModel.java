package fpg.ftc.si.pfg_inventory.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by AndroidDev on 2014/10/15.
 */
public class InventoryModel implements Serializable{
    public String WarehouseID;
    public String Location;
    public String BoxNumber;
    public String ProductCode;
    public String ClassLevel;
    public String GrossWeight;
    public String NetWeight;
    public String StatusCode;
    public String CreatorAccount;
    public String DateCreated;
    public String ModifierAccount;
    public String DateModified;
    public String Index;
    public String CarNo;
    public String Remark;


    public String getBoxNumber() {
        return BoxNumber;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public String getClassLevel() {
        return ClassLevel;
    }

    public String getGrossWeight() {
        return GrossWeight;
    }

    public String getWarehouseID() {
        return WarehouseID;
    }

    public String getLocation() {
        return Location;
    }

    public String getNetWeight() {
        return NetWeight;
    }

    public String getStatusCode(){return StatusCode;}

    public String getCreatorAccount() {return CreatorAccount;}

    public String getDateCreated() {return DateCreated;}

    public String getModifierAccount() {return ModifierAccount;}

    public String getDateModified() {return DateModified;}

    public String getIndex(){return Index;}

    public String getCarNo(){return CarNo;}

    public String getRemark(){return Remark;}

    public void setStatusCode(String statusCode) {StatusCode = statusCode;}

    public void setModifierAccount(String modifierAccount) {ModifierAccount = modifierAccount;}

    public void setDateModified(String dateModified) {DateModified = dateModified;}

    public void setProductCode(String productCode){ProductCode=productCode;}

    public InventoryModel(String warehouseID, String location, String boxNumber, String productCode
                          ,String classLevel, String grossWeight, String netWeight, String statusCode
                          ,String creatorAccount, String dateCreated, String modifierAccount, String dateModified
                          ,String index, String carNo,String remark) {

        WarehouseID = warehouseID;
        Location = location;
        BoxNumber = boxNumber;
        ProductCode = productCode;
        ClassLevel = classLevel;
        GrossWeight = grossWeight;
        NetWeight = netWeight;
        StatusCode = statusCode;
        CreatorAccount = creatorAccount;
        DateCreated = dateCreated;
        ModifierAccount = modifierAccount;
        DateModified = dateModified;
        Index = index;
        CarNo=carNo;
        Remark=remark;
    }
}

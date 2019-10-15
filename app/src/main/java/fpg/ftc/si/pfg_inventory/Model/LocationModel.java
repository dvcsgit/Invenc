package fpg.ftc.si.pfg_inventory.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AndroidDev on 2014/10/17.
 */
public class LocationModel implements Serializable {

    public String WarehouseID;
    public String LoactionID;

    public List<InventoryModel> InventoryModel;

    public String getWarehouseID() {return WarehouseID;}

    public String getLoactionID(){return LoactionID;}

    public LocationModel(String WarehouseID,String loactionID) {
        this.WarehouseID=WarehouseID;
        this.LoactionID = loactionID;
    }

    public List<InventoryModel> getInventoryModel() {
        return InventoryModel;
    }

    public void setInventoryModel(List<InventoryModel> inventoryModel) {
        InventoryModel = inventoryModel;
    }
}

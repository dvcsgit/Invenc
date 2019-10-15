package fpg.ftc.si.pfg_inventory.Model;

/**
 * Created by AndroidDev on 2014/10/13.
 */
public class ExportWarehouseModel {
    public String WarehouseID;
    public String WarehouseName;
    public String Capacity;

    public String getWarehouseID(){return WarehouseID;}
    public String getWarehouseName(){return WarehouseName;}
    public String getCapacity(){return Capacity;}

    public ExportWarehouseModel(String WarehouseID,String WarehouseName,String Capacity)
    {
        this.WarehouseID=WarehouseID;
        this.WarehouseName=WarehouseName;
        this.Capacity=Capacity;
    }
}

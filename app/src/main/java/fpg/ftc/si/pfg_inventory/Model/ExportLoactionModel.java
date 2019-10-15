package fpg.ftc.si.pfg_inventory.Model;

/**
 * Created by AndroidDev on 2014/10/14.
 */
public class ExportLoactionModel {
    public String  LoactionID;
    public String  Total;
    public String  HasMake;

    public String getHasMake() {
        return HasMake;
    }

    public String  getLoactionID(){return LoactionID;}
    public String  getTotal(){return Total;}


    public ExportLoactionModel(String loactionID,String total,String hasMake) {

        LoactionID = loactionID;
        Total=total;
        HasMake=hasMake;
    }

//    public ExportLoactionModel(String loactionID) {
//        LoactionID = loactionID;
//    }

}

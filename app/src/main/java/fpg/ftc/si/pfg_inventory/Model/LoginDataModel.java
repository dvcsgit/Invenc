package fpg.ftc.si.pfg_inventory.Model;

/**
 * Created by AndroidDev on 2014/9/30.
 */
public class LoginDataModel {
    public Boolean IsLoginValid;
    public String ErrorMessage;
    public String Account;
    public String Name;

    public LoginDataModel(Boolean isLoginValid, String errorMessage, String account, String name) {
        isLoginValid = isLoginValid;
        errorMessage = errorMessage;
        account = account;
        name = name;
    }

    public Boolean getIsLoginValid() {
        return IsLoginValid;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public String getAccount() {
        return Account;
    }

    public String getName() {
        return Name;
    }

}

package linked.main;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by iJohnJohn on 3/28/2017.
 */

public class Business_CLASS {
    public String owner;
    public String emailaddress;
    public String business_name;
    public String business_address;
    public String password;
    public String account_type;
    public String latitude;
    public String longitude;
    public List<String> activity_List;

    public Business_CLASS(){
        account_type = "Business";
        activity_List = new ArrayList<>();
    }
}

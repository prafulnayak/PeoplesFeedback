package shamgar.org.peoplesfeedback.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import shamgar.org.peoplesfeedback.R;

public class SharedPreferenceConfig {
    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceConfig(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.login_prferance), Context.MODE_PRIVATE);

    }

    public void writePhotoUrl(Uri url){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.photo_url_preference), String.valueOf(url));
        Log.i("SharedPreferanceWrite: ",""+url);
        editor.apply();
    }

    public String readPhotoUrl(){
        String url;
        url = sharedPreferences.getString(context.getResources().getString(R.string.photo_url_preference),"no");
        Log.i("SharedPreferanceRead: ",""+url);
        return url;
    }

    public void writeGender(String gender){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.gensder_preference), gender);
        Log.i("SharedPreferanceWrite: ",""+gender);
        editor.commit();
    }

    public String readGender(){
        String gender;
        gender = sharedPreferences.getString(context.getResources().getString(R.string.gensder_preference),"no");
        Log.i("SharedPreferanceRead: ",""+gender);
        return gender;
    }


    public void writeEmail(String email){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.emil_preference), email);
        Log.i("SharedPreferanceWrite: ",""+email);
        editor.commit();
    }

    public String readEmail(){
        String email;
        email = sharedPreferences.getString(context.getResources().getString(R.string.emil_preference),"no");
        Log.i("SharedPreferanceRead: ",""+email);
        return email;
    }

    public void writeState(String state){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.state_preference), state);
        Log.i("SharedPreferanceWrite: ",""+state);
        editor.commit();
    }

    public String readState(){
        String state;
        state = sharedPreferences.getString(context.getResources().getString(R.string.state_preference),"no");
        Log.i("SharedPreferanceRead: ",""+state);
        return state;
    }

    public void writeDistrict(String dist){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.district_preference), dist);
        Log.i("SharedPreferanceWrite: ",""+dist);
        editor.commit();
    }

    public String readDistrict(){
        String dist;
        dist = sharedPreferences.getString(context.getResources().getString(R.string.district_preference),"no");
        Log.i("SharedPreferanceRead: ",""+dist);
        return dist;
    }

    public void writeConstituancy(String con){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.con_preference), con);
        Log.i("SharedPreferanceWrite: ",""+con);
        editor.commit();
    }

    public String readConstituancy(){
        String con;
        con = sharedPreferences.getString(context.getResources().getString(R.string.con_preference),"no");
        Log.i("SharedPreferanceRead: ",""+con);
        return con;
    }

    public void writePhoneNo(String phoneNo){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.phone_no_preference), phoneNo);
        Log.i("SharedPreferanceWrite: ",""+phoneNo);
        editor.commit();
    }

    public String readPhoneNo(){
        String phoneNo;
        phoneNo = sharedPreferences.getString(context.getResources().getString(R.string.phone_no_preference),"no");
        Log.i("SharedPreferanceRead: ",""+phoneNo);
        return phoneNo;
    }

    public void writeName(String name){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.name_preference), name);
        Log.i("SharedPreferanceWrite: ",""+name);
        editor.commit();
    }

    public String readName(){
        String name;
        name = sharedPreferences.getString(context.getResources().getString(R.string.name_preference),"no");
        Log.i("SharedPreferanceRead: ",""+name);
        return name;
    }

    public void setLatitude(String latitude){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.latitude_preference), latitude);
        Log.i("SharedPreferanceWrite: ",""+latitude);
        editor.commit();
    }

    public String getLatitude(){
        String name;
        name = sharedPreferences.getString(context.getResources().getString(R.string.latitude_preference),null);
        Log.i("SharedPreferanceRead: ",""+name);
        return name;
    }

    public void setLongitude(String longitude){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.longitude_preference), longitude);
        Log.i("SharedPreferanceWrite: ",""+longitude);
        editor.commit();
    }

    public String getLongitude(){
        String name;
        name = sharedPreferences.getString(context.getResources().getString(R.string.longitude_preference),null);
        Log.i("SharedPreferanceRead: ",""+name);
        return name;
    }

}
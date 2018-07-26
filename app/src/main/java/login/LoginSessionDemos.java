package login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;



@SuppressLint("CommitPrefEdits")
public class LoginSessionDemos {

    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Context
    private Context _context;

    // nama sharepreference
    private static final String PREF_NAME = "SesiLogIn";


    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String NAMA_LENGKAP_DEMOS = "nama";
    public static final String ID_USER_DEMOS = "id_user";
    public static final String ID_APHRIS_DEMOS = "id_aphris";
    public static final String PWD_DEMOS = "pwd";
    public static final String EMAIL_DEMOS  = "email";
    public static final String LEVEL_DEMOS  ="level";
    public static final String JENIS_USER   ="jenis_user";
    public static final String NAMA_DEPT_DEMOS  ="nama_dept";


    // Constructor
    public LoginSessionDemos(Context context){
        this._context = context;
        int PRIVATE_MODE = 0;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    public void createLoginSessionDemos(String nama_lengkap, int id_user, String pwd, String email, int id_aphris, int level, int jenis, String dept){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(NAMA_LENGKAP_DEMOS, nama_lengkap);
        editor.putString(ID_USER_DEMOS, String.valueOf(id_user));
        editor.putString(PWD_DEMOS, pwd);
        editor.putString(EMAIL_DEMOS, email);
        editor.putString(ID_APHRIS_DEMOS, String.valueOf(id_aphris));
        editor.putString(LEVEL_DEMOS, String.valueOf(level));
        editor.putString(JENIS_USER, String.valueOf(jenis));
        editor.putString(NAMA_DEPT_DEMOS, dept );
        editor.commit();
    }



    public void checkLoginDemos(){
        // Check login status

        if(!this.isLoggedInDemos()){
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }

    }

    public HashMap<String, String> getUserDetailDemos(){
        HashMap<String, String> user = new HashMap<>();

        user.put(NAMA_LENGKAP_DEMOS, pref.getString(NAMA_LENGKAP_DEMOS, null));
        user.put(ID_USER_DEMOS, pref.getString(ID_USER_DEMOS, null));
        user.put(PWD_DEMOS, pref.getString(PWD_DEMOS,null));
        user.put(EMAIL_DEMOS, pref.getString(EMAIL_DEMOS,null));
        user.put(ID_APHRIS_DEMOS, pref.getString(ID_APHRIS_DEMOS,null));
        user.put(LEVEL_DEMOS, pref.getString(LEVEL_DEMOS,null));
        user.put(JENIS_USER, pref.getString(JENIS_USER,null));
        user.put(NAMA_DEPT_DEMOS, pref.getString(NAMA_DEPT_DEMOS,null));

        return user;
    }



    public void logoutUserDemos(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

    }

    public boolean isLoggedInDemos(){
        return pref.getBoolean(IS_LOGIN, false);
    }

}

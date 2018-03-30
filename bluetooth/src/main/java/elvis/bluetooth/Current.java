package elvis.bluetooth;

import android.content.Context;
import android.util.Log;

public class Current {
        private static User curUser;
        private static final String TAG = Current.class.getSimpleName();
        public static void addCurUser(User user, Context context){
            Log.v(TAG, "adding cur user with shared pref");
            curUser = new User(user.getEmail(), user.getPassword(), user.getID());
        }

        /**
         * Adds a saved user to Current user instance
         * @param user
         */
        public static void addCurUser(User user){
            curUser = user;
        }


        public static int getCurUserID()    {
            if(curUser != null)
                return curUser.getID();
            else
                return 0;
        }

        public static void deleteCurUser(Context context){
            curUser = null;
        }

        public static User getCurUser(){
            return curUser;
        }

        public static String getCurUserEmail()  {return curUser.getEmail();}
}

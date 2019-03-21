package com.company.coder.publicTaxi.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.company.coder.publicTaxi.modles.Driver;
import com.company.coder.publicTaxi.modles.Owner;
import com.company.coder.publicTaxi.modles.User;
import com.company.coder.publicTaxi.modles.Vehicle;

public class SharedPref {

    public static final String TAG = "Shared_Pref";

    private static final String PACKAGE_NAME = "com.example.alisons.pta.utils";

    private static String USER_NAME = "username";
    private static String USER_CONTACT = "userContact";
    private static String USER_PASSWORD = "userPassword";
    private static String USER_PUSHKEY = "userPushKey";

    private static String OWNER_NAME = "ownerName";
    private static String OWNER_PHONE = "ownerPhone";
    private static String OWNER_ADDRESS = "ownerAddress";
    private static String OWNER_PASSWORD = "ownerPassword";
    private static String OWNER_IMAGE = "ownerImage";
    private static String OWNER_PUSHKEY = "ownerPushKey";

    private static String DRIVER_NAME = "driverName";
    private static String DRIVER_CONTACT = "driverContact";
    private static String DRIVER_PASSWORD = "driverPassword";
    private static String DROVER_IMAGE = "driverImage";
    private static String DRIVER_VEH_UID = "driver_vehicle_uuid";
    private static String DRIVER_LAT = "driver_lat";
    private static String DRIVER_LNG = "driver_lng";
    private static String DRIVER_PUSH_KEY = "driver_push_key";
    private static String DRIVER_OWNER_KEY = "driver_owner_key";

    private static String VEHICLE_MAKE = "vehicle_make";
    private static String VEHICLE_MODEL = "vehicle_model";
    private static String VEHICLE_COLOR = "vehicle_color";
    private static String VEHICLE_PLATE = "vehicle_plate_number";
    private static String VEHICLE_START_LAT = "vehicle_start_lat";
    private static String VEHICLE_START_LNG = "vehicle_start_lng";
    private static String VEHICLE_START_NAME = "vehicle_start_name";
    private static String VEHICLE_END_LAT = "vehicle_end_lat";
    private static String VEHICLE_END_LNG = "vehicle_end_lng";
    private static String VEHICLE_END_NAME = "vehicle_end_name";
    private static String VEHICLE_IMAGE = "vehicle_image_url";
    private static String VEHICLE_OWNER_ID = "vehicle_owner_id";

    private static String USER_TYPE_KEY = "current_user_type";


    // User
    public static void setCurrentUser(Context context, User user) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(USER_NAME, user.getName()).apply();
            prefs.edit().putString(USER_CONTACT, user.getContact()).apply();
            prefs.edit().putString(USER_PASSWORD, user.getPassword()).apply();
            prefs.edit().putString(USER_PUSHKEY, user.getPushKey()).apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static User getCurrentUser(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            User user = new User();
            user.setName(prefs.getString(USER_NAME, ""));
            user.setContact(prefs.getString(USER_CONTACT, ""));
            user.setPassword(prefs.getString(USER_PASSWORD, ""));
            user.setPushKey(prefs.getString(USER_PUSHKEY, ""));
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Owner
    public static void setCurrentOwner(Context context, Owner owner) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(OWNER_NAME, owner.getName()).apply();
            prefs.edit().putString(OWNER_PHONE, owner.getPhone()).apply();
            prefs.edit().putString(OWNER_ADDRESS, owner.getAddres()).apply();
            prefs.edit().putString(OWNER_PASSWORD, owner.getPassword()).apply();
            prefs.edit().putString(OWNER_IMAGE, owner.getImageUrl()).apply();
            prefs.edit().putString(OWNER_PUSHKEY, owner.getPushKey()).apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Owner getCurrentOwner(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            Owner owner = new Owner();
            owner.setName(prefs.getString(OWNER_NAME, ""));
            owner.setPhone(prefs.getString(OWNER_PHONE, ""));
            owner.setAddres(prefs.getString(OWNER_ADDRESS, ""));
            owner.setPassword(prefs.getString(OWNER_PASSWORD, ""));
            owner.setImageUrl(prefs.getString(OWNER_IMAGE, ""));
            owner.setPushKey(prefs.getString(OWNER_PUSHKEY, ""));
            return owner;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Driver
    public static void setCurrentDriver(Context context, Driver driver) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(DRIVER_NAME, driver.getName()).apply();
            prefs.edit().putString(DRIVER_CONTACT, driver.getContact()).apply();
            prefs.edit().putString(DROVER_IMAGE, driver.getImage()).apply();
            prefs.edit().putString(DRIVER_PASSWORD, driver.getPassword()).apply();
            prefs.edit().putString(DRIVER_VEH_UID, driver.getVehicleUUID()).apply();
            prefs.edit().putString(DRIVER_LAT, driver.getLat()).apply();
            prefs.edit().putString(DRIVER_LNG, driver.getLng()).apply();
            prefs.edit().putString(DRIVER_PUSH_KEY, driver.getPushKey()).apply();
            prefs.edit().putString(DRIVER_OWNER_KEY, driver.getOwnerUUID()).apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Driver getCurrentDriver(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            Driver driver = new Driver();
            driver.setName(prefs.getString(DRIVER_NAME, ""));
            driver.setContact(prefs.getString(DRIVER_CONTACT, ""));
            driver.setImage(prefs.getString(DROVER_IMAGE, ""));
            driver.setPassword(prefs.getString(DRIVER_PASSWORD, ""));
            driver.setVehicleUUID(prefs.getString(DRIVER_VEH_UID, ""));
            driver.setLat(prefs.getString(DRIVER_LAT, ""));
            driver.setLng(prefs.getString(DRIVER_LNG, ""));
            driver.setPushKey(prefs.getString(DRIVER_PUSH_KEY, ""));
            driver.setOwnerUUID(prefs.getString(DRIVER_OWNER_KEY, ""));
            return driver;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Driver
    public static void setCurrentVehicle(Context context, Vehicle vehicle) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(VEHICLE_MAKE, vehicle.getmMake()).apply();
            prefs.edit().putString(VEHICLE_MODEL, vehicle.getmModel()).apply();
            prefs.edit().putString(VEHICLE_COLOR, vehicle.getmColor()).apply();
            prefs.edit().putString(VEHICLE_PLATE, vehicle.getmPlateNumber()).apply();
            prefs.edit().putString(VEHICLE_START_LAT, vehicle.getmStartLat()).apply();
            prefs.edit().putString(VEHICLE_START_LNG, vehicle.getmStartLng()).apply();
            prefs.edit().putString(VEHICLE_START_NAME, vehicle.getmStartName()).apply();
            prefs.edit().putString(VEHICLE_END_LAT, vehicle.getmEndLat()).apply();
            prefs.edit().putString(VEHICLE_END_LNG, vehicle.getmEndLng()).apply();
            prefs.edit().putString(VEHICLE_END_NAME, vehicle.getmEndName()).apply();
            prefs.edit().putString(VEHICLE_IMAGE, vehicle.getmImgUrl()).apply();
            prefs.edit().putString(VEHICLE_OWNER_ID, vehicle.getmOwnerUuid()).apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Vehicle getCurrentVehicle(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            Vehicle vehicle = new Vehicle();
            vehicle.setmMake(prefs.getString(VEHICLE_MAKE, ""));
            vehicle.setmModel(prefs.getString(VEHICLE_MODEL, ""));
            vehicle.setmColor(prefs.getString(VEHICLE_COLOR, ""));
            vehicle.setmPlateNumber(prefs.getString(VEHICLE_PLATE, ""));
            vehicle.setmStartLat(prefs.getString(VEHICLE_START_LAT, ""));
            vehicle.setmStartLng(prefs.getString(VEHICLE_START_LNG, ""));
            vehicle.setmStartName(prefs.getString(VEHICLE_START_NAME, ""));
            vehicle.setmEndLat(prefs.getString(VEHICLE_END_LAT, ""));
            vehicle.setmEndLng(prefs.getString(VEHICLE_END_LNG, ""));
            vehicle.setmEndName(prefs.getString(VEHICLE_END_NAME, ""));
            vehicle.setmImgUrl(prefs.getString(VEHICLE_IMAGE, ""));
            vehicle.setmOwnerUuid(prefs.getString(VEHICLE_OWNER_ID, ""));
            return vehicle;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    // User Type
    public static void setCurrentUserType(Context context, String type) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(USER_TYPE_KEY, type).apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getCurrentUserType(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            return prefs.getString(USER_TYPE_KEY, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public static void clearAllData(Context context) {
        try {
            SharedPreferences alllpreferences = context.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor alleditor = alllpreferences.edit();
            alleditor.clear();
            alleditor.apply();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}

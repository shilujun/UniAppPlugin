package com.johnny.sms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.EventsEntity;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import io.dcloud.feature.uniapp.common.UniModule;

public class SystemInfo extends UniModule{

  private static final String TAG = "SystemInfo";

  private static String CALENDER_URL = "content://com.android.calendar/calendars";

  private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
  private static final int mMaxDayNum = 15;


  private Context mContext;
  private Object Calendars;

  public SystemInfo(Context context) {
    this.mContext = context;
  }

  /**
   * 获取日程数据
   */
  @RequiresApi(api = Build.VERSION_CODES.M)
  public List<JSONObject> getCalendars() {
    String title = "";
    long dtstart = 0L;
    long dtend = 0L;
    String description = "";
    String allDay = "";
    String timeZone = "";


    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    //一天的开始时间 yyyy:MM:dd 00:00:00
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    long dayStart = calendar.getTime().getTime();

    long beginTime = dayStart - mMaxDayNum * 24 * 3600000;
    long endTime = dayStart + mMaxDayNum * 24 * 3600000;

    List<JSONObject> calendars = new ArrayList<>();

    Cursor eventCursor = mContext.getApplicationContext().getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null,

        null, null, null);
    if(eventCursor == null) {
      return calendars;
    }
    Log.i(TAG, "eventCursor" + eventCursor.toString());

    while (eventCursor.moveToNext()){
      dtstart = eventCursor.getLong(eventCursor.getColumnIndex(EventsEntity.DTSTART));
      dtend = eventCursor.getLong(eventCursor.getColumnIndex(EventsEntity.DTEND));
      if(dtstart < beginTime || dtend > endTime) {
        continue;
      }
      title = eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.TITLE));
      description = eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.DESCRIPTION));
      allDay = eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.ALL_DAY));
      timeZone = eventCursor.getString(eventCursor.getColumnIndex(EventsEntity.EVENT_TIMEZONE));

      JSONObject calendarInfo = new JSONObject();
      calendarInfo.put("allDay", allDay);
      calendarInfo.put("title", title);
      calendarInfo.put("timeZone", timeZone);
      calendarInfo.put("dtstart", dtstart);
      calendarInfo.put("dtend", dtend);
      calendarInfo.put("description", description);


      calendars.add(calendarInfo);

    }

    return calendars;

  }

  /**
   * 获取联系人姓名及手机号
   */
  @RequiresApi(api = Build.VERSION_CODES.M)
  public List<JSONObject> getAllContacts() {
    List<JSONObject> contacts = new ArrayList<>();
    Cursor cursor = mContext.getApplicationContext().getContentResolver().query(
        ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
    while (cursor.moveToNext()) {
      //新建一个联系人实例
      JSONObject contact = new JSONObject();
      String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
      Log.d(TAG, "contactId:" + contactId);
      //获取联系人姓名
      String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
      contact.put("displayName", name);

      //获取联系人电话号码
      Cursor phoneCursor =
          mContext.getApplicationContext().getContentResolver().query(Phone.CONTENT_URI,
          null, Phone.CONTACT_ID + "=" + contactId, null, null);
      int phoneNum = 0;
      List<JSONObject> phoneNumbers = new ArrayList<>();
      while (phoneCursor.moveToNext()) {
        phoneNum++;
        JSONObject phoneInfo = new JSONObject();
        String phone = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
        String type = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.TYPE));
//        phone = phone.replace("-", "");
//        phone = phone.replace(" ", "");
        phoneInfo.put("id", phoneNum);
        phoneInfo.put("value", phone);
        phoneInfo.put("type", type);
        phoneNumbers.add(phoneInfo);
      }
      contact.put("phoneNumbers", phoneNumbers);
      contacts.add(contact);
      //记得要把cursor给close掉
      phoneCursor.close();
    }
    cursor.close();
    return contacts;
  }

}

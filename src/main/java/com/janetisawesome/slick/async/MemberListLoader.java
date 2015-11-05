package com.janetisawesome.slick.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.janetisawesome.slick.model.Member;
import com.janetisawesome.slick.model.Profile;
import com.janetisawesome.slick.utils.NetworkHelper;
import com.janetisawesome.slick.utils.PrefsHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
/**
 * MemberListLoader:
 * Cache & Asynchronous downloader for the list of Members
 *
 * If network is connected:
 *    - retrieves member list from slack Webservice
 *    - stores the retrieved JSON string to preferences (TODO: use a real DB for storage)
 *
 * If network is NOT connected:
 *    - retrieves member list from local storage
 *
 * cacheList - allows the application to cache a previously retrieved list for
 * use by other parts of the application
 *
 */
public class MemberListLoader {

    private static final String TAG = "MemberListLoader";
    private static final String DOWNLOAD_URL = "https://slack.com/api/users.list?token=xoxp-5048173296-5048346304-5180362684-7b3865";


    /////////////////////////////////////////////////////////
    // Listener interface - always posted to the main thread

    public interface DownloadListener {
        void onMemberListDownloaded(List<Member> members);
    }

    /////////////////////////////////////////////////////////
    // Global Instance
    // (NOT thread-safe. for UI use only)
    private static MemberListLoader sInstance = null;
    public static MemberListLoader getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MemberListLoader(context);
        }
        return sInstance;
    }


    /////////////////////////////////////////////////////////
    // Class Members
    private Gson mGson;
    private DownloadListener mListener;
    private List<Member> mCachedList;
    private Context mContext;

    private AsyncTask<Void, Void, List<Member>> mPrefsDownloadTask = null;  //AsyncTask to retrieve member list from prefs
    private AsyncTask<Void, Void, List<Member>> mHttpDownloadTask = null;   //AsyncTask to retrieve member list from network


    /**
     *
     * @param context
     */
    private MemberListLoader(Context context) {
        mContext = context;
        mGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }

    /**
     * startDownload()
     * start a new download from network or retrieve from the local
     * saved version. This will guarantee a callback to onMemberListDownloaded,
     * unless the download is cancelled. (NOTE: does *not* change the cached list)
     */
    public void startDownload(DownloadListener listener) {

        cancelDownload();
        mListener = listener;

        if (!NetworkHelper.isNetworkConnected(mContext)) {

            // Retrieve the members from saved Preferences
            mPrefsDownloadTask = new AsyncTask<Void, Void, List<Member>>() {
                @Override
                protected List<Member> doInBackground(Void... params) {

                    String jsonString = PrefsHelper.getStringPref(mContext, PrefsHelper.PREF_MEMBER_LIST_KEY, null);
                    if (jsonString == null) {
                        Log.w(TAG, "PrefsDownloadTask doInBackground found NULL pref");

                        // return an empty array
                        return new ArrayList<Member>();
                    }

                    List<Member> result = null;
                    JsonReader reader = null;
                    try {
                        reader = new JsonReader(new StringReader(jsonString));
                        result = readJsonMembers(reader);
                        reader.close();
                    }
                    catch (Exception ex) {
                        Log.e(TAG, "PrefsDownloadTask doInBackground", ex);
                    }

                    return result;
                }

                @Override
                protected void onPostExecute(List<Member> members) {
                    onMembersJsonRetrieved(members);
                    mPrefsDownloadTask = null;
                }
            };
            mPrefsDownloadTask.execute();
        }
        else {
            // Retrieve the members from Slack API
            mHttpDownloadTask = new AsyncTask<Void, Void, List<Member>>() {
                @Override
                protected List<Member> doInBackground(Void... params) {
                    List<Member> result = null;
                    HttpURLConnection conn = null;
                    JsonReader reader = null;
                    try {
                        conn = (HttpURLConnection)new URL(DOWNLOAD_URL).openConnection();
                        reader = new JsonReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        String name = null;
                        String message = null;
                        boolean status = false;
                        reader.beginObject();
                        while (reader.hasNext()) {
                            name = reader.nextName();
                            if ("ok".equals(name)) {
                                status = reader.nextBoolean();
                                Log.d(TAG, "HttpDownloadTask: okay=" + status);
                            }
                            else if (!status && "error".equals(name)) {
                                //TODO: show error
                                message = reader.nextString();
                                Log.d(TAG, "HttpDownloadTask: error:" + message);
                                break;
                            }
                            else if ("members".equals(name)) {
                                result = readJsonMembers(reader);
                                // Log.d(TAG, "HttpDownloadTask: result=" + result);
                                // Save to prefs
                                String membersString = writeJsonMembers(result);
                                PrefsHelper.setStringPref(mContext, PrefsHelper.PREF_MEMBER_LIST_KEY, membersString);
                                //Log.e(TAG, "members serialization: " + membersString);
                            }
                        }
                        reader.close();

                    } catch (IOException ioe) {
                        Log.e(TAG, "HTTPDownloadTask doInBackground IOException ", ioe);
                    } finally {
                        Log.i(TAG, "HTTPDownloadTask disconnecting connection");
                        conn.disconnect();
                    }

                    return result;
                }

                @Override
                protected void onPostExecute(List<Member> members) {
                    //Save to prefs
                    onMembersJsonRetrieved(members);
                    mHttpDownloadTask = null;
                }
            };
            mHttpDownloadTask.execute();
        }
    }

    /**
     * cancel()
     * Cancels any on-going member list retrieval.
     * onMemberListDownloaded will not be called.
     */
    public void cancelDownload() {
        if (mPrefsDownloadTask != null) {
            mPrefsDownloadTask.cancel(true);
            mPrefsDownloadTask = null;
        }

        if (mHttpDownloadTask != null) {
            mHttpDownloadTask.cancel(true);
            mHttpDownloadTask = null;
        }
        mListener = null;
    }


    /**
     * Get a cached Member
     */
    public Member getCachedMember(int position) {
        Log.e(TAG, "getCachedMember " + position + ":" + (mCachedList == null ? "null" : mCachedList.get(position)));
        if (mCachedList == null || mCachedList.size() <= position) {
            return null;
        }
        return mCachedList.get(position);
    }

    /**
     * Get the number of cached members
     * @return
     * size of cached member list
     */
    public int getCachedMemberCount() {
        if (mCachedList == null) {
            return 0;
        }
        return mCachedList.size();
    }

    /**
     * Private helper function to trigger the callback
     */
    private void onMembersJsonRetrieved(List<Member> members) {

        if (mListener != null) {
            mListener.onMemberListDownloaded(members);
        }
        mCachedList = members;
    }


    /**
     * Private helper function to read Member list from JSON
     * @param reader
     * @return
     * list of members
     */
    private List<Member> readJsonMembers(JsonReader reader) {
        return mGson.fromJson(reader, new TypeToken<ArrayList<Member>>(){}.getType());
    }

    /**
     * Write a list of members to string
     * @param members
     * @return
     */
    private String writeJsonMembers(List<Member> members) {
        return mGson.toJson(members, new TypeToken<ArrayList<Member>>() {
        }.getType());
    }

    public String toJsonString(Member member) {
        return mGson.toJson(member, new TypeToken<Member>() {
        }.getType());
    }

    public String toJsonString(Profile profile) {
        return mGson.toJson(profile, new TypeToken<Member>(){}.getType());
    }

}
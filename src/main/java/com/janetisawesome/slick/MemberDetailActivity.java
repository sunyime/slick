package com.janetisawesome.slick;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.janetisawesome.slick.async.MemberImageLoader;
import com.janetisawesome.slick.async.MemberListLoader;
import com.janetisawesome.slick.model.Member;
import com.janetisawesome.slick.utils.AndroidHelper;
import com.janetisawesome.slick.utils.NetworkHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


/**
 * An activity representing a single Member detail screen.
 */
public class MemberDetailActivity extends AppCompatActivity {

    private static final String TAG = "MemberDetailActivity";
    private static final String ARG_MEMBER_POSITION = "arg_member_position";

    /**
     * Start the Member Detail Activity
     * @param context
     * @param memberPosition position
     */
    public static boolean startActivity(Context context, int memberPosition) {
        final Intent intent = new Intent(context, MemberDetailActivity.class);
        intent.putExtra(ARG_MEMBER_POSITION, memberPosition);
        return AndroidHelper.startActivity(context, intent);
    }


    private Member mMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int position = getIntent().getIntExtra(ARG_MEMBER_POSITION, 0);
        mMember = MemberListLoader.getInstance(this).getCachedMember(position);

        if (mMember == null) {
            Log.e(TAG, "Unexpected error! member at position " + position + " is NULL");
            return;
        }

        // Populate the required data
        ImageView imageView = (ImageView) findViewById(R.id.avatar);
        Target t;
        // Populate the profile image from Network and save to file cache
        if (NetworkHelper.isNetworkConnected(this)) {
            Log.d(TAG, "Loading image " + mMember.getImageUri(this));
            t = new MemberImageLoader.LoaderTarget(this, mMember, imageView);

            Picasso.with(this)
                    .load(mMember.getImageUri(this))
                    .into(t);
        }
        // Load the image from File cache
        else {
            t = new MemberImageLoader.LoaderTarget(imageView);
            Picasso.with(this)
                    .load(mMember.getImageCacheFile(this))
                    .into(t);
        }

        // Populate the required data
        TextView tv = (TextView) findViewById(R.id.realname);
        tv.setText(mMember.getRealName());

        tv = (TextView) findViewById(R.id.title);
        tv.setText(mMember.getTitle());

        tv = (TextView) findViewById(R.id.username);
        tv.setText(mMember.getUserName());

        // Populate the optional data
        String optionalData = mMember.getPhoneNumber();
        View optionalRow = findViewById(R.id.phone_row);
        if (optionalData != null && !optionalData.isEmpty()) {
            tv = (TextView) optionalRow.findViewById(R.id.phone_text);
            tv.setText(optionalData);
            optionalRow.setVisibility(View.VISIBLE);
        }
        else {
            optionalRow.setVisibility(View.GONE);
        }

        optionalData = mMember.getEmail();
        optionalRow = findViewById(R.id.email_row);
        if (optionalData != null && !optionalData.isEmpty()) {
            tv = (TextView) optionalRow.findViewById(R.id.email_text);
            tv.setText(optionalData);
            optionalRow.setVisibility(View.VISIBLE);
        }
        else {
            optionalRow.setVisibility(View.GONE);
            optionalRow.requestLayout();
        }

        optionalData = mMember.getSkype();
        optionalRow = findViewById(R.id.skype_row);
        if (optionalData != null && !optionalData.isEmpty()) {
            tv = (TextView) optionalRow.findViewById(R.id.skype_text);
            tv.setText(optionalData);
            optionalRow.setVisibility(View.VISIBLE);
        }
        else {
            optionalRow.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MemberListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClickEmail(View v) {
        Log.i(TAG, "onClickEmail");

        /*
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
        //intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, mMember.getEmail());
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.default_email_subject));

        startActivity(Intent.createChooser(intent, "Send Email"));
        */
    }

    public void onClickPhone(View v) {
        Log.i(TAG, "onClickPhone");
    }

}

package com.janetisawesome.slick;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.janetisawesome.slick.async.MemberImageLoader;
import com.janetisawesome.slick.async.MemberListLoader;
import com.janetisawesome.slick.model.Member;
import com.janetisawesome.slick.utils.NetworkHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MemberListActivity extends AppCompatActivity implements MemberListLoader.DownloadListener {

    private static final String TAG = "MemberListActivity";


    private static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

        public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ImageView mImgView;
            private TextView mTextView;
            private int mMemberPosition = -1;

            public ItemViewHolder(View view) {
                super(view);
                view.setOnClickListener(this);
                this.mImgView = (ImageView)view.findViewById(R.id.thumbnail);
                this.mTextView = (TextView)view.findViewById(R.id.realname);
            }

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick memberPosition=" + mMemberPosition);
                MemberDetailActivity.startActivity(v.getContext(), mMemberPosition);
            }

            public void setMemberPosition(int position) {
                mMemberPosition = position;
            }
        }


        private Context mContext;

        public ItemAdapter(Context context) {
            mContext = context;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.member_list_item, parent, false);

            return new ItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {

            // - get element from dataset at this position
            // - replace the contents of the view with that element
            Member member = MemberListLoader.getInstance(mContext).getCachedMember(position);
            if (member == null) {
                Log.e(TAG, "onBindViewHolder member is null at position " + position);
                return;
            }

            // Set the member position for Detail view launch
            holder.setMemberPosition(position);

            // Load the text
            holder.mTextView.setText(member.getRealName());

            // Load the image
            MemberImageLoader.LoaderTarget t = (MemberImageLoader.LoaderTarget) holder.mImgView.getTag();
            if (t != null) {
                t.cancel();
            }


            // Load the image from Network and save to file cache
            if (NetworkHelper.isNetworkConnected(mContext)) {
                Log.d(TAG, "Loading image " + member.getThumbnailUri(mContext));
                t = new MemberImageLoader.LoaderTarget(mContext, member, holder.mImgView);
                holder.mImgView.setTag(t);

                Picasso.with(mContext)
                        .load(member.getThumbnailUri(mContext))
                        .into(t);
            }
            // Load the image from File cache
            else {
                t = new MemberImageLoader.LoaderTarget(holder.mImgView);
                holder.mImgView.setTag(t);

                Picasso.with(mContext)
                        .load(member.getThumbnailCacheUri(mContext))
                        .into(t);
            }

        }

        @Override
        public int getItemCount() {
            return MemberListLoader.getInstance(mContext).getCachedMemberCount();
        }
    }

    private RecyclerView mListView;
    private ItemAdapter mListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        // Show the Up button in the action bar.
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (RecyclerView) findViewById(R.id.member_list);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mListView.setLayoutManager(mLayoutManager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_member_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            // Launch about dialog
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onMemberListDownloaded(List<Member> members) {
        if (mListAdapter == null) {
            // create the adapter for the first time
            mListAdapter = new ItemAdapter(this);
            mListView.setAdapter(mListAdapter);
            mListView.setHasFixedSize(true);
        } else {
            mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cancel the list download
        MemberListLoader.getInstance(this).cancelDownload();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MemberListLoader.getInstance(this).startDownload(this);
       /* if (MemberListLoader.getInstance(this).getCachedMemberCount() == 0) {
            // Start the download
            MemberListLoader.getInstance(this).startDownload(this);
        } */
    }
}

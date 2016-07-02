package com.akalizakeza.apps.ishusho.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.akalizakeza.apps.ishusho.R;
import com.akalizakeza.apps.ishusho.custom.TabViewPager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        Tab1Gallery.OnFragmentInteractionListener,
        Tab2Artists.OnFragmentInteractionListener,
        Tab3Create.OnFragmentInteractionListener,
        Tab4Profile.OnFragmentInteractionListener,
        NewPostUploadTaskFragment.OnFragmentInteractionListener{

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private TabViewPager tabViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabViewPager = (TabViewPager) findViewById(R.id.viewpager);
        setupViewPager(tabViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(tabViewPager);
        setupTabIcons();
    }

    @Override
    public void onPostComment(String postKey) {
        Intent intent = new Intent(this, CommentsActivity.class);
        intent.putExtra(CommentsActivity.POST_KEY_EXTRA, postKey);
        startActivity(intent);
    }

    @Override
    public void onPostLike(final String postKey) {
        final String userKey = FirebaseUtil.getCurrentUserId();
        final DatabaseReference postLikesRef = FirebaseUtil.getLikesRef();
        postLikesRef.child(postKey).child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User already liked this post, so we toggle like off.
                    postLikesRef.child(postKey).child(userKey).removeValue();
                } else {
                    postLikesRef.child(postKey).child(userKey).setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    /**
     * Adding fragments to ViewPager
     *
     * @param tabViewPager
     */
    private void setupViewPager(TabViewPager tabViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addTab(new Tab1Gallery(), "@string/title_activity_gallery");
        adapter.addTab(new Tab2Artists(), "@string/title_activity_artists");
        adapter.addTab(new Tab3Create(), "@string/title_activity_create");
        adapter.addTab(new Tab4Profile(), "@string/title_activity_profile");
        tabViewPager.setAdapter(adapter);
        tabViewPager.setPagingEnabled(false);
    }

    private void setupTabIcons() {

        //RelativeLayout tabOneLayout = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        ImageView tabOne = (ImageView) LayoutInflater.from(this).inflate(R.layout.tab, null);
        tabOne.setContentDescription("@string/title_activity_gallery");
        tabOne.setImageResource(R.drawable.ic_home_black_48dp);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        ImageView tabTwo = (ImageView) LayoutInflater.from(this).inflate(R.layout.tab, null);
        tabTwo.setContentDescription("@string/title_activity_artists");
        tabTwo.setImageResource(R.drawable.ic_person_follow_black_48dp);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        ImageView tabThree = (ImageView) LayoutInflater.from(this).inflate(R.layout.tab, null);
        tabThree.setContentDescription("@string/title_activity_create");
        tabThree.setImageResource(R.drawable.ic_brush_black_48dp);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        ImageView tabFour = (ImageView) LayoutInflater.from(this).inflate(R.layout.tab, null);
        tabThree.setContentDescription("@string/title_activity_profile");
        tabFour.setImageResource(R.drawable.ic_person_black_48dp);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mTabList = new ArrayList<>();
        private final List<String> mTabTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mTabList.get(position);
        }

        @Override
        public int getCount() {
            return mTabList.size();
        }

        public void addTab(Fragment fragment, String title) {
            mTabList.add(fragment);
            mTabTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_settings) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.about:
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle(R.string.app_name);
                String versionname = "1";
                try {
                    PackageInfo manager = getPackageManager().getPackageInfo(
                            getPackageName(), 0);
                    versionname = manager.versionName;
                } catch (PackageManager.NameNotFoundException nof) {

                }
                alertDialog.setMessage("Ishusho v" + versionname
                        + "\n"
                        + getResources().getString(R.string.about_text));
                alertDialog.setButton(getResources().getString(R.string.home_page),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String url = "http://AkalizaKeza.com";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        });
                alertDialog.setIcon(R.mipmap.ic_launcher);
                alertDialog.show();
                break;
            case (R.id.settings):
                break;
            case (R.id.exit):
                finish();
                System.runFinalization();
                System.exit(2);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(Uri uri) {
        //you can leave it empty
    }
}

package me.moonshadow.xperia.tools;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import me.moonshadow.xperia.tools.fragments.CpuSettings;
import me.moonshadow.xperia.tools.fragments.CpuStatusBar;
import me.moonshadow.xperia.tools.fragments.CpuStatusPie;
import me.moonshadow.xperia.tools.fragments.GerneralInfo;
import me.moonshadow.xperia.tools.fragments.IoGovernor;
import me.moonshadow.xperia.tools.fragments.KnockOnSettings;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final int GERNERALINFO = 0;
    private static final int CPUSTATUSBAR = 1;
    private static final int CPUSTATUSPIE = 2;
    private static final int CPUSETTINGS = 3;
    private static final int IOGOV = 4;
    private static final int KNOCKONSETTINGS = 5;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private GerneralInfo mGerneralInfo;
    private CpuStatusBar mCpuStatusBar;
    private CpuStatusPie mCpuStatusPie;
    private CpuSettings mCpuSettings;
    private IoGovernor mIoGovernor;
    private KnockOnSettings mKnockOnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        onSectionAttached(GERNERALINFO);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = null;
        onSectionAttached(position);
        switch (position) {
            case GERNERALINFO:
                if (mGerneralInfo == null) {
                    mGerneralInfo = new GerneralInfo();
                }
                fragment = mGerneralInfo;
                break;
            case CPUSTATUSBAR:
                if (mCpuStatusBar == null) {
                    mCpuStatusBar = new CpuStatusBar();
                }
                fragment = mCpuStatusBar;
                break;
            case CPUSTATUSPIE:
                if (mCpuStatusPie == null) {
                    mCpuStatusPie = new CpuStatusPie();
                }
                fragment = mCpuStatusPie;
                break;
            case CPUSETTINGS:
                if (mCpuSettings == null) {
                    mCpuSettings = new CpuSettings(this);
                }
                fragment = mCpuSettings;
                break;
            case IOGOV:
                if (mIoGovernor == null) {
                    mIoGovernor = new IoGovernor(this);
                }
                fragment = mIoGovernor;
                break;
            case KNOCKONSETTINGS:
                if (mKnockOnSettings == null) {
                    mKnockOnSettings = new KnockOnSettings(this);
                }
                fragment = mKnockOnSettings;
                break;
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case GERNERALINFO:
                mTitle = getString(R.string.tab_title_1);
                break;
            case CPUSTATUSBAR:
                mTitle = getString(R.string.tab_title_2);
                break;
            case CPUSTATUSPIE:
                mTitle = getString(R.string.tab_title_3);
                break;
            case CPUSETTINGS:
                mTitle = getString(R.string.tab_title_4);
                break;
            case IOGOV:
                mTitle = getString(R.string.tab_title_5);
                break;
            case KNOCKONSETTINGS:
                mTitle = getString(R.string.tab_title_6);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
            localBuilder.setTitle(R.string.help);
            localBuilder.setMessage(R.string.help_msg);
            localBuilder.setPositiveButton(R.string.ok, null);
            localBuilder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

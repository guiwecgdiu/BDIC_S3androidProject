package com.example.myapplication.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.DBStorage.SiteInfoSQLiteOpenHelper;
import com.example.myapplication.Model.SiteInfo;
import com.example.myapplication.R;
import com.example.myapplication.presentation.FieldDialogFragment;
import com.example.myapplication.presentation.GridAdaptor;
import com.example.myapplication.presentation.SiteInfoAdapter;

import java.io.File;
import java.util.ArrayList;

public class MainMenu extends AppCompatActivity implements FieldDialogFragment.FieldDialogCallback,NavigationView.OnNavigationItemSelectedListener{
    final String TAG = "MainMenu";
    /**
     *  2019/11/6
    * @param gofolder: folderButton
    * @param goServer  serverButton
     */
    Button gofolder;
    LinearLayout addServer;
    FloatingActionButton fad;
    GridView folderGrids;
    GridAdaptor gridAdaptor;
    ArrayList<File> foldersList;
 //   GridView gridFile;
    RecyclerView siteView;
  //  GridAdaptor gridAdaptor;
    SiteInfoAdapter siteAdapter;
    ArrayList<SiteInfo> siteInfoListArray;

    String rootPath;

    static String remotePath;
    static String localPath;
    /*
    * 2019/11/6
    * Set the click listenner
    * @param gofolder
    * @param goServer
     */
    private String m_Text = "";
    public void inpurDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Folder With Name:");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        GridLayout.LayoutParams params=new GridLayout.LayoutParams();
        params.setMargins(20,0,20,0);
        input.setLayoutParams(params);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                File localFolder = new File(Environment.getExternalStorageDirectory().toString()+"/lazyDocument"+"/"+m_Text);
                if(!localFolder.exists()){
                    localFolder.mkdir();

                }
                loadFolders();
                gridAdaptor.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });



        builder.show();
    }


    private void initClickListenner(){
        fad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inpurDialog();

            }
        });


        gofolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenu.this, FolderActivity.class);
                startActivity(i);
            }
        });

        addServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFieldDialog();
            }
        });
//        siteAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                GridView gridView = (GridView) parent;
//                SiteInfo item = (SiteInfo) gridView.getAdapter().getItem(position);
//                Intent i = new Intent(MainMenu.this,SftpMenu.class);
//                Bundle b =new Bundle();
//                i.putExtra("siteInfo",item);
//                startActivity(i);
//            }
//        });
//
//        siteView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                GridView gridView = (GridView) parent;
//                SiteInfo item = (SiteInfo) gridView.getAdapter().getItem(position);
//                remove(item);
//                return true;
//            }
//        });//xxxx
        siteAdapter.setOnItemClickListener(new SiteInfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SiteInfo item = (SiteInfo) siteAdapter.getItem(position);
                Intent i = new Intent(MainMenu.this,SftpMenu.class);
                Bundle b =new Bundle();
                i.putExtra("siteInfo",item);
                startActivity(i);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                SiteInfo item = (SiteInfo) siteAdapter.getItem(position);
                remove(item);
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Intent i= new Intent(this, MainMenu.class);
            startActivity(i);
        } else if (id == R.id.nav_document) {
            Intent i= new Intent(this, FolderActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_InTest) {
            Intent i= new Intent(this, ShareActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_github) {

        } else if (id == R.id.nav_blog) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
     * 2019/11/6
    * Init the components
    * @param goServer
    * @param goFolder
     */


    private void init(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitleTextAppearance(this,R.style.Toolbar_TitleText);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(Color.BLACK);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


      fad=findViewById(R.id.btn_float);
      gofolder= findViewById(R.id.bFolder_mainmenu);
      addServer = findViewById(R.id.baddServer);
      //gridFile = (GridView)findViewById(R.id.bFileGrid);
        this.folderGrids=findViewById(R.id.gridFolders);
        gridAdaptor=new GridAdaptor(this,foldersList);
        folderGrids.setAdapter(gridAdaptor);
        folderGrids.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file =(File) parent.getAdapter().getItem(position);
                Intent i =new Intent(MainMenu.this,FolderActivity.class);
                i.putExtra("MainIntent",file.getName());
                startActivity(i);

            }
        });
        folderGrids.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final File item =(File)parent.getAdapter().getItem(position);
                AlertDialog aldg;
                AlertDialog.Builder adBd=new AlertDialog.Builder(MainMenu.this);
                adBd.setTitle("Alert");
                adBd.setMessage("Do you want to delete this folder and all files");
                adBd.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                        Boolean isSuccess=deleteDir(item);
                        loadFolders();
                        gridAdaptor.notifyDataSetChanged();
                        if (isSuccess) {
                            Toast.makeText(MainMenu.this,"Delete success", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainMenu.this, "Failed to delete", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                adBd.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                aldg=adBd.create();
                aldg.show();
                return true;
            }
        });




        siteView = (RecyclerView) findViewById(R.id.siteinfoView);
        siteInfoListArray =new ArrayList<SiteInfo>();

       // gridFile.setAdapter(gridAdaptor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        siteView.setLayoutManager(layoutManager);
       siteAdapter = new SiteInfoAdapter(siteInfoListArray);
        siteView.setAdapter(siteAdapter);
        siteView.setFocusableInTouchMode(false);
        siteInfoListArray.addAll(loadDatabase());
        siteAdapter.notifyDataSetChanged();

        //xxxx

    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }


    void showFieldDialog() {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(FieldDialogFragment.FRAGTAG);
        if (prev != null) {
            ft.remove(prev);
        } else {
            ft.addToBackStack(null);
        }
        FieldDialogFragment.newInstance(1).show(getSupportFragmentManager(), FieldDialogFragment.FRAGTAG);
    }


    /** 2019/11/6 
     * The start of a lifecycle
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_menu);
        setContentView(R.layout.entry_main);
        powerPermission();
        initData();
        init();
        initClickListenner();

    }

    protected void initData(){
        foldersList=new ArrayList<File>();
        loadFolders();
    }
    File rootFolder;
    protected void loadFolders(){
        foldersList.clear();
        rootFolder = loadLocalFolder();
        File [] subfolders = rootFolder.listFiles();
        Log.d(TAG,subfolders.length+"");
        for(int i=0;i<subfolders.length;i++){
            foldersList.add(subfolders[i]);
        }

    }

    protected void powerPermission(){
        if(  ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"Line 89: The readExternal is denied");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }else{
            Log.d(TAG,"line95: The readExternal is granted");
        }

        if(  ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED){
            Log.d(TAG,"Line 89: The writeExternal is denied");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2);
        }else{
            Log.d(TAG,"line95: The writeExternal is granted");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == 1) {
            if(grantResults.length>0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"The read is setted",Toast.LENGTH_LONG);
            }else {
                //Permission denied
                Toast.makeText(this,"The permission is denied",Toast.LENGTH_LONG);
            }
        }

        if (requestCode == 2) {
            if(grantResults.length>0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"The write is setted",Toast.LENGTH_LONG);
            }else {
                //Permission denied
                Toast.makeText(this,"The permission is denied",Toast.LENGTH_LONG);
            }
        }
    }

    protected File loadLocalFolder(){
        File file = new File(Environment.getExternalStorageDirectory().toString()+"/lazyDocument");
        File localFolder = new File(Environment.getExternalStorageDirectory().toString()+"/lazyDocument"+"/Local");
        File remoteFolder = new File(Environment.getExternalStorageDirectory().toString()+"/lazyDocument"+"/Remote");
        if(!file.exists()){
            file.mkdir();
            localFolder.mkdir();

            remoteFolder.mkdir();

        }
        localPath = localFolder.toString();
        remotePath = remoteFolder.toString();
        Log.d(TAG,"I am here"+file.getName());
        return file;
    }






    @Override
    public void doPositiveClick(Bundle bundle) {
//

        String mHost_ip = bundle.getString(FieldDialogFragment.BHostAddress, "default");
        String mSite_Name = bundle.getString(FieldDialogFragment.BSiteName, "default");
        String mProtocol= bundle.getString(FieldDialogFragment.BProtocol, "default");
         String mUsername= bundle.getString(FieldDialogFragment.BUsername, "default");
         String mPassward=bundle.getString(FieldDialogFragment.BPassward, "default");

        SiteInfo sf = new SiteInfo(mHost_ip, mSite_Name,mUsername,mPassward);
        //SQlite test
        //deleteDatabase(getFilesDir()+"/test.db");
        addRecord(sf);
       // Log.d(TAG,loadDatabase().toString());
    }
    public void addRecord(SiteInfo info){
        ArrayList<SiteInfo> temp;
        SiteInfoSQLiteOpenHelper mHelper = SiteInfoSQLiteOpenHelper.getInstance(this, 2);
        SQLiteDatabase db = mHelper.openWriteLink();
        long i = mHelper.insert(info);
        temp=loadDatabase();
        Log.d(TAG,"Line 212:"+temp.toString());
        SiteInfo last = temp.get(temp.size()-1);
        int id = last.mId;
        info.setId(id);
        siteInfoListArray.add(info);

        mHelper.closeLink();
        siteAdapter.notifyDataSetChanged();//xxx
        siteView.scrollToPosition(siteAdapter.getItemCount());
    }
    public ArrayList<SiteInfo> loadDatabase(){
        SiteInfoSQLiteOpenHelper mHelper = SiteInfoSQLiteOpenHelper.getInstance(this,2);
        SQLiteDatabase db = mHelper.openWriteLink();
        ArrayList<SiteInfo> tempArray = mHelper.queryAll();
        //Log.d(Tag,"Successful load Array with length = "+tempArray.size());
        mHelper.closeLink();
        return tempArray;
    }

    public boolean remove(final SiteInfo i ){
        SiteInfoSQLiteOpenHelper mHelper = SiteInfoSQLiteOpenHelper.getInstance(this,2);
        SQLiteDatabase db = mHelper.openWriteLink();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainMenu.this);
        builder.setMessage("Are you sure to remove");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(i);
                //Log.d(Tag, "deleteItem(item);");
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
        //Avoid response the both click, consume the request here by returning true
        return true;
    }

    private void deleteItem(SiteInfo siteInfo){
        SiteInfoSQLiteOpenHelper mHelper = SiteInfoSQLiteOpenHelper.getInstance(this,2);
        SQLiteDatabase DB = mHelper.openWriteLink();
        int id = mHelper.deleteSiteInfo(siteInfo);
        siteInfoListArray.remove(siteInfo);
        mHelper.closeLink();
        siteAdapter.notifyDataSetChanged();//xx

    }


    @Override
    public void doNegativeClick() {

    }

    public void test(View view) {
        Intent i= new Intent(this, ShareActivity.class);
        startActivity(i);
    }
}

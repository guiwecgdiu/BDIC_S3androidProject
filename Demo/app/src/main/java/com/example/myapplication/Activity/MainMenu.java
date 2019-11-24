package com.example.myapplication.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DBStorage.SiteInfoSQLiteOpenHelper;
import com.example.myapplication.FileUtils.RequestCode;
import com.example.myapplication.Model.SiteInfo;
import com.example.myapplication.R;
import com.example.myapplication.Test;
import com.example.myapplication.presentation.FieldDialogFragment;
import com.example.myapplication.presentation.GridAdaptor;
import com.example.myapplication.presentation.SiteInfoAdapter;

import java.util.ArrayList;

public class MainMenu extends AppCompatActivity implements FieldDialogFragment.FieldDialogCallback {
    final String TAG = "MainMenu";
    /**
     *  2019/11/6
    * @param gofolder: folderButton
    * @param goServer  serverButton
     */
    Button gofolder;
    LinearLayout addServer;
 //   GridView gridFile;
    RecyclerView siteView;
  //  GridAdaptor gridAdaptor;
    SiteInfoAdapter siteAdapter;
    ArrayList<SiteInfo> siteInfoListArray;
    /*
    * 2019/11/6
    * Set the click listenner
    * @param gofolder
    * @param goServer
     */
    private void initClickListenner(){

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

    /*
     * 2019/11/6
    * Init the components
    * @param goServer
    * @param goFolder
     */


    private void init(){
      gofolder= findViewById(R.id.bFolder_mainmenu);
      addServer = findViewById(R.id.baddServer);
      //gridFile = (GridView)findViewById(R.id.bFileGrid);
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
        init();
        initClickListenner();

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
        Intent i= new Intent(this, Test.class);
        startActivity(i);
    }
}

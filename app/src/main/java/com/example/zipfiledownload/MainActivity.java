package com.example.zipfiledownload;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DownloadFragment.DownloadCallbacks, FileDownloadInterface {

    private int REQUEST_PERMISSIONS = 100;
    String PERMISSIONS_REQUIRED[] = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String DOWNLOAD_FRAGMENT = "download_fragment";
    private DownloadFragment downloadFragment;
    RecyclerView recyclerView;
    String s1[];
    String s2[];
    String s3[];
    String s4[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        s1 = getResources().getStringArray(R.array.station);
        s2 = getResources().getStringArray(R.array.prixGo);
        s3 = getResources().getStringArray(R.array.prixSP95);
        s4 = getResources().getStringArray(R.array.prixSP98);

        recyclerView = (RecyclerView) findViewById(R.id.fragment_main_recycler_view);

        MyAdapter myAdapter = new MyAdapter(this, s1, s2, s3, s4);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, REQUEST_PERMISSIONS);

        // Téléchargement du fichier des carburants à l'ouverture de l'application
        FragmentManager fm = getSupportFragmentManager();
        downloadFragment = (DownloadFragment) fm.findFragmentByTag(DOWNLOAD_FRAGMENT);
        // if it's null, it was created, otherwise it was created and retained
        if (downloadFragment == null) {
            downloadFragment = new DownloadFragment();
            fm.beginTransaction().add(downloadFragment, DOWNLOAD_FRAGMENT).commit();
        }
    }

    public String GetUnzipFilePath(String filePath) {

        DocumentBuilderFactory factory	=	DocumentBuilderFactory.newInstance();
        DocumentBuilder builder	= null;

        try {
            builder = factory.newDocumentBuilder();
            Document dom	=	builder.parse(new FileInputStream(filePath));
            Element root	=	dom.getDocumentElement();
            NodeList items	= root.getElementsByTagName("pdv");

            for	(int	i	=	0;	i	<	items.getLength();	i++) {

                Node item = items.item(i);
                String id = item.getAttributes().getNamedItem("id").getNodeValue();
                int nbtt = items.getLength();
                String tt ="";
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return "message";
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.async_fragment_download) {
            /*FragmentManager fm = getSupportFragmentManager();
            downloadFragment = (DownloadFragment) fm.findFragmentByTag(DOWNLOAD_FRAGMENT);

            // if it's null, it was created, otherwise it was created and retained
            if (downloadFragment == null) {
                downloadFragment = new DownloadFragment();
                fm.beginTransaction().add(downloadFragment, DOWNLOAD_FRAGMENT).commit();
            }*/
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Message apres le download
    public void onPostExecute(String msg) {
        Toast.makeText(this, "Mis à jour de l'API effectué", Toast.LENGTH_SHORT).show();
        removeDownloadFragment();
    }

    private void removeDownloadFragment() {
        FragmentManager fm = getSupportFragmentManager();
        downloadFragment = (DownloadFragment) fm.findFragmentByTag(DOWNLOAD_FRAGMENT);
        if (downloadFragment != null) {
            fm.beginTransaction()
                    .remove(downloadFragment)
                    .commit();
        }
    }
}

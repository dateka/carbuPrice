package com.example.zipfiledownload;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DownloadFragment extends Fragment implements FileDownloadInterface{

    interface DownloadCallbacks {
        void onPostExecute(String msg);
    }

    private DownloadCallbacks mCallbacks;
    private DownloadTask mTask;
    Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // Retain this fragment across configuration changes.

        File gameDir = new File("/data/data/" + getActivity().getPackageName() + "/file");
        gameDir.mkdirs();

        // Create and execute the background task.
        mTask = new DownloadTask();
        mTask.execute("https://www.data.gouv.fr/fr/datasets/r/087dfcbc-8119-4814-8412-d0a387fac561", "/data/data/" + getActivity().getPackageName() + "/file/2048.zip");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (DownloadCallbacks) getActivity();
        if(context instanceof Activity){
            activity = (Activity) context;
        }
    }

    @Override
    public String GetUnzipFilePath(String filePath) {
        return filePath;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    private boolean unpackZip(String filePath) {
        InputStream is;
        ZipInputStream zis;
        try {
            File zipfile = new File(filePath);
            String parentFolder = zipfile.getParentFile().getPath();
            String filename;
            is = new FileInputStream(filePath);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                filename = ze.getName();
                if (ze.isDirectory()) {
                    File fmd = new File(parentFolder + "/" + filename);
                    fmd.mkdirs();
                    continue;
                }
                FileOutputStream fout = new FileOutputStream(parentFolder + "/" + filename);

                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }
                fout.close();
                zis.closeEntry();
            }
            zis.close();
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            String destinationFilePath = "";
            try {
                URL url = new URL(args[0]);
                destinationFilePath = args[1];

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }

                // download the file
                input = connection.getInputStream();

                Log.d("DownloadFragment ", "destinationFilePath=" + destinationFilePath);
                new File(destinationFilePath).createNewFile();
                output = new FileOutputStream(destinationFilePath);

                byte data[] = new byte[4096];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }

            File f = new File(destinationFilePath);

            Log.d("DownloadFragment ", "f.getParentFile().getPath()=" + f.getParentFile().getPath());
            Log.d("DownloadFragment ", "f.getName()=" + f.getName().replace(".zip", ""));
            //unpackZip(f.getParentFile().getPath(), f.getName().replace(".zip", ""));
            boolean test = unpackZip(destinationFilePath);

            try{
                ((FileDownloadInterface) activity).GetUnzipFilePath("/data/data/com.example.zipfiledownload/file/PrixCarburants_instantane.xml");
            }catch (ClassCastException ignored) {}

            // ------------------------------------------- IMPOTANT ----------------------------------
            /*if(test){
                DocumentBuilderFactory factory	=	DocumentBuilderFactory.newInstance();
                DocumentBuilder builder	= null;

                try {
                    builder = factory.newDocumentBuilder();
                    Document dom	=	builder.parse(new	FileInputStream("/data/data/com.example.zipfiledownload/file/PrixCarburants_instantane.xml"));
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
            }*/
            // ------------------------------------------- FIN IMPOTANT ----------------------------------
            return "Download complete !";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("DownloadFragment ", s);
            mCallbacks.onPostExecute(s);
        }
    }

}

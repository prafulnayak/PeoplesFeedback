package shamgar.org.peoplesfeedback.Fragments;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import shamgar.org.peoplesfeedback.Model.Phone;
import shamgar.org.peoplesfeedback.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Politicians extends Fragment implements View.OnClickListener {
    private static final String TAG = "shivanshTag";
    Button uploadButton;

    private Uri filePath;
    ArrayList<String> contacts;

    private StorageReference mStorageRef;
    private String results;
    Phone toJson;


    public Politicians() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_politicians, container, false);

        uploadButton = (Button)view.findViewById(R.id.uploadButton);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        uploadButton.setOnClickListener(this);

         toJson = new Phone();


        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            Log.d("Politicians","visible");
        }else{
            // fragment is not visible
        }
    }

    @Override
    public void onClick(View v) {

         contacts = createContactJSON();

         Log.i("contacts",contacts.toString());


//Contacts added to a JSON Object
        for (int i=0;i<contacts.size();i++)
        {
            toJson.name = 1;
            toJson.phone = 1;
            toJson.array = new ArrayList<>();
            toJson.array.add(contacts.get(i));
            Gson gson = new Gson();
            String JSON = gson.toJson(toJson);

            Log.i("json",JSON);


            DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
            ref.child("phone").push().setValue(JSON).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getContext(),"uploaded",Toast.LENGTH_LONG).show();
                }


            });

        }


    }

    public ArrayList<String> createContactJSON(){

        //to store name-number pair
       ArrayList<String> contacts=new ArrayList<>();

        try {
            Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

            while (phones.moveToNext()) {
                int name = phones.getInt(Integer.parseInt(phones.getColumnName(Integer.parseInt(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(name,phoneNumber);
            }
            phones.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        
        return contacts;
       
    }



    private Uri listToFile() {

        String path = null;
        try {
            Writer output = null;
            path = Environment.getExternalStorageDirectory()+"/sample.json";
            File file = new File(path);
            output = new BufferedWriter(new FileWriter(file));
            output.write(contacts.toString());
            output.close();
           // Toast.makeText(getActivity(), "Saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
           // Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        getActivity().finish();
        return Uri.fromFile(new File("/sdcard/sample.json"));

    }


    //this method will upload the file
    private void uploadToServer() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
          //  final ProgressDialog progressDialog = new ProgressDialog(getContext());
           // progressDialog.setTitle("Uploading...");
          //  progressDialog.show();

            StorageReference riversRef = mStorageRef.child("jsonFile/sample.json");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            Toast.makeText(getActivity(), "File Uploaded", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                           // progressDialog.dismiss();
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.i(TAG, "Here");
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                           // progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            Log.i(TAG, "Error");
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
        }
    }



}

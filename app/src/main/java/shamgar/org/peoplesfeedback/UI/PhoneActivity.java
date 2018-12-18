package shamgar.org.peoplesfeedback.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import shamgar.org.peoplesfeedback.Model.Followings;
import shamgar.org.peoplesfeedback.Model.People;
import shamgar.org.peoplesfeedback.Model.Politics;
import shamgar.org.peoplesfeedback.R;
import shamgar.org.peoplesfeedback.Utils.SharedPreferenceConfig;

public class PhoneActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "PhoneActivity";
    private EditText phoneNo;
    private EditText code;

    private RelativeLayout rlgender,rllocation,rlphone;

    private Button sendVerifyPhoneNo,btnnext,btncontinue;
    private Button verifyCode;
    private Button reSend;

    private CheckBox checkmale,checkfemale;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ProgressDialog loadingbar;
    ArrayList<String> state = new ArrayList<String>();
    ArrayList<String> districts = new ArrayList<String>();
    ArrayList<String> constituency = new ArrayList<String>();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    FirebaseAuth mAuth;
    private Spinner spinstate,spindist,spinnerConstituency;
    private ArrayAdapter adapter,adapter1,adapter2;
    private String result;
    private TextView stateTextview,districtTextview;
    private SharedPreferences.Editor editor;
    private String district;
    private TextView constituencyTextview;
    private SharedPreferenceConfig sharedPreference;
    private DatabaseReference databaseReference,userRef;

    //    @Override
        //    protected void onStart() {
        //        super.onStart();
        //        FirebaseUser currentUser = mAuth.getCurrentUser();
        //        updateUI(currentUser);
        //    }

    private void updateUI(FirebaseUser currentUser)
    {
        if(currentUser != null)
        {
            Toast.makeText(this,""+currentUser.getEmail()+" : "+currentUser.getDisplayName(),Toast.LENGTH_SHORT).show();
            sharedPreference.writePhoneNo(currentUser.getPhoneNumber());
           // sharedPreference.writeName(currentUser.getDisplayName());

            Intent intent = new Intent(PhoneActivity.this,HomeScreenActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signuplayout);
        loadingbar=new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sharedPreference = new SharedPreferenceConfig(this);
        mAuth = FirebaseAuth.getInstance();
        userRef= FirebaseDatabase.getInstance().getReference().child("people");

        FirebaseUser user = mAuth.getCurrentUser();
        if(user.getPhoneNumber()!=null){
            updateUI(user);
        }

        phoneNo = findViewById(R.id.e_phone_no_verification);
        sendVerifyPhoneNo = findViewById(R.id.b_verify_phone_no);
        btncontinue = findViewById(R.id.btncontinue);
        btnnext = findViewById(R.id.btnnext);
        rlgender = findViewById(R.id.rlgender);
        rllocation = findViewById(R.id.rllocation);
        rlphone = findViewById(R.id.rlphone);

        sendVerifyPhoneNo.setOnClickListener(this);
        btncontinue.setOnClickListener(this);
        btnnext.setOnClickListener(this);

        spinstate =  findViewById(R.id.spinnerstate);
        spindist = findViewById(R.id.spinnerdistrict);
        spinnerConstituency =  findViewById(R.id.spinnerConstituency);

        checkmale =  findViewById(R.id.checkmale);
        checkfemale =  findViewById(R.id.checkfemale);

        adapter= new ArrayAdapter(this,android.R.layout.simple_spinner_item,state);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter1= new ArrayAdapter(this,android.R.layout.simple_spinner_item,districts);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter2= new ArrayAdapter(this,android.R.layout.simple_spinner_item,constituency);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DatabaseReference states = database.getReference("states");
        states.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot children) {

                        //Get map of users in datasnapshot
                        state.clear();
                        for(DataSnapshot states:children.getChildren())
                        {
                            state.add((String) states.getKey());
                        }
                        spinstate.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        spinstate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                stateTextview = (TextView)adapterView.getSelectedView();
                result = stateTextview.getText().toString();

                DatabaseReference ref = database.getReference("states/"+result);
                ref.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                districts.clear();
                                for(DataSnapshot district : dataSnapshot.getChildren())
                                {
                                    districts.add((String) district.getKey());
                                }
                                spindist.setAdapter(adapter1);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {
                                //handle databaseError
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });
        spindist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                districtTextview = (TextView)adapterView.getSelectedView();
                district = districtTextview.getText().toString();
                DatabaseReference ref = database.getReference("states/"+result+"/"+district+"/con");
                ref.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                constituency.clear();
                                for(DataSnapshot constituencies : dataSnapshot.getChildren())
                                {
                                    constituency.add((String) constituencies.getKey());
                                }
                                spinnerConstituency.setAdapter(adapter2);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {
                                //handle databaseError
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerConstituency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                constituencyTextview = (TextView)adapterView.getSelectedView();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential)
            {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                String phonenumber= phoneNo.getText().toString();
                sharedPreference.writePhoneNo(phonenumber);
               signInWithPhoneAuthCredential(credential);
                loadingbar.setTitle("Phone verification");
                loadingbar.setMessage("please wait,while we are verifying with your code");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.v(TAG, "onCodeSent:" + verificationId);
                Log.v(TAG, "onCodeSent:" + token);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                loadingbar.dismiss();

                // ...
            }
        }; // new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){}




    }



    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task1)
                    {
                        if (task1.isSuccessful())
                        {
                            loadingbar.dismiss();

                            String currentUserId=mAuth.getCurrentUser().getPhoneNumber();
                            String deviceToken= FirebaseInstanceId.getInstance().getToken();
                            userRef.child(currentUserId.substring(3)).child("deviceToken").setValue(deviceToken)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.v(TAG, "signInWithCredential:success");
                                            Toast.makeText(PhoneActivity.this, "Success",Toast.LENGTH_SHORT).show();
                                            //                            signOut();
                                            FirebaseUser user = task1.getResult().getUser();
//                            signInWithPhoneAuthCredential(credential);
//                            Log.v(TAG, "signInWithCredential:success"+user.getPhoneNumber());
                                            updateUI(user);
                                            insertNewUser();
                                            // ...
                                        }
                                    });


                        }
                        else {
                            loadingbar.dismiss();
                                // Sign in failed, display a message and update the UI
                                Log.v(TAG, "signInWithCredential:failure", task1.getException());
                                Toast.makeText(PhoneActivity.this, "Failed",Toast.LENGTH_SHORT).show();
                                if (task1.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void insertNewUser() {
        Politics politics = new Politics();
        politics.setPoliticianId("1234");
          String timeStamp = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss").format(Calendar.getInstance().getTime());

        Followings followings = new Followings();
        final People people = new People("u",
                sharedPreference.readName(),
                sharedPreference.readEmail(),
                sharedPreference.readPhoneNo(),
                sharedPreference.readState(),
                sharedPreference.readDistrict(),
                sharedPreference.readConstituancy(),
                sharedPreference.readGender(),
                timeStamp,
                "",
                "");
//        String type,name, phoneno,state,dist,constituancy,gender,createdon,shortheading,desc;
//        Followings followings;
//        Follwers follwers;
//        Tagging tag;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("people").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(sharedPreference.readPhoneNo().substring(3))){
                    Toast.makeText(PhoneActivity.this, "Exist ", Toast.LENGTH_LONG).show();
                    updateExistingUserDetails();
                }else {

                    Toast.makeText(PhoneActivity.this, "Does not Exist ", Toast.LENGTH_LONG).show();
                    insertNewUserDetails(people);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void insertNewUserDetails(final People people) {
        databaseReference.child("people").child(sharedPreference.readPhoneNo().substring(3)).setValue(people).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){


                    Toast.makeText(PhoneActivity.this, "login success: "+people.getName(), Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(PhoneActivity.this, "login Fail: "+people.getName(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateExistingUserDetails() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Posts").child("Z6zi5Dl0hj");
                        Map<String, Object> updates = new HashMap<String,Object>();
                        updates.put("constituancy",sharedPreference.readConstituancy());
                        updates.put("dist", sharedPreference.readDistrict());
                        updates.put("email", sharedPreference.readEmail());
                        updates.put("name", sharedPreference.readName());
                        updates.put("state", sharedPreference.readState());
////                        ref.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
////                            @Override
////                            public void onSuccess(Void aVoid) {
////
////                            }
////                        });
        databaseReference.child("people").child(sharedPreference.readPhoneNo().substring(3))
                .updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(PhoneActivity.this, "Information Updated ", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.b_verify_phone_no:
                sendVerificationCode(phoneNo.getText().toString());
                break;

            case R.id.btnnext:

                if (!checkmale.isChecked() && !checkfemale.isChecked())
                {
                    Toast.makeText(getApplicationContext(),"please select gender",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    rlgender.setVisibility(View.GONE);
                    rllocation.setVisibility(View.VISIBLE);
                    btnnext.setVisibility(View.GONE);
                    btncontinue.setVisibility(View.VISIBLE);



                    if (checkmale.isChecked()&& !checkfemale.isChecked())
                     {

                    sharedPreference.writeGender("MALE");

                      }
                    if(!checkmale.isChecked() && checkfemale.isChecked())
                    {
                        sharedPreference.writeGender("FEMALE");

                    }
                }


                break;

            case R.id.btncontinue:

                rllocation.setVisibility(View.GONE);
                btncontinue.setVisibility(View.GONE);
                rlphone.setVisibility(View.VISIBLE);
                sendVerifyPhoneNo.setVisibility(View.VISIBLE);

                String state= stateTextview.getText().toString();
                String district= districtTextview.getText().toString();
                String constit= constituencyTextview.getText().toString();

//               Toast.makeText(getApplicationContext(),"state: "+state+"\n"+" district "+district+"\n"+"constituency: "+constit,Toast.LENGTH_LONG).show();


                sharedPreference.writeState(state);

                sharedPreference.writeDistrict(district);

                sharedPreference.writeConstituancy(constit);



                break;

        }

    }

//    private void resendVerificationCode(String phoneNumber,
//                                        PhoneAuthProvider.ForceResendingToken token) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,        // Phone number to verify
//                60,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks,         // OnVerificationStateChangedCallbacks
//                token);             // ForceResendingToken from callbacks
//    }

//    private void verifySentCode(String s) {
//
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, s);
//        signInWithPhoneAuthCredential(credential);
//    }

    private void sendVerificationCode(String phoneNumber)
    {
        loadingbar.setTitle("Sending verfication code");
        loadingbar.setMessage("please wait,while we are authenticating with your phone");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }



    public void checkedButton(View view)
    {

        switch(view.getId()) {

            case R.id.checkmale:

                checkfemale.setChecked(false);


                break;

            case R.id.checkfemale:

                checkmale.setChecked(false);

                break;

        }
    }



}

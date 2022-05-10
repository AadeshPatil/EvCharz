package com.project.evcharz.Pages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.project.evcharz.Model.UserModel;
import com.project.evcharz.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    UserModel userModel;
    StorageReference storageReference;
    Context context;
    private final int PICK_IMAGE_REQUEST = 22;
    String currentUid;
    EditText txt_name,txt_email,txt_phone_no;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);

        ImageButton backBtn = this.findViewById(R.id.backBtn);

        ImageButton profile_pic = findViewById(R.id.btn_upload_profile_pic);
         txt_name = findViewById(R.id.eTextUserName);
         txt_email = findViewById(R.id.email_address);
         txt_phone_no = findViewById(R.id.mb_no);

        Button updateBtn = findViewById(R.id.update_btn);


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("userDetails");
        currentUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        getUserData();

        profile_pic.setOnClickListener(v-> SelectImage());

        backBtn.setOnClickListener(v -> finish());



        updateBtn.setOnClickListener(v->{
            String name = txt_name.getText().toString();
            String phone = txt_phone_no.getText().toString();
            String address = txt_email.getText().toString();

            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(phone) && TextUtils.isEmpty(address)) {
                Toast.makeText(this, "Please fill All the Values", Toast.LENGTH_SHORT).show();
            }else{
                String id = databaseReference.push().getKey();
                userModel = new UserModel(currentUid,name,address , phone);
                    if (currentUid != null){
                        databaseReference.child(currentUid).setValue(userModel).addOnCompleteListener(it->{
                            if (it.isSuccessful()){
                                Toast.makeText(this,"Profile Updated", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(this,"Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

            }
        });



    }

    private void uploadPic(String filePath2) {
            if (filePath2 != null) {
                StorageReference ref = storageReference.child("profilePic/"+currentUid);
                ref.putFile(filePath)
                        .addOnSuccessListener(
                                taskSnapshot -> Toast.makeText(this, "Profile  Uploaded!!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
    }

    private void SelectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            filePath = data.getData();

            String filePath2 = copyFileToInternalStorage(filePath,"temp");
            System.out.println("Selectedfilepath "+filePath);
//            uploadPic(filePath2);
        }
    }



    private void getUserData() {

        databaseReference.child(currentUid).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("firebase", "Error getting data", task.getException());
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                UserModel userModel = task.getResult().getValue(UserModel.class);

                txt_phone_no.setText(userModel.getMobileNo());
                txt_name.setText(userModel.getName());
                txt_email.setText(userModel.getEmailId());
                txt_phone_no.setEnabled(false);
            }

        });
    }


    private String copyFileToInternalStorage(Uri uri,String newDirName) {

        Cursor returnCursor = context.getContentResolver().query(uri, new String[]{
                OpenableColumns.DISPLAY_NAME,OpenableColumns.SIZE
        }, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));

        File output;
        if(!newDirName.equals("")) {
            File dir = new File(context.getFilesDir() + "/" + newDirName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            output = new File(context.getFilesDir() + "/" + newDirName + "/" + name);
        }
        else{
            output = new File(context.getFilesDir() + "/" + name);
        }
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(output);
            int read = 0;
            int bufferSize = 1024;
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();

        }
        catch (Exception e) {

            Log.e("Exception", e.getMessage());
        }

        return output.getPath();
    }


}
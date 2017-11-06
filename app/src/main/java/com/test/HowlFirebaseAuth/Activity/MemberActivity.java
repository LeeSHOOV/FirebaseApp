package com.test.HowlFirebaseAuth.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.test.HowlFirebaseAuth.R;
import com.test.HowlFirebaseAuth.Utility.ProgressDialogTask;
import com.test.HowlFirebaseAuth.ValueObject.Member;

public class MemberActivity extends AppCompatActivity {

    private EditText memberNameEditText;
    private Spinner spinner;
    private String position;

    FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;

    private FirebaseUser mFirebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        memberNameEditText = (EditText) findViewById(R.id.member_name_editText);

        String[] spinnerItems = getResources().getStringArray(R.array.spinnerArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerItems);
        spinner = (Spinner) findViewById(R.id.member_position_spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                position = (String)spinner.getItemAtPosition(i);
                Toast.makeText(MemberActivity.this, position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button createMemberBtn = (Button) findViewById(R.id.create_member_button);
        createMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Member member = new Member();
                member.setMemberEmail(mFirebaseUser.getEmail());
                member.setName(memberNameEditText.getText().toString());
                member.setPosition(position);
                member.setWorkingFlag(false);

                final ProgressDialogTask task = new ProgressDialogTask(MemberActivity.this);
                task.execute();
                task.dismissDialog();

                mFirebaseDatabase.getReference().child("members").push().setValue(member).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        task.dismissDialog();
                        Toast.makeText(MemberActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MemberActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish(); //현재 Activity 사라짐*/
                    }
                });
            }
        });
    }
}

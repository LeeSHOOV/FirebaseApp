package com.test.HowlFirebaseAuth.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.test.HowlFirebaseAuth.R;
import com.test.HowlFirebaseAuth.Utility.CustomDocumentPrintAdapter;
import com.test.HowlFirebaseAuth.ValueObject.WorkInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class WorkInfoListActivity extends AppCompatActivity {
    private static final String TABLE_HEADER_NAME = "名前";
    private static final String TABLE_HEADER_WORKINGTIME = "勤務時間";
    private static final String TABLE_HEADER_DATE = "出・退勤時間";
    private static final String TABLE_HEADER_MODIFIER = "修正";

    private FirebaseDatabase mFirebaseDatabase;

    private List<WorkInfo> workInfoList = new ArrayList<>();
    private List<String> uidList = new ArrayList<>();

    private RecyclerView recyclerView;
    private ImageButton printButton;

    String datePickerMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_info_list);

        datePickerMsg = (String) getIntent().getExtras().get("datePickerMsg");

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.workInfoList_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final WorkInfoListActivity.WorkInfoListRecyclerViewAdapter workInfoListRecyclerViewAdapter
                = new WorkInfoListActivity.WorkInfoListRecyclerViewAdapter();
        recyclerView.setAdapter(workInfoListRecyclerViewAdapter);

        printButton = (ImageButton) findViewById(R.id.print_Button);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPrint();
            }
        });

        mFirebaseDatabase.getReference().child("workinfo").addValueEventListener(new ValueEventListener(){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                workInfoList.clear();
                uidList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    WorkInfo workInfo = snapshot.getValue(WorkInfo.class);
                    String uidKey = snapshot.getKey();
                    if(dateFormat.format(workInfo.getCreateOnWorkDate()).equals(datePickerMsg)){
                        workInfoList.add(workInfo);
                        uidList.add(uidKey);
                    }
                }
                //역순 정렬
                Collections.reverse(workInfoList);
                Collections.reverse(uidList);
                //Refresh
                workInfoListRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    class WorkInfoListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workinfo, parent, false);

            return new WorkInfoListActivity.WorkInfoListRecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd(E) HH:mm", new Locale("en", "US"));
            try{
                //[SET WORKINFO FIELD]
                //nameTextView
                ((CustomViewHolder) holder).nameTextView.setText(workInfoList.get(position).getName());
                //workingTimeTextView
                ((CustomViewHolder) holder).workingTimeTextView.setText(
                        String.format("%2d:%2d",
                        workInfoList.get(position).getWorkingTime() / 60,
                        workInfoList.get(position).getWorkingTime() % 60)
                );
                //onWorkDateTextView
                ((CustomViewHolder) holder).onWorkDateTextView.setText(dateFormat.format(workInfoList.get(position).getCreateOnWorkDate()));
                //modifyOnWorkDateButton
                ((CustomViewHolder) holder).modifyOnWorkDateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //OnWork
                        Intent intent = new Intent(WorkInfoListActivity.this, ModifyWorkInfoActivity.class);
                        workInfoList.get(position).setOnOffWork("onwork");
                        intent.putExtra("workinfo", workInfoList.get(position));
                        startActivity(intent);
                    }
                });

                //offWorkDateTextView
                //modifyOffWorkDateButton
                if(workInfoList.get(position).getCreateOffWorkDate() == null){
                    ((CustomViewHolder) holder).offWorkDateTextView.setText("-");
                    ((CustomViewHolder) holder).modifyOffWorkDateButton.setEnabled(false);
                    ((CustomViewHolder) holder).modifyOffWorkDateButton.setBackgroundColor(Color.parseColor("#cfd8dc"));
                }else{
                    ((CustomViewHolder) holder).offWorkDateTextView.setText(dateFormat.format(workInfoList.get(position).getCreateOffWorkDate()));
                    ((CustomViewHolder) holder).modifyOffWorkDateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(WorkInfoListActivity.this, ModifyWorkInfoActivity.class);
                            workInfoList.get(position).setOnOffWork("offwork");
                            intent.putExtra("workinfo", workInfoList.get(position));
                            startActivity(intent);
                        }
                    });
                }

                //[CONDITIONAL FIELD]
                if(workInfoList.get(position).getWorkingTime() >= 720){
                    ((CustomViewHolder) holder).workingTimeTextView.setTextColor(Color.RED);
                }

                if(workInfoList.get(position).getMemberEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    //Listの中で自分の情報である場合
                    ((CustomViewHolder) holder).modifyOnWorkDateButton.setEnabled(true);
                    ((CustomViewHolder) holder).modifyOffWorkDateButton.setEnabled(true);
                }else{
                    //Listの中で自分の情報ではない場合
                    ((CustomViewHolder) holder).modifyOnWorkDateButton.setEnabled(false);
                    ((CustomViewHolder) holder).modifyOffWorkDateButton.setEnabled(false);
                    ((CustomViewHolder) holder).modifyOnWorkDateButton.setBackgroundColor(Color.parseColor("#b0bec5"));
                    ((CustomViewHolder) holder).modifyOffWorkDateButton.setBackgroundColor(Color.parseColor("#cfd8dc"));
                }

            }catch(NullPointerException e){
                e.printStackTrace();
            }

        }

        public int getItemCount(){
            return workInfoList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder{
            // FIXME: TextViewの命名をもっと具体的にしてください
            TextView nameTextView;
            TextView workingTimeTextView;
            TextView onWorkDateTextView;
            TextView offWorkDateTextView;
            Button modifyOnWorkDateButton;
            Button modifyOffWorkDateButton;

            public CustomViewHolder(View view){
                super(view);
                nameTextView = (TextView) view.findViewById(R.id.item_nameTextView);
                workingTimeTextView = (TextView) view.findViewById(R.id.item_workingTimeTextView);
                onWorkDateTextView = (TextView) view.findViewById(R.id.item_onWorkDateTextView);
                offWorkDateTextView = (TextView) view.findViewById(R.id.item_offWorkDateTextView);
                modifyOnWorkDateButton = (Button) view.findViewById(R.id.modifyOnWorkDate_button);
                modifyOffWorkDateButton = (Button) view.findViewById(R.id.modifyOffWorkDate_button);
            }
        }
    }

    private void doPrint(){
        CustomDocumentPrintAdapter adapter = new CustomDocumentPrintAdapter(WorkInfoListActivity.this, workInfoList);
        printWithAdapter("custom.pdf", adapter);
    }

    private void printWithAdapter(String jobName, PrintDocumentAdapter adapter) {
        if (PrintHelper.systemSupportsPrint()) {
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
            printManager.print(jobName, adapter, null);
        } else {
            Toast.makeText(this, "この端末では印刷をサポートしていません", Toast.LENGTH_SHORT).show();
        }
    }
}

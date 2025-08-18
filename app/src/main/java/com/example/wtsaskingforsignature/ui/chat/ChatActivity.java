package com.example.wtsaskingforsignature.ui.chat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wtsaskingforsignature.R;
import com.example.wtsaskingforsignature.data.api.ChatMessage;

import java.util.Collections;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity implements ChatCallback {
    public static final String EXTRA_ID = "extra_id";

    private ChatMessageAdapter adapter;
    private EditText input;
    private EditText etName, etAge, etBirthplace, etBirthdate, etBirthtime;
    private ProgressBar progress;
    private int fortuneId;
    private TextView header;
    private String head4Text = "";

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        fortuneId = getIntent().getIntExtra(EXTRA_ID, 1);

        header = findViewById(R.id.header);
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatMessageAdapter();
        recycler.setAdapter(adapter);

        input = findViewById(R.id.input);
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etBirthplace = findViewById(R.id.et_birthplace);
        etBirthdate = findViewById(R.id.et_birthdate);
        etBirthtime = findViewById(R.id.et_birthtime);
        Button send = findViewById(R.id.send);
        Button backContent = findViewById(R.id.btn_back_content);
        Button backDraw = findViewById(R.id.btn_back_draw);
        progress = findViewById(R.id.progress);

        adapter.setMessages(Collections.emptyList());

        // 顯示說明與輸入提示，帶入籤文前四行
        String head4 = getIntent().getStringExtra("extra_head4");
        if (head4 == null) head4 = "(無)";
        head4Text = head4;
        header.setText("【籤文內容（前四行）】\n" + head4 + "\n\n請輸入：姓名、年齡、出生地、出生日期與時間。\n請提出問題，DeepSeek 會為你深入分析。");

        // 時間仍提供選擇器（若要也改手動可刪除以下代碼）
        etBirthtime.setOnClickListener(v -> {
            java.util.Calendar c = java.util.Calendar.getInstance();
            new TimePickerDialog(this, (view, h, min) -> {
                String hh = String.format(java.util.Locale.US, "%02d", h);
                String mm = String.format(java.util.Locale.US, "%02d", min);
                etBirthtime.setText(hh + ":" + mm);
            }, c.get(java.util.Calendar.HOUR_OF_DAY), c.get(java.util.Calendar.MINUTE), true).show();
        });

        send.setOnClickListener(v -> {
            if (!validateInputs()) return;
            String base = input.getText().toString().trim();
            if (base.isEmpty()) return;

            String firstLine = head4Text.split("\n")[0];
            String prompt = "【籤文首句】" + firstLine +
                    "\n【基本資料】" +
                    "\n姓名：" + etName.getText().toString().trim() +
                    " 年齡：" + etAge.getText().toString().trim() +
                    " 出生地：" + etBirthplace.getText().toString().trim() +
                    " 出生日期：" + etBirthdate.getText().toString().trim() +
                    " 出生時間：" + etBirthtime.getText().toString().trim() +
                    "\n【回答要求】請以繁體中文、最多500字，先顯示『籤文首句：" + firstLine + "』，再條列重點分析，最後給一句建議。" +
                    "\n【問題】" + base;

            input.setText("");
            progress.setVisibility(View.VISIBLE);
            ChatApiBridge.chat(this, fortuneId, prompt, this);
        });

        // 返回籤文：直接結束，回到啟動此 Activity 的 Compose 內容頁
        backContent.setOnClickListener(v -> finish());

        // 返回抽籤：回到主頁並導航至直接抽籤頁
        backDraw.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.example.wtsaskingforsignature.MainActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("navigate", "direct_draw");
            startActivity(intent);
            finish();
        });
    }

    private boolean validateInputs() {
        boolean ok = true;
        StringBuilder sb = new StringBuilder();
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String birthplace = etBirthplace.getText().toString().trim();
        String birthdate = etBirthdate.getText().toString().trim();
        String birthtime = etBirthtime.getText().toString().trim();

        if (name.isEmpty()) { etName.setError("必填"); sb.append("• 姓名為必填\n"); ok = false; }

        int age = -1;
        try { age = Integer.parseInt(ageStr); } catch (Exception ignore) {}
        if (age < 1 || age > 120) { etAge.setError("1~120 的數字"); sb.append("• 年齡需為 1~120 的數字\n"); ok = false; }

        if (birthplace.isEmpty()) { etBirthplace.setError("必填"); sb.append("• 出生地為必填\n"); ok = false; }

        if (!isValidDate(birthdate)) { etBirthdate.setError("格式 YYYY-MM-DD"); sb.append("• 出生日期格式為 YYYY-MM-DD\n"); ok = false; }
        if (!isValidTime(birthtime)) { etBirthtime.setError("格式 HH:mm"); sb.append("• 出生時間格式為 HH:mm\n"); ok = false; }

        if (input.getText().toString().trim().isEmpty()) { input.setError("請輸入問題"); sb.append("• 請輸入問題\n"); ok = false; }

        if (!ok) {
            new AlertDialog.Builder(this)
                .setTitle("輸入錯誤")
                .setMessage(sb.toString())
                .setPositiveButton("確定", null)
                .show();
        }
        return ok;
    }

    private boolean isValidDate(String s) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            f.setLenient(false);
            f.parse(s);
            return true;
        } catch (Exception e) { return false; }
    }

    private boolean isValidTime(String s) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("HH:mm", Locale.US);
            f.setLenient(false);
            f.parse(s);
            return true;
        } catch (Exception e) { return false; }
    }

    @Override public void onSuccess(List<ChatMessage> messages) {
        progress.setVisibility(View.GONE);
        adapter.setMessages(messages);
    }

    @Override public void onError(String message) {
        progress.setVisibility(View.GONE);
        // 簡單回顯錯誤
        input.setError(message);
    }
}



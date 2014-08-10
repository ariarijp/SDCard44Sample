package com.example.sdcard44sample;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MyActivity extends Activity {
    private static final String TAG = MyActivity.class.getSimpleName();

    /**
     * 外部ストレージパス。
     * Xperia Z1の場合のパスを指定。
     */
    private static final String EXTERNAL_STORAGE_PATH = "/storage/sdcard1";

    /**
     * パッケージ名。外部ストレージへの書き込み時に使用
     */
    private static final String PACKAGE_NAME = MyActivity.class.getPackage().getName();

    /**
     * ファイル名。外部ストレージへの書き込み時に使用
     */
    private static final String TEST_FILE_NAME = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        Log.i(TAG, "SD_CARD_PATH: " + EXTERNAL_STORAGE_PATH);
        Log.i(TAG, "PACKAGE_NAME: " + PACKAGE_NAME);
        Log.i(TAG, "TEST_FILE_NAME: " + TEST_FILE_NAME);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getExternalFilesDirを呼ぶと内部ストレージ、外部ストレージの双方にディレクトリが作成される。
                // 引数は省略できないため仮にDIRECTORY_DOWNLOADSを指定している。
                // getExternalFilesDirが返すFileのgetAbsolutePath()を呼び出しても、
                // 外部ストレージのパスでなく内部ストレージのパスが返されることが多い
                Log.i(TAG, "getExternalFilesDirを呼び出します");
                File extDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                Log.i(TAG, "getExternalFilesDirが返すパス: " + extDir.getAbsolutePath());

                // <SDカードのパス>/Android/data/<アプリのパッケージ名>以下には書き込みできる。
                doTest(EXTERNAL_STORAGE_PATH + "/Android/data/" + PACKAGE_NAME + "/files/" + TEST_FILE_NAME);
                doTest(EXTERNAL_STORAGE_PATH + "/Android/data/" + PACKAGE_NAME + "/" + TEST_FILE_NAME);

                // <SDカードのパス>/Android/data/<アプリのパッケージ名>以外には書き込みできない。
                doTest(EXTERNAL_STORAGE_PATH + "/Android/data/" + TEST_FILE_NAME);
                doTest(EXTERNAL_STORAGE_PATH + "/Android/" + TEST_FILE_NAME);
                doTest(EXTERNAL_STORAGE_PATH + "/" + TEST_FILE_NAME);
            }
        });

        findViewById(R.id.getExternalFilesDirs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File[] extDirs = getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS);

                TextView tv = (TextView)findViewById(R.id.log);
                StringBuilder sb = new StringBuilder();
                for (File extDir: extDirs) {
                    Log.d(TAG, extDir.getAbsolutePath());
                    sb.append(extDir.getAbsoluteFile()).append("\n");
                }

                tv.setText(sb.toString());
            }
        });
    }

    private void doTest(String testFilePath) {
        File testFile = new File(testFilePath);
        Log.i(TAG, testFilePath + "を作成します");
        createTestFile(testFile);

        if (testFile.exists()) {
            Log.i(TAG, testFilePath + "が作成されました");
        } else {
            Log.i(TAG, testFilePath + "の作成に失敗しました");
        }

        readTestFile(testFile);
    }

    private void createTestFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                if (file.canWrite()) {
                    FileWriter fw = new FileWriter(file);

                    // 実行時のタイムスタンプをファイルに書き込む
                    String timeStamp = String.valueOf(System.currentTimeMillis());
                    fw.append(timeStamp);
                    Log.i(TAG, "テキストファイルに書き込んだ文字列: " + timeStamp);

                    fw.close();
                }
            } catch (IOException e) {
                // e.printStackTrace();
            }
        }
    }

    private void readTestFile(File file) {
        if (file.exists() && file.canRead()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String body;
                if ((body = br.readLine()) != null) {
                    Log.i(TAG, "テキストファイルから読み込んだ文字列: " + body);
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

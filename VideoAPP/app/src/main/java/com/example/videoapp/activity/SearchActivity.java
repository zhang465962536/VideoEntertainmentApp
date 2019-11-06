package com.example.videoapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videoapp.R;
import com.example.videoapp.Utils.JsonParser;
import com.example.videoapp.Utils.LogUtil;
import com.example.videoapp.Utils.URL;
import com.example.videoapp.view.TitleBar;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
/*
* 搜索界面逻辑*/
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etSeache;
    private ImageView ivInput;
    private TextView tvSearch;
    private ListView lvResult;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViews();
    }

    private void findViews() {
        etSeache = (EditText)findViewById( R.id.et_seache );
        ivInput = (ImageView)findViewById( R.id.iv_input );
        tvSearch = (TextView)findViewById( R.id.tv_search );
        lvResult = (ListView)findViewById( R.id.lv_result );

        ivInput.setOnClickListener(this);
        tvSearch.setOnClickListener(this);

        tvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Toast.makeText(SearchActivity.this,s.toString(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search://搜索
                Toast.makeText(SearchActivity.this, "搜索", Toast.LENGTH_SHORT).show();
                startSearche();
                break;
            case R.id.iv_input://语音输入
                Toast.makeText(SearchActivity.this, "语音输入", Toast.LENGTH_SHORT).show();
                startVoice();
                break;
        }
    }


    /**
     * 开始搜索
     */
    private void startSearche() {
        String text = tvSearch.getText().toString().trim();//
        if (!TextUtils.isEmpty(text)) {
            try {
                text = URLEncoder.encode(text, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
           // String url = URL.SEARCH_URL + text;
           // getDataFromNet(url);


        } else {
            Toast.makeText(this, "你没有输入您要搜索的内容", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataFromNet(String url) {
        RequestParams params = new RequestParams(url);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("联网成功=="+result);
                //processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("联网失败=="+ex.getMessage());

            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
                LogUtil.e("onCancelled=="+cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });
    }

    /**
     * 解析和显示数据
     * @param json
     */
    /*private void processData(String json) {
        SearchBean searchBean = parseJson(json);
        //有数据了
        items   = searchBean.getItems();

        if(items != null && items.size()>0){

            lvResult.setAdapter(new SearchAdapter(this,items));

        }else{
            Toast.makeText(SearchActivity.this, "没有搜索到数据", Toast.LENGTH_SHORT).show();
        }

    }
    */

    /**
     * json解析
     * @return
     */
  /* private SearchBean parseJson(String json) {
        Gson gson = new Gson();
        SearchBean searchBean = gson.fromJson(json,SearchBean.class);
        return searchBean;
    }*/


    private void startVoice() {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener());
        //2.设置accent、 language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");//设置中文
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");//设置普通话
        mDialog.setParameter(SpeechConstant.DOMAIN, "iat");//设置日常用语
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener(new MyRecognizerDialogListener());
        //4.显示dialog，接收语音输入
        mDialog.show();
    }


    class MyInitListener implements InitListener {

        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                Toast.makeText(SearchActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();

            }
        }
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.e("MainActivity", recognizerResult.getResultString());
            printResult(recognizerResult);
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.e("MainActivity", "出错了");
        }
    }

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        Log.e("MainActivity", "解析好的内容==" + text);

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        Log.e("MainActivity", "最终结果==" + resultBuffer.toString());
        etSeache.setText(resultBuffer.toString());
        etSeache.setSelection(etSeache.length());
    }

}
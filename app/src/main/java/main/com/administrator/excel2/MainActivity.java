package main.com.administrator.excel2;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView txt = null;
    //private List<ExcelBean> excelBeans = new ArrayList<ExcelBean>();
    private List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
    private ListView list_excel;

    public static final String REGEX_GET_MOBILE=
            "(?is)(<tr[^>]+>[\\s]*<td[^>]+>[\\s]*卡号归属地[\\s]*</td>[\\s]*<td[^>]+>([^<]+)</td>[\\s]*</tr>)";

    public static final String REGEX_IS_MOBILE=
            "(?is)(^1[3|4|5|8][0-9]\\d{4,8}$)";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //txt = (TextView)findViewById(R.id.textView1);
        //txt.setMovementMethod(ScrollingMovementMethod.getInstance());
        list_excel = (ListView)findViewById(R.id.list_excel);
        readExcel();
        list_excel.setAdapter(new SimpleAdapter(this,
                data, R.layout.item,
                new String[]{"name","tel"},
                new int[]{R.id.tv_name,R.id.tv_tel}));
    }
    public void readExcel() {
        try {
            //获取文件流
            //InputStream is = new FileInputStream("mnt/sdcard/codedemo.xls");
            AssetManager asset = getAssets();
            InputStream is = asset.open("users.xls");
            Workbook book = Workbook.getWorkbook(is);
            int num = book.getNumberOfSheets();
            //txt.setText("联系人信息：\n");
            //txt.setText("the num of sheets is " + num+ "\n");
            Sheet sheet = book.getSheet(0);
            int Rows = sheet.getRows();
            int Cols = sheet.getColumns();
            //txt.append("the name of sheet is " + sheet.getName() + "\n");
           // txt.append("总行数：" + Rows + "     ");
            //txt.append("总列数：" + Cols + "\n");
            for (int i = 1; i < Rows; ++i) {
                    Map<String,Object> map=new HashMap<String, Object>();
                    map.put("name", sheet.getCell(1,i).getContents());
                    map.put("tel", sheet.getCell(2,i).getContents());
                    android.util.Log.d("HAHAHA",""+sheet.getCell(2,i).getContents());
                   // getMobileFrom(sheet.getCell(3,i).getContents());
                    data.add(map);
                    //map = null;
                   // txt.append(sheet.getCell(j, i).getContents() + "  ");
               // }
               // txt.append("\n\n");
            }
            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }



    /**
     * 验证手机号
     * @param mobileNumber
     * @return
     */
    public static boolean veriyMobile(String mobileNumber){
        Pattern p=null;
        Matcher m=null;

        p= Pattern.compile(REGEX_IS_MOBILE);
        m=p.matcher(mobileNumber);

        return m.matches();
    }

    public static String parseMobileFrom(String htmlSource){
        Pattern p=null;
        Matcher m=null;
        String result=null;

        p=Pattern.compile(REGEX_GET_MOBILE);
        m=p.matcher(htmlSource);

        while(m.find()){
            if(m.start(2)>0){
                result=m.group(2);
                result=result.replaceAll("&nbsp;", " ");
            }
        }
        return result;
    }

    public void  getMobileFrom(String mobileNumber) throws Exception {
        if (!veriyMobile(mobileNumber)) {
            throw new Exception("不是完整的11位手机号或者正确的手机号前七位");
        }
        String result=null;
        OkHttpClient client = null;
        client = new OkHttpClient();
        FormBody formBody = new FormBody
                .Builder()
                .add("mobile", "13382135545")
                .add("action", "mobile")
                .build();
        final Request request = new Request.Builder()
                .url("http://www.ip138.com/")
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               Toast.makeText(MainActivity.this,"请求出错",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                android.util.Log.d("HAHAHA",""+responseStr);
               // ToastUtil.showToast(PostStringActivity.this, "Code：" + String.valueOf(response.code()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // tv_result.setText(responseStr);
                    }
                });
            }
        });
    }

}


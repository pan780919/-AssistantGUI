package com.jhengweipan.tattoofans;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jhengweipan.Guandisignonehundred.R;

import java.util.ArrayList;
import java.util.Calendar;

public class MenuListActivity extends Activity {
    private ListView mListview;
    private static final String TAG = "MenuListActivity";
    private  int year,month,day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        Calendar calendar = Calendar.getInstance();
         year = calendar.get(Calendar.YEAR)-1911;
        month = calendar.get(Calendar.MONTH)+1;
         day =calendar.get(Calendar.DATE);
        initLayout();

    }

    private void initLayout() {
        mListview = (ListView) findViewById(R.id.menulist_Listview);
        ArrayList<String> marraylist = new ArrayList<>();
        marraylist.add("01月、02月統一發票中獎號碼");
        marraylist.add("03月、04月統一發票中獎號碼");
        marraylist.add("05月、06月統一發票中獎號碼");
        marraylist.add("07月、08月統一發票中獎號碼");
        marraylist.add("09月、10月統一發票中獎號碼");
        marraylist.add("11月、12月統一發票中獎號碼");
        ArrayAdapter<String> pageName = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, marraylist);
        mListview.setAdapter(pageName);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        Log.d(TAG, "onItemClick: "+year);
                        Log.d(TAG, "onItemClick: "+month);
                        Log.d(TAG, "onItemClick: "+day);
                        if(year>=105){
                            if (month>3){

                                String uri = " https://www.etax.nat.gov.tw/etw-main/front/ETW183W2_"+year+"01"+"/";
                                Log.d(TAG, "goTo: "+uri);
                                //new一個intent物件，並指定Activity切換的class
                                Intent intent = new Intent();
                                intent.setClass(MenuListActivity.this, MainActivity.class);
                                //new一個Bundle物件，並將要傳遞的資料傳入
                                Bundle bundle = new Bundle();
                                bundle.putString("url",uri);
                                //將Bundle物件assign給intent
                                intent.putExtras(bundle);
                                //切換Activity
                                startActivity(intent);
                            }else if(month==3){
                                if(day>=28){

                                    String uri = " https://www.etax.nat.gov.tw/etw-main/front/ETW183W2_"+year+"01"+"/";

                                    //new一個intent物件，並指定Activity切換的class
                                    Intent intent = new Intent();
                                    intent.setClass(MenuListActivity.this, MainActivity.class);
                                    //new一個Bundle物件，並將要傳遞的資料傳入
                                    Bundle bundle = new Bundle();
                                    bundle.putString("url",uri);
                                    //將Bundle物件assign給intent
                                    intent.putExtras(bundle);
                                    //切換Activity
                                    startActivity(intent);
                                }else {
                                    Toast.makeText(getApplication(),"尚未到開獎日期喔",Toast.LENGTH_SHORT).show();
                                }

                            }

                            else {
                                Toast.makeText(getApplication(),"尚未到開獎日期喔",Toast.LENGTH_SHORT).show();
                            }
                        }


                        break;
                    case 1:
                        goTo(year,5,28,03);
                        break;
                    case 2:
                        goTo(year,7,28,05);
                        break;
                    case 3:
                        goTo(year,9,28,07);
                        break;
                    case 4:
                        goTo(year,11,28,9);
                        break;
                    case 5:
                        goTo(year+1,1,28,11);
                        break;

                }

            }
        });
    }
    private void showDilog(){
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.poke)
                .setTitle("關於")
                .setMessage("這個是類似共享的平台,玩家可以分享文章至blog,也可以用推播訊息 告知附近有什麼特別的pokemon\n\n" +
                        "如有意願或興趣 可以加入我們\n\n" +
                        "blog 開放申請 可以寫信告知會第一時間把您加入作者行列\n\n" +
                        "推播訊息平台每個人都可以使用\n" +
                        "帳號:pokemontw\n" +
                        "密碼:pokemon123\n" +
                        "請勿亂改密碼 以便大家使用 感謝!!"

                )
                .setNegativeButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }
    private void goTo(int gotoyear,int gotomonth,int gotoday,int openmonth){
        if(year>=gotoyear){
            if (month>gotomonth){
                String uri = " https://www.etax.nat.gov.tw/etw-main/front/ETW183W2_"+year+openmonth+"/";

                //new一個intent物件，並指定Activity切換的class
                Intent intent = new Intent();
                intent.setClass(MenuListActivity.this, MainActivity.class);
                //new一個Bundle物件，並將要傳遞的資料傳入
                Bundle bundle = new Bundle();
                bundle.putString("url",uri);
                //將Bundle物件assign給intent
                intent.putExtras(bundle);
                //切換Activity
                startActivity(intent);
            }else if(month==gotomonth){
                if (day>=gotomonth){
                    String uri = " https://www.etax.nat.gov.tw/etw-main/front/ETW183W2_"+year+openmonth+"/";
                    Log.d(TAG, "goTo: "+uri);
                    //new一個intent物件，並指定Activity切換的class
                    Intent intent = new Intent();
                    intent.setClass(MenuListActivity.this, MainActivity.class);
                    //new一個Bundle物件，並將要傳遞的資料傳入
                    Bundle bundle = new Bundle();
                    bundle.putString("url",uri);
                    //將Bundle物件assign給intent
                    intent.putExtras(bundle);
                    //切換Activity
                    startActivity(intent);

                }else {
                    Toast.makeText(getApplication(),"尚未到開獎日期喔",Toast.LENGTH_SHORT).show();
                }


            }else {
                Toast.makeText(getApplication(),"尚未到開獎日期喔",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getApplication(),"尚未到開獎日期喔",Toast.LENGTH_SHORT).show();

        }

    }
}

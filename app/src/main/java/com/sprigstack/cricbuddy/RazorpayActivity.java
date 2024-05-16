package com.sprigstack.cricbuddy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.razorpay.Checkout;
import com.razorpay.ExternalWalletListener;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class RazorpayActivity extends AppCompatActivity implements PaymentResultWithDataListener, ExternalWalletListener {

    private static final String TAG = RazorpayActivity.class.getSimpleName();
    private AlertDialog.Builder alertDialogBuilder;
    private String orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_razorpay);
        Checkout.preload(getApplicationContext());
        String paymentUrl = getIntent().getStringExtra("paymentUrl");
        Log.d("parsedUrl=====", paymentUrl);
        Uri uri = Uri.parse(paymentUrl);
        String paymentDataStr = uri.getQueryParameter("paymentData");
        Log.d("paymentDataStr=====", paymentDataStr);
        Map<String, String> queryParams = getQueryParams(paymentUrl);
        String paymentData = queryParams.get("paymentData");

        if (paymentData != null) {
            try {
                String decodedJson = URLDecoder.decode(paymentData, "UTF-8");
                JSONObject jsonObject = new JSONObject(decodedJson);
                Button payBtn = (Button) findViewById(R.id.payButton);
                payBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderId = jsonObject.optString("order_id");
                        startPayment(jsonObject);
                    }
                });
                payBtn.performClick();

            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }
        }

        alertDialogBuilder = new AlertDialog.Builder(RazorpayActivity.this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Payment Result");
        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
            //do nothing
        });


    }
    public static Map<String, String> getQueryParams(String url) {
        Map<String, String> queryParams = new HashMap<>();
        int questionMarkIndex = url.indexOf('?');
        if (questionMarkIndex != -1) {
            String queryString = url.substring(questionMarkIndex + 1);
            String[] params = queryString.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    queryParams.put(key, value);
                }
            }
        }
        return queryParams;
    }
    public void startPayment(JSONObject options) {
        final Activity activity = this;
        final Checkout co = new Checkout();
        co.setKeyID("rzp_live_oQjEDMyUkDJRzj");
            try {
                Log.d("jsonObject=====", options.toString(4));
                co.open(activity, options);
            } catch (Exception e) {
                Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
            }

    }

    @Override
    public void onExternalWalletSelected(String s, PaymentData paymentData) {
        try{
            alertDialogBuilder.setMessage("External Wallet Selected:\nPayment Data: "+paymentData.getData());
            alertDialogBuilder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        try{
//            alertDialogBuilder.setMessage("Payment Successful :\nPayment ID: "+s+"\nPayment Data: "+paymentData.getData());
//            alertDialogBuilder.show();
            String successUrl = "https://dev.cricbuddy.in/payment/" + orderId;
            Log.d("successUrl=====", successUrl);
            Intent intent = new Intent();
            intent.putExtra("url", successUrl);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        try{
//            alertDialogBuilder.setMessage("Payment Failed:\nPayment Data: "+paymentData.getData());
//            alertDialogBuilder.show();
            String successUrl = "https://dev.cricbuddy.in/payment/" + orderId;
            Intent intent = new Intent();
            intent.putExtra("url", successUrl);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

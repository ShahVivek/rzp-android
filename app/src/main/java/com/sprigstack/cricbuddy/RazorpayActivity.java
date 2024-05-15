package com.sprigstack.cricbuddy;

import static android.content.Intent.getIntent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();
        co.setKeyID("rzp_live_oQjEDMyUkDJRzj");

            try {
                options.remove("key");
                options.put("amount", "100");
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                options.remove("order_id");
                Log.d("jsonObject=====", options.toString(4));

//                JSONObject options = new JSONObject();
//                options.put("name", "Razorpay Corp");
//                options.put("description", "Demoing Charges");
//                options.put("send_sms_hash",true);
//                options.put("allow_rotation", true);
//                //You can omit the image option to fetch the image from dashboard
//                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
//                options.put("currency", "INR");
//                options.put("amount", "100");
//
//                JSONObject preFill = new JSONObject();
//                preFill.put("email", "v7shah@gmail.com");
//                preFill.put("contact", "9687029992");
//
//                options.put("prefill", preFill);

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

            String successUrl = "https://dev.cricbuddy.in/payment/" + s; // Change this to your success URL

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
            alertDialogBuilder.setMessage("Payment Failed:\nPayment Data: "+paymentData.getData());
            alertDialogBuilder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

package payal.cluebix.www.ecommerce;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import payal.cluebix.www.ecommerce.Adapter.CartAdapter;
import payal.cluebix.www.ecommerce.Datas.Base_url;
import payal.cluebix.www.ecommerce.Datas.sample_Cart;
import payal.cluebix.www.ecommerce.Handlers.RquestHandler;
import payal.cluebix.www.ecommerce.Handlers.SessionManager;
import payal.cluebix.www.ecommerce.R;

/**
 * Created by speed on 11-Jun-18.
 */

public class CartFragment extends Fragment implements CartAdapter.ClickListener {

    CartAdapter adapter;
    RecyclerView recyclerView;
    public static ArrayList<sample_Cart> array_cart_items = new ArrayList<>();
    public static ArrayList<sample_Cart> array_temp = new ArrayList<>();
    public static ArrayList<sample_Cart> product_item;
    ArrayList<String> element_quantity_array=new ArrayList<>();
    ArrayList<String> Cart_id_array=new ArrayList<>();
    ArrayList<String> Product_id_array=new ArrayList<>();
    ArrayList<Integer> availables=new ArrayList<>();

    String url1= Base_url.Update_Cart_quantity;//cart_id/user_id/quantity
    String url2= Base_url.Remove_cart_product;/*cart id / current user id*/
    String url3=Base_url.All_Cart_element;/*/user_id*/
    String url4=Base_url.Send_quotation_to_superVendor;/*user_id*/

    SessionManager session;
    String Uname,Uid;
    TextView cartsub_total,cartsub_total2,t_checkout;
    int total=0; float GrandTotal=0;
    private static int itemCount=0;


    public CartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.activity_cart, container, false);
        product_item = new ArrayList<>();
        itemCount=0;

        cartsub_total=(TextView)v.findViewById(R.id.cart_subtotal1);
        cartsub_total2=(TextView)v.findViewById(R.id.cart_subtotal2);
        t_checkout=(TextView)v.findViewById(R.id.t_checkout);
        recyclerView=(RecyclerView)v.findViewById(R.id.recycler_cart);

        session=new SessionManager(getContext());
        HashMap<String, String> user = session.getUserDetails();
        Uname = user.get(SessionManager.KEY_NAME);
        Uid = user.get(SessionManager.KEY_ID);
        Log.d("session","name_userId="+Uid+"\nemail_user_name="+Uname);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        array_cart_items=get_old_Element();

        array_temp.addAll(array_cart_items);

   //     cartsub_total.setText("Cart Subtotal ("+total+" items): ");
        cartsub_total.setText("Cart Subtotal ("+itemCount+" items): ");
        cartsub_total2.setText("Rs."+GrandTotal);
        adapter=new CartAdapter(getContext(),product_item);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
 //       if (pro)


        t_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!product_item.isEmpty()) {

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url4 + Uid, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("cartscreen", "checkout res= " + response);
                            String quote_id = null;
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                String success=jsonObject.getString("success");
                                quote_id=jsonObject.getString("quote_id");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent i = new Intent(getActivity(), Quotation_items_list.class);
                            i.putExtra("screen","1");
                            i.putExtra("quote_id",quote_id);
                            i.putExtra("userid",Uid);
                            startActivity(i);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Cartscreen", error + "");
                            Toast.makeText(getActivity(), "Server Connection Failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    RquestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);

                }else
                    Toast.makeText(getActivity(), "No data in Cart!", Toast.LENGTH_SHORT).show();

            }
        });

        return v;
    }

    @Override
    public void plusClicked(final ArrayList<sample_Cart> array, View view, final int position) {

        final int quantity = Integer.parseInt(element_quantity_array.get(position)) + 1;

        if ((quantity > 0 )&& (quantity<=availables.get(position))){
            element_quantity_array.set(position, "" + quantity);

            StringRequest stringRequest = new StringRequest(Request.Method.POST
                    , url1 + Cart_id_array.get(position) + "/" + Uid + "/" + quantity, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {
                    Log.e("cartscreenurl", "plus response=" + response);
                    if (response.equalsIgnoreCase("success")) {
                        // Toast.makeText(Cart.this, "Quantity Increased", Toast.LENGTH_SHORT).show();
                        product_item.set(position, new sample_Cart(array_cart_items.get(position).getCart_id(), array_cart_items.get(position).getProduct_id()
                                , array_cart_items.get(position).getProduct_nam(), array_cart_items.get(position).getPrice()
                                , array_cart_items.get(position).getSamplePrice()
                                , "" + quantity, array_cart_items.get(position).getUser_id()
                                , array_cart_items.get(position).getManufacturing(), array_cart_items.get(position).getDescription()
                                , array_cart_items.get(position).getBrand(), array_cart_items.get(position).getImages_string()
                                , array_cart_items.get(position).getQuantity()));

        //                Toast.makeText(getActivity(), "" + product_item.size(), Toast.LENGTH_SHORT).show();

                        total = total + 1;
                        GrandTotal = GrandTotal + Float.parseFloat(product_item.get(position).getPrice());
                        //     cartsub_total.setText("Cart Subtotal ("+total+" items): ");
                        cartsub_total.setText("Cart Subtotal ("+itemCount+" items): ");
                        cartsub_total2.setText("Rs." + GrandTotal);


                    } else {
                        Toast.makeText(getActivity(), "Error:" + response, Toast.LENGTH_SHORT).show();
                    }

                    adapter.notifyData(product_item);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("cartscreen_error", error + "");
                    Toast.makeText(getActivity(), "Server Connection Failed!", Toast.LENGTH_SHORT).show();
                }
            });

            RquestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);

        }else{
            //less then zero not allowed
        }

    }


    @Override
    public void minusClicked(ArrayList<sample_Cart> array,View view, final int position) {

        final int quantity=Integer.parseInt(element_quantity_array.get(position))-1;

        if(quantity>=0) {
            element_quantity_array.set(position, "" + quantity);
           StringRequest stringRequest = new StringRequest(Request.Method.POST
                    , url1 + Cart_id_array.get(position) + "/" + Uid + "/" + quantity, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {
                    if (response.equalsIgnoreCase("success")) {
                        product_item.set(position, new sample_Cart(array_cart_items.get(position).getCart_id(), array_cart_items.get(position).getProduct_id()
                                , array_cart_items.get(position).getProduct_nam(), array_cart_items.get(position).getPrice()
                                ,array_cart_items.get(position).getSamplePrice()
                                , "" + quantity, array_cart_items.get(position).getUser_id(),
                                array_cart_items.get(position).getManufacturing(),
                                array_cart_items.get(position).getDescription()
                                , array_cart_items.get(position).getBrand(), array_cart_items.get(position).getImages_string()
                            ,array_cart_items.get(position).getQuantity()));
       //                 Toast.makeText(getActivity(), "" + product_item.size(), Toast.LENGTH_SHORT).show();

                        total = total - 1;
                        GrandTotal = GrandTotal - Float.parseFloat(product_item.get(position).getPrice());
                        //     cartsub_total.setText("Cart Subtotal ("+total+" items): ");
                        cartsub_total.setText("Cart Subtotal ("+itemCount+" items): ");
                        cartsub_total2.setText("Rs." + GrandTotal);

                    } else {
                        Toast.makeText(getActivity(), "Error:" + response, Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyData(product_item);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Server Connection Failed!", Toast.LENGTH_SHORT).show();
                }
            });

            RquestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);

        }
        else{
            //less then zero not allowed
        }
    }


    @Override
    public void imageClicked(View view, int position) {
        Intent i=new Intent(getActivity(),ProductDetail.class);
        i.putExtra("selected_prod_id",product_item.get(position).getProduct_id());
        i.putExtra("ParentScreen","1");
        startActivity(i);
    }

    @Override
    public void onLongClick(View view, int position) {

    }


    @Override
    public void itemClicked(View view,final int position) {
      /*  int qt=Integer.parseInt(product_item.get(position).getQty());
        total=total-qt;
        itemCount--;
        GrandTotal=GrandTotal-(Float.parseFloat(product_item.get(position).getPrice())*qt)
                        -Float.parseFloat(product_item.get(position).getSamplePrice());
        //     cartsub_total.setText("Cart Subtotal ("+total+" items): ");
        cartsub_total.setText("Cart Subtotal ("+itemCount+" items): ");
        cartsub_total2.setText("Rs."+GrandTotal);*/

        StringRequest stringRequest=new StringRequest(Request.Method.POST
                , url2+Cart_id_array.get(position)+"/"+Uid, new Response.Listener<String>(){
            @Override
            public void onResponse(final String response) {
                Log.d("cartscreen","remove res="+response);
                if(response.equalsIgnoreCase("success")){
                    Toast.makeText(getActivity(), "Removed", Toast.LENGTH_SHORT).show();

                    int qt=Integer.parseInt(product_item.get(position).getQty());
                    total=total-qt;
                    itemCount--;
                    GrandTotal=GrandTotal-(Float.parseFloat(product_item.get(position).getPrice())*qt)
                            -Float.parseFloat(product_item.get(position).getSamplePrice());
                    //     cartsub_total.setText("Cart Subtotal ("+total+" items): ");
                    cartsub_total.setText("Cart Subtotal ("+itemCount+" items): ");
                    cartsub_total2.setText("Rs."+GrandTotal);
                    product_item.remove(position);
                }
                else {
                    Toast.makeText(getActivity(), "Error! Retry after some time", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyData(product_item);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Login_error_response",error.getMessage());
                Toast.makeText(getActivity(), "Server Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });

        RquestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private ArrayList<sample_Cart> get_old_Element() {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, url3+Uid, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {

                JSONObject post_data;
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++) {
                        post_data = jsonArray.getJSONObject(i);

                        String cart_id = post_data.getString("id");
                        String product_id = post_data.getString("product_id");
                        String product_name = post_data.getString("product_name");
                        String price = post_data.getString("price");
                        String sample_price = post_data.getString("sample_price");
                        String qty = post_data.getString("qty");
                        String user_id = post_data.getString("user_id");
                        String manufacturing = post_data.getString("manufacturing");
                        String description = post_data.getString("description");
                        String brand = post_data.getString("brand");
                        String product_images = post_data.getString("product_images");
                        String quantity = post_data.getString("quantity");
                        String amount = post_data.getString("amount");
                        String percent = post_data.getString("percent");

                        /*
                        *
        "id": "276",
        "product_id": "24",
        "product_name": "this is test product",
        "price": "345.00",//product real price
        "sample_price": "0.00",//sample price zero if sample not ordered else it will show price of sample
        "qty": "5",//qty is no of this item ordered
        "user_id": "39",
        "manufacturing": "0",
        "description": "this is dummy description",
        "brand": "my comp",
        "product_images": "79e,79f",
        "quantity": "4",//it shows maximum available items
        "amount": "5.00",//commision amount
        "percent": "%"
    }*/
                        availables.add(Integer.parseInt(quantity));

                        Log.e("cartscreen",cart_id);
                        total=total+Integer.parseInt(qty);
                        itemCount++;
                        GrandTotal=GrandTotal+(Float.parseFloat(price)*Integer.parseInt(qty))
                                +Float.parseFloat(sample_price);

                        Cart_id_array.add(cart_id);
                        element_quantity_array.add(qty);
                        Product_id_array.add(product_id);
                        //     cartsub_total.setText("Cart Subtotal ("+total+" items): ");
                        cartsub_total.setText("Cart Subtotal ("+itemCount+" items): ");
                        cartsub_total2.setText("Rs."+GrandTotal);

/* sample_Cart(String cart_id,String product_id,String product_name,String price,String qty
            ,String user_id,String description,String brand, String product_images){
*/
                        product_item.add(new sample_Cart(cart_id, product_id, product_name, price, sample_price
                                , qty, user_id,manufacturing, description, brand, product_images, quantity));

                        Log.d("cartscreen",product_item+"=inside");
                  /*      array_temp.add(new sample_Cart(cart_id, product_id, product_name, price
                                , qty, user_id, description, brand, product_images));
              */      }

                    adapter.notifyData(product_item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("dashboard_error_res",error+"");
                Toast.makeText(getActivity(), "Server Connection Failed!", Toast.LENGTH_SHORT).show();
            }
        });
        RquestHandler.getInstance(getActivity()).addToRequestQueue(stringRequest);

        Log.d("cartscreen","before return="+product_item);
        return product_item;
    }


}

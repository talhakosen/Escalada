/*
 * Copyright (C) ${YEAR} Antonio Leiva
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package escalada.talhakosen.com.escaladaassignment.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import escalada.talhakosen.com.escaladaassignment.R;
import escalada.talhakosen.com.escaladaassignment.adapters.RecyclerViewAdapter;
import escalada.talhakosen.com.escaladaassignment.core.Constants;
import escalada.talhakosen.com.escaladaassignment.core.MyVolley;
import escalada.talhakosen.com.escaladaassignment.exception.ProductImageNotFoundException;
import escalada.talhakosen.com.escaladaassignment.models.Base;
import escalada.talhakosen.com.escaladaassignment.models.Product;
import escalada.talhakosen.com.escaladaassignment.models.ProductDetail;
import escalada.talhakosen.com.escaladaassignment.utils.ScrollManager;


public class MainActivity extends ActionBarActivity implements RecyclerViewAdapter.OnItemClickListener {
    RecyclerView recyclerView;
    private CircleProgressBar circleProgressBar;
    private TextView txt_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txt_error = (TextView) findViewById(R.id.txt_error);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        circleProgressBar = (CircleProgressBar) findViewById(R.id.progressBar);
        circleProgressBar.setColorSchemeResources(android.R.color.holo_green_dark);

        final Button fab = (Button) findViewById(R.id.fab);
        fab.setText(getResources().getText(R.string.fab));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        // Toolbar height needs to be known before establishing the initial offset
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                ScrollManager manager = new ScrollManager();
                manager.attach(recyclerView);
                manager.addView(toolbar, ScrollManager.Direction.UP);
                manager.addView(fab, ScrollManager.Direction.DOWN);
                manager.setInitialOffset(toolbar.getHeight());
            }
        });

        getProductList();
    }

    @Override
    public void onItemClick(View view, Product product) {
        try {
            getProductDetail(view, product.getProduct_id());
        } catch (ProductImageNotFoundException e) {
            e.printStackTrace();
        }
    }

    List<Product> productList;
    public void getProductList() {
        productList = new ArrayList<>();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Constants.PRODUCT_LIST_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray productsArray = new JSONArray(response.getString("products"));

                    for (int i = 0; i < productsArray.length(); i++) {
                        Product product = new Product(productsArray.getJSONObject(i));
                        productList.add(product);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                loadDataToRecyclerView(productList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                circleProgressBar.setVisibility(View.GONE);
                txt_error.setVisibility(View.VISIBLE);
                circleProgressBar.setVisibility(View.GONE);
            }
        });

        MyVolley.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }


    public void getProductDetail(final View view, int productId) throws ProductImageNotFoundException {
        circleProgressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(String.format(Constants.PRODUCT_DETAIL_URL, productId), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    gotoDetailActivity(view,response);
                } catch (JSONException e) {

                }
                circleProgressBar.setVisibility(View.GONE);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                circleProgressBar.setVisibility(View.GONE);
                txt_error.setVisibility(View.VISIBLE);
                circleProgressBar.setVisibility(View.GONE);
            }
        });


        MyVolley.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest);
    }

    private void gotoDetailActivity(View view,JSONObject response) throws JSONException {
        ProductDetail productDetail = new ProductDetail(response);
        DetailActivity.navigate(MainActivity.this, view.findViewById(R.id.image), productDetail);
    }


    private void loadDataToRecyclerView(final List<? extends Base> countryList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerViewAdapter adapter = new RecyclerViewAdapter(MainActivity.this, countryList);
                adapter.setOnItemClickListener(MainActivity.this);
                recyclerView.setAdapter(adapter);
                circleProgressBar.setVisibility(View.GONE);
            }
        });
    }

}

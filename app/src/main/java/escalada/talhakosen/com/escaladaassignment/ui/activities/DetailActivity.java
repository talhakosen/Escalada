package escalada.talhakosen.com.escaladaassignment.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import escalada.talhakosen.com.escaladaassignment.R;
import escalada.talhakosen.com.escaladaassignment.adapters.TransitionAdapter;
import escalada.talhakosen.com.escaladaassignment.exception.ProductImageNotFoundException;
import escalada.talhakosen.com.escaladaassignment.models.ProductDetail;
import escalada.talhakosen.com.escaladaassignment.utils.WindowCompatUtils;

public class DetailActivity extends ActionBarActivity {
    private static final String EXTRA_PARAMS = "escalada.talhakosen.com.escaladaassignment.models.ProductDetail";
    private Toolbar toolbar;
    private ProductDetail productDetail;
    private ImageView image,image2;
    Button fab;
    private TextView title, desc;


    public static void navigate(ActionBarActivity activity, View transitionImage, ProductDetail productDetail) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(EXTRA_PARAMS, productDetail);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, productDetail.getImage());
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityTransitions();
        setContentView(R.layout.activity_detail);
        ActivityCompat.postponeEnterTransition(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (Button) findViewById(R.id.fab);
        title = (TextView) findViewById(R.id.title);
        desc = (TextView) findViewById(R.id.desc);
        productDetail = getIntent().getParcelableExtra(EXTRA_PARAMS);

        setTitle(productDetail.getName());

        //just for demonstrating custom error
        try {
            loadCountryFlagToImageView();
        } catch (ProductImageNotFoundException e) {
            Toast.makeText(DetailActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        title.setText(productDetail.getName());
        desc.setText(productDetail.getDescription());
        fab.setText(productDetail.getFormattedPrice() + getString(R.string.currency));

    }

    private void loadCountryFlagToImageView() throws ProductImageNotFoundException {
        if(productDetail.getImage().equals(""))
            throw  new ProductImageNotFoundException("There no image for this item");

        image = (ImageView) findViewById(R.id.image);
        image2 = (ImageView) findViewById(R.id.image2);

        ViewCompat.setTransitionName(image, productDetail.getName());

        Picasso.with(this).load(productDetail.getImage()).into(image, new Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        applyPalette(palette, image);
                    }
                });
            }

            @Override
            public void onError() {

            }
        });

        // To make scroll enable i added an image
        Picasso.with(this).load(productDetail.getImage()).into(image2);
    }

    // methods for material desing, can be found on any blogs
    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }

    private void applyPalette(Palette palette, ImageView image) {
        int primaryDark = getResources().getColor(R.color.primary_dark);
        int primary = getResources().getColor(R.color.primary);
        toolbar.setBackgroundColor(palette.getMutedColor(primary));
        WindowCompatUtils.setStatusBarcolor(getWindow(), palette.getDarkMutedColor(primaryDark));
        initScrollFade(image);
        ActivityCompat.startPostponedEnterTransition(this);
    }

    private void initScrollFade(final ImageView image) {
        final View scrollView = findViewById(R.id.scroll);

        setComponentsStatus(scrollView, image);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                setComponentsStatus(scrollView, image);
            }
        });
    }

    private void setComponentsStatus(View scrollView, ImageView image) {
        int scrollY = scrollView.getScrollY();
        if (Build.VERSION.SDK_INT > 10)
            image.setTranslationY(-scrollY / 2);

        ColorDrawable background = (ColorDrawable) toolbar.getBackground();
        int padding = scrollView.getPaddingTop();
        double alpha = (1 - (((double) padding - (double) scrollY) / (double) padding)) * 255.0;
        alpha = alpha < 0 ? 0 : alpha;
        alpha = alpha > 255 ? 255 : alpha;

        background.setAlpha((int) alpha);

        float scrollRatio = (float) (alpha / 255f);
        int titleColor = getAlphaColor(Color.WHITE, scrollRatio);
        toolbar.setTitleTextColor(titleColor);
    }

    private int getAlphaColor(int color, float scrollRatio) {
        return Color.argb((int) (scrollRatio * 255f), Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * It seems that the ActionBar view is reused between activities. Changes need to be reverted,
     * or the ActionBar will be transparent when we go back to Main Activity
     */
    private void restablishActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getReturnTransition().addListener(new TransitionAdapter() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    toolbar.setTitleTextColor(Color.WHITE);
                    toolbar.getBackground().setAlpha(255);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        restablishActionBar();
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        NavUtils.navigateUpTo(this, upIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            restablishActionBar();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

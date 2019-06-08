package designs.attitude.forextradingstrategy;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.shockwave.pdfium.PdfDocument;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements OnPageChangeListener,OnLoadCompleteListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String SAMPLE_FILE = "FOREXSTRATEGYFINALPDF.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    private InterstitialAd mInterstitialAd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // initialize the AdMob app
        MobileAds.initialize(this, getString(R.string.admob_app_id));



        //display pdf
        pdfView= (PDFView)findViewById(R.id.pdfView);
        displayFromAsset(SAMPLE_FILE);

        // Find Banner ad
       AdView mAdView = findViewById(R.id.adView);
     //  mAdView.setAdSize(AdSize.BANNER);
      // mAdView.setAdUnitId(String.valueOf(R.string.banner_home_footer));
        AdRequest adRequest = new AdRequest.Builder()
               // .addTestDevice("935CBC95E3262DD940F0A12C86546B2F")
                .build();
        // Display Banner ad
        mAdView.loadAd(adRequest);





        prepareAd();

        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            public void run() {
                Log.i("hello", "world");
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Log.d("TAG"," Interstitial not loaded");
                        }

                        prepareAd();


                    }
                });

            }
        }, 45, 45, TimeUnit.SECONDS);






    }



    public void  prepareAd(){


        mInterstitialAd=new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_full_screen));
        AdRequest adRequest1=new AdRequest.Builder()//.addTestDevice("935CBC95E3262DD940F0A12C86546B2F")
                .build();
        mInterstitialAd.loadAd(adRequest1);
    }


    private void displayFromAsset(String assetFileName) {
        pdfFileName = assetFileName;

        pdfView.fromAsset(SAMPLE_FILE)

                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .enableAntialiasing(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }



    @Override
    public void loadComplete(int nbPages) {

        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");



    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
}
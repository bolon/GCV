package com.rere.fish.gcv.result;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.rere.fish.gcv.App;
import com.rere.fish.gcv.BuildConfig;
import com.rere.fish.gcv.R;
import com.rere.fish.gcv.modules.GCVInterface;
import com.rere.fish.gcv.modules.SelfServiceInterface;
import com.rere.fish.gcv.utils.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ResultActivity extends AppCompatActivity implements OnFinishVisionProcess {
    private static final String KEY_PATH = "FILEPATH";

    @BindView(R.id.rootView)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.textAck)
    TextView textView;
    @BindView(R.id.animation_view)
    LottieAnimationView animationView;
    @Inject
    SelfServiceInterface engineServices;
    @Inject
    GCVInterface gcvServices;
    private String tempFilePath;

    public static Intent createIntent(Context context, String filePath) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.putExtra(KEY_PATH, filePath);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ButterKnife.bind(this);
        App.get(getApplicationContext()).getInjector().inject(this);

        tempFilePath = getIntent().getStringExtra(KEY_PATH);

        //callEngineService();
        try {
            callCloudVision(FileUtil.getBitmapFromStorage(tempFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void callEngineService() {
        RequestBody requestRaw = RequestBody.create(MediaType.parse("application/json"), loadJSONFromAsset());

        engineServices.getLabel(requestRaw).enqueue(new Callback<ResponseEngineModel>() {
            @Override
            public void onResponse(Call<ResponseEngineModel> call, Response<ResponseEngineModel> response) {
                Timber.i("result_size : " + response.body().getListResponsePair().size());
            }

            @Override
            public void onFailure(Call<ResponseEngineModel> call, Throwable t) {

            }
        });

        //TODO : GET RESULT FROM BL
    }

    private void callCloudVision(Bitmap bitmap) throws IOException {
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                try {

                    VisionRequestInitializer visionRequestInitializer = new VisionRequestInitializer(BuildConfig.API_KEY_GCV);
                    Vision.Builder visionBuilder = new Vision.Builder(httpTransport, jsonFactory, null);
                    visionBuilder.setVisionRequestInitializer(visionRequestInitializer);

                    Vision vision = visionBuilder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = generateGCVRequest(bitmap);
                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();

                    return convertResponseToString(response);
                } catch (IOException e) {
                    Timber.e(Arrays.toString(e.getStackTrace()));
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                onReceivedResult(s);
                Timber.i(s);
                return;
            }
        }.execute();
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getResources().getAssets().open("request_body_engine.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Timber.e("Error reading file", ex.getMessage());
            ex.printStackTrace();
            return null;
        }

        return json;
    }

    public BatchAnnotateImagesRequest generateGCVRequest(Bitmap bmp) {
        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("WEB_DETECTION");
                labelDetection.setMaxResults(10);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);

        }});

        return batchAnnotateImagesRequest;
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) throws IOException {
        String message = "I found these things:<br/><br/>";

        Timber.i(response.toPrettyString());
        message += "done";

        List<AnnotateImageResponse> responses = response.getResponses();

        for (AnnotateImageResponse a : responses) {
            /*WebDetection annotation = a.getWebDetection();
            message += ("Entity:Id:Score");
            message += ("===============");
            for (WebEntity entity : annotation.getWebEntities()) {
                message += (entity.getDescription() + " : " + entity.getEntityId() + " : "
                        + entity.getScore());
            }*/
/*            message += ("\nPages with matching images: Score\n==");
            for (WebPage page : annotation.getPagesWithMatchingImages()) {
                message += (page.getUrl() + " : " + page.getScore());
            }
            message += ("\nPages with partially matching images: Score\n==");
            for (WebImage image : annotation.getPartialMatchingImages()) {
                message += (image.getUrl() + " : " + image.getScore());
            }
            message += ("\nPages with fully matching images: Score\n==");
            for (WebImage image : annotation.getFullMatchingImages()) {
                message += (image.getUrl() + " : " + image.getScore());
            }*/
        }

        return message;
    }

    @Override
    public void onReceivedResult(String result) {
        textView.setText(Html.fromHtml(result));
        animationView.setVisibility(View.GONE);
        coordinatorLayout.setBackgroundColor(getColor(R.color.colorAccent));
    }
}

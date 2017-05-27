package com.rere.fish.gcv.result.preexec;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.google.api.services.vision.v1.model.WebDetection;
import com.google.api.services.vision.v1.model.WebEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rere.fish.gcv.App;
import com.rere.fish.gcv.BuildConfig;
import com.rere.fish.gcv.R;
import com.rere.fish.gcv.modules.BukalapakInterface;
import com.rere.fish.gcv.modules.SelfServiceInterface;
import com.rere.fish.gcv.result.OnFinishEngineProcess;
import com.rere.fish.gcv.result.OnFinishVisionProcess;
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

/**
 * And dev
 */
public class PreProcessFragment extends Fragment implements OnFinishVisionProcess, OnFinishEngineProcess {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    String tempKeyWords = "";

    @BindView(R.id.rootFragmentView) FrameLayout rootView;
    @BindView(R.id.textAck) TextView textView;
    @BindView(R.id.animation_view) LottieAnimationView animationView;

    @Inject SelfServiceInterface engineServices;

    @Inject BukalapakInterface bukalapakInterface;

    private String tempFilePath;
    private String mParam2;

    private View v;
    private OnLoadingFragmentInteractionListener eventListener;

    public PreProcessFragment() {
        // Required empty public constructor
    }

    public static PreProcessFragment newInstance(String tempFilePath) {
        PreProcessFragment fragment = new PreProcessFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, tempFilePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tempFilePath = getArguments().getString(ARG_PARAM1);
        }

        //Getinjector
        App.get(getActivity()).getInjector().inject(this);

        try {
            callCloudVision(FileUtil.getBitmapFromStorage(tempFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pre_process, container, false);

        ButterKnife.bind(this, v);

        updateLayout(false, "<blink>Processing your image</blink>");
        animationView.setAnimation("cube_loader.json");
        animationView.loop(true);
        animationView.addColorFilterToLayer("bg",
                new PorterDuffColorFilter(getResources().getColor(R.color.colorPrimary),
                        PorterDuff.Mode.DARKEN));
        animationView.playAnimation();

        return v;
    }

    private void callCloudVision(Bitmap bitmap) throws IOException {
        new AsyncTask<Object, Void, JsonObject>() {
            @Override
            protected JsonObject doInBackground(Object... params) {
                HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                try {

                    VisionRequestInitializer visionRequestInitializer = new VisionRequestInitializer(
                            BuildConfig.API_KEY_GCV);
                    Vision.Builder visionBuilder = new Vision.Builder(httpTransport, jsonFactory,
                            null);
                    visionBuilder.setVisionRequestInitializer(visionRequestInitializer);

                    Vision vision = visionBuilder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = generateGCVRequest(
                            bitmap);
                    Vision.Images.Annotate annotateRequest = vision.images().annotate(
                            batchAnnotateImagesRequest);
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
            protected void onPostExecute(JsonObject obj) {
                onReceivedResultFromGCV(obj);
            }
        }.execute();
    }

    private void callEngineService(JsonObject jsonObject) {
        //        RequestBody requestRaw = RequestBody.create(MediaType.parse("application/json"),
        //                loadJSONFromAsset());

        RequestBody requestRaw;

        if (jsonObject != null) {
            requestRaw = RequestBody.create(MediaType.parse("application/json"),
                    jsonObject.toString());
        } else {
            requestRaw = RequestBody.create(MediaType.parse("application/json"), new String(""));
        }


        engineServices.getLabel(requestRaw).enqueue(new Callback<ResponseEngine>() {
            @Override
            public void onResponse(Call<ResponseEngine> call, Response<ResponseEngine> response) {
                onReceivedResultFromEngine(response.body());
            }

            @Override
            public void onFailure(Call<ResponseEngine> call, Throwable t) {
                Timber.e("Failed to call engine services.. " + t.getMessage());
                updateLayout(false, "Error occurred");
            }
        });
    }

    private String unifiedKeywords(List<ResponseEngine.ResponsePair> resp) {
        String keyWords = "";

        for (ResponseEngine.ResponsePair r : resp) {
            if (resp.indexOf(r) == resp.size() - 1) keyWords += r.keyword;
            else keyWords += r.keyword + "+";
        }

        return keyWords;
    }

    private BatchAnnotateImagesRequest generateGCVRequest(Bitmap bmp) {
        BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
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

    private JsonObject convertResponseToString(BatchAnnotateImagesResponse response) throws IOException {
        String message = "";

        List<AnnotateImageResponse> responses = response.getResponses();

        for (AnnotateImageResponse a : responses) {
            WebDetection detectionRes = a.getWebDetection();

            if (!detectionRes.getWebEntities().isEmpty()) {
                for (WebEntity e : detectionRes.getWebEntities()) {
                    if (e.getScore() > 0.8001) {
                        tempKeyWords += e.getDescription() + "+";
                        if (detectionRes.getWebEntities().indexOf(e) == 1) break;
                    }
                }
            } else {
                tempKeyWords += "Ikan lele";
            }

            for (WebEntity e : detectionRes.getWebEntities()) {
                Timber.i(
                        "entity_get : " + e.getDescription() + " = " + e.getScore() + " --> annotate pos : " + responses.indexOf(
                                a));
            }
        }

        JsonObject requestEngineObj = new JsonObject();
        JsonArray jsonElements = new JsonArray();

        for (AnnotateImageResponse a : responses) {
            WebDetection detectionRes = a.getWebDetection();
            message = detectionRes.getWebEntities().toString();

            for (WebEntity w : detectionRes.getWebEntities()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("entityId", w.getEntityId());
                jsonObject.addProperty("score", w.getScore());
                jsonObject.addProperty("description", w.getDescription());

                jsonElements.add(jsonObject);
            }
            break;
        }
        requestEngineObj.add("webEntities", jsonElements);

        return requestEngineObj;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoadingFragmentInteractionListener) {
            eventListener = (OnLoadingFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement OnLoadingFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        eventListener = null;
    }

    @Override
    public void onReceivedResultFromGCV(JsonObject obj) {
        Timber.i("Calling from GCV is done...");
        updateLayout(false, "Filter the result");
        callEngineService(obj);
    }

    private void updateLayout(boolean isFinish, String text) {
        if (text.equals("")) textView.setText(Html.fromHtml("Done"));
        else textView.setText(Html.fromHtml(text));

        if (isFinish) {
            animationView.setVisibility(View.GONE);
            textView.setTextColor(getResources().getColor(R.color.colorTextDark));
            rootView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            animationView.setVisibility(View.VISIBLE);
            textView.setTextColor(getResources().getColor(R.color.colorTextPrimary));
            rootView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public void onReceivedResultFromEngine(ResponseEngine resp) {
        Timber.i("Calling from Engine is done...");

        if (eventListener != null) {
            try {
                if (!resp.listResponsePair.isEmpty())
                    eventListener.onReceivedKeywords(unifiedKeywords(resp.listResponsePair));
            } catch (NullPointerException ex) {
                Timber.e("Response from engine null, using gcv result instead");
                eventListener.onReceivedKeywords(tempKeyWords);
            }
        }
    }

    public interface OnLoadingFragmentInteractionListener {
        void onReceivedKeywords(String keywords);
    }

}

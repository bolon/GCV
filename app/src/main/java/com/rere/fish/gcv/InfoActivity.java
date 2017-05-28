package com.rere.fish.gcv;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InfoActivity extends AppCompatActivity {
    @BindView(R.id.text_step_how_to) TextView textHowTo;
    @BindView(R.id.text_list_lib) TextView textLibs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ButterKnife.bind(this);

        String howTo = "1. Pilih citra yang akan dideteksi <br>" +
                "2. Lakukan crop pada gambar <br>" +
                "3. Tekan tombol Next <br>" +
                "4. Jelajahi pilihan produk yang ditampilkan <br>" +
                "5. Lanjutkan transaksi di aplikasi Bukalapak <br>";

        String libs =
                "<a href=\"http://square.github.io/dagger/\">Dagger</a> <br>" +
                "<a href=\"http://square.github.io/retrofit/\">Retrofit</a> <br>" +
                "<a href=\"https://github.com/JakeWharton/timber\">Timber</a> <br>" +
                "<a href=\"http://jakewharton.github.io/butterknife\">Butterknife</a> <br>" +
                "<a href=\"https://github.com/airbnb/lottie-android\">Lottie</a> <br>" +
                "<a href=\"https://github.com/Karumi/Dexter\">Karumi Permission</a> <br>" +
                "<a href=\"https://github.com/gogopop/CameraKit-Android\">Camera Kit</a> <br>" +
                "<a href=\"https://github.com/ArthurHub/Android-Image-Cropper\">Image Cropper</a> <br>" +
                "<a href=\"https://github.com/johncarl81/parceler\">Parceler</a> <br>" +
                "<a href=\"https://github.com/fiskurgit/ChipCloud\">Chip Cloud</a> <br>" +
                "<a href=\"https://github.com/bumptech/glide\">Glide</a> <br>" ;

        textHowTo.setText(Html.fromHtml(howTo).toString());
        textLibs.setText(Html.fromHtml(libs));
        textLibs.setLinkTextColor(Color.BLUE);
        textLibs.setMovementMethod(LinkMovementMethod.getInstance());
    }
}

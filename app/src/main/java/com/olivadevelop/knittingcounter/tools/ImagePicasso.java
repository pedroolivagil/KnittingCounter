package com.olivadevelop.knittingcounter.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.olivadevelop.knittingcounter.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ImagePicasso {

    private ImagePicasso() {
    }

    public static void load(String url, ImageView view) {
        load(url, view, new RoundedTransformationBuilder().build());
    }

    public static void load(File url, ImageView view) {
        load(url, view, new RoundedTransformationBuilder().build());
    }

    public static void load(String url, ImageView view, Transformation transformation) {
        Picasso.get().load(url).noFade().fit().transform(transformation).centerCrop()
                .placeholder(R.drawable.image_area_alpha)
                .error(R.drawable.image_broken_variant_alpha)
                .into(view);
    }

    public static void load(File url, ImageView view, Transformation transformation) {
        Picasso.get().load(url).noFade().fit().transform(transformation).centerCrop()
                .placeholder(R.drawable.image_area_alpha)
                .error(R.drawable.image_broken_variant_alpha)
                .into(view);
    }

    public static void loadWithoutTransform(Context c, String url, ImageView view, float[] sizes) {
        float deseado = Tools.convertPixelsToDp(c, 400);
        Picasso.get().load(url).noFade().placeholder(R.drawable.image_area_alpha)
                .error(R.drawable.image_broken_variant_alpha)
                .resize((int) deseado, (int) resizeImg(sizes[0], sizes[1], deseado))
                .centerCrop().into(view);
    }

    private static float resizeImg(float anchoOriginal, float altoOriginal, float anchoDeseado) {
        return (anchoDeseado * altoOriginal) / anchoOriginal;
    }


    public static String getImageBase64FromUrl(String url) {
        if (url != null) {
            Bitmap bm = resizeBitmap(BitmapFactory.decodeFile(url.trim()));
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            return base64ToString(bao.toByteArray());
        } else {
            return null;
        }
    }

    public static String base64ToString(byte[] str) {
        return Base64.encodeToString(str, Base64.DEFAULT);
    }

    public static byte[] StringTobase64(String str) {
        return Base64.decode(str, Base64.DEFAULT);
    }

    public static Bitmap resizeBitmap(Bitmap b) {
        while (b.getHeight() > 4096 || b.getWidth() > 4096) {
            b = getResizedBitmap(b, .9f);
        }
        return b;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, float scaleXY) {
        if (bm != null) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            //matrix.postScale(scaleWidth, scaleHeight);
            matrix.postScale(scaleXY, scaleXY);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);
            bm.recycle();
            return resizedBitmap;
        }
        return null;
    }

    /**
     * Redimensiona una imagen (drawable) y devuelve un objeto drawable
     *
     * @param ctx   Contexto
     * @param resId ID del drawable a redimensionar
     * @param w     Ancho deseado
     * @param h     Alto deseado
     * @return Drawable
     */
    public static Drawable resizeImage(Context ctx, int resId, int w, int h) {

        // cargamos la imagen de origen
        Bitmap BitmapOrg = BitmapFactory.decodeResource(ctx.getResources(),
                resId);

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();

        // calculamos el escalado de la imagen destino
        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        // para poder manipular la imagen
        // debemos crear una matriz

        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);

        // volvemos a crear la imagen con los nuevos valores
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0,
                width, height, matrix, true);

        // si queremos poder mostrar nuestra imagen tenemos que crear un
        // objeto drawable y así asignarlo a un botón, imageview...
        return new BitmapDrawable(resizedBitmap);

    }
}

package android.course.a_b_c.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public final class Converter {

    private Converter(){}

    public static String fromListToString(HashSet<String> list){
        StringBuilder sb = new StringBuilder();
        if (list != null) {
            int i = 0;
            for (String s : list) {
                sb.append(s);
                if (i < list.size()-1)
                    sb.append(", ");
                i++;
            }
        }
        return sb.toString();
    }

    public static String fromListToString(List<String> list){
        StringBuilder sb = new StringBuilder();
        if (list != null) {
            int i = 0;
            for (String s : list) {
                sb.append(s);
                if (i < list.size()-1)
                    sb.append(", ");
                i++;
            }
        }
        return sb.toString();
    }

    public static String combineLines(List<String> list){
        StringBuilder sb = new StringBuilder();
        if (list != null) {
            int i = 0;
            for (String s : list) {
                sb.append(s);
                if (i < list.size()-1)
                    sb.append("\n");
                i++;
            }
        }
        return sb.toString();
    }

    public static Bitmap decodeImage(byte[] arr){
        Bitmap img;

        img = BitmapFactory.decodeByteArray(arr, 0,arr.length);

        return img;
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap){
        ByteArrayOutputStream bas;
        byte[] bArray;
        try {
            bas = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,0 ,bas);
            bArray = bas.toByteArray();
            bas.flush();
            bas.close();
        } catch (IOException e) {
            Log.e("IOException ",e.getMessage());
            return null;
        }
        return bArray;

    }

    public static boolean saveBitmap(Context ctx,String filename, Bitmap bitmap){
        File file = new File(ctx.getFilesDir(), "images");
        file.mkdirs();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file + filename+".png");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static List<String> fromStringToLines(String lines) {
        String[] line = lines.split("\n");
        return Arrays.asList(line);
    }

    public static Bitmap cropThumbnail(Bitmap image) {
        try {
            return ThumbnailUtils.extractThumbnail(image, 108, 108);
        }catch (Exception e){
            return null;
        }

    }
}

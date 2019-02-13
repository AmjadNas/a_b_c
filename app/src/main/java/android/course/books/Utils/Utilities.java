package android.course.books.Utils;

import android.content.Context;
import android.content.Intent;
import android.course.books.DatabaseHandkers.DataHandler;
import android.course.books.Objects.Story;
import android.course.books.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public final class Utilities {

    private Utilities(){}

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

    public static Uri getImageUri(Context ctx, String filename , Bitmap img) {
        try {
            Uri uri = null;
            File imageFile = saveBitmap(ctx, filename,  img);
            if (imageFile != null)
                uri = FileProvider.getUriForFile(ctx, "android.course.books.fileprovider", imageFile);

            return uri;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void shareStory(Context ctx, Story story ){
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_TEXT, " Story title: " + story.getTitle() +
                "\n Author: " + story.getAuthor() +
                "\n Synopsis: " + story.getSynopsis());
        Bitmap img = DataHandler.getInstance().getStoryCover(story.getTitle());
        if (img != null) {
            Uri uri = Utilities.getImageUri(ctx, story.getTitle(), img);
            if (uri != null) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // intent.setDataAndType(uri, getContentResolver().getType(uri));
                // intent.setData(uri);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            }
        }
        intent.setType("*/*");

        ctx.startActivity(Intent.createChooser(intent, ctx.getResources().getText(R.string.send_to)));
    }

    private static File saveBitmap(Context ctx, String filename, Bitmap bitmap){
        // Create an image file name
        try {
            File storageDir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    filename,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            FileOutputStream fos = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG,0 ,fos);
            fos.flush();
            fos.close();
            return image;
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

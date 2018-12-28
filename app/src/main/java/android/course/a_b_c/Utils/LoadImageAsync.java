package android.course.a_b_c.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ProgressBar;

public class LoadImageAsync extends AsyncTask<Bitmap,Bitmap,Bitmap> {

    private static final int HEIGHT = 500;
    private static final int WIDTH = 500;
    private OnImageResized listener;

    public LoadImageAsync(OnImageResized listener){
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... objects) {
        try {
            if (objects.length > 0){
                Bitmap temp = Bitmap.createScaledBitmap(objects[0], WIDTH, HEIGHT, false);

                return Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), null, false);
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Bitmap o) {
        if (o != null) {
            listener.ImageResized(o);
        }else {
            listener.ErrorResizingImage();
        }

        listener = null;

    }

    @Override
    protected void onPreExecute() {

    }

    public interface OnImageResized{
        void ImageResized(Bitmap o);
        void ErrorResizingImage();
    }
}

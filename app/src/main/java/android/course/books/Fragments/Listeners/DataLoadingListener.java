package android.course.books.Fragments.Listeners;

public interface DataLoadingListener {
    /**
     * indicates that the data is being loaded
     */
    void onLoadingStart();
    /**
     * indicates that the data is done loading
     */
    void onnLoadingDone();
}

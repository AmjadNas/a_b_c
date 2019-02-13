package android.course.books.Services;

import org.json.JSONObject;

import java.util.List;

public interface IResultReceiver {

	void onRecieve(List<JSONObject> results);
}

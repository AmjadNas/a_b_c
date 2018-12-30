package android.course.books_312316433_313601130.Services;

import org.json.JSONObject;

import java.util.List;

public interface IResultReceiver {

	void onRecieve(List<JSONObject> results);
}

package android.course.a_b_c.Services;

import org.json.JSONObject;

import java.util.List;

public interface IResultReceiver {

	void onRecieve(List<JSONObject> results);
}

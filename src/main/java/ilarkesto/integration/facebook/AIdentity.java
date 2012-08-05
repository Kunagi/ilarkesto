package ilarkesto.integration.facebook;

import ilarkesto.base.time.DateAndTime;
import ilarkesto.json.JsonObject;

public abstract class AIdentity extends AObject {

	public AIdentity(JsonObject json) {
		super(json);
		if (!json.contains("id"))
			throw new IllegalStateException("Missing id in FacebookIdentity: " + json.toString());
	}

	public final String getId() {
		return json.getString("id");
	}

	public final DateAndTime getCreatedTime() {
		return getDate("created_time");
	}

	public final boolean containsFrom() {
		return json.contains("from");
	}

	public final String getFacebookGraphUrl() {
		return "https://graph.facebook.com/" + getId();
	}

	public final String getFacebookLink() {
		return "https://www.facebook.com/" + getId();
	}

}
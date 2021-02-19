package trong.lixco.com.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "time_bound")
public class TimeBound extends AbstractEntity {
	private String name;
	private int hour;
	// de them duoc so 0 dang truoc
	private String minutes;
	private String description;
	private boolean is_allow_register_current = false;

	public boolean isIs_allow_register_current() {
		return is_allow_register_current;
	}

	public void setIs_allow_register_current(boolean is_allow_register_current) {
		this.is_allow_register_current = is_allow_register_current;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public String getMinutes() {
		return minutes;
	}

	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}
}
